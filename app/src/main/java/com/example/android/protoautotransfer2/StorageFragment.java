package com.example.android.protoautotransfer2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import java.text.DecimalFormat;

public class StorageFragment extends Fragment
{


    public StorageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView =  inflater.inflate(R.layout.fragment_storage, container, false);
        TextView totalStorage = rootView.findViewById(R.id.total_storage_editable);
        TextView availableStorage = rootView.findViewById(R.id.available_storage_editable);
        PieChart storageChart = rootView.findViewById(R.id.storage_chart);
        Button checkFiles = rootView.findViewById(R.id.button_check_files);

        checkFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivity(intent);

                Intent intent  = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                startActivityForResult(intent, 0);
            }
        });

        String ts = calculateTotalStorage(bytesToHuman(totalMemory()));
        String as = bytesToHuman(freeMemory());
        double freePercent = Double.parseDouble(as.substring(0, as.length()-2))/Double.parseDouble(ts.substring(0,ts.length()-2)) * 100;
        int sliceFree = (int) freePercent;
        storageChart.addPieSlice(
                new PieModel(
                        "used",
                        100-sliceFree,
                        Color.parseColor("#35ffff")));

        storageChart.addPieSlice(
                new PieModel(
                        "Free",
                        sliceFree,
                        Color.parseColor("#394450")));

        storageChart.startAnimation();

        totalStorage.setText(ts);

        availableStorage.setText(as);

        return rootView;
    }

    private long totalMemory()
    {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long   total  = (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
        return Math.round(total*1.024);
    }

    private long freeMemory()
    {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long   free   = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
        return Math.round(free*1.024*1.048);
    }

    private long busyMemory()
    {
        return totalMemory()-freeMemory();
    }

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

        return "???";
    }
    private String calculateTotalStorage(String give_space)
    {
        String suffix = give_space.substring(give_space.length()-2, give_space.length());
        Double num  = Double.parseDouble(give_space.substring(0, give_space.length()-2));

        if(num < 1)
        {
            num = 1.00;
        }
        else if(num > 1 && num < 2)
        {
            num = 2.00;
        }
        else if(num > 2 && num < 4)
        {
            num =4.00;
        }
        else if(num > 4 && num < 8)
        {
            num = 8.00;
        }
        else if(num > 8 && num < 16)
        {
            num = 16.00;
        }
        else if(num > 16 && num < 32)
        {
            num = 32.00;
        }
        else if(num >32 && num < 64)
            num = 64.00;
        else if(num > 64 && num < 128)
            num = 128.00;
        else if(num > 128 && num < 256)
            num = 256.00;
        else if(num > 256 && num < 512)
            num = 512.00;


        return num+suffix;
    }
}