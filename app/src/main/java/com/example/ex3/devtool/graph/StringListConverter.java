package com.example.ex3.devtool.graph;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringListConverter {

    @TypeConverter
    public String fromList(List<String> list) {
        return list.stream().collect(Collectors.joining(","));
    }

    @TypeConverter
    public List<String> fromString(String value) {
        return Arrays.asList(value.split(","));
    }
}
