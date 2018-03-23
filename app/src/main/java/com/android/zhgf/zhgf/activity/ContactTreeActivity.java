package com.android.zhgf.zhgf.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.groupchat.GroupListActivity;
import com.android.zhgf.zhgf.adapter.ListUserAdapter;
import com.android.zhgf.zhgf.adapter.SimpleTreeListViewAdapter;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.Contact;
import com.android.zhgf.zhgf.bean.OrgBean;
import com.android.zhgf.zhgf.core.ICommand;
import com.android.zhgf.zhgf.core.RuningTime;
import com.android.zhgf.zhgf.core.WSClient;
import com.android.zhgf.zhgf.core.cmd.InvisitCommand;
import com.android.zhgf.zhgf.core.cmd.RefreshOnlineUserCommand;
import com.android.zhgf.zhgf.database.ContactColumns;
import com.android.zhgf.zhgf.util.Utils;
import com.android.zhgf.zhgf.utils.Node;
import com.android.zhgf.zhgf.utils.adapter.TreeListViewAdapter;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.utils.DialogUtil;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContactTreeActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener,
        SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemScrollBackListener, SlideAndDragListView.OnItemDeleteListener,OnClickListener{

    private static final String TAG = ContactActivity.class.getSimpleName();

    private Menu mMenu;
    private List<com.android.zhgf.zhgf.database.bean.Contact> lstContact;
    private SlideAndDragListView mListView;
    private Toast mToast;

    private ListView mTree;
    private DrawerLayout mDrawerLayout;
    private SimpleTreeListViewAdapter<OrgBean> treeAdapter;
    private List<OrgBean> mDatas2;
    private MaterialSearchView searchView;

    private TextView titleTv;
    private String[] suggestions;
    private List<com.android.zhgf.zhgf.database.bean.Contact> contactList = new ArrayList<com.android.zhgf.zhgf.database.bean.Contact>();

    public ContactTreeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().show();
        setContentView(R.layout.activity_contact_tree);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        titleTv = (TextView) findViewById(R.id.toolbar_title);
        titleTv.setText("通讯录");
        /*// 返回图标
        setBack();
        // 右图标
        setRight(R.drawable.icons8_people48);
        setTitle("通讯录");*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mTree = (ListView) findViewById(R.id.id_lv_left_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        //ab.setDisplayShowTitleEnabled(true);
        //设置title.
        //ab.setTitle("通讯录");
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        //设置Margin边距.
        //toolbar.setTitleMargin();
        //设置字体.
        //toolbar.setTitleTextAppearance();
        //设置字的颜色
        //toolbar.setTitleTextColor(0);
        if (ab != null){
            ab.setDisplayShowTitleEnabled(false);
            ab.setHomeAsUpIndicator(R.drawable.icons8_left48);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleTv.getText().equals("搜索")){
                    titleTv.setText("通讯录");
                    lstContact = ContactColumns.selectAll();
                    mAdapter.notifyDataSetChanged();
                }else{
                    finish();
                }

            }
        });
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        //getpeople();
        //getTree();
        getContact(((AppApplication)getApplicationContext()).getConfigures().GetGPSURL("GetCurrentJGMB"));
        //setUpDrawer();
        /*initSearchBar();
        initMenu();
        initUiAndListener();*/



        //这里是按收到ClientA的邀请后触发
        RuningTime.getWSClient().addCommand("INVISITVIDEO",new InvisitCommand());
        ((InvisitCommand)RuningTime.getWSClient().getCommand("INVISITVIDEO")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                Log.e(TAG, "MainActivity:::onEmitter:: 同意视频");

                try{
                    WSClient.OnlineUser tUser = WSClient.getInstance().getUser(payload.getString("socket_id"));
                    Intent mit = new Intent(ContactTreeActivity.this,VideoCallee.class);
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
                        if(lstContact.size()>0){
                            for(int i = 0;i < lstContact.size();i++){

                                com.android.zhgf.zhgf.database.bean.Contact contact = lstContact.get(i);
                                contact.setOnlineBln(false);

                                for(int j = 0;j < mUserList.size();j++){

                                    if(contact.getTelephoneNo().equals(mUserList.get(j).getUsername())){
                                        contact.setOnlineBln(true);
                                        break;
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        mToast = Toast.makeText(ContactTreeActivity.this, "", Toast.LENGTH_SHORT);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(titleTv.getText().equals("搜索")){
                titleTv.setText("通讯录");
                lstContact = ContactColumns.selectAll();
                mAdapter.notifyDataSetChanged();
            }else{
                finish();
            }
        }
        return true;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                setUpDrawer();
            }else if (msg.what==2) {
                initSearchBar();
                initMenu();
                initUiAndListener();
            }
            super.handleMessage(msg);
        }

    };
    private void getContact(String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, Object> mPostValues = new HashMap<String,Object>();
                //mPostValues.put("ProvinceCityId","e5a765de-2976-4b48-8908-95efe043f26c");
                User mUser=new User(getApplicationContext());
                mPostValues.put("clientmb",(String)mUser.getCache(User.CACHE_KEY_USER_PHONE));
                mPostValues.put("xm",(String)mUser.getCache(User.CACHE_KEY_USER_NAME));
                Log.e(TAG, "submit:正在提交中");
                //String url = ((AppApplication)getApplicationContext()).getConfigures().GetGPSURL("GetCurrentJGMB");
                (new NetworkUtil()).HttpSendPostData(url, mPostValues, new INetwork.OnNetworkStatusListener() {
                    @Override
                    public void onConnected(boolean isSuccess) {

                    }

                    @Override
                    public void onDisconnected(boolean isSuccess) {

                    }

                    @Override
                    public void onGetResult(boolean isSuccess, Object pData) {

                    }

                    @Override
                    public void onPostResult(boolean isSuccess, Object pData) {


                        NetworkUtil.HttpStatus mStatus = (NetworkUtil.HttpStatus)pData;
                        if (mStatus.getStatusCode()==200){
                            Log.e(TAG, "onPostResult: 提交成功!"+mStatus.getData());

                            com.android.zhgf.zhgf.database.bean.Contact contact = new com.android.zhgf.zhgf.database.bean.Contact();
                            contactList = contact.getcurrentJGMBList((String)mStatus.getData());
                            ContactColumns.deleteAll();
                            ContactColumns.insert(contactList);
                            lstContact = ContactColumns.selectAll();


                            ArrayList<String> tempList = new ArrayList<String>();
                            for(int i = 0 ;i < lstContact.size();i++){
                                tempList.add(lstContact.get(i).getName());
                                tempList.add(lstContact.get(i).getTelephoneNo());
                            }
                            suggestions =new String[tempList.size()];
                            for(int i=0;i<tempList.size();i++){
                                suggestions[i]=tempList.get(i);
                            }
                            Message msg = new Message();
                            msg.what=2;
                            msg.obj = true;
                            handler.sendMessage(msg);
                        }else{
                            Log.e(TAG, "onPostResult: 提交失败!" );
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMessage) {

                    }
                });
            }
        }).start();
    }
    private void getTree(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, Object> mPostValues = new HashMap<String,Object>();
                //mPostValues.put("ProvinceCityId","e5a765de-2976-4b48-8908-95efe043f26c");
                //mPostValues.put("clientmb","13819047139");
                //mPostValues.put("xm","张国强");
                Log.e(TAG, "submit:正在提交中");
                String url = ((AppApplication)getApplicationContext()).getConfigures().GetGPSURL("GetProvinceCityTree");
                (new NetworkUtil()).HttpSendPostData(url, mPostValues, new INetwork.OnNetworkStatusListener() {
                    @Override
                    public void onConnected(boolean isSuccess) {

                    }

                    @Override
                    public void onDisconnected(boolean isSuccess) {

                    }

                    @Override
                    public void onGetResult(boolean isSuccess, Object pData) {

                    }

                    @Override
                    public void onPostResult(boolean isSuccess, Object pData) {


                        NetworkUtil.HttpStatus mStatus = (NetworkUtil.HttpStatus)pData;
                        if (mStatus.getStatusCode()==200){
                            Log.e(TAG, "onPostResult: 提交成功!"+mStatus.getData());
                            com.android.zhgf.zhgf.database.bean.Contact contact = new com.android.zhgf.zhgf.database.bean.Contact();
                            //String strtemp = Constant.JSON;
                            try{
                                contactList = contact.getJGMBList((String)mStatus.getData());
                            }catch(Exception e){
                                e.printStackTrace();
                                getTree();
                                return;
                            }
                            //List<OrgBean> mDatas = new ArrayList<OrgBean>() ;
                            mDatas2 = new ArrayList<OrgBean>();
                            for(int i = 0;i <contactList.size();i++ ){
                                OrgBean bean = new OrgBean(
                                        Integer.parseInt(contactList.get(i).getNode_id()),
                                        Integer.parseInt(contactList.get(i).getParent_id()),
                                        contactList.get(i).getName());
                                mDatas2.add(bean);
                            }
                            Message msg = new Message();
                            msg.what=1;
                            msg.obj = true;
                            handler.sendMessage(msg);
                        }else{
                            Log.e(TAG, "onPostResult: 提交失败!" );
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMessage) {

                    }
                });
            }
        }).start();
    }
    private void getpeople(){
        mDatas2 = new ArrayList<OrgBean>();
        ArrayList<String> tempList = new ArrayList<String>();
        String [] people = getResources().getStringArray(R.array.query_suggestions);
        for(int i = 0;i < people.length;i++){

            String[] temp=people[i].split(",");
            if( !(temp[2].equals(""))){
                tempList.add(temp[1]);
                tempList.add(temp[2]);
            }else{
                OrgBean bean = new OrgBean( Integer.parseInt(temp[0]), Integer.parseInt(temp[3]), temp[1]);
                mDatas2.add(bean);
            }
            com.android.zhgf.zhgf.database.bean.Contact contact = new com.android.zhgf.zhgf.database.bean.Contact();
            contact.setNode_id(temp[0]);
            contact.setName(temp[1]);
            contact.setTelephoneNo(temp[2]);
            contact.setParent_id(temp[3]);
            contact.setUtil("基干民兵");
            if( !(temp[2].equals(""))){
                contact.setPersonFlg(1);
            }else{
                contact.setPersonFlg(0);
            }
            contactList.add(contact);
        }
        suggestions =new String[tempList.size()];
        for(int i=0;i<tempList.size();i++){
            suggestions[i]=tempList.get(i);
        }
        ContactColumns.deleteAll();
        ContactColumns.insert(contactList);
        lstContact = ContactColumns.selectAll();

        ;
    }
    private void initSearchBar(){

        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setSuggestions(suggestions);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.id_drawer_layout), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String name =  (String) parent.getAdapter().getItem(position);
                lstContact.clear();
                lstContact = ContactColumns.selectPeople(name);
                if(lstContact.size() > 0){
                    mAdapter.notifyDataSetChanged();
                    titleTv.setText("搜索");
                    searchView.closeSearch();
                }else{
                    DialogUtil.Toast(ContactTreeActivity.this, "未搜索到联系人");
                }

            }
        });
    }
    private void setUpDrawer()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        //mTree.addHeaderView(inflater.inflate(R.layout.header_lessonlist, mTree, false));

        //initTreeDatas();
        //mLvLeftMenu.setAdapter(new MenuItemAdapter(this));
        try {
            treeAdapter = new SimpleTreeListViewAdapter<OrgBean>(this, mTree, mDatas2, 1);
            mTree.setAdapter(treeAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initEvent();
    }
    /**
     * 初始化数据
     * @Title: initDatas void
     */
    private void initTreeDatas() {
/*		mDatas = new ArrayList<FileBean>();

		FileBean bean = new FileBean(1, 0, "根目录1");
		mDatas.add(bean);
		bean = new FileBean(2, 0, "根目录2");
		mDatas.add(bean);
		bean = new FileBean(3, 0, "根目录3");
		mDatas.add(bean);

		bean = new FileBean(4, 1, "根目录1-1");
		mDatas.add(bean);
		bean = new FileBean(5, 1, "根目录1-2");
		mDatas.add(bean);

		bean = new FileBean(6, 5, "根目录1-2-1");
		mDatas.add(bean);

		bean = new FileBean(7, 3, "根目录3-1");
		mDatas.add(bean);
		bean = new FileBean(8, 3, "根目录3-2");
		mDatas.add(bean);*/

        mDatas2 = new ArrayList<OrgBean>();

        OrgBean bean = new OrgBean(1, 0, "根目录1");
        mDatas2.add(bean);
        bean = new OrgBean(2, 0, "根目录2");
        mDatas2.add(bean);
        bean = new OrgBean(3, 0, "根目录3");
        mDatas2.add(bean);

        bean = new OrgBean(4, 1, "根目录1-1");
        mDatas2.add(bean);
        bean = new OrgBean(5, 1, "根目录1-2");
        mDatas2.add(bean);

        bean = new OrgBean(6, 5, "根目录1-2-1");
        mDatas2.add(bean);

        bean = new OrgBean(7, 3, "根目录3-1");
        mDatas2.add(bean);
        bean = new OrgBean(8, 3, "根目录3-2");
        mDatas2.add(bean);
        bean = new OrgBean(9, 3, "根目录3-3");
        mDatas2.add(bean);
        bean = new OrgBean(10, 3, "根目录3-4");
        mDatas2.add(bean);
        bean = new OrgBean(11, 3, "根目录3-5");
        mDatas2.add(bean);
        bean = new OrgBean(12, 3, "根目录3-6");
        mDatas2.add(bean);
        bean = new OrgBean(13, 3, "根目录3-7");
        mDatas2.add(bean);
        bean = new OrgBean(14, 3, "根目录3-8");
        mDatas2.add(bean);
        bean = new OrgBean(15, 3, "根目录3-9");
        mDatas2.add(bean);
        bean = new OrgBean(16, 3, "根目录3-10");
        mDatas2.add(bean);
        bean = new OrgBean(17, 3, "根目录3-11");
        mDatas2.add(bean);
        bean = new OrgBean(18, 3, "根目录3-12");
        mDatas2.add(bean);
        bean = new OrgBean(19, 3, "根目录3-13");
        mDatas2.add(bean);
        bean = new OrgBean(20, 3, "根目录3-14");
        mDatas2.add(bean);
        bean = new OrgBean(21, 3, "根目录3-15");
        mDatas2.add(bean);
        bean = new OrgBean(22, 3, "根目录3-16");
        mDatas2.add(bean);

    }

    /**
     * 初始化响应事件
     * @Title: initEvent void
     */
    private void initEvent() {
        treeAdapter.setOnTreeNodeClickLitener(new TreeListViewAdapter.OnTreeNodeClickListener() {

            @Override
            public void onClick(Node node, int position) {
                if (node.isLeaf()) {
                    //Toast.makeText(MainActivity.this, node.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getContact(((AppApplication)getApplicationContext()).getConfigures().GetGPSURL("GetJGMBList"));
            }
        });
        /*mTree.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactTreeActivity.this);
                builder.setTitle("添加节点");
                final EditText et = new EditText(ContactTreeActivity.this);
                builder.setView(et);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            return;
                        }
                        treeAdapter.addExtraNode(position, name);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                return true;	//返回值为tru表示长按事件触发后不会触发点击事件
            }
        });*/
    }
    public void initData() {
        /*lstContact = new LinkedList<Contact>();
        lstContact.add(new Contact("联系人1",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人2",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人3",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人4",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人5",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人6",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人7",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人8",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人9",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人10",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人11",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人12",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("联系人13",R.drawable.icons8_contacts48));*/


    }

    public void initMenu() {
        mMenu = new Menu(true);
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setIcon(Utils.getDrawable(this, R.drawable.icons8_chat48))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setIcon(Utils.getDrawable(this,R.drawable.icons8_videocall48))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setIcon(Utils.getDrawable(this,R.drawable.icons8_delete48))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(Utils.getDrawable(this,R.drawable.icons8_delete48))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(Utils.getDrawable(this,R.drawable.icons8_videocall48))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(Utils.getDrawable(this,R.drawable.icons8_chat48))
                .build());
    }

    public void initUiAndListener() {
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit);
        mListView.setMenu(mMenu);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(this);
        //mListView.setOnDragDropListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        //mListView.setOnItemDeleteListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemScrollBackListener(this);
        //this.ivRight.setOnClickListener(this);
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return lstContact.size();
        }

        @Override
        public Object getItem(int position) {
            return lstContact.get(position);
        }

        @Override
        public long getItemId(int position) {
            return lstContact.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(ContactTreeActivity.this).inflate(R.layout.item_custom, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                cvh.txtTel = (TextView) convertView.findViewById(R.id.tel_item_edit);
                cvh.txtUtil = (TextView) convertView.findViewById(R.id.util_item_edit);
                cvh.txtOnline = (TextView) convertView.findViewById(R.id.online_item_edit);
                //cvh.btnClick.setOnClickListener(mOnClickListener);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            // ApplicationInfo item = (ApplicationInfo) this.getItem(position);
            cvh.txtName.setText(lstContact.get(position).getName());
            cvh.txtTel.setText(lstContact.get(position).getTelephoneNo());
            cvh.txtUtil.setText(lstContact.get(position).getUtil());
            if(lstContact.get(position).isOnlineBln()){
                cvh.txtOnline.setText("在线");
                cvh.txtOnline.setTextColor(getResources().getColor(R.color.colorGreen));
            }else{
                cvh.txtOnline.setText("离线");
                cvh.txtOnline.setTextColor(getResources().getColor(R.color.ccc));
            }
            //cvh.btnClick.setText(position + "");
            //cvh.btnClick.setTag(position);
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            public TextView txtTel;
            public TextView txtUtil;
            public TextView txtOnline;
            //public Button btnClick;
        }

        private OnClickListener mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o instanceof Integer) {
                    //startActivity(new Intent(ContactActivity.this, ChatActivity.class));
                    mToast.setText("button click-->" + ((Integer) o));
                    mToast.show();
                }
            }
        };
    };

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(ContactTreeActivity.this, com.android.zhgf.zhgf.activity.chat.ChatActivity.class);
        intent.putExtra(com.android.zhgf.zhgf.activity.chat.ChatActivity.FROM_ID, "user2@192.168.10.104");
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    @Override
    public void onScrollBackAnimationFinished(View view, int position) {

    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        //toast("onMenuItemClick   itemPosition--->" + itemPosition + "  buttonPosition-->" + buttonPosition + "  direction-->" + direction);
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        startActivity(new Intent(ContactTreeActivity.this, ChatActivity.class));
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                        return Menu.ITEM_SCROLL_BACK;
                    case 2:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                    case 1:
                        return Menu.ITEM_SCROLL_BACK;
                    case 2:
                        startActivity(new Intent(ContactTreeActivity.this, ChatActivity.class));
                        return Menu.ITEM_SCROLL_BACK;
                }
        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {

    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }

    private void toast(String toast) {
        mToast.setText(toast);
        mToast.show();
    }

    @Override
    public void onItemDeleteAnimationFinished(View view, int position) {
        lstContact.remove(position - mListView.getHeaderViewsCount());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivRight:
                startActivity(new Intent(ContactTreeActivity.this, GroupListActivity.class));
                //finish();//关闭页面
                break;



        }
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        android.view.MenuItem item1 = menu.findItem(R.id.action_search);
        android.view.MenuItem item2 = menu.findItem(R.id.action_groupchat);
        searchView.setMenuItem(item1);
        searchView.setGroupChatItem(item2,new android.view.MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                startActivity(new Intent(ContactTreeActivity.this, GroupListActivity.class));
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
