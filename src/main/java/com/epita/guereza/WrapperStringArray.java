package com.epita.guereza;

import java.util.Arrays;
import java.util.List;

public class WrapperStringArray {
    public List<String> content;

    public WrapperStringArray() {}

    public WrapperStringArray(String[] content) {
        this.content = Arrays.asList(content);
    }
}
