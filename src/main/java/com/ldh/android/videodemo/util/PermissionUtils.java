package com.ldh.android.videodemo.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.ldh.android.videodemo.R;

/**
 * Created by ldh on 2018/4/25.
 */
public final class PermissionUtils {


    // //在Activity中使用，走Activity的onRequestPermissionsResult回调
    public static boolean checkActivityPermisssion(Activity activity, String permission, String dialogMessage, int requestCode, boolean finished) {
        if (!hasPermission(activity, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                createPermissionDialog(activity, dialogMessage, finished);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
            return false;
        }
        return true;
    }

    //在Fragment中使用，走Fragment的onRequestPermissionsResult回调
    public static boolean checkFragmentPermisssion(Activity activity, Fragment fragment, String permission, String dialogMessage, int requestCode, boolean finished) {
        if (!hasPermission(activity, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                createPermissionDialog(activity, dialogMessage, finished);
            } else {
                fragment.requestPermissions(new String[]{permission}, requestCode);
            }
            return false;
        }
        return true;
    }

    public static void createPermissionDialog(final Activity activity, String message, final boolean finished) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false)
                .setMessage(message)
                .setTitle(activity.getString(R.string.permission))
                .setPositiveButton(activity.getString(R.string.permission_setting), (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:com.ldh.android.videodemo"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    dialog.dismiss();
                    if (finished) {
                        activity.finish();
                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), (dialog, which) -> {
                    dialog.dismiss();
                    if (finished) {
                        activity.finish();
                    }
                })
                .show();
    }

    public static void createPermissionDialog(final Activity activity, String message, DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false)
                .setMessage(message)
                .setTitle(activity.getString(R.string.permission))
                .setPositiveButton(activity.getString(R.string.permission_setting), (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:com.ldh.android.videodemo"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    dialog.dismiss();
                    activity.finish();
                })
                .setNegativeButton(activity.getString(R.string.cancel), onCancel)
                .show();
    }

    public static boolean hasPermission(Context context, String permisssion) {
        try {
            return ContextCompat.checkSelfPermission(context, permisssion) == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable throwable) {
            return false;
        }
    }

    /**
     * 判断外置存储卡是否有写入权限
     *
     * @param context Application context
     * @return boolean
     */
    public static boolean hasExternalStoragePermission(Context context) {
        return hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
