package org.gosky.aroundight.service

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.ext.mongo.MongoClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.gosky.aroundight.http.UploadApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

/**
 * @Auther: guozhong
 * @Date: 2019-05-27 23:28
 * @Description:
 */


@Service
class UploadService {

    @Autowired
    private lateinit var uploadApi: UploadApi

    @Autowired
    private lateinit var mongo: MongoClient

    fun smms(fileName: String, requestBody: RequestBody): Observable<String> {

//
        val part = MultipartBody.Part.createFormData("smfile", fileName, requestBody)

        return uploadApi.smms(part)
                .map { response ->
                    File("file-uploads").deleteRecursively()
                    val jsonObject = JsonObject(response.string())
                    if (jsonObject.getString("code") == "success") {
                        val url = jsonObject.getJsonObject("data").getString("url")

                        val document = JsonObject()
                                .put("name", fileName)
                                .put("url", url)
                                .put("type", "smms")
                        return@map document
                    } else {
                        throw RuntimeException("smms upload faild!")
                    }
                }
                .flatMap { document ->
                    return@flatMap mongo.rxSave("images", document).map { document.getString("name") }.toObservable()
                }

    }


    fun juejin(fileName: String, requestBody: RequestBody): Observable<String> {

//
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

        return uploadApi.juejin(part)
                .subscribeOn(Schedulers.io())
                .map { response ->
                    File("file-uploads").deleteRecursively()
                    val jsonObject = JsonObject(response.string())
                    if (jsonObject.getString("m") == "ok") {
                        val url = jsonObject.getJsonObject("d").getJsonObject("url").getString("https")

                        val document = JsonObject()
                                .put("name", fileName)
                                .put("url", url)
                                .put("type", "juejin")
                        return@map document
                    } else {
                        throw RuntimeException("smms upload faild!")
                    }
                }
                .flatMap { document ->
                    return@flatMap mongo.rxSave("images", document).map { document.getString("name") }.toObservable()
                }

    }
}
