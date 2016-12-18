package com.awiserk.kundalias.rootboot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Abhishek on 12/18/2016.
 */

public class ListViewAdapter extends ArrayAdapter<ButtonData> {
    public ListViewAdapter(Context context, ArrayList<ButtonData> buttonDataArrayList) {
        super(context, R.layout.list_item, buttonDataArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Check if the existing view is being reused, else inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //Get the current button data
        ButtonData currentButtonData = getItem(position);

        //Find the ImageView from list_item.xml and assign current button data to it
        ImageView buttonImage = (ImageView) listItemView.findViewById(R.id.image_icon);
        buttonImage.setImageResource(currentButtonData.getImageResourceID());

        //Find the Title View from listitem.xml and assign the current button data title to it
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentButtonData.getTitleID());

        //Find the Description View from the listitem.xml and assign the current button data desc to it
        TextView description = (TextView) listItemView.findViewById(R.id.description);
        description.setText(currentButtonData.getDescriptionID());


        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
