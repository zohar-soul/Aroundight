package org.gosky.aroundight.config

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.IndexOptions
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.mongo.MongoClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

/**
 * @Auther: guozhong
 * @Date: 2019-05-28 18:12
 * @Description:
 */

@Component
class MongoDbConfig {

    @Autowired
    private lateinit var vertx: Vertx

    @Value("\${mongo.host}")
    private lateinit var host:String

    @Bean
    fun mongoDb(): MongoClient {
        val config = JsonObject()
                .put("host", host)
                .put("port", 27017)

        val mongoClient = MongoClient.createShared(vertx, config)

        mongoClient.rxCreateIndexWithOptions("images", JsonObject().put("name", 1).put("type", 1), IndexOptions().unique(true)).subscribe()
        return mongoClient

    }
}