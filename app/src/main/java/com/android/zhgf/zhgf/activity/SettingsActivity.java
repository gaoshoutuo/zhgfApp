package com.android.zhgf.zhgf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.util.SharedPreferencesUtils;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.ksxkq.materialpreference.SimpleOnPreferenceCallback;
import com.ksxkq.materialpreference.widget.PreferenceContainer;
import com.ksxkq.materialpreference.widget.ScreenPreference;

public class SettingsActivity extends BaseActivity {
    /**
     * 系统设置跳转Activity ResultCode
     */
    public static final int RESULT_CODE = 10001;
    private static final int REQUEST_CODE_FOR_SUMMARY = 0;
    PreferenceContainer mContainer;
    private SimpleOnPreferenceCallback onPreferenceCallback;
    String str;
    Button exitBtn;
    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // 返回图标
        setBack();
        setTitle("系统设置");
        mUser = new User(getApplication());
        exitBtn = (Button)this.findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CODE, intent);
                SharedPreferencesUtils helper = new SharedPreferencesUtils(SettingsActivity.this, "setting");
                helper.putValues(new SharedPreferencesUtils.ContentValue("autoLogin", false));
                /*mUser.setCache(User.CACHE_KEY__USER_ISFORGET_NAME, false);
                ((AppApplication) getApplication()).setUser(mUser);
                boolean isForgetName = mUser.getBooleanCache(User.CACHE_KEY__USER_ISFORGET_NAME);*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mUser.Logout(null);
                    }
                }).start();
                finish();//关掉自己
            }
        });

        /*if (savedInstanceState == null) {
            Fragment preferenceFragment = new SettingsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.pref_container, preferenceFragment);
            ft.commit();
        }*/
        mContainer = (PreferenceContainer) findViewById(R.id.container);
        mContainer
                .addCategoryPreference("cate_list", "服务器设置")
                .addListPreference("server", "服务器", R.array.server_names_array, R.array.server_values_array)
                .addCategoryPreference("cate_list2", "地图设置")
                .addListPreference("gpstime", "地图实时上报时间", R.array.gpstime_names_array, R.array.gpstime_values_array);
                //.addSeekbarPreference("seekbar_transparency", "地图实时上报时间", 5, 60);

        onPreferenceCallback = new SimpleOnPreferenceCallback() {
            @Override
            public void onPreferenceClick(String key, View view) {

            }

            @Override
            public void onCheckedChanged(String key, CompoundButton compoundButton, boolean isChecked) {
                super.onCheckedChanged(key, compoundButton, isChecked);
            }

            @Override
            public void onSingleChoice(String key, String name, String value, View view) {
                super.onSingleChoice(key, name, value, view);
            }

            @Override
            public void onSecondIconClick(String key, View view) {
                super.onSecondIconClick(key, view);
            }

            @Override
            public void onInfoIconClick(String key, String title, View view) {

            }
            @Override
            public void onProgressChanged(String key, SeekBar seekBar, int progress, boolean isUser) {

            }
        };
        mContainer.registerCallback(onPreferenceCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContainer.unregisterCallback(onPreferenceCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SUMMARY && resultCode == RESULT_OK) {
            String key = data.getStringExtra("key");
            String summary = data.getStringExtra("summary");
            final ScreenPreference preference = mContainer.getPreference(key);
            if (preference != null) {
                preference.setSummary(summary);
            }
        }
    }
}
