package com.chalmers.outphonenum;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button = null;
    private Button button2 = null;
    private Button button3 = null;
    private Button button4 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhoneStateService.class);

                startService(intent);
                button.setText("服务已开启");
                Log.d("TAG","MainActivity");
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatabase();
            }
        });

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryDatabase();
            }
        });

        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabase();
            }
        });
    }

    private void initDatabase(){
        MyDatabaseHelper helper = new MyDatabaseHelper(MainActivity.this);
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();

        //添加测试数据
        ContentValues values = new ContentValues();
        values.put("userId", 1);
        values.put("number", "15574363447");
        values.put("type", 0);

        writableDatabase.insert("blacklist", null, values);

        ContentValues values2 = new ContentValues();
        values2.put("userId", 1);
        values2.put("number", "15574363447");
        values2.put("type", 1);

        writableDatabase.insert("blacklist", null, values2);

        ContentValues values3 = new ContentValues();
        values3.put("userId", 1);
        values3.put("number", "15700721904");
        values3.put("type", 0);
        writableDatabase.insert("blacklist", null, values3);

    }

    private void queryDatabase(){
        MyDatabaseHelper helper = new MyDatabaseHelper(MainActivity.this);
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Cursor cursor = readableDatabase.query("blacklist", null, null, null, null, null, null);
        while(cursor.moveToNext()){
            Log.d("TAG","userId-->"+cursor.getInt(cursor.getColumnIndex("userId")));
            Log.d("TAG","number-->"+cursor.getString(cursor.getColumnIndex("number")));
            Log.d("TAG","type-->"+cursor.getInt(cursor.getColumnIndex("type")));
        }
    }

    private void deleteDatabase(){
        MyDatabaseHelper helper = new MyDatabaseHelper(MainActivity.this);
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        writableDatabase.delete("blacklist",null,null);
    }
}
