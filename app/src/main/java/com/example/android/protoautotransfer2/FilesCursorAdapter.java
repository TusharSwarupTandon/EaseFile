package com.example.android.protoautotransfer2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.protoautotransfer2.data.FilesContract.FilesEntry;

public class FilesCursorAdapter extends CursorAdapter
{
    public FilesCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView mNameTextView = view.findViewById(R.id.name);
        TextView mSourceTextView = view.findViewById(R.id.source_folder_editable);
        TextView mDestinationTextView = view.findViewById(R.id.destination_folder_editable);
        TextView mTaskTextView = view.findViewById(R.id.task_type_editable);

        String name = cursor.getString(cursor.getColumnIndex(FilesEntry.COLUMN_TASK_NAME));
        String source = cursor.getString(cursor.getColumnIndex(FilesEntry.COLUMN_SOURCE_DIRECTORY));
        String destination = cursor.getString(cursor.getColumnIndex(FilesEntry.COLUMN_DESTINATION_DIRECTORY));
        int task = cursor.getInt(cursor.getColumnIndex(FilesEntry.COLUMN_TASK));


        mNameTextView.setText(name);
        mSourceTextView.setText(source);
        mDestinationTextView.setText(destination);
        if(task == 0)
        {
            mTaskTextView.setText(R.string.task_move);
        }
        else
        {
            mTaskTextView.setText(R.string.task_copy);
        }
    }
}