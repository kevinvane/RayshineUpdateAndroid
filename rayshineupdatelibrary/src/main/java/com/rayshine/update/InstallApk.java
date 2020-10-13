package com.rayshine.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.FileProvider;

import com.yanzhenjie.permission.AndPermission;

import java.io.File;

public class InstallApk {

    private static final String TAG = "InstallApk";

    /**
     * 调用AndPermission的接口，直接交由AndPermission处理
     * @param context
     * @param apkFile
     */
    public static void installByAndPermission(Activity context, File apkFile){


        AndPermission.with(context)
                .install()
                .file(apkFile)
                .start();

        // AndPermission.with(context)
        //         .install()
        //         .file(file)
        //         .onGranted(file0 -> {
        //             // Log.i(TAG, "onGranted: " + file0.getPath());
        //             // installApk(context,file);
        //         })
        //         .onDenied(file1 -> {
        //
        //             // Log.i(TAG, "onDenied: " + file1.getPath());
        //             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //
        //                 Uri packageURI = Uri.parse("package:" + context.getPackageName());
        //                 Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
        //                 // Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        //                 context.startActivity(intent);
        //             }
        //         })
        //         .start();
    }


    /**
     * 调用系统api检查是否有权限安装
     * @param context
     * @param apkFile
     */
    @Deprecated
    public static void installByManual(Activity context, File apkFile){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(!context.getPackageManager().canRequestPackageInstalls()){
                Uri packageURI = Uri.parse("package:" + context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                context.startActivity(intent);
            }else{
                installApkManual(context,apkFile);
            }
        }

    }


    /**
     * 安装apk
     * @param context
     * @param file
     */
    @Deprecated
    public static void installApkManual(Activity context, File file) {

        // https://www.cnblogs.com/chorm590/p/11696547.html
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//临时授权
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 从apk文件中读取版本号
     * @param activity
     * @param archiveFilePath
     * @return
     */
    public static int getApkFileVersionCode(Activity activity, String archiveFilePath) {
        PackageManager pm = activity.getPackageManager();
        PackageInfo packInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        int versionCode;
        try {
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            versionCode = 0;
        }
        return versionCode;
    }



    public static int getAppCurrentVersionCode(Context context){

        int version = 0;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            version = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }
}
