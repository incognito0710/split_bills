package com.splitbills.splitbills_backend.service;

import com.splitbills.splitbills_backend.model.*;
import com.splitbills.splitbills_backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SettlementService {

    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;

    public SettlementService(GroupRepository groupRepository, ExpenseRepository expenseRepository) {
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
    }

    // Method 1: Calculate simple balance per member
    public List<SettlementDTO> calculateSettlement(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<User> members = new ArrayList<>(group.getMembers());
        if (members.isEmpty()) return Collections.emptyList();

        List<Expense> expenses = expenseRepository.findByGroup(group);

        // Sum amounts defensively (skip null amounts)
        double totalExpense = 0.0;
        for (Expense e : expenses) {
            if (e == null) continue;
            Double amt = e.getAmount();
            totalExpense += (amt == null ? 0.0 : amt);
        }

        double share = totalExpense / members.size();

        Map<Long, Double> paidMap = new HashMap<>();
        for (User member : members) {
            Long id = member == null ? null : member.getId();
            if (id != null) paidMap.put(id, 0.0);
        }

        for (Expense e : expenses) {
            if (e == null) continue;
            User payer = e.getPaidBy();
            Double amt = e.getAmount();
            if (amt == null) amt = 0.0;
            if (payer == null || payer.getId() == null) {
                // Skip expenses without a valid payer
                System.out.println("Warning: Expense with id " + e.getId() + " has no valid payer");
                continue;
            }
            Long payerId = payer.getId();

            // Ensure there's an entry in the paidMap for the payer
            paidMap.putIfAbsent(payerId, 0.0);

            Double currentPaidObj = paidMap.get(payerId);
            double currentPaid = currentPaidObj == null ? 0.0 : currentPaidObj;
            paidMap.put(payerId, currentPaid + amt);

            if (!group.getMembers().contains(payer)) {
                System.out.println("Warning: User " + payerId + " is not a group member!");
            }
        }

        List<SettlementDTO> settlements = new ArrayList<>();
        for (User member : members) {
            if (member == null || member.getId() == null) continue;
            Double paidObj = paidMap.get(member.getId());
            double paid = paidObj == null ? 0.0 : paidObj;
            double balance = paid - share;
            settlements.add(new SettlementDTO(member.getId(), member.getName(), balance));
        }

        return settlements;
    }

    // Method 2: Calculate actual transactions "who pays whom"
    public List<TransactionDTO> calculateTransactions(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<User> members = new ArrayList<>(group.getMembers());
        if (members.isEmpty()) return Collections.emptyList();

        List<Expense> expenses = expenseRepository.findByGroup(group);

        double totalExpense = 0.0;
        for (Expense e : expenses) {
            if (e == null) continue;
            Double amt = e.getAmount();
            totalExpense += (amt == null ? 0.0 : amt);
        }

        double share = totalExpense / members.size();

        // Track paid amounts
        Map<Long, Double> paidMap = new HashMap<>();
        for (User member : members) {
            Long id = member == null ? null : member.getId();
            if (id != null) paidMap.put(id, 0.0);
        }

        for (Expense e : expenses) {
            if (e == null) continue;
            User payer = e.getPaidBy();
            Double amt = e.getAmount();
            if (amt == null) amt = 0.0;
            if (payer == null || payer.getId() == null) {
                System.out.println("Warning: Expense with id " + e.getId() + " has no valid payer");
                continue;
            }
            Long payerId = payer.getId();

            paidMap.putIfAbsent(payerId, 0.0);
            Double currentPaidObj = paidMap.get(payerId);
            double currentPaid = currentPaidObj == null ? 0.0 : currentPaidObj;
            paidMap.put(payerId, currentPaid + amt);
        }

        // Calculate balance per member
        Map<Long, Double> balanceMap = new HashMap<>();
        for (User member : members) {
            if (member == null || member.getId() == null) continue;
            Double paidObj = paidMap.get(member.getId());
            double paid = paidObj == null ? 0.0 : paidObj;
            balanceMap.put(member.getId(), paid - share); // +ve = creditor, -ve = debtor
        }

        // Separate creditors and debtors
        List<Map.Entry<Long, Double>> creditors = new ArrayList<>();
        List<Map.Entry<Long, Double>> debtors = new ArrayList<>();

        for (Map.Entry<Long, Double> entry : balanceMap.entrySet()) {
            Double val = entry.getValue();
            double v = val == null ? 0.0 : val;
            if (v > 0) creditors.add(entry);
            else if (v < 0) debtors.add(entry);
        }

        // Build quick lookup for members by id to avoid Optional.get()
        Map<Long, User> membersById = new HashMap<>();
        for (User u : members) if (u != null && u.getId() != null) membersById.put(u.getId(), u);

        // Generate transactions
        List<TransactionDTO> transactions = new ArrayList<>();
        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            Map.Entry<Long, Double> debtor = debtors.get(i);
            Map.Entry<Long, Double> creditor = creditors.get(j);

            double debtorVal = debtor.getValue() == null ? 0.0 : debtor.getValue();
            double creditorVal = creditor.getValue() == null ? 0.0 : creditor.getValue();

            double debtAmount = -debtorVal; // make positive
            double creditAmount = creditorVal;

            double settledAmount = Math.min(debtAmount, creditAmount);

            User fromUser = membersById.get(debtor.getKey());
            User toUser = membersById.get(creditor.getKey());

            if (fromUser == null || toUser == null) {
                // If user is missing from members (shouldn't happen), skip this pair
                if (fromUser == null) i++; else if (toUser == null) j++;
                continue;
            }

            transactions.add(new TransactionDTO(fromUser.getName(), toUser.getName(), settledAmount));

            // Update balances (use Double objects explicitly)
            double newDebtorVal = debtorVal + settledAmount;   // debt decreases (becomes closer to 0)
            double newCreditorVal = creditorVal - settledAmount; // credit decreases

            debtor.setValue(newDebtorVal);
            creditor.setValue(newCreditorVal);

            if (Math.abs(newDebtorVal) < 0.001) i++; // move to next debtor
            if (newCreditorVal < 0.001) j++;         // move to next creditor
        }

        return transactions;
    }
}
