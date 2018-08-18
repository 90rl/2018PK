package com.amazingteam.competenceproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.interfaces.Wardrobe;
import com.amazingteam.competenceproject.model.Cloth;
import com.amazingteam.competenceproject.ui.ClothesAdapter;
import com.amazingteam.competenceproject.ui.EmptyRecyclerView;
import com.amazingteam.competenceproject.ui.GridSpacingItemDecoration;
import com.amazingteam.competenceproject.util.DatabaseHelper;

import java.util.List;

import static com.amazingteam.competenceproject.util.Constants.CLOTH_ID;
import static com.amazingteam.competenceproject.util.Constants.EDIT_MODE;

public class WardrobeActivity extends AppCompatActivity implements Wardrobe {

    private static final String TAG = WardrobeActivity.class.getSimpleName();
    private EmptyRecyclerView recyclerView;
    private FloatingActionButton addClothButton;

    List<Cloth> clothes;
    private ClothesAdapter clothesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        addClothButton = (FloatingActionButton) findViewById(R.id.buttonAddNewClothes);
        addClothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WardrobeActivity.this, PhotoActivity.class);
                startActivity(intent);
            }
        });

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        clothes = databaseHelper.getAllClothes();
        Log.d(TAG, clothes.toString());
        databaseHelper.closeDataBase();
        setClothesRecyclerView();
    }

    @Override
    public void onDeleteSelected(int position) {
        Toast.makeText(this, clothes.get(position).getName() + " deleted", Toast.LENGTH_SHORT).show();
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        databaseHelper.deleteCloth(clothes.get(position).getId());
        clothes = databaseHelper.getAllClothes();
        databaseHelper.closeDataBase();
        clothesAdapter.setClothes(clothes);
        clothesAdapter.notifyItemRemoved(position);
        clothesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditSelected(int position) {
        Intent intent = new Intent(WardrobeActivity.this, SelectTagsActivity.class);
        intent.putExtra(EDIT_MODE, true);
        intent.putExtra(CLOTH_ID, clothes.get(position).getId());
        startActivity(intent);
    }

    private void setClothesRecyclerView() {
        recyclerView = (EmptyRecyclerView) findViewById(R.id.clothesRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(findViewById(R.id.empty_view));
        clothesAdapter = new ClothesAdapter(this, this);
        recyclerView.setAdapter(clothesAdapter);
        clothesAdapter.setClothes(clothes);
        clothesAdapter.notifyDataSetChanged();
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}
