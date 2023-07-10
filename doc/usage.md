# 2 项目使用说明

项目配置文件

*resources/application.properties*

## 2.1 开发项目

### 2.1.1 前置条件

修改项目配置为develop

`spring.profiles.active=dev`

编译protobuf

`mvnw protobuf:compile`

`mvnw protobuf:compile-custom`

开启MySQL Server
  - 端口3306
  - 根用户密码12345678

开启Redis Server
  - 端口6379

开启StaticAnalyzer-Algorithm服务
  - 端口8081

### 2.1.2 测试

`mvnw compile`

**仅测试** `mvnw test`

## 2.2 部署项目

修改项目配置为product，其它同上

`spring.profiles.active=prod`

**编译测试并打包** `mvnw package`

## 2.3 项目API

统一返回格式（data的内容即返回的数据）

```json
{
  "code": 0,
  "data": "object",
  "msg": "string"
}
```

### 2.3.1 POST /login

用户登录接口

**前端请求格式**（其中用户名长度为2-8的字符/数字/下划线组合，密码为长度8-20的任意组合）

```json
{
  "id": 0,
  "password": "string",
  "username": "string"
}
```

**返回数据格式**
```json
"data" : {
  "token": "string",
  "user": {
    "id" : 0,
    "password": "string",
    "username": "string"
  }
}
```

### 2.3.2 POST /user

用户注册接口

**前端请求格式**
```json
{
  "id": 0,
  "password": "string",
  "username": "string"
}
```

**返回数据格式**
```json
"data": {
  "token": "string",
  "user": {
    "id": 0,
    "password": "string",
    "username": "string"
  }
}
```

### 2.3.3 GET /user/{uid}

用户查询接口

前端请求无需附带额外数据，返回格式如下。

```json
"data": {
  "id": 0,
  "password": "string",
  "username": "string"
}
```

### 2.3.4 PUT /user/{uid}

用户修改接口

前端请求只需一个字符串表示需要修改的用户密码，返回的data字段为空。

### 2.3.5 POST /playground/test

上传单个文件

**前端请求格式**
```json
{
  "code": "string",
  "config": "string"
}
```

**返回数据格式**(anlayseResults是一个列表)
```json
"data": {
  "analyseResults": [
    {
      "endColumn": 0,
      "endLine": 0,
      "message": "string",
      "severity": "Pass",
      "startColumn": 0,
      "startLine": 0,
      "type": "string"
    }
  ],
  "name": "string",
  "src": "string"
}
```

### 2.3.5 GET /user/{uid}/project

项目查询接口

前端请求无需附带额外数据，返回格式如下（data和analyseBrief都是列表）。

```json
"data": [
  {
    "analyseBrief": [
      {
        "analyseType": "UninitializedVariable",
        "status": "Warning"
      }
    ],
    "config": "string",
    "id": 0,
    "status": "Complete",
    "timestamp": "2023-07-10T16:58:52.991Z"
  }
]
```

### 2.3.6 POST /user/{uid}/project

项目上传接口

**前端请求格式**（表单）
```
config="string"
sourceCode=file
uid=integer
```

返回的data字段为空。

### 2.3.7 GET /user/{uid}/project/{pid}

项目目录查询接口

前端请求无需附带额外数据，返回格式如下（directories是递归的）。

**返回数据格式**
```json
"data": {
  "directories": {},
  "files": {
    "additionalProp1": {
      "name": "string",
      "src": "string"
    },
    "additionalProp2": {
      "name": "string",
      "src": "string"
    },
    "additionalProp3": {
      "name": "string",
      "src": "string"
    }
  },
  "name": "string"
}
```

### 2.3.8 GET /user/{uid}/project/{pid}/file

文件查询接口

前端请求无需附带额外数据，返回格式如下（analyseResults是一个列表）。

**返回数据格式**
```json
"data": {
  "analyseResults": [
    {
      "endColumn": 0,
      "endLine": 0,
      "message": "string",
      "severity": "Pass",
      "startColumn": 0,
      "startLine": 0,
      "type": "string"
    }
  ],
  "name": "string",
  "src": "string"
}
```

### 2.3.9 GET /user/{uid}/project/{pid}/problem

问题查询接口

前端请求无需附带额外数据，返回格式如下（data是一个列表）。

**返回数据格式**
```json
"data": [
  {
    "endColumn": 0,
    "endLine": 0,
    "file": "string",
    "message": "string",
    "severity": "Pass",
    "startColumn": 0,
    "startLine": 0,
    "type": "string"
  }
]
```

## 2.4 其它文档

**swagger-ui** 在开发模式下，访问localhost:8080/swagger-ui.html

**javadoc** `mvnw javadoc:jar`
