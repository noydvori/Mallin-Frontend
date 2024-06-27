package com.example.ex3.devtool.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DatabaseUtils {
    public static void exportDatabase(Context context) {
        File src = context.getDatabasePath("graph_database8");
        File dst = new File(Environment.getExternalStorageDirectory(), "graph_database8.db");

        try (FileChannel srcChannel = new FileInputStream(src).getChannel();
             FileChannel dstChannel = new FileOutputStream(dst).getChannel()) {
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            Log.d("export file","success to export data");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("export file","failed to export data");
        }
    }
}
