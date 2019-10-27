package com.rakel.he.photo_booth.model;

import android.content.Context;

import com.litesuits.orm.LiteOrm;

public abstract class IModel {
    protected LiteOrm liteOrm;
    public  IModel(Context context)
    {
        liteOrm=LiteOrm.newSingleInstance(context, "photo_booth.db");
    }
}
