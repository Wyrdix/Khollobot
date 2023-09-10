package com.wyrdix.khollobot.field;

import com.wyrdix.khollobot.KUser;

public abstract class KField<T> {

    public KField() {}

    public abstract T get(KUser user);

    public abstract void set(KUser user, T value);

    public T sanitize(T value){
        return value;
    }

}
