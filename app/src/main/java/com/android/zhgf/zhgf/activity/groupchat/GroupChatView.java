package com.android.zhgf.zhgf.activity.groupchat;

import com.android.zhgf.zhgf.base.BaseView;
import com.android.zhgf.zhgf.database.bean.Chat;

import java.util.List;

//import com.android.zhgf.zhgf.datasource.model.Chat;

/**
 * @author mrtan
 * @version v1.0
 * date 10/6/16
 */

interface GroupChatView extends BaseView {
    void loadDate(List<Chat> chats);

    void setTitle(String title);

    void scrollToBottom();

    void addData(Chat chat);

    void addData(List<Chat> chats);

    void finish();

    void sendError();

    void sendSuccess();

    void cancelRefresh();

    void noMoreChat();
}
