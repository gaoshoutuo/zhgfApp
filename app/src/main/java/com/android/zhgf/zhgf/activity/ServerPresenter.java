package com.android.zhgf.zhgf.activity;

import com.android.zhgf.zhgf.base.BasePresenter;
import com.android.zhgf.zhgf.utils.CommonField;
import com.android.zhgf.zhgf.utils.PreferencesUtils;
import com.android.zhgf.zhgf.utils.ThreadUtil;
import com.android.zhgf.zhgf.utils.XMPPUtil;

import timber.log.Timber;

public class ServerPresenter extends BasePresenter<ServerView> {

    void Connect(final String name, final String address, final int port) {
        ThreadUtil.runONWorkThread(() -> {
            try {
                XMPPUtil.connect(name, address, port, null);
                saveConfig(name, address, port);
                ThreadUtil.runOnUIThread(() ->

                        mView.connectSuccess());
            } catch (Exception e) {
                Timber.e(e);
                ThreadUtil.runOnUIThread(() -> {
                    mView.connectFail();
                });

            }
        });
    }

    public void Connect() {
        ThreadUtil.runONWorkThread(() -> {
            try {
                XMPPUtil.connect(null);
                ThreadUtil.runOnUIThread(() -> mView.connectSuccess());
            } catch (Exception e) {
                Timber.e(e);
                ThreadUtil.runOnUIThread(() -> mView.connectFail());
            }
        });
    }

    private void saveConfig(String name, String address, int port) {
        PreferencesUtils.getEditor()
                .putString(CommonField.SERVER_NAME, name)
                .putString(CommonField.SERVER_ADDRESS, address)
                .putInt(CommonField.SERVER_PORT, port)
                .apply();
    }

}
