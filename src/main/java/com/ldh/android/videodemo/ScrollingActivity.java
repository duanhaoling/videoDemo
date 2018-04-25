package com.ldh.android.videodemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
