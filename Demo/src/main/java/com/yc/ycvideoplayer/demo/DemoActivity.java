package com.yc.ycvideoplayer.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yc.video.kernel.utils.VideoLogUtils;

import org.yc.ycvideoplayer.R;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTv1;
    private TextView mTv12;
    private TextView mTv2;
    private TextView mTv3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_player);

        mTv1 = findViewById(R.id.tv_1);
        mTv12 = findViewById(R.id.tv_1_2);
        mTv2 = findViewById(R.id.tv_2);
        mTv3 = findViewById(R.id.tv_3);

        mTv1.setOnClickListener(this);
        mTv12.setOnClickListener(this);
        mTv2.setOnClickListener(this);
        mTv3.setOnClickListener(this);
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
                startActivity(MediaActivity.class);
                break;
            case R.id.tv_1_2:
                startActivity(MediaActivity2.class);
                break;
            case R.id.tv_2:
                startActivity(IjkActivity.class);
                break;
            case R.id.tv_3:
                startActivity(ExoActivity.class);
                break;
        }
    }

    private void startActivity(Class c){
        startActivity(new Intent(this,c));
    }

}
