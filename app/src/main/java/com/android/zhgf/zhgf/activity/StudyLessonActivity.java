package com.android.zhgf.zhgf.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.bean.OrgBean;
import com.android.zhgf.zhgf.adapter.SimpleTreeListViewAdapter;
import com.android.zhgf.zhgf.utils.Node;
import com.android.zhgf.zhgf.utils.adapter.TreeListViewAdapter;
//import com.zhy.utils.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StudyLessonActivity extends AppCompatActivity
{
    private ListView mTree;
    private DrawerLayout mDrawerLayout;
    private SimpleTreeListViewAdapter<OrgBean> treeAdapter;
    private List<OrgBean> mDatas2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studylesson);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mTree = (ListView) findViewById(R.id.id_lv_left_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(0);
        toolbar.setTitle("军事理论");
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        setUpDrawer();
    }

    private void setUpDrawer()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        //mTree.addHeaderView(inflater.inflate(R.layout.header_lessonlist, mTree, false));

        initDatas();
        //mLvLeftMenu.setAdapter(new MenuItemAdapter(this));
        try {
            treeAdapter = new SimpleTreeListViewAdapter<OrgBean>(this, mTree, mDatas2, 1);
            mTree.setAdapter(treeAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_nav_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        mTree.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StudyLessonActivity.this);
                builder.setTitle("添加节点");
                final EditText et = new EditText(StudyLessonActivity.this);
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
        });
    }

    /**
     * 初始化数据
     * @Title: initDatas void
     */
    private void initDatas() {
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


}
