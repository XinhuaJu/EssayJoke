package com.justh.dell.baselibrary.dialog;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import static android.R.attr.id;

/**
 * Created by DELL on 2017/9/18.
 * Description :dilog view的辅助处理类
 */
class DialogViewHelper {

    private View mContentView = null;
    //用于存放设置文字的view  若mViews中没有，则findViewById  若存在，则直接取出
    private SparseArray<WeakReference<View>> mViews;

    public DialogViewHelper(Context context, int layoutResId) {
        mContentView = LayoutInflater.from(context).inflate(layoutResId,null);
        mViews = new SparseArray<>();
    }

    public DialogViewHelper() {
        mViews = new SparseArray<>();
    }

    /**
     * 设置布局
     * @param contentView
     */
    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    /**
     * 设置文本
     * @param viewId
     * @param text
     */
    public void setText(int viewId, CharSequence text) {
        TextView textView = getView(id);
        if(textView != null){
            textView.setText(text);
        }
    }

    public  <T extends View> T getView(int id) {
        WeakReference<View> referenceView = mViews.get(id);
        View view = null;
        if(referenceView != null){
            view = referenceView.get();
        }
        if(view == null){
            view = mContentView.findViewById(id);
            if(view != null){
                mViews.put(id,new WeakReference<>(view));
            }
        }
        return (T) view;
    }

    /**
     * 设置点击事件
     * @param viewId
     * @param listener
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(id);
        if(view != null){
            view.setOnClickListener(listener);
        }
    }

    public View getContentView() {
        return mContentView;
    }
}
