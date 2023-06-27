# StaticAnalyzer-Backend

南京大学软件工程大实验——静态分析工具业务后端

## Develop

修改项目配置为develop

resources/application.properties
```
spring.profiles.active=dev
```

编译protobuf并生成gRPC部分的java源码

`mvnw protobuf:compile`

`mvnw protobuf:compile-custom`

进行测试的步骤
- 开启MySQL Server于端口3306，账户root，密码12345678
- 开启Redis Server于端口6379
- 开启StaticAnalyzer-Algorithm于端口8081

`mvnw compile`

## Deploy

修改项目配置为product

resources/application.properties
```
spring.profiles.active=prod
```

预处理步骤同上，然后执行

`mvnw package`
