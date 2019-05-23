package org.gosky.kagayaku.verticle

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.gosky.kagayaku.ext.success
import org.gosky.kagayaku.http.UploadService
import org.gosky.kagayaku.model.ResultEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


/**
 * @Auther: guozhong
 * @Date: 2019-05-21 11:20
 * @Description:
 */

@Component
class MainVerticle : RestVerticle() {

    @Autowired
    private lateinit var uploadService: UploadService
    @Autowired
    private lateinit var mongo: MongoClient

    override fun initRouter() {
        router.get("/api/image/:name").handler { getImage(it) }
        router.post("/api/upload/smms").handler { smms(it) }

    }


    private fun getImage(routingContext: RoutingContext) {
        val name = routingContext.pathParam("name")
        mongo.find("images", JsonObject().put("name", name)) {
            if (it.succeeded()) {
//                routingContext.reroute(it.result().first().getString("url"))
                routingContext.response().putHeader("location",it.result().first().getString("url")).setStatusCode(302).end()
            }
        }

    }

    private fun smms(routingContext: RoutingContext) {

        val upload = routingContext.fileUploads().first()

        val name = upload.uploadedFileName().replace("file-uploads/","")

        val body = RequestBody.create(MediaType.parse("multipart/form-data"), File(upload.uploadedFileName()))
//
        val part = MultipartBody.Part.createFormData("smfile", upload.uploadedFileName(), body)

        uploadService.smms(part).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace();
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody?>) {
                //删除文件
                File("file-uploads").deleteRecursively()

                val jsonObject = JsonObject(response.body()?.string())
                if (jsonObject.getString("code") == "success") {

                    val url = jsonObject.getJsonObject("data").getString("url")

                    val document = JsonObject()
                            .put("name", name)
                            .put("url", url)
                            .put("type", "smms")

                    mongo.save("images", document) { res ->
                        if (res.succeeded()) {
                            routingContext.success(ResultEntity("success", name))
                        } else {
                            println(res.result())
                        }
                    }

                } else {

                }

            }
        })

    }
}
