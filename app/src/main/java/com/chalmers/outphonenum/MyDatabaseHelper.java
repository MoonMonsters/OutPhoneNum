package com.chalmers.outphonenum;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chalmers on 2016-05-02 15:18.
 * email:qxinhai@yeah.net
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    //黑名单
    //userId: 用户id
    //number: 加入黑名单号码
    //type: 类型，是接听黑名单，还是短信黑名单-->0:接听 1:短信
    public static final String CREATE_TABLE = "create table blacklist(" +
            "userId integer," +
            "number text," +
            "type integer)";

    public static final String DATABASE = "blacklist.db";
    public static final int VERSION = 1;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public MyDatabaseHelper(Context context){
        this(context,DATABASE,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}