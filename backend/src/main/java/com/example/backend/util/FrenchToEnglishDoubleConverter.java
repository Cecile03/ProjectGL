package com.example.backend.util;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class FrenchToEnglishDoubleConverter extends AbstractBeanField<String, Double> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (value.isEmpty()) {
            return null;
        }
        String englishValue = value.replace(',', '.');
        try {
            return Double.parseDouble(englishValue);
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }
}