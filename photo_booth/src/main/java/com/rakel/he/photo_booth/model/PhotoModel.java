package com.rakel.he.photo_booth.model;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.rakel.he.photo_booth.contacts.PhotoContacts;

import java.util.ArrayList;

public class PhotoModel implements PhotoContacts.Model {
    private LiteOrm liteOrm;

    public PhotoModel(Context context)
    {
        liteOrm=LiteOrm.newSingleInstance(context, "photo_booth.db");
    }

    @Override
    public void loadPhotoes(ModelListener modelListener) {
        QueryBuilder builder=new QueryBuilder<PhotoBean>(PhotoBean.class)
                .appendOrderDescBy("createTimestamp");
        ArrayList<PhotoBean> beans=liteOrm.query(builder);
        if(modelListener!=null)
            modelListener.completed(beans);
    }
}
