package com.dorid.android.rememberit;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Created by Dori on 8/17/13.
 */
public class SwitchActivity extends Activity implements View.OnClickListener, ViewSwitcher.ViewFactory {

    private TextSwitcher mSwitcher;
    private int counter = 0;
    private String[] words = new String[]{"one","two","three"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_layout);

        mSwitcher = (TextSwitcher) findViewById(R.id.textswitcher);
        mSwitcher.setFactory(this);

        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);

        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);

        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(this);

        updateCounter();
    }

    public void onClick(View v) {
        counter++;
        updateCounter();
    }

    private void updateCounter() {
        int index = counter % words.length;
        mSwitcher.setText(String.valueOf(words[index]));
    }

    public View makeView() {
        TextView t = new TextView(this);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(36);
        return t;
    }
}