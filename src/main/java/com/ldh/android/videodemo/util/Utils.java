/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ldh.android.videodemo.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private Utils() {}

    @TargetApi(11)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
//                vmPolicyBuilder
//                        .setClassInstanceLimit(ImageGridActivity.class, 1)
//                        .setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed
        // behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }



    /**
     * 保存bitmap到SD卡
     *
     * @param bitName
     * @param mBitmap
     * @throws IOException
     */
    public static String saveMyBitmap(String bitName, Bitmap mBitmap, String imgType) {
        String pathString = getSDPath() + "/ldh/ad/";
        if (null == imgType)
            imgType = ".jpg";
        String fileName = bitName + imgType;

        try {
            File dirFile = new File(pathString);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File myCaptureFile = new File(pathString, fileName);
            if (myCaptureFile.exists()) {
                myCaptureFile.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            if (imgType.equals(".jpg")) {
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            } else if (imgType.equals(".png")) {
                mBitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
            }
            bos.flush();
            bos.close();
            return pathString + bitName + imgType;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断某路径下某文件是否存在
     *
     * @param name
     * @param path 为空时，路径定位到广告存储路径
     * @return
     */
    public static boolean isExistsFile(String name, String path) {
        if (TextUtils.isEmpty(path)) {
            path = getSDPath() + "/ldh/ad/";
        }
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            return false;
        }
        File myCaptureFile = new File(path, name);
        if (myCaptureFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据url得到文件的名称
     *
     * @param url
     * @return
     */
    public static String getFileNameByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String[] strArray = url.split("/");
        if (strArray.length < 2) {
            return null;
        }
        return strArray[strArray.length - 2] + "_" + strArray[strArray.length - 1];
    }

    /**
     * 根据本地路径得到文件的名称
     *
     * @param path
     * @return
     */
    public static String getFileNameByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String[] strArray = path.split("/");
        if (strArray.length < 2) {
            return null;
        }
        return strArray[strArray.length - 1];
    }

    /**
     * 像数组中添加一个string元素
     *
     * @param arr
     * @param str
     * @return
     */
    public static String[] insert(String[] arr, String str) {
        int size = arr.length;
        String[] tmp = new String[size + 1];
        System.arraycopy(arr, 0, tmp, 0, size);
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        tmp[size] = str;
        return tmp;
    }

    /**
     * 获取SD卡目录
     *
     * @return
     */
    public static String getSDPath() {
        String pathForSD = "";
        try {
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            // 判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            }
            pathForSD = sdDir.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Utils", "获得SD卡根目录出错！");
        }
        return pathForSD;
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        //Environment.getExtemalStorageState() 获取SDcard的状态
        //Environment.MEDIA_MOUNTED 手机装有SDCard,并且可以进行读写
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static boolean deleteFolder(String dir) {
        File delfolder = new File(dir);
        File oldFile[] = delfolder.listFiles();
        try {
            for (int i = 0; i < oldFile.length; i++) {
                if (oldFile[i].isDirectory()) {
                    // 递归清空子文件夹
                    deleteFolder(dir + oldFile[i].getName() + "//");
                }
                oldFile[i].delete();
            }
            return true;
        } catch (Exception e) {
            Log.v("Utils", "清空文件夹操作出错!");
            return false;
        }
    }
}
