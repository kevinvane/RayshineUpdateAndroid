package com.rayshine.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 文件存储兼容性参考了：
 * 1. https://blog.csdn.net/wds1181977/article/details/103100280
 * 2. https://www.cnblogs.com/lilinjie/p/7065386.html
 */
public class RayshineUpdate {

    private static final String TAG = "RayshineUpdate";
    private static final String UPDATE_FILE_NAME = "rayshineUpdate.apk";
    private static final String SERVICE_HOST = "http://app.rayshine.cc";
    private static final String BASE_DOWNLOAD_URL = SERVICE_HOST + "/apk/download/";


    private static OkHttpClient okHttpClient = new OkHttpClient();

    private interface TaskListener {
        void progress(int max, int progress);
        void end(String filePath);
    }

    private static void getRequest(String url, Callback callback){

        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
    public static void check(Context context,String appName) {
        check(context,appName,true);
    }
    public static void check(Context context,String appName,boolean silent){

        String url = SERVICE_HOST + "/apk/release/" + appName;
        getRequest(url, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                int code = response.code();
                if(code == 200 && response.body()!=null){
                    String responseString = response.body().string();
                    Log.d(TAG,"响应：" + responseString);
                    // {"statusCode":200,"data":{"id":3,"versionName":"2.1.5","versionCode":27,"type":"apk","date":"2018-07-03T21:44:51.000+0000","size":4286900.0}}
                    try {
                        JSONObject jo = new JSONObject(responseString);
                        int statusCode = jo.getInt("statusCode");
                        if(statusCode == 200){

                            JSONObject data = jo.getJSONObject("data");
                            int versionCodeServer = data.getInt("versionCode");
                            String versionNameServer = data.getString("versionName");

                            int versionCurrent = InstallApk.getAppCurrentVersionCode(context);
                            if(versionCodeServer > versionCurrent){
                                showDialogLooper(context,appName,versionNameServer);
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(!silent)showToastNotUpdate(context);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

    }

    private static void showToastNotUpdate(Context context) {

        showToastLooper(context,"您已是最新版本");
    }

    /**
     * 在子线程显示show Toast
     * @param context
     * @param text
     */
    private static void showToastLooper(Context context,String text){

        Looper.prepare();
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        toast.setText(text);
        toast.show();
        Looper.loop();
    }
    // public static void check(Context context,String appName){
    //
    //     retrofit2.Call<VersionResponse> call = RayshineClient.getRayshineService().getVersion(appName);
    //     call.enqueue(new Callback<VersionResponse>() {
    //         @Override
    //         public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
    //
    //             VersionResponse body = response.body();
    //             if(body!=null && body.getStatusCode() == 200){
    //                 int versionCodeServer = body.getData().getVersionCode();
    //                 int versionCurrent = InstallApk.getAppCurrentVersionCode(context);
    //                 if(versionCodeServer > versionCurrent){
    //                     showDialog(context,appName,body.getData());
    //                 }else{
    //                     Toast.makeText(context,"您已是最新版本",Toast.LENGTH_LONG).show();
    //                 }
    //             }
    //         }
    //
    //         @Override
    //         public void onFailure(Call<VersionResponse> call, Throwable t) {
    //             t.printStackTrace();
    //         }
    //     });
    // }

    private static void showDialogLooper(Context context,String appName, String versionName){

        Looper.prepare();
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("升级提示")
                .setMessage("发现新版本" + versionName + ",是否立即升级?")
                .setNegativeButton("暂不更新", (dialog, which) -> Log.d(TAG, "暂不更新"))
                .setPositiveButton("更新", (dialog, which) -> downloadAuto(context, appName))
                .create();

        alertDialog.show();
        Looper.loop();

        // new XPopup.Builder(context)
        //         .asConfirm("升级提示",
        //                 "发现新版本" + dataBean.getVersionName() + ",是否立即升级?",
        //                 "忽略",
        //                 "升级",
        //                 () -> downloadAuto(context,appName),
        //                 () -> {},
        //                 false
        //         ).show();

    }


    private static void downloadAuto(Context context,String appName){

        download(context, BASE_DOWNLOAD_URL + appName, new TaskListener() {

            @Override
            public void progress(int max, int progress) {
                updateProgressNotify(context,max,progress);
            }

            @Override
            public void end(String filePath) {

                Log.i(TAG, "end: "+ filePath);
                InstallApk.installByAndPermission((Activity)context,new File(filePath));
            }
        });
    }


    private static void updateProgressNotify(Context context, int max, int progress){

        String channelId = "download_update";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId);

        boolean isFinish = (max == progress);

        mBuilder.setContentTitle(isFinish?"下载完成" : "正在下载更新包")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setProgress(max,progress,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //channel只能由NotificationManager来创建 （NotificationManagerCompat只能发通知，不能创建channel）
            NotificationManager manager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // 创建
            NotificationChannel channel = new NotificationChannel(channelId, "download",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setShowBadge(false);
            channel.setSound(null,null);
            channel.setDescription("下载服务进度更新");

            if(manager!=null && !manager.getNotificationChannels().contains(channel)){
                manager.createNotificationChannel(channel);
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1000, mBuilder.build());
    }

    /**
     * 先请求存储权限，拿到权限再下载，如果文件存储与公共目录，必须要先申请权限
     * @param context
     * @param url
     * @param listener
     */
    private static void downloadPermission(Context context,final String url,TaskListener listener){

        AndPermission.with(context)
                .runtime()
                .permission(
                        Permission.Group.STORAGE
                )
                .onGranted(permissions ->{
                    Log.i(TAG,"Permission 通过:" + permissions.toString());
                    download(context,url,listener);
                })
                .onDenied(permissions  -> {
                    Log.e(TAG,"STORAGE Permission Denied:读写" + permissions.toString());
                    Toast.makeText(context,"没有获取存储权限",Toast.LENGTH_LONG).show();
                })
                .start();
    }

    private static void download(Context context, String url, TaskListener listener){

        // 不要将文件存储在公共目录上，就不需要申请权限
        final String filePath = getUpdateFilePath(context);

        DownloadTask task = new DownloadTask.Builder(url, getUpdateDir(context))
                .setFilename(UPDATE_FILE_NAME)
                // 下载进度回调的间隔时间（毫秒）
                .setMinIntervalMillisCallbackProcess(30)
                // 任务过去已完成是否要重新下载
                .setPassIfAlreadyCompleted(false)
                .build();


        DownloadListener1 downloadListener1 = new DownloadListener1() {

            @Override
            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {

            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {

                int i = (int)(((double)currentOffset / (double)totalLength) * 100) ;
                if (listener != null) listener.progress(100,i);

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                if (listener != null) listener.end(filePath);
            }
        };

        task.enqueue(downloadListener1);
    }







    /**
     * file:///storage/emulated/0/Android/data/com.rayshine.sample/files/Download/Update/rayshineUpdate.apk
     * @param context
     * @return
     */
    private static String getUpdateFilePath(Context context){

        return getExternalCacheDir(context).getPath() + "/" + UPDATE_FILE_NAME;
    }

    private static File getUpdateDir(Context context){

        return getExternalCacheDir(context);
    }

    /**
     * 外部存储上的路径(/storage/emulated/0/Android/data/Download)，
     * 当用户卸载应用时，系统会清除这些文件
     *
     * @param context
     * @return
     */
    private static File getDownloadFileDir(Context context) {

        if(!isExternalStorageWritable()){
            throw new RuntimeException("外部存储不可用");
        }else{
            Log.i(TAG, "外部存储正常");
        }
        return getExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     *  验证外部存储是否可用
     * @return
     */
    private static boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /**
     * 存储一些公共的文件，在App被删除之后，文件还可以被保留
     * 始终需要申请SD写入权限的
     * @param context
     * @return
     */
    private static File getPublicExternalDir(Context context) {

        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取外部存储/SD卡上的文件目录 (/storage/emulated/0/Android/data/应用包名/files/)
     * API < 19：需要申请写权限，可以被其他的应用读取到的，随着App卸载而被删除
     *
     * @param context
     * @param type 尽量选用 Environment.DIRECTORY_*
     * @return
     */
    private static File getExternalFilesDir(Context context,String type) {

        return context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS);
    }
    /**
     * 获取外部存储/SD卡上的缓存目录 (/storage/emulated/0/Android/data/应用包名/cache/)
     * API < 19：需要申请写权限，可以被其他的应用读取到的，随着App卸载而被删除
     *
     * @param context
     * @return
     */
    private static File getExternalCacheDir(Context context) {

        return context.getExternalCacheDir();
    }


    /**
     * 获取内置存储下的文件目录 （/data/data/应用包名/files/）
     * 不可以被其他的应用读取到，可以用来保存不能公开给其他应用的一些敏感数据如用户个人信息
     *
     * @param context
     * @return
     */
    private static File getFilesDir(Context context) {

        return context.getFilesDir();
    }
    /**
     * 获取内置存储下的缓存目录（/data/data/应用包名/cache/）
     * 不可以被其他的应用读取到，可以用来保存一些缓存文件如图片，当内置存储的空间不足时将系统自动被清除
     *
     * @param context
     * @return
     */
    private static File getCacheDir(Context context) {

        return context.getCacheDir();
    }
}
