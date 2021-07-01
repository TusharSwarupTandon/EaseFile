package com.example.android.protoautotransfer2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

    }

    CatalogFragment catalogFragment = new CatalogFragment();
    StorageFragment storageFragment = new StorageFragment();
    CacheFragment   cacheFragment   = new CacheFragment();

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, catalogFragment).commit();
//                bottomNavigationView.setItemTextColor("35ffff");
                return true;
            case R.id.storage:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, storageFragment).commit();
                return true;
            case R.id.clear_cache:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, cacheFragment).commit();
                return true;
        }
        return false;
    }
}
