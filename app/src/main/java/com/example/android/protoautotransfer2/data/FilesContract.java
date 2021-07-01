package com.example.android.protoautotransfer2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class FilesContract
{
    public static final String CONTENT_AUTHORITY = "com.example.android.protoautotransfer2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FILES = "files";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FILES);

    private FilesContract()
    {

    }

    public static final class FilesEntry implements BaseColumns
    {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FILES);

        public final static String TABLE_NAME = "files";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FILES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FILES;


        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TASK_NAME ="name";

        public final static String COLUMN_SOURCE_DIRECTORY = "source_directory";

        public final static String COLUMN_DESTINATION_DIRECTORY = "destination_directory";

        public final static String COLUMN_TASK = "move_or_copy";

        public static boolean isValidTask(int gender)
        {
            return gender == TASK_COPY || gender == TASK_MOVE;
        }

        public static final int TASK_MOVE = 0;
        public static final int TASK_COPY = 1;
    }
}
