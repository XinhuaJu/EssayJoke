package com.justh.dell.essayjoke.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.justh.dell.baselibrary.ioc.OnClick;
import com.justh.dell.baselibrary.ioc.ViewById;
import com.justh.dell.essayjoke.R;
import com.justh.dell.framelibrary.BaseSkinActivity;

public class MainActivity extends BaseSkinActivity {

    @ViewById(R.id.sample_text)
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void initTitle() {
        //初始化头部
    }

    @Override
    protected void initView() {
        //初始化布局
    }

    @Override
    protected void initData() {
        //初始化数据
    }

    @OnClick({R.id.sample_text})
    void Click(View view){

    }


}
