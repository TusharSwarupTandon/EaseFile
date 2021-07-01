package com.example.android.protoautotransfer2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.protoautotransfer2.data.FilesContract.FilesEntry;

public class FilesDbHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public FilesDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String SQL_CREATE_TASKS_TABLE = "CREATE TABLE "+ FilesEntry.TABLE_NAME + "("
                + FilesEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FilesEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, "
                + FilesEntry.COLUMN_SOURCE_DIRECTORY + " TEXT NOT NULL, "
                + FilesEntry.COLUMN_DESTINATION_DIRECTORY + " TEXT NOT NULL, "
                + FilesEntry.COLUMN_TASK + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_TASKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public long insert(SQLiteDatabase db, String taskName, String sourceDir, int destinationDir, int task)
    {
        ContentValues values = new ContentValues();
        values.put(FilesEntry.COLUMN_TASK_NAME, taskName);
        values.put(FilesEntry.COLUMN_SOURCE_DIRECTORY, sourceDir);
        values.put(FilesEntry.COLUMN_DESTINATION_DIRECTORY, destinationDir);
        values.put(FilesEntry.COLUMN_TASK, task);

        long res = -1;
        try
        {
            res = db.insert(FilesEntry.TABLE_NAME, null, values);
//            db.delete(FilesEntry.TABLE_NAME,null,null);
        }
        catch (SQLException e)
        {
            res = -1;
        }
        return res;
    }
}
