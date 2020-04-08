// package com.rayshine.update.api;
//
// import okhttp3.Interceptor;
// import okhttp3.OkHttpClient;
// import okhttp3.Request;
// import okhttp3.logging.HttpLoggingInterceptor;
// import retrofit2.Retrofit;
// import retrofit2.converter.gson.GsonConverterFactory;
//
// public class RayshineClient {
//
//     private final String TAG = getClass().getName();
//     private static final String BASE_URL = "http://app.rayshine.cc";
//
//     private static RayshineService rayshineService;
//
//     private RayshineClient(){}
//
//     public static RayshineService getRayshineService() {
//
//         if(rayshineService == null){
//             OkHttpClient.Builder builder = new OkHttpClient.Builder();
//
//             HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//             loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//             Interceptor customInterceptor = chain -> {
//                 Request original = chain.request();
//
//                 Request request = original.newBuilder()
//                         .header("Content-Type","application/json")
//                         .method(original.method(), original.body())
//                         .build();
//                 return chain.proceed(request);
//             };
//             builder.addInterceptor(loggingInterceptor);
//             builder.addInterceptor(customInterceptor);
//
//             Retrofit retrofit = new Retrofit.Builder()
//                     .baseUrl(BASE_URL)
//                     .addConverterFactory(GsonConverterFactory.create())
//                     .client(builder.build())
//                     .build();
//             rayshineService = retrofit.create(RayshineService.class);
//         }
//         return rayshineService;
//     }
//
//
//
// }
