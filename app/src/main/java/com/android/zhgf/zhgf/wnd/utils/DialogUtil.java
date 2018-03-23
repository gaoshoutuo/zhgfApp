package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/4/21.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.widget.Toast;

//import com.android.zhgf.zhgf.wnd.FeedbackActivity;
//import io.dcloud.HBuilder.Hello.R;

/*******************************************************************************************************
 * Class Name:WndProject - DialogUtil
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title DialogUtil
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/21 15:35
 * @Description 弹出框工具类
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class DialogUtil {
    private static final String TAG = DialogUtil.class.getSimpleName();
    /**
     * @ClassName: DialogUtil
     * @Method: GlobalAlert static
     * @Params
     * @Description: 全局Alert
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/21 17:26
     * ${tags}
     */
    @SuppressWarnings("Display")
    public static void showGlobalAlert(Context c,String strTitle,String strMessage,int intIcon,String[] btnText,DialogInterface.OnClickListener listener) {
        Builder mBuilder=null;
        if (mBuilder == null) {
            mBuilder = new Builder(c.getApplicationContext());
            mBuilder.setTitle(strTitle).setMessage(strMessage);
            mBuilder.setIcon(intIcon);
            Log.e(TAG, "showGlobalAlert: " + btnText.length);
            try {
                if (btnText[0] != "") {
                    mBuilder.setPositiveButton(btnText[0], listener);
                }
                if (btnText[1] != "") {
                    mBuilder.setNegativeButton(btnText[1], listener);
                }
                if (btnText[2] != "") {
                    mBuilder.setNeutralButton(btnText[2], listener);
                }
            }catch (Exception e){
                //TODO 在这里使用了一个异常来屏蔽掉因为BtnText数组越界的问题，对于整体程序的影响还没有看到
                Log.e(TAG,"DlalogUtil->showGlobalAlert:在这里使用了一个异常来屏蔽掉因为BtnText数组越界的问题，对于整体程序的影响还没有看到");
            }



            AlertDialog dialog = mBuilder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }
    }

    public static void Toast(Context p_ctx,String msg){
        Toast.makeText(p_ctx,msg,Toast.LENGTH_SHORT).show();
    }

   /* public static void showProgress(FeedbackActivity p_ctx,int prect){
        LayoutInflater mInflater = p_ctx.getLayoutInflater();
        View mView = mInflater.inflate(R.layout.fullscreen_progress,null);

    }*/
}
