package com.amazingteam.competenceproject.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.model.Cloth;
import com.amazingteam.competenceproject.model.Tag;
import com.amazingteam.competenceproject.ui.EditTextNewEvents;
import com.amazingteam.competenceproject.util.BitmapLoader;
import com.amazingteam.competenceproject.util.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.amazingteam.competenceproject.util.Constants.CLOTH_ID;
import static com.amazingteam.competenceproject.util.Constants.CLOTH_IMAGE_PATH;
import static com.amazingteam.competenceproject.util.Constants.CLOTH_IMAGE_PATH_TO_DELETE;
import static com.amazingteam.competenceproject.util.Constants.CLOTH_TYPE;
import static com.amazingteam.competenceproject.util.Constants.EDIT_MODE;

public class SelectTagsActivity extends AppCompatActivity {

    private static final String TAG = SelectTagsActivity.class.getSimpleName();

    ImageView ivClothPreview;
    TextView tvClothName;
    Button btnAssignTags;
    TextView tvTagList;
    EditTextNewEvents editText;
    TextInputLayout textInputLayout;
    RelativeLayout relativeLayout;

    List<Tag> tagList = new ArrayList<>();

    String[] tagNameList;
    boolean[] selectedTags;
    boolean[] selectedTagsColour;
    boolean[] selectedTagsPattern;
    boolean[] selectedTagsDestination;
    boolean[] selectedTagsWeather;

    ArrayList<Integer> chosenTags = new ArrayList<>();

    int currentCategoryIndex = 0;
    List<Tag> selectedTagList = new ArrayList<>();

    int numberOfColours;
    int numberOfPatterns;
    int numberOfDestinations;
    int numberOfWeathers;

    private boolean editMode;
    private Cloth editedCloth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        Intent intent = getIntent();

        editMode = intent.getBooleanExtra(EDIT_MODE, false);
        if (editMode) {
            editedCloth = databaseHelper.getCloth(intent.getIntExtra(CLOTH_ID, 0));
        } else {
            File file = new File(intent.getStringExtra(CLOTH_IMAGE_PATH_TO_DELETE));
            file.delete();
            String imageName = null;
            if (!intent.getStringExtra(CLOTH_IMAGE_PATH).equalsIgnoreCase("")) {
                imageName = intent.getStringExtra(CLOTH_IMAGE_PATH);
            }
            editedCloth = new Cloth(
                    "",
                    intent.getStringExtra(CLOTH_TYPE),
                    imageName,
                    new ArrayList<Tag>());
        }

        ivClothPreview = (ImageView) findViewById(R.id.ivClothPreview);
        tvClothName = (TextView) findViewById(R.id.tvClothName);
        btnAssignTags = (Button) findViewById(R.id.btnAssignTags);
        tvTagList = (TextView) findViewById(R.id.tvTagList);

        editText = (EditTextNewEvents) findViewById(R.id.inputText);

        if (editedCloth.getImagePath() == null) {
            ivClothPreview.setImageResource(R.drawable.tshirt_1_icon);
        } else {
            ivClothPreview.setImageBitmap(BitmapLoader.load(editedCloth.getImagePath()));
        }

        tvTagList.setSelected(true);

