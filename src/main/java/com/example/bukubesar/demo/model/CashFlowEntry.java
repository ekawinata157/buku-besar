package com.example.bukubesar.demo.model;

import com.example.bukubesar.demo.annotation.DateFormat;
import com.example.bukubesar.demo.annotation.XlsxReadable;
import com.example.bukubesar.demo.annotation.XlsxWriteable;
import com.example.bukubesar.demo.interfaces.XlsxRead;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CashFlowEntry implements XlsxRead {
    @XlsxReadable
    private String no;
    @XlsxReadable(order = 1, type = Date.class)
    @XlsxWriteable(order = 1, headerName = "Date", type = Date.class)
    private Date date;
    @XlsxReadable(order = 2)
    @XlsxWriteable(order = 2, headerName = "Reference Code")
    private String referenceCode;
    @XlsxReadable(order = 3)
    @XlsxWriteable(order = 3, headerName = "Code")
    private String code;
    @XlsxReadable(order = 4)
    @XlsxWriteable(order = 4, headerName = "Opposite Code")
    private String oppositeCode;
    @XlsxReadable(order = 5)
    @XlsxWriteable(order = 5, headerName = "Description")
    private String description;
    @XlsxReadable(order = 6, type = double.class)
    @XlsxWriteable(order = 6, headerName = "Debit", type = double.class)
    private double debit;
    @XlsxReadable(order = 7, type = double.class)
    @XlsxWriteable(order = 7, headerName = "Credit", type = double.class)
    private double credit;
    @XlsxWriteable(order = 8, headerName = "Balance", type = double.class)
    private double balance;

    public boolean isInvalidEntry() {
        Field[] fields = this.getClass().getDeclaredFields();

        return Arrays.stream(fields)
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return Objects.isNull(f.get(this));
                    } catch (IllegalAccessException e) {
                        return true;
                    }
                }).collect(Collectors.toList())
                .contains(true);
    }

    public double calculateBalance(double currentBalance) {
        this.balance = currentBalance + this.debit - this.credit;
        return this.balance;
    }
}
