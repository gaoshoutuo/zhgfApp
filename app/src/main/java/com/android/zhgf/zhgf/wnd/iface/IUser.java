package com.android.zhgf.zhgf.wnd.iface;

import android.content.Intent;

import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;

/*******************************************************************************************************
 * Class Name:WndProject - AdminUser
 *
 *******************************************************************************************************/
public interface IUser {

    void Login(String pPhoneNumber, String pPassword, boolean puser_forget, boolean ppass_forget, INetwork.OnNetworkStatusListener listener);//用户登录操作
    void Logout(NetworkUtil.OnNetworkStatusListener listener);//用户登出

    /**
     * @param pIntent String
     * @Description 检查用户是否已经登录,如果没有登录，则跳转到登录页，而已经登录用户，跳转到参数指定的页
     */
    void checkLogin(Intent pIntent);

    /**
     * @param pUserDataName
     * @Author hnyashiro 5303363@qq.com
     * @date 2016/4/15 20:27
     * @Description 取用户缓存数据,如果缓存数据不存在，说明为非法用户，则自动跳转到登录页并给出提示
     * @return
     */
    Object getUserCacheDataCheckAndToLogin(String pUserDataName);

    void CheckPermission(int intPermission);

    /**
     * 定义接口事件，当用户在相应的环节中操作时，通过回调实现相应的功能
     * */
    public interface UserEventListener{
        void onLogined(boolean isSuccess, Object pData);
        void onLogouted(boolean isSuccess, Object pData);
        void onPermission(boolean isPermission);
    }
}
