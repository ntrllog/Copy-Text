package com.ntrllog.copytext;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TextAdapter extends ArrayAdapter<Text> {

    public TextAdapter(Activity context, ArrayList<Text> texts) {
        super(context, 0, texts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Text currentText = getItem(position);

        TextView type = listItemView.findViewById(R.id.type);
        type.setText(currentText.getType());

        return listItemView;
    }
}
