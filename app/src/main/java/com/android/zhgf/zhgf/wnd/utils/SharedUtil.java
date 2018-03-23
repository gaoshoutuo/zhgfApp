package com.android.zhgf.zhgf.wnd.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

/*******************************************************************************************************
 * Class Name:WndProject - SharedUtil
 *
 * @author hnyashiro 5303363@qq.com
 * @package com.jiechu.wnd.utils
 * @project WndProject
 * @Title SharedUtil
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/20 9:30
 * @Description 一个Android移动端的缓存操作的工具类
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class SharedUtil {
    private static final String TAG = SharedUtil.class.getSimpleName();
    private Context mContext;
    private String CACHE_KEY_NAME="DEFAULT_CACHE";
    private SharedPreferences mShared;
    private SharedPreferences.Editor mEditor;

    /**
     * @ClassName: SharedUtil
     * @Method: SharedUtil
     * @Params: Context pCtx
     * @Description: SharedUtil构造
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/20 9:35
     */
    public SharedUtil(Context pCtx,String pCacheName) throws InvaildSharedException {
        mContext = pCtx;
        if (mContext!=null){
            mShared = mContext.getSharedPreferences(pCacheName!=""?pCacheName:CACHE_KEY_NAME, Activity.MODE_PRIVATE);
            mEditor = mShared.edit();
        }else{
            Log.e(TAG, "SharedUtil: 获取设备缓存对象失败！");
            throw new InvaildSharedException();
        }
    }

    /*String setter/getter*/
    public void setString(String key,String val){
        mEditor.putString(key,val);
        mEditor.commit();
    }

    public void setBoolean(String key,boolean val){
        mEditor.putBoolean(key, val);
    }

    public void setInt(String key,int val){
        mEditor.putInt(key, val);
    }

    public void setFloat(String key,float val){
        mEditor.putFloat(key, val);
    }

    public void setLong(String key,long val){
        mEditor.putLong(key, val);
    }

    public void setStringSet(String key,Set<String> val){
        mEditor.putStringSet(key,val);
    }


    public String getString(String key) throws NotFoundDataByKeyException {
        try {
            return mShared.getString(key, "");
        }catch (Exception e){
            throw new NotFoundDataByKeyException();
        }
    }
    /*int setter/getter*/
    public void putInt(String key,int val){
        mEditor.putInt(key,val);
        mEditor.commit();
    }
    public int getInt(String key) throws NotFoundDataByKeyException {
        try {
            return mShared.getInt(key,0);
        }catch (Exception e){
            throw new NotFoundDataByKeyException();
        }
    }
    /*Boolean setter/getter*/
    public void putBoolean(String key,boolean val){
        mEditor.putBoolean(key,val);
        mEditor.commit();
    }
    public boolean getBoolean(String key) throws NotFoundDataByKeyException {
        try {
            return mShared.getBoolean(key, false);
        }catch (Exception e){
            throw new NotFoundDataByKeyException();
        }
    }

    /*Long setter/getter*/
    public void putLong(String key,long val){
        mEditor.putLong(key,val);
        mEditor.commit();
    }
    public long getLong(String key) throws NotFoundDataByKeyException {
        try {
            return mShared.getLong(key, 0);
        }catch (Exception e){
            throw new NotFoundDataByKeyException();
        }
    }

    /*Float setter/getter*/
    public void putFloat(String key,float val){
        mEditor.putFloat(key,val);
        mEditor.commit();
    }
    public float getFloat(String key) throws NotFoundDataByKeyException {
        try {
            return mShared.getLong(key, 0);
        }catch (Exception e){
            throw new NotFoundDataByKeyException();
        }
    }


    @Override
    protected void finalize() throws Throwable {
        mContext=null;
        mShared=null;
        mEditor=null;
    }

    public class InvaildSharedException extends  Exception{
        @Override
        public String getMessage() {
            return "获取设备共享对象失败！请检查1.共享名称是否正确;2.Context参数是否正确";
        }
    }

    public class NotFoundDataByKeyException extends Exception{
        @Override
        public String getMessage() {
            return "获取设备共享数据失败！1.共享名称是否正确;2.请检查Key是否正确!";
        }
    }
}
