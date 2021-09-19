package com.example.bukubesar.demo.service;

import com.example.bukubesar.demo.dto.CashFlowEntryRequestDTO;
import com.example.bukubesar.demo.entity.BranchInitialBalanceInformation;
import com.example.bukubesar.demo.model.CashFlowEntry;
import com.example.bukubesar.demo.reader.XlsxReader;
import com.example.bukubesar.demo.repository.BranchInitialBalanceInformationRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CashFlowEntryService {
    private final BranchInitialBalanceInformationRepository branchInitialBalanceInformationRepository;

    @Autowired
    public CashFlowEntryService(BranchInitialBalanceInformationRepository branchInitialBalanceInformationRepository) {
        this.branchInitialBalanceInformationRepository = branchInitialBalanceInformationRepository;
    }

    @SneakyThrows
    public Map<String, List<CashFlowEntry>> processCashFlowEntry(CashFlowEntryRequestDTO cashFlowEntryRequestDTO) {
        List<CashFlowEntry> cashFlowEntryList = readCashFlowEntry(cashFlowEntryRequestDTO);
        Map<String, List<CashFlowEntry>> groupedCashFlowEntry = groupCashFlowEntryByCode(cashFlowEntryList);

        for (Map.Entry<String, List<CashFlowEntry>> entry : groupedCashFlowEntry.entrySet()) {
            String currentCode = entry.getKey();
            List<CashFlowEntry> currentCashFlowEntry = entry.getValue();
            updateBalanceOfCashFlowEntry(cashFlowEntryRequestDTO, currentCode, currentCashFlowEntry);
        }

        return groupedCashFlowEntry;
    }

    @SneakyThrows
    private List<CashFlowEntry> readCashFlowEntry(CashFlowEntryRequestDTO cashFlowEntryRequestDTO) throws IOException {
        XlsxReader<CashFlowEntry> cashFlowEntryXlsxReader = new XlsxReader<>(cashFlowEntryRequestDTO.getFilePath(), cashFlowEntryRequestDTO.getPassword(), CashFlowEntry.class);
        return cashFlowEntryXlsxReader
                .getListOfData(cashFlowEntryRequestDTO.getSheetName())
                .stream()
                .filter(s -> !s.isInvalidEntry())
                .collect(Collectors.toList());
    }

    private Map<String, List<CashFlowEntry>> groupCashFlowEntryByCode(List<CashFlowEntry> cashFlowEntries) {
        return cashFlowEntries
                .stream()
                .collect(Collectors.groupingBy(CashFlowEntry::getCode,
                        () -> new TreeMap<>(Comparator.naturalOrder()),
                        Collectors.toList()
                        ));
    }

    private void updateBalanceOfCashFlowEntry(CashFlowEntryRequestDTO cashFlowEntryRequestDTO, String code, List<CashFlowEntry> cashFlowEntries) {
        Optional<BranchInitialBalanceInformation> initialBalanceInformation = this.branchInitialBalanceInformationRepository
                .findBranchInitialBalanceInformationByBranchAndPeriodAndCode(cashFlowEntryRequestDTO.getBranchName(), cashFlowEntryRequestDTO.getPeriod(), code);
        double initialBalance = initialBalanceInformation.map(s -> s.getDebit() + s.getCredit())
                .orElse(0.0);

        for (CashFlowEntry entry : cashFlowEntries) {
            initialBalance = entry.calculateBalance(initialBalance);
        }
    }
}
