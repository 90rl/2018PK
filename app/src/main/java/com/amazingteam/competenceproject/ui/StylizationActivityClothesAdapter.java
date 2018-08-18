package com.amazingteam.competenceproject.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.model.Cloth;
import com.amazingteam.competenceproject.util.BitmapLoader;

import java.util.List;


public class StylizationActivityClothesAdapter extends BaseAdapter {
    private List<Cloth> cloths;
    private LayoutInflater layoutInflater;

    public StylizationActivityClothesAdapter(Context context, List<Cloth> cloths) {
        this.cloths = cloths;
        this.layoutInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return cloths.size();
    }

    @Override
    public Object getItem(int i) {
        return cloths.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView thumbnail, thumbnail2;
        Cloth cloth = (Cloth) getItem(i);

        if (cloths.get(i).getType().equals("winterboots")) {
            view = layoutInflater.inflate(R.layout.stylization_activity_list_item_boots, viewGroup, false);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            thumbnail2 = (ImageView) view.findViewById(R.id.thumbnail2);
            if (cloth.getImagePath() != null) {
                thumbnail.setImageBitmap(BitmapLoader.load(cloth.getImagePath()));
                thumbnail2.setImageBitmap(BitmapLoader.load(cloth.getImagePath()));
            } else {
                thumbnail.setImageResource(R.drawable.tshirt_1_icon);
                thumbnail2.setImageResource(R.drawable.tshirt_1_icon);
            }
        } else {
            view = layoutInflater.inflate(R.layout.stylization_activity_list_item_cloth, viewGroup, false);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            thumbnail.setImageBitmap(BitmapLoader.load(cloth.getImagePath()));
        }
        return view;
    }

}
