package com.splitbills.splitbills_backend.controller;

import com.splitbills.splitbills_backend.model.Expense;
import com.splitbills.splitbills_backend.model.User;
import com.splitbills.splitbills_backend.repository.ExpenseRepository;
import com.splitbills.splitbills_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // Add expense for logged-in user
    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody Expense expense, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        expense.setPaidBy(user); // assign expense to logged-in user
        expenseRepository.save(expense);
        return ResponseEntity.ok(expense);
    }

    // Get all expenses of logged-in user
    @GetMapping("/my")
    public ResponseEntity<?> getMyExpenses(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(expenseRepository.findByPaidBy(user));
    }
}
