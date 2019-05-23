package org.gosky.aroundight.model

/**
 * @Auther: guozhong
 * @Date: 2019-05-21 23:12
 * @Description:
 */

data class ResultEntity(
        val code: String,
        val url: String
)

//fun ResultEntity.success(url: String): ResultEntity {
//    return ResultEntity("success", url)
//}