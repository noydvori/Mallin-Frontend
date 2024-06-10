package com.example.ex3.devtool.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.room.Room;

import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.enteties.MagneticField;
import com.example.ex3.devtool.enteties.Wifi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExportToXml {

    private GraphDatabase roomDatabase;
    private Context context;
    public ExportToXml(Context context) {
        this.context = context;
        roomDatabase = GraphDatabase.getDatabase(context);
    }

    public void exportDataToXml() {
        // Query data from your Room database
        List<Wifi> wifiList = roomDatabase.wifiDao().getAll(); // Assuming you have a Dao for Wifi entity
        List<MagneticField> magneticFieldList = roomDatabase.magneticFieldDao().getAll(); // Assuming you have a Dao for MagneticField entity

        // Convert data to XML format
        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlStringBuilder.append("<data>\n");

        // Export Wifi data
        for (Wifi wifi : wifiList) {
            xmlStringBuilder.append("  <wifi>\n");
            xmlStringBuilder.append("    <userID>").append(wifi.getUserID()).append("</userID>\n");
            xmlStringBuilder.append("    <SSID>").append(wifi.getSSID()).append("</SSID>\n");
            xmlStringBuilder.append("    <BSSID>").append(wifi.getBSSID()).append("</BSSID>\n");
            xmlStringBuilder.append("    <rssi>").append(wifi.getRssi()).append("</rssi>\n");
            xmlStringBuilder.append("    <nodeDataId>").append(wifi.getNodeDataId()).append("</nodeDataId>\n");
            xmlStringBuilder.append("  </wifi>\n");
        }

        // Export MagneticField data
        for (MagneticField magneticField : magneticFieldList) {
            xmlStringBuilder.append("  <magneticField>\n");
            xmlStringBuilder.append("    <phoneType>").append(magneticField.getPhoneType()).append("</phoneType>\n");
            xmlStringBuilder.append("    <x>").append(magneticField.getX()).append("</x>\n");
            xmlStringBuilder.append("    <y>").append(magneticField.getY()).append("</y>\n");
            xmlStringBuilder.append("    <z>").append(magneticField.getZ()).append("</z>\n");
            xmlStringBuilder.append("    <nodeId>").append(magneticField.getNodeId()).append("</nodeId>\n");
            xmlStringBuilder.append("  </magneticField>\n");
        }

        xmlStringBuilder.append("</data>");

        // Write XML data to file
        try {
            File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File xmlFile = new File(externalStorageDir, "exported_data.xml");

            OutputStream outputStream = new FileOutputStream(xmlFile);
            outputStream.write(xmlStringBuilder.toString().getBytes());
            outputStream.close();

            // Notify MediaStore about the new file
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, xmlFile.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, "application/xml");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
