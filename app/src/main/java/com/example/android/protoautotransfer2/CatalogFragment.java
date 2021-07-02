package com.example.android.protoautotransfer2;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.protoautotransfer2.data.FilesContract;
import com.example.android.protoautotransfer2.data.FilesContract.FilesEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CatalogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public final int EXTERNAL_REQUEST = 138;

    private Uri mCurrentFilesUri;

    private static int FILES_LOADER = 0;

    private FilesCursorAdapter mCursorAdapter;
    Context applicationContext;

    public CatalogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Make sure you have this line of code.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_catalog, container, false);
        requestForPermission();

        ImageButton menu = rootView.findViewById(R.id.pop_up_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(),menu);
                popupMenu.getMenuInflater().inflate(R.menu.menu_catalog, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.action_insert_dummy_data:
                                insertTask();
                                break;
                            case R.id.action_delete_all_entries:
                                deleteAllTasks();
                                return true;
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView filesListView = rootView.findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.empty_view);
        filesListView.setEmptyView(emptyView);

        mCursorAdapter = new FilesCursorAdapter(getContext(), null);
        filesListView.setAdapter(mCursorAdapter);

        //Setup click listener
        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView source = view.findViewById(R.id.source_folder_editable);
                String sourceDir = source.getText().toString();

                TextView destination = view.findViewById(R.id.destination_folder_editable);
                String destinationDir = destination.getText().toString();

                TextView taskType = view.findViewById(R.id.task_type_editable);
                String taskInfo = taskType.getText().toString();
                int condition = -1;
                if(taskInfo.equals("Move"))
                    condition = 0;
                else if(taskInfo.equals("Copy"))
                    condition = 1;

                Toast.makeText(getContext(), "Syncing...",Toast.LENGTH_SHORT).show();
                copyFileOrDirectory(sourceDir, destinationDir, condition);
            }
        });

        filesListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), EditorActivity.class);
            Uri currentPetUri = ContentUris.withAppendedId(FilesEntry.CONTENT_URI, id);
            intent.setData(currentPetUri);
            startActivity(intent);
            return true;
        });



        //Kick off the loader
        LoaderManager.getInstance(this).initLoader(0, null, this);

        return rootView;
    }

    private void insertTask()
    {
        ContentValues values = new ContentValues();
        values.put(FilesEntry.COLUMN_TASK_NAME, "Screenshots");
        values.put(FilesEntry.COLUMN_SOURCE_DIRECTORY, "/storage/emulated/0/DCIM/Screenshots/");
        values.put(FilesEntry.COLUMN_DESTINATION_DIRECTORY, "/storage/emulated/0/Pictures/Screenshots/" );
        values.put(FilesEntry.COLUMN_TASK, FilesEntry.TASK_MOVE);

        Uri newUri = getContext().getContentResolver().insert(FilesEntry.CONTENT_URI, values);
    }

    private void deleteAllTasks() {
        int rowsDeleted = getActivity().getContentResolver().delete(FilesEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args)
    {
        String[] projection = {
                FilesEntry._ID,
                FilesEntry.COLUMN_TASK_NAME,
                FilesEntry.COLUMN_SOURCE_DIRECTORY,
                FilesEntry.COLUMN_DESTINATION_DIRECTORY,
                FilesEntry.COLUMN_TASK
        };

        return new CursorLoader(getContext(),
                FilesEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data)
    {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
    {
        mCursorAdapter.swapCursor(null);
    }

    public void copyFileOrDirectory(String srcDir, String dstDir, int condition)
    {

        try
        {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory())
            {
                String[] files = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++)
                {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1, condition);
                }
            }
            else
            {
                copyFile(src, dst, condition);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            String type = "";
            if(condition  == 0)
                type = "Moved";
            if(condition == 1)
                type = "Copied";
            Toast.makeText(getContext(), "Files "+type+" Successfully.",Toast.LENGTH_SHORT).show();
        }
    }

    public static void copyFile(File sourceFile, File destFile, int condition) throws IOException
    {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists())
        {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try
        {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            if(condition == 0)
                deleteDir(sourceFile);
        }
        finally
        {
            if (source != null)
            {
                source.close();
            }
            if (destination != null)
            {
                destination.close();
            }
        }
    }

    public boolean requestForPermission()
    {
        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23)
        {
            if (!canAccessExternalSd())
            {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd()
    {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm)
    {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getContext(), perm));

    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success)
                {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
        {
            return dir.delete();
        }
        else
        {
            return false;
        }
    }


}