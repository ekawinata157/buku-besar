package com.example.bukubesar.demo;

import com.example.bukubesar.demo.dto.CashFlowEntryRequestDTO;
import com.example.bukubesar.demo.model.CashFlowEntry;
import com.example.bukubesar.demo.reader.XlsxReader;
import com.example.bukubesar.demo.service.CashFlowEntryService;
import com.example.bukubesar.demo.writer.XlsxWriter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(CashFlowEntryService cashFlowEntryService) {
        return args -> {
            CashFlowEntryRequestDTO cashFlowEntryRequestDTO = new CashFlowEntryRequestDTO();
            cashFlowEntryRequestDTO.setBranchName("SUKABUMI");
            cashFlowEntryRequestDTO.setPassword("23");
            cashFlowEntryRequestDTO.setFilePath("SKB 2020.xls");
            cashFlowEntryRequestDTO.setSheetName("Rekap BB Rinci");
            cashFlowEntryRequestDTO.setPeriod("2020");

            Map<String, List<CashFlowEntry>> groupedCashFlowEntry = cashFlowEntryService.processCashFlowEntry(cashFlowEntryRequestDTO);

            XlsxWriter<CashFlowEntry> xlsxWriter = new XlsxWriter<>(CashFlowEntry.class);
            xlsxWriter.writeToExcel(groupedCashFlowEntry.get("12001"), "SKB_2020_TEST_TEST.xls");
            System.out.println("Done processing");
        };
    }
}
