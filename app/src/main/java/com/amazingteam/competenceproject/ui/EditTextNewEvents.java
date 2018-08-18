package com.amazingteam.competenceproject.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class EditTextNewEvents extends android.support.v7.widget.AppCompatEditText {

    Context context;
    public boolean isBackClicked = false;

    public EditTextNewEvents(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            InputMethodManager mgr = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert mgr != null;
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            changeBackClickStatus();
            this.clearFocus();
            return true;
        }
        return false;
    }

    public void changeBackClickStatus() {
        isBackClicked = !isBackClicked;
    }
}
