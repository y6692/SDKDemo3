package com.centaurstech.sdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * @author Leon(黄长亮)
 * @describe EditText
 * @date 2019/4/16
 */
@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private ArrayList<TextWatcher> mListeners = null;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (mListeners == null){
            mListeners = new ArrayList<>();
        }
        mListeners.add(watcher);
        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        if (mListeners != null){
            int i = mListeners.indexOf(watcher);
            if (i >= 0){
                mListeners.remove(i);
            }
        }
        super.removeTextChangedListener(watcher);
    }

    public void clearTextChangedListener(){
        if(mListeners != null){
            for(TextWatcher watcher : mListeners){
                super.removeTextChangedListener(watcher);
            }
            mListeners.clear();
            mListeners = null;
        }
    }

}
