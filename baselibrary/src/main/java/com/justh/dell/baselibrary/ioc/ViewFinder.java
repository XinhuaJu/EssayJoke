package com.justh.dell.baselibrary.ioc;

import android.app.Activity;
import android.view.View;

/**
 * Created by DELL on 2017/8/19.
 */

public class ViewFinder {

    private Activity mActivity;
    private View mView;

    public ViewFinder(Activity activity){
        this.mActivity = activity;
    }

    public ViewFinder(View view){
        this.mView = view;
    }

    public View findViewById(int viewId){
        return mActivity == null?mView.findViewById(viewId):mActivity.findViewById(viewId);
    }


}
