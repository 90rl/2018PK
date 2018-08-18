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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.model.Meeting;
import com.amazingteam.competenceproject.model.Tag;
import com.amazingteam.competenceproject.ui.EditTextNewEvents;
import com.amazingteam.competenceproject.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MeetingActivity extends AppCompatActivity {

    public static final String TAG = MeetingActivity.class.getSimpleName();

    List<Tag> tagList = new ArrayList<>();
    String[] tagNameList;
    ArrayList<Integer> chosenTags = new ArrayList<>();
    List<Tag> selectedTagList = new ArrayList<>();
    boolean[] selectedTags;

    Meeting meeting = new Meeting();
    ArrayList<String> emptyList = new ArrayList<>();
    Boolean weatherDependant = true;

    Button btnAssignTags;
    TextView tvMeetingName;
    TextView tvTagList;
    EditTextNewEvents editText;
    TextInputLayout textInputLayout;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

        tvMeetingName = (TextView) findViewById(R.id.tvMeetingName);
        btnAssignTags = (Button) findViewById(R.id.btnAssignTags);
        tvTagList = (TextView) findViewById(R.id.tvTagList);
        editText = (EditTextNewEvents) findViewById(R.id.inputText);


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
                    tvMeetingName.setText(editText.getText().toString());
                    meeting.setName(editText.getText().toString());
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
                tvMeetingName.setText(editText.getText().toString());
            }
        });

        textInputLayout = (TextInputLayout) findViewById(R.id.meetingNameWrapper);
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

        btnAssignTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingActivity.this);
                mBuilder.setTitle("Select tags for meeting");
                mBuilder.setMultiChoiceItems(tagNameList, selectedTags, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!chosenTags.contains(position)) {
                                chosenTags.add(position);
                            }
                            }else if (chosenTags.contains(position)) {
                                chosenTags.remove((Integer) position);
                        }
                    }

                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String tag = "";
                        for (int i = 0; i < chosenTags.size(); i++) {
                            tag = tag + tagNameList[chosenTags.get(i)];
                            if (i != chosenTags.size() - 1) {
                                tag = tag + ", ";
                            }
                        }
                        tvTagList.setText(tag);
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        for (int i = 0; i < selectedTags.length; i++) {
                            selectedTags[i] = false;
                            chosenTags.clear();
                            tvTagList.setText("none");
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    public void onClickCancel(View view) {
        onBackPressed();
    }

    public void onClickRelativeLayoutMeeting(View view) {
        if (editText.isFocused()) {
            editText.clearFocus();
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

    public void onClickSaveMeeting(View view) {
        if ((meeting.getName().equals("")) && (chosenTags.size() < 3)) {
            showInfoToast("You should name your cloth. \nPlease select at least 3 tags.");
        } else if (meeting.getName().equals("")) {
            showInfoToast("You should name your cloth.");
        } else if (chosenTags.size() < 3) {
            showInfoToast("Please select at least 3 tags.");
        } else {
            meeting.setTagList(getSelectedTagList());
            meeting.setWeatherDependant(weatherDependant);
            meeting.setClothesSet(emptyList);
            Log.d(TAG, meeting.getName() + meeting.getTagList());
            addMeetingToDataBase(meeting);
            clearSelectedTagList();
            Intent intent = new Intent(MeetingActivity.this, StylizationActivity.class);
            startActivity(intent);
        }
    }

    public void addMeetingToDataBase(Meeting meeting) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        databaseHelper.createMeeting(meeting);
        databaseHelper.closeDataBase();
    }

    private void showInfoToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(MeetingActivity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            editText.clearFocus();
            meeting.setName(editText.getText().toString());
            textInputLayout.clearFocus();
        }
    }



}
