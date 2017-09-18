package com.justh.dell.baselibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.justh.dell.baselibrary.ioc.ViewUtils;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置布局layout
        setContentView();

        //特定的处理 子类基本上都需要
        ViewUtils.inject(this);

        //初始化头部
        initTitle();

        //初始化界面
        initView();

        //初始化数据
        initData();

    }

    //设置布局layout
    protected abstract void setContentView();

    //初始化头部
    protected abstract void initTitle();

    //初始化界面
    protected abstract void initView();

    //初始化数据
    protected abstract void initData();

    /**
     * 启动Activity
     * @param clazz
     */
    protected void startActivity(Class<?> clazz){
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
    }
}
