package com.xuwt.baidu.xuwtframework;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuwt.baidu.activity.BaseActivity;
import com.xuwt.baidu.annotation.ViewInject;


public class MainActivity extends BaseActivity {

    @ViewInject(id=R.id.textview) TextView mTextView;
    @ViewInject(id=R.id.button , click="btnClick") Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View v){
        mTextView.setText("text set from button");
    }

}
