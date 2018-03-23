package com.android.zhgf.zhgf.activity;

import com.android.zhgf.zhgf.base.BasePresenter;
import com.android.zhgf.zhgf.utils.ThreadUtil;
import com.android.zhgf.zhgf.utils.XMPPUtil;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import timber.log.Timber;

/**
 * 登陆的业务类
 */

final class LoginPresenter extends BasePresenter<LoginView> {

    private String mUsername;
    private String mPassword;

    void Login(final String username, final String password) {
        ThreadUtil.runONWorkThread(() -> {
                    //如果未登陆服务器，首先登陆服务器
                    if (!XMPPUtil.isConnected()) {
                        mView.connect();
                        //return;
                    }
                    try {
                        Timber.i("start login");
                        XMPPUtil.addConnectionListener(mConnectionListener);
                        XMPPUtil.login(username, password);
                        mUsername = username;
                        mPassword = password;
                    } catch (Exception e) {
                        Timber.e(e);
                        ThreadUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mView.loginFail();
                            }
                        });
                    }
                }

        );
    }

    @Override
    public void onAttach(LoginView view) {
        super.onAttach(view);
        if (XMPPUtil.isLogin()) {
            mView.loginSuccess();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        XMPPUtil.removeConnectionListener(mConnectionListener);
        mConnectionListener = null;
    }

    private ConnectionListener mConnectionListener = new XMPPUtil.ConnectionListenerWrapper() {
        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            ThreadUtil.runOnUIThread(() -> {
                mView.loginSuccess();
            });
        }
    };

}
