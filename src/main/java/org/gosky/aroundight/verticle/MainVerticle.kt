package org.gosky.aroundight.verticle

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.reactivex.ext.mongo.MongoClient
import mu.KotlinLogging
import okhttp3.MediaType
import okhttp3.RequestBody
import org.gosky.aroundight.ext.success
import org.gosky.aroundight.model.ResultEntity
import org.gosky.aroundight.service.UploadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File


/**
 * @Auther: guozhong
 * @Date: 2019-05-21 11:20
 * @Description:
 */
private val logger = KotlinLogging.logger {}

@Component
class MainVerticle : RestVerticle() {

    @Autowired
    private lateinit var mongo: MongoClient

    @Autowired
    private lateinit var uploadService: UploadService

    override fun initRouter() {
        router.get("/health").handler { it.success("ok!") }
        router.get("/api/image/:uuid").handler { getImage(it) }
        router.post("/api/upload/:platform").handler { upload(it) }
//        router.post("/api/upload/juejin").handler { smms(it) }
        router.errorHandler(500) { routerContext ->
            logger.error { routerContext.failure().message }
        }
    }


    private fun getImage(routingContext: RoutingContext) {
        val name = routingContext.pathParam("uuid")
        mongo.find("images", JsonObject().put("uuid", name)) {
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
            "smms" -> uploadService.smms(name, body)
            "juejin" -> uploadService.juejin(name, body)
            "souhu" -> uploadService.souhu(name, body)
            else -> throw RuntimeException("unKnow platform!")
        }.flatMap { document ->
            return@flatMap mongo.rxSave("images", document).map { document.getString("uuid") }.toObservable()
        }.subscribe {
            routingContext.success(ResultEntity("success", "http://aroundight.gosky.xyz/api/image/$it"))
        }

    }


}
