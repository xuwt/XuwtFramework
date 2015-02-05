package com.xuwt.baidu.framework;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuwt.baidu.framework.activity.BaseActivity;
import com.xuwt.baidu.framework.annotation.ViewInject;


public class MainActivity extends BaseActivity {

    @ViewInject(id=R.id.textview) TextView mTextView;
    @ViewInject(id=R.id.button) Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("text set from button");
            }
        });
    }


}
