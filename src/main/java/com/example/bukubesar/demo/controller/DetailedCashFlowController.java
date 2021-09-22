package com.example.bukubesar.demo.controller;

import com.example.bukubesar.demo.dto.CashFlowEntryRequestDTO;
import com.example.bukubesar.demo.model.CashFlowEntry;
import com.example.bukubesar.demo.service.CashFlowEntryService;
import com.example.bukubesar.demo.writer.XlsxWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cash-flow")
public class DetailedCashFlowController {
    private final CashFlowEntryService cashFlowEntryService;

    @Autowired
    public DetailedCashFlowController(CashFlowEntryService cashFlowEntryService) {
        this.cashFlowEntryService = cashFlowEntryService;
    }

    @PostMapping("/detailed-cash-flow")
    public ResponseEntity<String> produceDetailedCashFlowReport(@RequestBody CashFlowEntryRequestDTO cashFlowEntryRequestDTO) {
        Map<String, List<CashFlowEntry>> groupedCashFlow = this.cashFlowEntryService.processCashFlowEntry(cashFlowEntryRequestDTO);
        XlsxWriter<CashFlowEntry> cashFlowEntryXlsxWriter = new XlsxWriter<>(CashFlowEntry.class);
        cashFlowEntryXlsxWriter.writeFile(groupedCashFlow, cashFlowEntryRequestDTO.getTargetFilePath());
        return ResponseEntity.ok("Processed");
    }
}
