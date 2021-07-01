package com.example.android.protoautotransfer2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Method;

public class CacheFragment extends Fragment
{


    public CacheFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView =  inflater.inflate(R.layout.fragment_cache, container, false);
        Button clearCacheButton = rootView.findViewById(R.id.button_clear_cache);
        ProgressBar progressBar = rootView.findViewById(R.id.progress_circular);

        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache(getContext());
                progressBar.setVisibility(View.VISIBLE);
                clearCacheButton.setClickable(false);
                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        clearCacheButton.setClickable(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);

            }

        });
        return rootView;
    }

    public static void deleteCache(Context context) {
        try
        {
            File dir = context.getCacheDir();
            deleteDir(dir);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
        else if(dir.isFile())
        {
            return dir.delete();
        }
        else
        {
            return false;
        }
    }


}