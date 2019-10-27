package com.rakel.he.photo_booth.model;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.rakel.he.photo_booth.contacts.PhotoGalleryContacts;

import java.util.ArrayList;

public class PhotoGalleryModel extends  IModel implements PhotoGalleryContacts.Model {
    private LiteOrm liteOrm;

    public PhotoGalleryModel(Context context)
    {
        super(context);
    }

    @Override
    public void loadPhotoes(final ModelListener modelListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                QueryBuilder builder=new QueryBuilder<PhotoBean>(PhotoBean.class)
                        .appendOrderDescBy("createTimestamp");
                ArrayList<PhotoBean> beans=liteOrm.query(builder);
                if(modelListener!=null)
                    modelListener.completed(beans);
            }
        }).start();

    }
}
