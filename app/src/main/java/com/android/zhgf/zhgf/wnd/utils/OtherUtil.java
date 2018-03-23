package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/4/22.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/*******************************************************************************************************
 * Class Name:WndProject - OtherUtil
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title OtherUtil
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/22 11:53
 * @Description 一个工具类的集合
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class OtherUtil {
    /**
     * @ClassName: OtherUtil
     * @Method: callBeidou static
     * @Params
     * @Description: 打北斗盒子APP，如果他存在的话，不存在就报错
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/22 12:06
     * ${tags}
     */
    public static void callBeidou(Context p_ctx){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.bdbox.new", "com.bdbox.activity.SplashActivity");
        intent.setComponent(cn);
        p_ctx.startActivity(intent);
        //doStartApplicationWithPackageName("com.bdbox.activity");
    }
    /**
     * @ClassName: OtherUtil
     * @Method: doStartApplicationWithPackageName
     * @Params 
     * @Description: 通过包名来查找应用是否存在，如果存在则返回它的详细信息并启动它
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/22 12:07
     * ${tags} 
     */ 
    public void doStartApplicationWithPackageName(Context p_ctx,String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = p_ctx.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = p_ctx.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            p_ctx.startActivity(intent);
        }
    }
}
