package com.example.secrets;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SecretsAdapter extends BaseAdapter {

    private List<Pair<String, BitmapDrawable>> secrets;
    private LayoutInflater layoutInflater;

    public SecretsAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        secrets = new ArrayList<Pair<String, BitmapDrawable>>();
    }

    public void addSecret(String text, BitmapDrawable imageBitmapDrawable) {
        secrets.add(new Pair(text, imageBitmapDrawable));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return secrets.size();
    }

    @Override
    public Object getItem(int i) {
        return secrets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.secret_list_item, viewGroup, false);
        }

        TextView secretTextView = (TextView) convertView.findViewById(R.id.list_secret_text);
        secretTextView.setText(secrets.get(i).first);

        ImageView secretImageView = (ImageView) convertView.findViewById(R.id.list_secret_image);
        secretImageView.setImageDrawable(secrets.get(i).second);

        return convertView;
    }
}
