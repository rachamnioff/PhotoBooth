package com.rakel.he.photo_booth.model;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;

@Table("photo")
public class PhotoModel {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @NotNull
    private String name;

    @NotNull
    @Unique
    private String filePath;

    @NotNull
    private long createTimestamp;
}
