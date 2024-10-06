package com.example.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CSVData {
    private List<String[]> data;

    public CSVData() {
        this.data = new ArrayList<>();
    }

}