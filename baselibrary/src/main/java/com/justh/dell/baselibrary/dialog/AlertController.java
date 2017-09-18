package com.justh.dell.baselibrary.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by DELL on 2017/9/18.
 * Description :
 */
class AlertController {

    private AlertDialog mDialog;
    private Window mWindow;

    //创建一个viewHelper对象
    private DialogViewHelper mDialogViewHelper;

    public AlertController(AlertDialog alertDialog, Window window) {
        this.mDialog = alertDialog;
        this.mWindow = window;
    }

    public void setDialogViewHelper(DialogViewHelper dialogViewHelper){
        this.mDialogViewHelper = dialogViewHelper;
    }

    /**
     *
     * 获取dialog
     * @return
     */
    public AlertDialog getDialog() {
        return mDialog;
    }

    /**
     * 获取dailog的window
     * @return
     */
    public Window getWindow() {
        return mWindow;
    }

    /**
     * 设置文本
     * @param viewId
     * @param text
     */
    public void setText(int viewId, CharSequence text) {
        mDialogViewHelper.setText(viewId,text);
    }

    public  <T extends View> T getView(int viewId) {
        return mDialogViewHelper.getView(viewId);
    }

    /**
     * 设置点击事件
     * @param viewId
     * @param listener
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        mDialogViewHelper.setOnClickListener(viewId,listener);
    }

    public static class AlertParams{

        Context mContext;
        int mThemeResId;
        //点击空白是否能够取消
        public boolean mCancelable = true;
        //dialog Cancel监听
        public DialogInterface.OnCancelListener mOnCancelListener;
        //dialog Dismiss监听
        public DialogInterface.OnDismissListener mOnDismissListener;
        //dialog key监听
        public DialogInterface.OnKeyListener mOnKeyListener;
        public View mView;
        public int mLayoutResId;
        //比HashMap更高效
        SparseArray<CharSequence> mTextArray = new SparseArray<>();
        //存放监听事件
        public SparseArray<View.OnClickListener> mClickArray = new SparseArray<>();
        //宽度
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        //动画
        public int mAnimations = 0;
        //位置
        public int mGravity = Gravity.CENTER;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        public AlertParams(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeResId = themeResId;
        }

        /**
         * 绑定和设置参数
         * @param alert
         */
        public void apply(AlertController alert) {
            //设置参数

            //设置布局DialogViewHelper
            DialogViewHelper viewHelper = null;
            if(mLayoutResId != 0){
                viewHelper = new DialogViewHelper(mContext,mLayoutResId);
            }

            if(mView != null){
                viewHelper = new DialogViewHelper();
                viewHelper.setContentView(mView);
            }

            if(viewHelper == null){
                throw new IllegalArgumentException("请设置布局setContentView()");
            }

            alert.setDialogViewHelper(viewHelper);

            alert.getDialog().setContentView(viewHelper.getContentView());

            //设置文本
            int textArraySize = mTextArray.size();
            for(int i = 0;i<textArraySize;i++){
                viewHelper.setText(mTextArray.keyAt(i),mTextArray.valueAt(i));
            }

            //设置点击
            int clickArraySize = mClickArray.size();
            for (int i = 0; i < clickArraySize;i++){
                viewHelper.setOnClickListener(mClickArray.keyAt(i),mClickArray.valueAt(i));
            }

            //设置自定义的效果  如全屏  底部弹出   默认动画
            Window window = alert.getWindow();
            //设置位置
            window.setGravity(mGravity);

            //设置动画
            if(mAnimations != 0){
                window.setWindowAnimations(mAnimations);
            }

            //设置宽高
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = mWidth;
            params.height = mHeight;

            window.setAttributes(params);

        }
    }

}