        editText.clearFocus();
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    tvClothName.setText(editText.getText().toString());
                    editedCloth.setName(editText.getText().toString());
                    editText.clearFocus();
                    return true;
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvClothName.setText(editText.getText().toString());
            }
        });

        textInputLayout = (TextInputLayout) findViewById(R.id.clothNameWrapper);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        List<String> tagNameListL = new ArrayList<>();
        List<Tag> tags = databaseHelper.getAllTags();

        for (Tag tag : tags) {
            tagNameListL.add(tag.getName());
        }

        tagNameList = tagNameListL.toArray(new String[tagNameListL.size()]);

        tagList = databaseHelper.getAllTags();

        for (int i = 0; i < tagList.size(); i++) {
            tagNameList[i] = tagList.get(i).getName();
        }

        selectedTags = new boolean[tagNameList.length];

        numberOfColours = databaseHelper.getNumberOfItemsInCategory("colour");
        numberOfPatterns = databaseHelper.getNumberOfItemsInCategory("pattern");
        numberOfDestinations = databaseHelper.getNumberOfItemsInCategory("destination");
        numberOfWeathers = databaseHelper.getNumberOfItemsInCategory("weather");

        selectedTagsColour = new boolean[numberOfColours];
        selectedTagsPattern = new boolean[numberOfPatterns];
        selectedTagsDestination = new boolean[numberOfDestinations];
        selectedTagsWeather = new boolean[numberOfWeathers];

        Log.d(TAG, numberOfColours + " " + numberOfPatterns + " " + numberOfDestinations + " " + numberOfWeathers);

        if (editedCloth.getTagList() != null) {
            for (Tag tag : editedCloth.getTagList()) {
                Log.d(TAG, tag.getId() + "");
                if (tag.getId() <= selectedTagsColour.length) {
                    selectedTagsColour[tag.getId() - 1] = true;
                } else if (tag.getId() > numberOfColours
                        && tag.getId() <= numberOfColours + numberOfPatterns) {
                    selectedTagsPattern[tag.getId() - 1 - numberOfColours] = true;
                } else if (tag.getId() > numberOfColours + numberOfPatterns && tag.getId()
                        <= numberOfColours + numberOfPatterns + numberOfDestinations) {
                    selectedTagsDestination[tag.getId() - 1 - numberOfColours - numberOfPatterns] = true;
                } else {
                    selectedTagsWeather[tag.getId() - 1 - numberOfColours - numberOfPatterns - numberOfDestinations] = true;
                }
                chosenTags.add(tag.getId() - 1);
            }
        }

        tvClothName.setText(editedCloth.getName());
        editText.setText(editedCloth.getName());
        StringBuilder item = new StringBuilder();
        for (int j = 0; j < chosenTags.size(); j++) {
            item.append(tagNameList[chosenTags.get(j)]);
            if (j != chosenTags.size() - 1) {
                item.append(", #");
            }
        }
        Log.d("chosenTags: ", chosenTags.toString());
        tvTagList.setText("#" + item.toString());
        if (chosenTags.size() == 0) {
            tvTagList.setText(R.string.temp_tag_list);
        }

        databaseHelper.closeDataBase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        if (!editText.isBackClicked && !editMode) {
            File file = new File(intent.getStringExtra(CLOTH_IMAGE_PATH));
            file.delete();
            intent = new Intent(SelectTagsActivity.this, PhotoActivity.class);
            startActivity(intent);
        }
        if (editMode) {
            intent = new Intent(SelectTagsActivity.this, WardrobeActivity.class);
            startActivity(intent);
        }
    }

    public void onClickAssignTags(View view) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        List<String> categories = databaseHelper.getAllCategories();
        showMultiCheckboxList(categories, currentCategoryIndex, categories.size());
        databaseHelper.closeDataBase();
    }

    private void showMultiCheckboxList(final List<String> categories, int actualCategory, int numberOfCategories) {
        actualCategory++;
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(SelectTagsActivity.this);
        adBuilder.setTitle("Select "
                + categories.get(currentCategoryIndex)
                + " tags. \n("
                + actualCategory
                + " from "
                + numberOfCategories
                + ")");
        switch (currentCategoryIndex) {
            case 0:
                List<String> tagNameListColour = Arrays.asList(tagNameList);
                tagNameListColour = tagNameListColour.subList(0, numberOfColours);
                String[] tagNameArrayColour = tagNameListColour.toArray(new String[tagNameListColour.size()]);
                adBuilder.setMultiChoiceItems(tagNameArrayColour, selectedTagsColour, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        Log.d("POSITION: ", Integer.toString(position));
                        Log.d("offsetColour: ", "0");
                        if (isChecked) {
                            if (!chosenTags.contains(position)) {
                                chosenTags.add(position);
                                selectedTagsColour[position] = true;
                            }
                        } else if (chosenTags.contains(position)) {
                            chosenTags.remove((Integer) position);
                            selectedTagsColour[position] = false;
                        }
                    }
                });
                break;
            case 1:
                final int offsetPattern = numberOfColours;
                List<String> tagNameListPattern = Arrays.asList(tagNameList);
                tagNameListPattern = tagNameListPattern.subList(
                        offsetPattern, offsetPattern + numberOfPatterns);
                String[] tagNameArrayPattern = tagNameListPattern.toArray(new String[tagNameListPattern.size()]);
                adBuilder.setMultiChoiceItems(tagNameArrayPattern, selectedTagsPattern, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        Log.d("POSITION: ", Integer.toString(position));
                        Log.d("offsetPattern: ", Integer.toString(offsetPattern));
                        if (isChecked) {
                            if (!chosenTags.contains(position + offsetPattern)) {
                                chosenTags.add(position + offsetPattern);
                                selectedTagsPattern[position] = true;
                            }
                        } else if (chosenTags.contains(position + offsetPattern)) {
                            chosenTags.remove(chosenTags.indexOf(position + offsetPattern));
                            selectedTagsPattern[position] = false;
                        }
                    }
                });
                break;
            case 2:
                final int offsetDestination = numberOfColours + numberOfPatterns;
                List<String> tagNameListDestinations = Arrays.asList(tagNameList);
                tagNameListDestinations = tagNameListDestinations.subList(
                        offsetDestination, offsetDestination + numberOfDestinations);
                String[] tagNameArrayDestinations = tagNameListDestinations.toArray(new String[tagNameListDestinations.size()]);
                adBuilder.setMultiChoiceItems(tagNameArrayDestinations, selectedTagsDestination, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        Log.d("POSITION: ", Integer.toString(position));
                        Log.d("offsetDestination: ", Integer.toString(offsetDestination));
                        if (isChecked) {
                            if (!chosenTags.contains(position + offsetDestination)) {
                                chosenTags.add(position + offsetDestination);
                                selectedTagsDestination[position] = true;
                            }
                        } else if (chosenTags.contains(position + offsetDestination)) {
                            chosenTags.remove(chosenTags.indexOf(position + offsetDestination));
                            selectedTagsDestination[position] = false;
                        }
                    }
                });
                break;
            case 3:
                final int offsetWeather = numberOfColours + numberOfPatterns + numberOfDestinations;
                List<String> tagNameListWeathers = Arrays.asList(tagNameList);
                tagNameListWeathers = tagNameListWeathers.subList(
                        offsetWeather,
                        offsetWeather + numberOfWeathers);
                String[] tagNameArrayWeathers = tagNameListWeathers.toArray(new String[tagNameListWeathers.size()]);
                adBuilder.setMultiChoiceItems(tagNameArrayWeathers, selectedTagsWeather, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        Log.d("POSITION: ", Integer.toString(position));
                        Log.d("offsetWeather: ", Integer.toString(offsetWeather));
                        if (isChecked) {
                            if (!chosenTags.contains(position + offsetWeather)) {
                                chosenTags.add(position + offsetWeather);
                                selectedTagsWeather[position] = true;
                            }
                        } else if (chosenTags.contains(position + offsetWeather)) {
                            chosenTags.remove(chosenTags.indexOf(position + offsetWeather));
                            selectedTagsWeather[position] = false;
                        }
                    }
                });
                break;
        }

        adBuilder.setCancelable(false);

        adBuilder.setPositiveButton(R.string.Next_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder item = new StringBuilder();
                for (int j = 0; j < chosenTags.size(); j++) {
                    item.append(tagNameList[chosenTags.get(j)]);
                    if (j != chosenTags.size() - 1) {
                        item.append(", #");
                    }
                }
                Log.d("chosenTags: ", chosenTags.toString());
                tvTagList.setText("#" + item.toString());
                if (item.toString().equals("")) tvTagList.setText(R.string.temp_tag_list);
                currentCategoryIndex++;
                if (currentCategoryIndex < categories.size()) {
                    showMultiCheckboxList(categories, currentCategoryIndex, categories.size());
                } else {
                    currentCategoryIndex = 0;
                }
            }
        });

        adBuilder.setNegativeButton(R.string.Prev_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder item = new StringBuilder();
                for (int j = 0; j < chosenTags.size(); j++) {
                    item.append(tagNameList[chosenTags.get(j)]);
                    if (j != chosenTags.size() - 1) {
                        item.append(", #");
                    }
                }
                tvTagList.setText("#" + item.toString());
                if (item.toString().equals("")) tvTagList.setText(R.string.temp_tag_list);
                currentCategoryIndex--;
                if (currentCategoryIndex >= 0) {
                    showMultiCheckboxList(categories, currentCategoryIndex, categories.size());
                } else {
                    currentCategoryIndex = 0;
                }
            }
        });

        adBuilder.setNeutralButton(R.string.ClearAll_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < selectedTags.length; j++) {
                    selectedTags[j] = false;
                    chosenTags.clear();
                    tvTagList.setText(R.string.temp_tag_list);
                }
                for (int j = 0; j < selectedTagsColour.length; j++) {
                    selectedTagsColour[j] = false;
                }
                for (int j = 0; j < selectedTagsPattern.length; j++) {
                    selectedTagsPattern[j] = false;
                }
                for (int j = 0; j < selectedTagsDestination.length; j++) {
                    selectedTagsDestination[j] = false;
                }
                for (int j = 0; j < selectedTagsWeather.length; j++) {
                    selectedTagsWeather[j] = false;
                }
                currentCategoryIndex = 0;
            }
        });

        AlertDialog adDialog = adBuilder.create();
        adDialog.setContentView(R.layout.dialog_layout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(adDialog.getWindow().getAttributes());

        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        int dialogWindowHeight = (int) (displayHeight * 0.65f);

        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        adDialog.getWindow().setAttributes(layoutParams);

        adDialog.show();
    }

    public void onClickCancel(View view) {
        Intent intent = getIntent();
        if (editMode) {
            intent = new Intent(SelectTagsActivity.this, WardrobeActivity.class);
        } else {
            File file = new File(intent.getStringExtra(CLOTH_IMAGE_PATH));
            file.delete();
            intent = new Intent(SelectTagsActivity.this, PhotoActivity.class);
        }
        startActivity(intent);
    }

    public void onClickSaveCloth(View view) {
        if ((editedCloth.getName().equals("")) && (chosenTags.size() < 3)) {
            showInfoToast("You should name your cloth. \nPlease select at least 3 tags.");
        } else if (editedCloth.getName().equals("")) {
            showInfoToast("You should name your cloth.");
        } else if (chosenTags.size() < 3) {
            showInfoToast("Please select at least 3 tags.");
        } else {
            editedCloth.setTagList(getSelectedTagList());
            Log.d(TAG, editedCloth.toString());
            addClothToDataBase(editedCloth);
            clearSelectedTagList();
            Intent intent = new Intent(SelectTagsActivity.this, WardrobeActivity.class);
            startActivity(intent);
        }
    }

    public List<Tag> getSelectedTagList() {
        for (int j = 0; j < tagList.size(); j++) {
            if (chosenTags.contains(j)) {
                selectedTagList.add(tagList.get(j));
            }
        }
        return selectedTagList;
    }

    public void clearSelectedTagList() {
        selectedTagList.clear();
        chosenTags.clear();
    }

    public void addClothToDataBase(Cloth cloth) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        if (editMode) {
            databaseHelper.updateCloth(cloth);
        } else {
            databaseHelper.createCloth(cloth);
        }
        databaseHelper.closeDataBase();
    }

    private void showInfoToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void onClickRelativeLayout(View view) {
        if (editText.isFocused()) {
            editText.clearFocus();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(SelectTagsActivity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            editText.clearFocus();
            editedCloth.setName(editText.getText().toString());
            textInputLayout.clearFocus();
        }
    }
}