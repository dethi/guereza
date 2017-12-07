package com.epita.guereza.winter;

public class Singleton<BEAN_TYPE> implements Provider {
    private BEAN_TYPE instance;

    public Singleton(BEAN_TYPE instance) {
        this.instance = instance;
    }

    @Override
    public BEAN_TYPE getInstance() {
        return instance;
    }
}
