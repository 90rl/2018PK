package com.amazingteam.competenceproject.ui;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.interfaces.Wardrobe;
import com.amazingteam.competenceproject.model.Cloth;
import com.amazingteam.competenceproject.model.Tag;
import com.amazingteam.competenceproject.util.BitmapLoader;

import java.util.List;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ClothViewHolder> {

    private Context context;
    private List<Cloth> clothes;
    private Wardrobe wardrobe;

    static class ClothViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView tags;
        ImageView thumbnail;
        ImageView overflow;

        ClothViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            tags = (TextView) view.findViewById(R.id.tags);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public ClothesAdapter(Context context, Wardrobe wardrobe) {
        this.context = context;
        this.wardrobe = wardrobe;
    }

    @Override
    public ClothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cloth, parent, false);
        return new ClothViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ClothViewHolder holder, final int position) {
        Cloth cloth = clothes.get(position);
        holder.name.setText(cloth.getName());
        holder.tags.setText(createTagString(cloth));

        if (cloth.getImagePath() != null) {
            holder.thumbnail.setImageBitmap(BitmapLoader.load(cloth.getImagePath()));
        } else {
            holder.thumbnail.setImageResource(R.drawable.tshirt_1_icon);
        }

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (clothes != null && clothes.size() > 0) {
            return clothes.size();
        }
        return 0;
    }

    public void setClothes(List<Cloth> clothes) {
        this.clothes = clothes;
    }

    private String createTagString(Cloth cloth) {
        StringBuilder tags = new StringBuilder();
        List<Tag> tagList = cloth.getTagList();
        for (int i = 0; i < tagList.size(); i++) {
            Tag tag = tagList.get(i);
            tags.append("#")
                    .append(tag.getName())
                    .append(" ");
        }
        return tags.toString();
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_cloth, popup.getMenu());
        popup.setOnMenuItemClickListener(new ClothMenuItemClickListener(position));
        popup.show();
    }

    class ClothMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        ClothMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit_cloth:
                    wardrobe.onEditSelected(position);
                    return true;
                case R.id.action_delete_cloth:
                    wardrobe.onDeleteSelected(position);
                    return true;
                default:
            }
            return false;
        }
    }
}
