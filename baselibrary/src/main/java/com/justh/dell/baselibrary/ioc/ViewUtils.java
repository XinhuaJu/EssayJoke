package com.justh.dell.baselibrary.ioc;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by DELL on 2017/8/19.
 */

public class ViewUtils {

    public static void inject(Activity activity){
        inject(new ViewFinder(activity),activity);
    }

    public static void inject(View view){
        inject(new ViewFinder(view),view);
    }

    public static void inject(View view,Object object){
        inject(new ViewFinder(view),object);
    }

    public static void inject(ViewFinder finder,Object object){
        //注入属性
        injectField(finder,object);
        //注入事件
        injectEvent(finder,object);
    }

    private static void injectField(ViewFinder finder, Object object) {
        //获取所有的属性
        Class<?> clz = object.getClass();
        Field[] fields = clz.getDeclaredFields();
        //获取注解属性的view
        for(Field field : fields){
            ViewById viewById = field.getAnnotation(ViewById.class);
            if(viewById != null){
                //获取注解的ID
                int value = viewById.value();
                //获取到对应ID的View
                View view = finder.findViewById(value);
                if(view != null){
                    field.setAccessible(true);//操纵所有的变量，不管是私有还是公共的
                    try {
                        field.set(object,view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void injectEvent(ViewFinder finder, Object object) {

        //获取class
        Class clz = object.getClass();
        //获取class中的所有方法
        Method[] methods = clz.getDeclaredMethods();

        for(Method method : methods){
            OnClick onClick = method.getAnnotation(OnClick.class);
            if(onClick != null){
                //获取到所有的viewId
                int[] viewIds = onClick.value();
                boolean isCheckNet = (method.getAnnotation(CheckNet.class) != null);
                for(int viewId : viewIds){
                    View view = finder.findViewById(viewId);
                    if(view != null){
                        view.setOnClickListener(new DeclaredOnClickListener(method,object,isCheckNet));
                    }
                }
            }
        }
    }

    private static class DeclaredOnClickListener implements View.OnClickListener {

        private Method mMethod;
        private Object mObject;
        private boolean mIsCheckNet;

        public DeclaredOnClickListener(Method method, Object object,boolean isCheckNet) {
            this.mMethod = method;
            this.mObject = object;
            this.mIsCheckNet = isCheckNet;
        }

        @Override
        public void onClick(View v) {
            if(mIsCheckNet){
                if(!networkAvailable(v.getContext())){
                    Toast.makeText(v.getContext(),"网络不太给力哦!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            try {
                mMethod.setAccessible(true);
                //有参
                mMethod.invoke(mObject,v);
            } catch (Exception e){
                e.printStackTrace();
                try {
                    //无参
                    mMethod.invoke(mObject,null);
                } catch ( Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * 判断当前网络是否可用
     */
    private static boolean networkAvailable(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activityNetWorkInfo = connectivityManager.getActiveNetworkInfo();
            if(activityNetWorkInfo != null && activityNetWorkInfo.isConnected()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
