package org.gosky.aroundight

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.gosky.aroundight.http.UploadService
import org.gosky.aroundight.verticle.MainVerticle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.annotation.PostConstruct


@SpringBootApplication
class KagayakuApplication {

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
    fun retrofit(): UploadService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY

        }
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://have.no.baseurl")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(UploadService::class.java)
    }

    @Bean
    fun mongoDb(): MongoClient {
        val config = JsonObject()
                .put("host", "127.0.0.1")
                .put("port", 27017)

        return MongoClient.createShared(vertx, config)

    }


}

fun main(args: Array<String>) {
    SpringApplication.run(KagayakuApplication::class.java, *args)
}

