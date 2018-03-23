package com.android.zhgf.zhgf.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.zhgf.zhgf.adapter.ListUserAdapter;
import com.android.zhgf.zhgf.core.ICommand;
import com.android.zhgf.zhgf.core.RuningTime;
import com.android.zhgf.zhgf.core.SoundPlayer;
import com.android.zhgf.zhgf.core.WSClient;
import com.android.zhgf.zhgf.core.cmd.InvisitCommand;
import com.android.zhgf.zhgf.core.cmd.LoginCommand;
import com.android.zhgf.zhgf.core.cmd.RefreshOnlineUserCommand;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.wnd.iface.impl.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends MPermissionActivity {
    public static String TAG = MainActivity.class.getSimpleName();
    ListView lsOnlinesBox = null;
    ListUserAdapter mUsersAdapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(RuningTime.mPermissions,0x001);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void permissionSuccess(int requestCode) {
        switch (requestCode){
            case 0x001:
                Init();
                TestActivity();
                break;
            default:
                break;
        }
    }


    void Init(){
        lsOnlinesBox = (ListView) findViewById(R.id.lsOnlineusers);
        lsOnlinesBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WSClient.OnlineUser tUser = RuningTime.getWSClient().getOnlineUsers().get(position);
                Intent mit = new Intent(MainActivity.this,VideoCaller.class);
                mit.putExtra("ClientB",tUser);
                startActivity(mit);
            }
        });




        //用户登录的操作
        RuningTime.getWSClient().addCommand("LOGIN",new LoginCommand());
        ((LoginCommand)RuningTime.getWSClient().getCommand("LOGIN")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                /*Intent mit = new Intent(MainActivity.this,VideoCaller.class);
                startActivity(mit);*/
            }
        });

        //这里是按收到ClientA的邀请后触发
        RuningTime.getWSClient().addCommand("INVISITVIDEO",new InvisitCommand());
        ((InvisitCommand)RuningTime.getWSClient().getCommand("INVISITVIDEO")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                Log.e(TAG, "MainActivity:::onEmitter:: 同意视频");

                try{
                    WSClient.OnlineUser tUser = WSClient.getInstance().getUser(payload.getString("socket_id"));
                    Intent mit = new Intent(MainActivity.this,VideoCallee.class);
                    mit.putExtra("ClientB",tUser);
                    startActivity(mit);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });


        //自动刷新在线好友
        RuningTime.getWSClient().addCommand("REFRESHONLINEUSERS",new RefreshOnlineUserCommand());
        ((RefreshOnlineUserCommand)RuningTime.getWSClient().getCommand("REFRESHONLINEUSERS")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                final LinkedList<WSClient.OnlineUser> mUserList = new LinkedList<>();
                Iterator keys = payload.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();

                    try {
                        JSONObject item = payload.getJSONObject(key);
                        String tpUsername = RuningTime.getWSClient().getUsername();
                        if (!key.equals(tpUsername)) {
                            WSClient.OnlineUser user = new WSClient.OnlineUser();
                            user.setUsername(item.getString("username"));
                            user.setSocketID(item.getString("socket_id"));
                            user.setRoomname(item.getString("room_name"));
                            mUserList.add(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                RuningTime.getWSClient().setOnlineUsers(mUserList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (MainActivity.this!=null){
                            if (mUsersAdapter==null){
                                mUsersAdapter = new ListUserAdapter(getApplicationContext(),RuningTime.getWSClient().getOnlineUsers());
                            }else{
                                mUsersAdapter.setData(RuningTime.getWSClient().getOnlineUsers());
                            }
                            lsOnlinesBox.setAdapter(mUsersAdapter);
                        }
                    }
                });
            }
        });





       /* //测试与信令服务器的交互
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请输入你的称呼").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(inputServer.getText().toString()!=""){
                        JSONObject msg = new JSONObject();
                        msg.put("username",inputServer.getText());
                        msg.put("userpwd","1");
                        msg.put("room_name","live");
                        ((LoginCommand) RuningTime.getWSClient().getCommand("LOGIN")).SendMessage("LOGIN",msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();*/
        User mUser=new User(getApplicationContext());
        try {
            JSONObject msg = new JSONObject();
            msg.put("username",(String)mUser.getCache(User.CACHE_KEY_USER_NAME));
            msg.put("userpwd","1");
            msg.put("room_name","live");
            ((LoginCommand) RuningTime.getWSClient().getCommand("LOGIN")).SendMessage("LOGIN",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SoundPlayer mPlay = SoundPlayer.getmInstance(this);
        int StreamID = mPlay.play(0);

    }

    void TestActivity(){

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
