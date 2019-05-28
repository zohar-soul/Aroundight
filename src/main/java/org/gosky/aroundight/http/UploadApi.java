package org.gosky.aroundight.http;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApi {

    @POST("https://sm.ms/api/upload")
    @Multipart
    @Headers("User-Agent: PostmanRuntime/7.11.0")
    Observable<ResponseBody> smms(@Part MultipartBody.Part smfile);

    @POST("https://cdn-ms.juejin.im/v1/upload?bucket=gold-user-assets")
    @Multipart
    @Headers("User-Agent: PostmanRuntime/7.11.0")
    Observable<ResponseBody> juejin(@Part MultipartBody.Part file);

    @POST("http://changyan.sohu.com/api/2/comment/attachment")
    @Multipart
    @Headers("User-Agent: PostmanRuntime/7.11.0")
    Observable<ResponseBody> souhu(@Part MultipartBody.Part file);

}