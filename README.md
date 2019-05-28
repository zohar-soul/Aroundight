# Aroundight

 **基于vert.x & kotlin & rxjava 的高性能图床**
 
 ### api
 
 #### api上传
 
 * /api/upload/:platform 
 * `:name`目前支持 `juejin`,`souhu`,`sm.ms`
 
 返回结果:
  ```
 {
     "code": "success",
     "uuid": "cdc0e5bd-effe-4049-9a8d-911142bf84c5"
 }
 ```
 
 #### api获取图片
 
 * /api/image/:uuid
 * `:uuid` 为上传时返回的`url`
