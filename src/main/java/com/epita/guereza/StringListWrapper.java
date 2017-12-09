package com.epita.guereza;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
public class StringListWrapper {
    public List<String> list;

    public StringListWrapper() {
    }

    public StringListWrapper(List<String> list) {
        this.list = list;
    }

    public StringListWrapper(String[] array) {
        this.list = Arrays.asList(array);
    }
}
