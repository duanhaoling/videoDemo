package com.ldh.android.videodemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ldh.android.videodemo.util.PermissionConstant;
import com.ldh.android.videodemo.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {
    private String msg1, msg2, msg3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Capture with your own app", Snackbar.LENGTH_LONG)
                        .setAction("Capture", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkShootVideoPermissions();
                            }
                        }).show();
            }
        });

        //批量申请权限测试
        checkPermissions();
    }

    /**
     * 批量申请权限：1.获取设备id 2.写sd卡 3获取定位
     * 若不授予则继续后续逻辑
     */
    private void checkPermissions() {
        int canReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int canWriteExStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int canLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> list = new ArrayList<>();
        if (PackageManager.PERMISSION_DENIED == canReadPhoneState) {
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (PackageManager.PERMISSION_DENIED == canWriteExStorage) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (PackageManager.PERMISSION_DENIED == canLocation) {
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        String[] permissions = list.toArray(new String[list.size()]);
        if (PackageManager.PERMISSION_DENIED == canReadPhoneState
                || PackageManager.PERMISSION_DENIED == canWriteExStorage
                || PackageManager.PERMISSION_DENIED == canLocation) {
            ActivityCompat.requestPermissions(this, permissions, 0x100);
        } else {
            // go on
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkShootVideoPermissions() {
        msg1 = getString(R.string.permission_tips, getString(R.string.app_name), getString(R.string.permission_camera));
        msg2 = getString(R.string.permission_tips, getString(R.string.app_name), getString(R.string.permission_storage));
        msg3 = getString(R.string.permission_tips, getString(R.string.app_name), getString(R.string.permission_micro));

        if (PermissionUtils.checkActivityPermisssion(this, Manifest.permission.CAMERA, msg1,
                PermissionConstant.PERMISSIONS_REQUEST_CAMERA, true)) {
            if (PermissionUtils.checkActivityPermisssion(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, msg2,
                    PermissionConstant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE, true)) {
                if (PermissionUtils.checkActivityPermisssion(this, Manifest.permission.RECORD_AUDIO, msg3,
                        PermissionConstant.PERMISSIONS_REQUEST_RECORD_AUDIO, true)) {
                    gotoActivity(ShootVideoActivity.class);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionConstant.PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkShootVideoPermissions();
                    } else {
                        PermissionUtils.createPermissionDialog(this, msg1, true);
                    }
                }
                break;
            case PermissionConstant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkShootVideoPermissions();
                    } else {
                        PermissionUtils.createPermissionDialog(this, msg2, true);
                    }
                }
                break;
            case PermissionConstant.PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkShootVideoPermissions();
                    } else {
                        PermissionUtils.createPermissionDialog(this, msg3, true);
                    }
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void gotoActivity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }
}
