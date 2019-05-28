package org.gosky.aroundight

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.IndexOptions
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.mongo.MongoClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.gosky.aroundight.http.UploadApi
import org.gosky.aroundight.verticle.MainVerticle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.annotation.PostConstruct


@SpringBootApplication
class AroundightApplication {

    @Autowired
    private lateinit var vertx: Vertx

    @Autowired
    private lateinit var apiVerticle: MainVerticle

    @PostConstruct
    fun deployVerticle() {
        //        vertx.deployVerticle(staticServer);
        vertx.deployVerticle(apiVerticle)
    }

    @Bean
    fun vertx(): Vertx {
        return Vertx.vertx()
    }

    @Bean
    fun retrofit(): UploadApi {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY

        }
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://have.no.baseurl")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(UploadApi::class.java)
    }

    @Bean
    fun mongoDb(): MongoClient {
        val config = JsonObject()
                .put("host", "127.0.0.1")
                .put("port", 27017)

        val mongoClient = MongoClient.createShared(vertx, config)

        mongoClient.rxCreateIndexWithOptions("images", JsonObject().put("name", 1).put("type", 1), IndexOptions().unique(true)).subscribe()
        return mongoClient

    }


}

fun main(args: Array<String>) {
    SpringApplication.run(AroundightApplication::class.java, *args)
}

