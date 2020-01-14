package me.ghui.v2er.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Assets {

    public static String getString(String path, Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(path));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                stringBuilder.append(line);
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
