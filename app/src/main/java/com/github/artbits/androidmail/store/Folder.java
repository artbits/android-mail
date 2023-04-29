package com.github.artbits.androidmail.store;

import com.github.artbits.quickio.core.IOEntity;

public final class Folder extends IOEntity {
    public String name;

    public Folder() { }

    public Folder(String name) {
        this.name = name;
    }
}
