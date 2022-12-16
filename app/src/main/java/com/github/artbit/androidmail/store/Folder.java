package com.github.artbit.androidmail.store;

import org.litepal.crud.LitePalSupport;

public class Folder extends LitePalSupport {

    public String name;


    public Folder() { }


    public Folder(String name) {
        this.name = name;
    }

}
