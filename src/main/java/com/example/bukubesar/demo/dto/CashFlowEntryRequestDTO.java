package com.example.bukubesar.demo.dto;

import lombok.Data;

@Data
public class CashFlowEntryRequestDTO {
    private String branchName;
    private String sheetName;
    private String period;
    private String password;
    private String filePath;
    private String targetFilePath;
}
