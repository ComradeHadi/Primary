package com.quaap.primary.base;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quaap.primary.R;
import com.quaap.primary.base.component.InputMode;
import com.quaap.primary.base.component.Keyboard;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tom on 12/28/16.
 * <p>
 * Copyright (C) 2016  tom
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
public abstract class StdGameActivity extends SubjectBaseActivity {

    private int mProblemView;

    private Timer timer;
    private TimerTask hinttask;
    //private volatile int hintPos=0;
    private volatile boolean showHint = false;


    public StdGameActivity(int problemView) {
        super(R.layout.std_game_layout);
        mProblemView = problemView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup probarea = (ViewGroup)findViewById(R.id.problem_area);

        ViewGroup item = (ViewGroup) LayoutInflater.from(this).inflate(mProblemView, probarea);

    }

    @Override
    protected void onPause() {

        cancelHint();

        if (timer!=null) {
            timer.cancel();
            timer.purge();
            timer=null;
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation== Configuration.ORIENTATION_LANDSCAPE) {

            LinearLayout answerarea = (LinearLayout)findViewById(R.id.answer_area);
            answerarea.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout centercol = (LinearLayout)findViewById(R.id.centercol);
            centercol.setOrientation(LinearLayout.HORIZONTAL);

            //LinearLayout scores_area = (LinearLayout)findViewById(R.id.scores_area);
            //scores_area.setOrientation(LinearLayout.HORIZONTAL);

            //LinearLayout scores_level = (LinearLayout)findViewById(R.id.scores_level);
            //scores_level.setOrientation(LinearLayout.HORIZONTAL);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar!=null) {
                actionBar.hide();
            }
        }
    }

    @Override
    protected void setStatus(String text) {
        final TextView status = (TextView)findViewById(R.id.txtstatus);
        status.setText(text);

    }


    @Override
    protected void onShowLevel() {


        if (((StdLevel)getLevel()).getInputMode()== InputMode.Buttons) {
            showHint = false;
        } else {
            showHint = true;
        }
        Keyboard.hideKeys(getKeysArea());
    }

    protected ViewGroup getKeysArea() {
        return (ViewGroup) findViewById(R.id.keypad_area);
    }

    protected LinearLayout getAnswerArea() {
        return (LinearLayout)findViewById(R.id.answer_area);
    }

    protected void startHint(int firstHintDelayMillis, int nextHintDelaysMillis) {


        cancelHint();

        if (showHint) {

            hinttask = new TimerTask() {
                @Override
                public void run() {
                    performHint();
                }
            };

            timer.scheduleAtFixedRate(hinttask, firstHintDelayMillis, nextHintDelaysMillis);
        }
    }

    protected void performHint() {
        //override to do something.
    }

    protected void cancelHint() {
        if (hinttask!=null) {
            hinttask.cancel();
            hinttask = null;
        }
    }
}
