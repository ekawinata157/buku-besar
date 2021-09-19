package com.example.bukubesar.demo.reader;

import com.example.bukubesar.demo.annotation.DateFormat;
import com.example.bukubesar.demo.annotation.XlsxReadable;
import com.example.bukubesar.demo.interfaces.XlsxRead;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class XlsxReader<T extends XlsxRead> {
    private final Workbook hssfWorkbook;
    private final Class<T> tClass;

    public XlsxReader(String filePath, String password, Class<T> tClass) throws IOException {
        this.tClass = tClass;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(bufferedInputStream);
        Biff8EncryptionKey.setCurrentUserPassword(password);
        this.hssfWorkbook = new HSSFWorkbook(poifsFileSystem, true);
    }

    public List<T> getListOfData(String sheetName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Sheet sheet = this.hssfWorkbook.getSheet(sheetName);
        List<T> tList = new ArrayList<>();
        for (Row currentRow : sheet) {
            tList.add(convertRowToEntry(currentRow));
        }
        return tList;
    }

    private T convertRowToEntry(Row row) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        List<Field> orderedFields = this.getOrderedFields();
        Iterator<Cell> cellIterator = row.cellIterator();
        T entry = tClass.getDeclaredConstructor().newInstance();
        int cellCount = 0;
        while (cellIterator.hasNext() && cellCount <= 7) {
            try {
                Cell cell = cellIterator.next();
                Field fieldToSet = orderedFields.get(cellCount);
                this.setObjectValue(entry, fieldToSet, cell);
                cellCount++;
            } catch (Exception e) {
                break;
            }
        }
        return entry;
    }

    private List<Field> getOrderedFields() {
        return Arrays.stream(tClass.getDeclaredFields()).filter(f -> f.isAnnotationPresent(XlsxReadable.class)).sorted((a, b) -> {
            XlsxReadable aXsxlxReadable = a.getAnnotation(XlsxReadable.class);
            XlsxReadable bXsxlxReadable = b.getAnnotation(XlsxReadable.class);
            return Integer.compare(aXsxlxReadable.order(), bXsxlxReadable.order());
        }).collect(Collectors.toList());
    }

    private void setObjectValue(T t, Field field, Cell cell) throws IllegalAccessException, ParseException {
        field.setAccessible(true);
        if (field.getAnnotation(XlsxReadable.class).type() == Date.class) {
            Date parsedCellDate = cell.getDateCellValue();
            field.set(t, parsedCellDate);
            return;
        }
        if (field.getAnnotation(XlsxReadable.class).type() == int.class) {
            int parsedInt = (int) cell.getNumericCellValue();
            field.set(t, parsedInt);
            return;
        }
        if (field.getAnnotation(XlsxReadable.class).type() == double.class) {
            field.set(t, cell.getNumericCellValue());
            return;
        }
        cell.setCellType(CellType.STRING);
        String parsedString = cell.getStringCellValue().replace(".0", "");
        field.set(t, parsedString);
    }
}

