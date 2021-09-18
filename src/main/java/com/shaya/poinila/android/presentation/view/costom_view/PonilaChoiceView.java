package com.shaya.poinila.android.presentation.view.costom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by iran on 8/15/2016.
 */
public class PonilaChoiceView extends LinearLayout implements View.OnClickListener {


    public static final int
            FIRST_OPTION = 0,
            SECOND_OPTION = 1,
            THIRD_OPTION = 2;

    private int optionSelected = FIRST_OPTION;

    private OnOptionSelected onOptionSelected;

    @Bind(R.id.first_option)
    TextView firstOption;

    @Bind(R.id.second_option)
    TextView secondOption;

    @Bind(R.id.third_option)
    TextView thirdOption;

    public PonilaChoiceView(Context context) {
        super(context);
        init();
    }

    public PonilaChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PonilaChoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_ponila_choice, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

        optionSelected = FIRST_OPTION;
        firstOption.setSelected(true);
        secondOption.setSelected(false);
        thirdOption.setSelected(false);

        firstOption.setOnClickListener(this);
        secondOption.setOnClickListener(this);
        thirdOption.setOnClickListener(this);
    }

    public void setOptionsText(String firstOptionText, String secondOptionText, String thirdOptionText){
        firstOption.setText(firstOptionText);
        secondOption.setText(secondOptionText);
        thirdOption.setText(thirdOptionText);
    }

    public void setOptionsText(int firstOptionText, int secondOptionText, int thirdOptionText){
        firstOption.setText(firstOptionText);
        secondOption.setText(secondOptionText);
        thirdOption.setText(thirdOptionText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.first_option:
                optionSelected = FIRST_OPTION;
                firstOption.setSelected(true);
                secondOption.setSelected(false);
                thirdOption.setSelected(false);
                if(onOptionSelected != null)
                    onOptionSelected.onFirstOption();
                break;
            case R.id.second_option:
                optionSelected = SECOND_OPTION;
                firstOption.setSelected(false);
                secondOption.setSelected(true);
                thirdOption.setSelected(false);
                if(onOptionSelected != null)
                    onOptionSelected.onSecondOption();
                break;
            case R.id.third_option:
                optionSelected = THIRD_OPTION;
                firstOption.setSelected(false);
                secondOption.setSelected(false);
                thirdOption.setSelected(true);
                if(onOptionSelected != null)
                    onOptionSelected.onThirdOption();
                break;
        }
    }

    public PonilaChoiceView setOnOptionSelected(OnOptionSelected onOptionSelected) {
        this.onOptionSelected = onOptionSelected;
        return this;
    }

    public void setDefaultSelected(int option){
        switch (option){
            case FIRST_OPTION:
                optionSelected = FIRST_OPTION;
                firstOption.setSelected(true);
                secondOption.setSelected(false);
                thirdOption.setSelected(false);
                break;
            case SECOND_OPTION:
                optionSelected = SECOND_OPTION;
                firstOption.setSelected(false);
                secondOption.setSelected(true);
                thirdOption.setSelected(false);
                break;
            case THIRD_OPTION:
                optionSelected = THIRD_OPTION;
                firstOption.setSelected(false);
                secondOption.setSelected(false);
                thirdOption.setSelected(true);
                break;
        }
    }

    public interface OnOptionSelected{
        void onFirstOption();
        void onSecondOption();
        void onThirdOption();
    }

    public int getOptionSelected(){
        return optionSelected;
    }
}
