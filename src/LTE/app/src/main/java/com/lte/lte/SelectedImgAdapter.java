package com.lte.lte;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class SelectedImgAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Bitmap> mBitmaps;
    private final int MAX_DIMENSION = 300;
    private Boolean misCbVisible;

    public SelectedImgAdapter(Context mContext, ArrayList<Bitmap> bitmaps) {
        this.mContext = mContext;
        this.mBitmaps = bitmaps;
        this.misCbVisible = false;
    }


    @Override
    public int getCount() {
        return this.mBitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mBitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_am_picture, parent, false);
        }

        ImageView imgView = convertView.findViewById(R.id.item_img);
        CheckBox checkBox = convertView.findViewById(R.id.item_cb);
        if (!misCbVisible)
            checkBox.setVisibility(View.GONE);
        else
            checkBox.setVisibility(View.VISIBLE);

        imgView.setImageBitmap(this.mBitmaps.get(position));
        imgView.setLayoutParams(new RelativeLayout.LayoutParams(GridView.AUTO_FIT, MAX_DIMENSION));


        return convertView;
    }
    void setCbVisibility(Boolean isVisible){
        misCbVisible=isVisible;
    }
    void remove(int position){
        mBitmaps.remove(position);
    }

}
