package com.runops.imagesearch.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.runops.imagesearch.R;
import com.runops.imagesearch.model.Result;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class ResultArrayAdapter extends ArrayAdapter<Result> {

    public ResultArrayAdapter(Context context, ArrayList<Result> resultList) {
        super(context, 0, resultList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Result result = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_result, parent, false);
        }

        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);

        Picasso.with(getContext()).load(result.tbUrl)
                .into(ivThumbnail);

        return convertView;
    }
}
