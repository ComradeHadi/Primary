package com.quaap.primary.math;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quaap.primary.Levels;
import com.quaap.primary.R;
import com.quaap.primary.base.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Math1Activity extends BaseActivity {

    private int num1;
    private int num2;
    private MathOp op;
    private int answer;

    public static final String LevelSetName = "Math1Levels";



    public Math1Activity() {
        levels = Levels.getLevels(LevelSetName);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subject = getString(R.string.subject_math1);
        int layoutId=R.layout.activity_math1;

        OnCreateCommon(layoutId);

        showProb();

    }



    private List<Button> answerbuttons = new ArrayList<>();

    protected void showProb() {

        TextView num1txt = (TextView)findViewById(R.id.num1);
        TextView num2txt = (TextView)findViewById(R.id.num2);
        TextView optxt = (TextView)findViewById(R.id.op);

        makeRandomProblem();

        num1txt.setText(Integer.toString(num1));
        num2txt.setText(Integer.toString(num2));
        optxt.setText(op.toString());
        answer = getAnswer(num1, num2, op);

        LinearLayout answerarea = (LinearLayout)findViewById(R.id.answer_area);
        answerarea.removeAllViews();
        answerbuttons.clear();

        int numans = 4;
        List<Integer> answers = getAnswerChoices(numans);

        float fontsize = num1txt.getTextSize();
        for (int i=0; i<answers.size(); i++) {
            int tmpans = answers.get(i);
            makeAnswerButton(tmpans, answerarea, fontsize);
        }
        starttime = System.currentTimeMillis();

    }

    private void answerGiven(int ans) {
        long timespent = System.currentTimeMillis() - starttime;

        for (Button ab: answerbuttons) {
            ab.setEnabled(false);
        }
        boolean isright = ans == answer;

        final TextView status = (TextView)findViewById(R.id.txtstatus);
        if (isright) {
            correct++;
            correctInARow++;
            totalCorrect++;
            tscore += (Math.abs(num1)+Math.abs(num2)) * (op.ordinal()+1) * ((correctInARow+1)/2);

            if (actwriter !=null) {
                actwriter.log(levelnum+1, num1 + op.toString() + num2, answer+"", ans+"", isright, timespent, getCurrentPercentFloat());
            }

            if (correct>=levels[levelnum].getRounds()) {
                status.setText("Correct!");
                correct = 0;
                incorrect = 0;
                if (levelnum+1>=levels.length) {
                    status.setText("You've completed all the levels!");
                    return;
                } else {
                    if (highestLevelnum<levelnum+1) {
                        highestLevelnum = levelnum+1;
                    }
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Level complete!")
                            .setMessage("Go to the next level?")
                            .setPositiveButton("Next level", new DialogInterface.OnClickListener()  {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    levelnum++;
                                    showProb();
                                    setLevelFields();
                                }

                            })
                            .setNegativeButton("Repeat this level", new DialogInterface.OnClickListener()  {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    correct = 0;
                                    incorrect = 0;
                                    showProb();
                                    setLevelFields();
                                }

                            })
                            .show();
                    //status.setText("Correct! On to " + levelnum);
                }
            } else {
                status.setText("Correct!");
            }
            final int corrects = correct;
            final int incorrects = incorrect;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (corrects == correct && incorrects == incorrect) {
                        status.setText(" ");
                    }
                }
            }, status.getText().length() * 300);
            showProb();
        } else {
            incorrect++;
            correctInARow = 0;
            totalIncorrect++;
            status.setText("Try again!");
            if (actwriter !=null) {
                actwriter.log(levelnum+1, num1 + op.toString() + num2, answer+"", ans+"", isright, timespent, getCurrentPercentFloat());
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (Button ab: answerbuttons) {
                        ab.setEnabled(true);
                    }
                    status.setText(" ");
                }
            }, 1500);
        }
        setLevelFields();
    }

    private void makeRandomProblem() {
        int max = ((Math1Level)levels[levelnum]).getMaxNum();
        if (correct>levels[levelnum].getRounds()/2) {
            num1 = getRand(max / 2, max);
        } else {
            num1 = getRand(max);
        }
        num2 = getRand(max);
        if (num2==0 && Math.random()>.3) num2 = getRand(1, max);
        if (num2==1 && Math.random()>.3) num2 = getRand(2, max);

        op = MathOp.random(((Math1Level)levels[levelnum]).getMinMathOp(), ((Math1Level)levels[levelnum]).getMaxMathOp());

        if (op == MathOp.Minus || op == MathOp.Divide) {
            if (num1<num2) {
                int tmp = num1;
                num1 = num2;
                num2 = tmp;
            }
            if (op == MathOp.Divide) {
                if (num1%num2 != 0) {
                    num1 = num1*num2;
                }
            }
        }
    }

    @NonNull
    private List<Integer> getAnswerChoices(int numans) {
        List<Integer> answers = new ArrayList<>();
        answers.add(answer);
        for (int i=1; i<numans; i++) {
            int tmpans;
            do {
                tmpans = answer + getRand(-Math.min(answer*2/3, 7), 6);
            } while (answers.contains(tmpans));
            answers.add(tmpans);
        }
        Collections.shuffle(answers);
        return answers;
    }

    private void makeAnswerButton(int tmpans, LinearLayout answerarea, float fontsize) {
        Button ansbutt = new Button(this);
        ansbutt.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontsize);
        ansbutt.setText(tmpans+"");
        ansbutt.setTag(tmpans);
        ansbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerGiven((int)view.getTag());
            }
        });
        ansbutt.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.RIGHT;
        ansbutt.setLayoutParams(lparams);
        answerarea.addView(ansbutt);
        answerbuttons.add(ansbutt);
    }



    private int getAnswer(int n1, int n2, MathOp op) {
        switch (op) {
            case Plus:
                return n1 + n2;
            case Minus:
                return n1 - n2;
            case Times:
                return n1 * n2;
            case Divide:
                return n1 / n2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }


}
