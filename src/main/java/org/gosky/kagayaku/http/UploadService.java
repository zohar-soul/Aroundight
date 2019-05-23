package org.gosky.kagayaku.http;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {

    @POST("https://sm.ms/api/upload")
    @Multipart
    @Headers("User-Agent: PostmanRuntime/7.11.0")
    Call<ResponseBody> smms(@Part MultipartBody.Part smfile);
}