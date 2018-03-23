package com.android.zhgf.zhgf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.zhgf.zhgf.service.XMPPService;
import com.android.zhgf.zhgf.utils.Common;
import com.android.zhgf.zhgf.utils.ThreadUtil;
import com.android.zhgf.zhgf.utils.XMPPUtil;

import timber.log.Timber;

/**
 * Created by mrtan on 10/2/16.
 */

public final class InternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //连接服务器，唤醒服务
        if (Common.isNetworkAvailable(context) && !XMPPUtil.isConnected()) {
            Timber.i("internet is connected");
            ThreadUtil.runONWorkThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        XMPPUtil.closeConnected();
                        //if (XMPPUtil.connect(null)) {
                        XMPPUtil.getXMPPConnection(null);
                            XMPPUtil.login();
                            context.startService(new Intent(context, XMPPService.class));
                        //}
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            });
        }
    }
}
