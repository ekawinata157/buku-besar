package com.example.bukubesar.demo.writer;

import com.example.bukubesar.demo.annotation.XlsxWriteable;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class XlsxWriter<T> {
    private Class<T> tClass;

    public void writeToExcel(List<T> tList, String filePath) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        writeHeader(sheet.createRow(1));
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.0###"));
        int rowCount = 1;

        for (T t : tList) {
            Row row = sheet.createRow(++rowCount);
            Cell numberCell = row.createCell(1);
            numberCell.setCellValue(rowCount - 1);
            writeSingleRow(t, row, cellStyle);
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeader(Row row) {
        Field[] fields = this.tClass.getDeclaredFields();
        AtomicInteger cellNumber = new AtomicInteger(1);

        Arrays.stream(fields)
                .filter(a -> a.isAnnotationPresent(XlsxWriteable.class))
                .sorted((a, b) -> {
                    XlsxWriteable xlsxReadableA = a.getAnnotation(XlsxWriteable.class);
                    XlsxWriteable xlsxReadableB = b.getAnnotation(XlsxWriteable.class);
                    return Integer.compare(xlsxReadableA.order(), xlsxReadableB.order());
                })
                .forEach(a -> row.createCell(cellNumber.getAndIncrement())
                        .setCellValue(a.getAnnotation(XlsxWriteable.class).headerName()));
    }

    private void writeSingleRow(T t, Row row, CellStyle cellStyle) {
        Field[] fields = this.tClass.getDeclaredFields();
        AtomicInteger cellNumber = new AtomicInteger(1);

        Arrays.stream(fields)
                .filter(a -> a.isAnnotationPresent(XlsxWriteable.class))
                .sorted((a, b) -> {
                    XlsxWriteable xlsxReadableA = a.getAnnotation(XlsxWriteable.class);
                    XlsxWriteable xlsxReadableB = b.getAnnotation(XlsxWriteable.class);
                    return Integer.compare(xlsxReadableA.order(), xlsxReadableB.order());
                })
                .forEach(a -> {
                    Cell cell = row.createCell(cellNumber.getAndIncrement());
                    try {
                        a.setAccessible(true);
                        XlsxWriteable xlsxWriteable = a.getAnnotation(XlsxWriteable.class);
                        if (xlsxWriteable.type().equals(double.class)) {
                            cell.setCellStyle(cellStyle);
                            Double doubleValue = (Double) a.get(t);
                            cell.setCellValue(String.format("%.2f", doubleValue));
                        }
                        else if (xlsxWriteable.type().equals(Date.class)) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            cell.setCellValue(simpleDateFormat.format(a.get(t)));
                        }
                        else {
                            cell.setCellValue(a.get(t).toString());
                        }
                    } catch (IllegalAccessException e) {
                        cell.setCellValue("");
                    }
                });
    }
}
