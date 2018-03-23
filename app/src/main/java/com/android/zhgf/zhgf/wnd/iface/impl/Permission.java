package com.android.zhgf.zhgf.wnd.iface.impl;/**
 * Created by Administrator on 2016/4/30.
 */

/*******************************************************************************************************
 * Class Name:WndProject - Permission
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title Permission
 * @Package com.jiechu.wnd.iface
 * @date 2016/4/30 20:21
 * @Description 权限验证
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class Permission {
    /**
    * String PERMISSION_LIST  default 0;
    */
    private static String PERMISSION_LIST  = "000000000";
    /**
    * Permission mInstance  default null;
    */
    private static Permission mInstance  = null;

    private Permission(String p_pm){
        PERMISSION_LIST = p_pm;
    }

    public static Permission getInstance(String p){
        if (mInstance==null){
            mInstance = new Permission(p);
        }
        return mInstance;
    }

    boolean check(int p){
        if (p>PERMISSION_LIST.length())return false;
        String tpInt = PERMISSION_LIST.substring(p,p+1);
        if (Integer.parseInt(tpInt)==1){
            return true;
        }else{
            return false;
        }
    }

}
