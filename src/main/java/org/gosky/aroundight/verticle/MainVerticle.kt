package org.gosky.aroundight.verticle

import io.reactivex.Observable
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.reactivex.ext.mongo.MongoClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.gosky.aroundight.ext.success
import org.gosky.aroundight.http.UploadService
import org.gosky.aroundight.model.ResultEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
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
        router.post("/api/upload/:platform").handler { upload(it) }
//        router.post("/api/upload/juejin").handler { smms(it) }
        router.errorHandler(500) { routerContext ->
            routerContext.failure().printStackTrace()
        }
    }


    private fun getImage(routingContext: RoutingContext) {
        val name = routingContext.pathParam("name")
        mongo.find("images", JsonObject().put("name", name)) {
            if (it.succeeded()) {
//                routingContext.reroute(it.result().first().getString("url"))
                routingContext.response().putHeader("location", it.result().first().getString("url")).setStatusCode(302).end()
            }
        }

    }

    private fun upload(routingContext: RoutingContext) {
        val platform = routingContext.pathParam("platform")

        val upload = routingContext.fileUploads().first()

        val name = upload.uploadedFileName().replace("file-uploads/", "")

        val body = RequestBody.create(MediaType.parse("multipart/form-data"), File(upload.uploadedFileName()))

        when (platform) {
            "smms" -> smms(name, body)
//            "juejin" ->
            else -> throw RuntimeException("unKnow platform!")
        }.subscribe {
            routingContext.success(ResultEntity("success", it))
        }

    }

    private fun smms(fileName: String, requestBody: RequestBody): Observable<String> {

//
        val part = MultipartBody.Part.createFormData("smfile", fileName, requestBody)

        return uploadService.smms(part)
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


}
