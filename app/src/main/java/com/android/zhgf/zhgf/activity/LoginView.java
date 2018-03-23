package com.android.zhgf.zhgf.activity;

import com.android.zhgf.zhgf.base.BaseView;

/**
 * Created by mrtan on 9/28/16.
 */

public interface LoginView extends BaseView {
    void loginSuccess();
    void loginFail();
    void connect();
}
