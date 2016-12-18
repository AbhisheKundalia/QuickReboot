package com.awiserk.kundalias.rootboot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Abhishek on 12/18/2016.
 */

public class ListViewAdapter extends ArrayAdapter<ButtonData> {
    public ListViewAdapter(Context context, ArrayList<ButtonData> buttonDataArrayList) {
        super(context, 0, buttonDataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
