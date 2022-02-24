package com.yc.ycvideoplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.yc.ycvideoplayer.R;

import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.ycvideoplayer.demo.DemoActivity;
import com.yc.ycvideoplayer.newPlayer.activity.TypeActivity;

import cn.ycbjie.ycstatusbarlib.bar.StateAppBar;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/11/18
 * 描    述：Main主页面
 * 修订历史：
 * ================================================
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTv1;
    private TextView mTv2;
    private TextView mTv3;
    private TextView mTv4;
    private TextView mTv5;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StateAppBar.setStatusBarLightMode(this, Color.WHITE);
        initView();
    }

    private void initView() {
        mTv1 = (TextView) findViewById(R.id.tv_1);
        mTv2 = (TextView) findViewById(R.id.tv_2);
        mTv3 = (TextView) findViewById(R.id.tv_3);
        mTv4 = (TextView) findViewById(R.id.tv_4);
        mTv5 = (TextView) findViewById(R.id.tv_5);

        mTv1.setOnClickListener(this);
        mTv2.setOnClickListener(this);
        mTv3.setOnClickListener(this);
        mTv4.setOnClickListener(this);
        mTv5.setOnClickListener(this);

        String[] p =new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            VideoLogUtils.d("permission:"+checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE"));
                requestPermissions(p,123);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_1:
                startActivity(DemoActivity.class);
                break;
            case R.id.tv_2:
                startActivity(TypeActivity.class);
                break;
        }
    }

    private void startActivity(Class c){
        startActivity(new Intent(this,c));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoLogUtils.d("requestCode:"+requestCode+"/"+permissions.toString());
    }
}
