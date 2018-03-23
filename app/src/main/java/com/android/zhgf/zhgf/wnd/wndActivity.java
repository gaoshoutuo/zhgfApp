package com.android.zhgf.zhgf.wnd;/**
 * Created by Administrator on 2016/4/21.
 */

/*******************************************************************************************************
 * Class Name:WndProject - wndActivity
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title wndActivity
 * @Package com.jiechu.wnd
 * @date 2016/4/21 15:33
 * @Description 所有Activity的基类
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.android.zhgf.zhgf.wnd.iface.impl.User;

public class wndActivity extends Activity {

    public static final String T=wndActivity.class.getSimpleName();

    long exit_time_flag = 0;
    int exit_time_interval=1000;
    protected User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mUser = new User(this);
    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.i(T, "wndActivity KeyDown!");
        return super.onKeyDown(keyCode, event);
    }
}
