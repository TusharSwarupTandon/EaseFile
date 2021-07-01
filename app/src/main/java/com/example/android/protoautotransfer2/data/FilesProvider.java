/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.protoautotransfer2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.protoautotransfer2.data.FilesContract.FilesEntry;

public class FilesProvider extends ContentProvider
{
    public static final String LOG_TAG = FilesProvider.class.getSimpleName();

    private static final int TASKS = 100;

    private static final int TASK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sUriMatcher.addURI(FilesContract.CONTENT_AUTHORITY, FilesContract.PATH_FILES, TASKS);
        sUriMatcher.addURI(FilesContract.CONTENT_AUTHORITY, FilesContract.PATH_FILES + "/#", TASK_ID);
    }

    private FilesDbHelper mDbHelper;

    @Override
    public boolean onCreate()
    {
        mDbHelper = new FilesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case TASKS:
                cursor = database.query(FilesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TASK_ID:
                selection = FilesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(FilesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case TASKS:
                return insertTask(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTask(Uri uri, ContentValues values)
    {
        String name = values.getAsString(FilesEntry.COLUMN_TASK_NAME);
        if (name == null)
        {
            throw new IllegalArgumentException("Task requires a name");
        }

        Integer task = values.getAsInteger(FilesEntry.COLUMN_TASK);
        if (task == null || !FilesEntry.isValidTask(task))
        {
            throw new IllegalArgumentException("A valid task is required");
        }

        String source = values.getAsString(FilesEntry.COLUMN_SOURCE_DIRECTORY);
        if (source == null)
        {
            throw new IllegalArgumentException("Task requires valid source directory");
        }

        String destination = values.getAsString(FilesEntry.COLUMN_DESTINATION_DIRECTORY);
        if (destination == null)
        {
            throw new IllegalArgumentException("Task requires valid destination directory");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(FilesEntry.TABLE_NAME, null, values);
        if (id == -1)
        {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case TASKS:
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TASK_ID:
                selection = FilesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTask(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        if (values.containsKey(FilesEntry.COLUMN_TASK_NAME))
        {
            String name = values.getAsString(FilesEntry.COLUMN_TASK_NAME);
            if (name == null)
            {
                throw new IllegalArgumentException("Task requires a name");
            }
        }

        if (values.containsKey(FilesEntry.COLUMN_TASK))
        {
            Integer task = values.getAsInteger(FilesEntry.COLUMN_TASK);
            if (task == null || !FilesEntry.isValidTask(task))
            {
                throw new IllegalArgumentException("A valid task is required");
            }
        }

        if (values.containsKey(FilesEntry.COLUMN_SOURCE_DIRECTORY))
        {
            String source = values.getAsString(FilesEntry.COLUMN_SOURCE_DIRECTORY);
            if (source == null)
            {
                throw new IllegalArgumentException("Task requires a valid source");
            }
        }

        if (values.containsKey(FilesEntry.COLUMN_DESTINATION_DIRECTORY))
        {
            String destination = values.getAsString(FilesEntry.COLUMN_DESTINATION_DIRECTORY);
            if (destination == null)
            {
                throw new IllegalArgumentException("Task requires a valid destination");
            }
        }

        if (values.size() == 0)
        {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(FilesEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case TASKS:
                rowsDeleted = database.delete(FilesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                selection = FilesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(FilesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case TASKS:
                return FilesEntry.CONTENT_LIST_TYPE;
            case TASK_ID:
                return FilesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
