package com.example.backend.dto;

import com.example.backend.util.FrenchToEnglishDoubleConverter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentDTO {

    @CsvBindByPosition(position = 0)
    private int id;

    @CsvBindByPosition(position = 1)
    private String fullName;

    @CsvBindByPosition(position = 2)
    private String gender;

    @CsvBindByPosition(position = 3)
    private String bachelor;

    @CsvCustomBindByPosition(position = 4, converter = FrenchToEnglishDoubleConverter.class)
    private double average;

    @CsvCustomBindByPosition(position = 5, converter = FrenchToEnglishDoubleConverter.class)
    private double padl;

    @CsvCustomBindByPosition(position = 6, converter = FrenchToEnglishDoubleConverter.class)
    private double pdlo;

    @CsvCustomBindByPosition(position = 7, converter = FrenchToEnglishDoubleConverter.class)
    private double pwnd;

    @CsvCustomBindByPosition(position = 8, converter = FrenchToEnglishDoubleConverter.class)
    private double irs;

    @CsvCustomBindByPosition(position = 9, converter = FrenchToEnglishDoubleConverter.class)
    private double stages7;

    @CsvCustomBindByPosition(position = 10, converter = FrenchToEnglishDoubleConverter.class)
    private double s5;

    @CsvCustomBindByPosition(position = 11, converter = FrenchToEnglishDoubleConverter.class)
    private double s6;


}