package com.splitbills.splitbills_backend.controller;

import com.splitbills.splitbills_backend.model.SettlementDTO;
import com.splitbills.splitbills_backend.model.TransactionDTO;
import com.splitbills.splitbills_backend.service.SettlementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    // Endpoint 1: Get balance per member
    @GetMapping("/{groupId}")
    public List<SettlementDTO> getSettlement(@PathVariable Long groupId) {
        return settlementService.calculateSettlement(groupId);
    }

    // Endpoint 2: Get actual transactions (who pays whom)
    @GetMapping("/transactions/{groupId}")
    public List<TransactionDTO> getTransactions(@PathVariable Long groupId) {
        return settlementService.calculateTransactions(groupId);
    }
}
