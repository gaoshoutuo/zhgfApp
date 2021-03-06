package com.android.zhgf.zhgf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.bean.Contact;
import com.android.zhgf.zhgf.util.Utils;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.LinkedList;
import java.util.List;

public class MultiContactActivity extends BaseActivity implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener,
        SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemScrollBackListener, SlideAndDragListView.OnItemDeleteListener,OnClickListener {

    private static final String TAG = ContactActivity.class.getSimpleName();

    private Menu mMenu;
    private List<Contact> lstContact;
    private SlideAndDragListView mListView;
    private Toast mToast;
    public MultiContactActivity() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_contact);
        // 返回图标
        setBack();
        // 右图标
        setRight(R.drawable.icons8_singlepeople64);
        setTitle("群组");

        initData();
        initMenu();
        initUiAndListener();
        mToast = Toast.makeText(MultiContactActivity.this, "", Toast.LENGTH_SHORT);

    }
    public void initData() {
        lstContact = new LinkedList<Contact>();
        lstContact.add(new Contact("单位",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("工作",R.drawable.icons8_contacts48));
        lstContact.add(new Contact("娱乐",R.drawable.icons8_contacts48));



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
        mListView = findViewById(R.id.lv_edit);
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
        this.ivRight.setOnClickListener(this);
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
                convertView = LayoutInflater.from(MultiContactActivity.this).inflate(R.layout.item_custom, null);
                cvh.imgLogo = convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = convertView.findViewById(R.id.txt_item_edit);
                //cvh.btnClick.setOnClickListener(mOnClickListener);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            // ApplicationInfo item = (ApplicationInfo) this.getItem(position);
            cvh.txtName.setText(lstContact.get(position).getaName());
            cvh.imgLogo.setBackgroundResource(lstContact.get(position).getaIcon());
            //cvh.btnClick.setText(position + "");
            //cvh.btnClick.setTag(position);
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            //public Button btnClick;
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o instanceof Integer) {
                    //startActivity(new Intent(ContactActivity.this, ChatActivity.class));
                    mToast.setText("button click-->" + o);
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
                        startActivity(new Intent(MultiContactActivity.this, ChatActivity.class));
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
                        startActivity(new Intent(MultiContactActivity.this, ChatActivity.class));
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

                finish();//关闭页面
                break;



        }
    }
}
