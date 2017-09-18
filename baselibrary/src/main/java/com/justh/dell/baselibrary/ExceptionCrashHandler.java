package com.justh.dell.baselibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 2017/9/6.
 * Description :
 */
public class ExceptionCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = ExceptionCrashHandler.class.getSimpleName();
    private static ExceptionCrashHandler mInstance;
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

    //获取该类的实例
    public static ExceptionCrashHandler getInstance(){
        if(mInstance == null){
            //同步锁
            synchronized (ExceptionCrashHandler.class){
                if(mInstance == null){
                    mInstance = new ExceptionCrashHandler();
                }
            }

        }
        return mInstance;
    }

    //私有的构造方法
    private ExceptionCrashHandler(){

    }

    //获取该应用的一些信息
    private Context mContext;
    public void init(Context context){
        this.mContext = context;
        //设置当前类为
        Thread.currentThread().setUncaughtExceptionHandler(this);
        mDefaultExceptionHandler = Thread.currentThread().getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //全局异常
        Log.e(TAG,"程序向你抛出一个异常!");

        //将exception 当前的引用版本号  手机信息等   写入到本地文件中去

        //1:崩溃的详细信息

        //2：应用信息  包名  版本号

        //3：手机信息

        //4：保存当前文件，等应用再次启动再上传(上传文件不在这里处理   耗时)

        String crashFileName = saveInfoToSd(e);

        Log.e(TAG,"fileName------>" + crashFileName);


        //缓存崩溃的日志文件
        cacheCrashFile(crashFileName);

        //让系统默认处理
        mDefaultExceptionHandler.uncaughtException(t,e);
    }

    /**
     * 缓存崩溃日志文件
     *
     * @param fileName
     */
    private void cacheCrashFile(String fileName) {
        SharedPreferences sp = mContext.getSharedPreferences("crash",Context.MODE_PRIVATE);
        sp.edit().putString("CRASH_FILE_NAME",fileName).commit();
    }

    /**
     * 获取崩溃文件名称
     *
     * @return
     */
    public File getCrashFile(){
        String crashFileName = mContext.getSharedPreferences("crash",Context.MODE_PRIVATE)
                .getString("CRASH_FILE_NAME","");
        return new File(crashFileName);
    }


    /**
     * 保存获取的 软件信息，设备信息和出错信息   保存在SDcard中
     * @param e
     * @return
     */
    private String saveInfoToSd(Throwable e) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        //1：手机信息 + 应用信息
        for(Map.Entry<String,String> entry : obtainSimpleInfo(mContext).entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }

        //2：异常信息
        sb.append(obtainExceptionInfo(e));

        if(Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            File dir = new File(mContext.getFilesDir()+File.separator+"crash" +
                    File.separator);

            //先删除之前的异常信息
            if(dir.exists()){
                //删除该目录下的所有子文件
                deleteDir(dir);
            }

            //再重新创建文件夹
            if(!dir.exists()){
                dir.mkdir();
            }


            try {
                fileName = dir.toString()+File.separator+getAssignTime("yyyy_MM_dd_mm")
                        +".txt";
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return fileName;
    }

    private String getAssignTime(String dateFormatStr) {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        long currentTime = System.currentTimeMillis();
        return dateFormat.format(currentTime);
    }

    private boolean deleteDir(File dir) {
        if(dir.isDirectory()){
            File[] children = dir.listFiles();

            //递归删除目录中的子目录
            for (File file: children) {
                file.delete();
            }
        }
        //目录此时为空，可以删除
        return true;
    }

    /**
     * 获取系统未捕捉的错误信息
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    /**
     * 获取一些简单的信息，软件版本，手机版本，型号等信息存放在HashMap中
     * @param context
     * @return
     */
    private HashMap<String,String> obtainSimpleInfo(Context context) {
        HashMap<String,String> map = new HashMap<>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        map.put("versionName",mPackageInfo.versionName);
        map.put("versionCode",""+mPackageInfo.versionCode);
        map.put("MODEL",""+Build.MODEL);
        map.put("SDK_INT",""+Build.VERSION.SDK_INT);
        map.put("PRODUCT",""+Build.PRODUCT);
        map.put("MOBLE_INFO",getMobileInfo());
        return map;
    }

    /**
     * Cell phone information
     * @return
     */
    public static String getMobileInfo(){
        StringBuffer sb = new StringBuffer();
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for(Field field : fields){
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
