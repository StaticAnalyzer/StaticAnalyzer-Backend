# 3 测试报告

## 3.1 测试概要

### 3.1.1 测试范围

除Config与Annotation以外的其它所有类。

单元测试将覆盖Utils、Service。
其中，Entity、Mapper的使用将会在测试样例中涉及；
集成测试将覆盖Controller和Interceptor，以及全部的单元测试。

### 3.1.2 测试工具

单元测试 **junit**

集成测试 **thunder client**

## 3.2 测试内容

### 3.2.1 单元测试

#### 3.2.1.1 令牌

*utils/JwtTest.java*

**测试接口**
- 令牌生成
  - 接收：密钥、过期时间、用户id
  - 生成：令牌字符串
- 令牌解析
  - 接收：密钥、令牌字符串
  - 生成：用户id

**步骤**
- 随机化生成100组密钥、过期时间、用户id
- 用100组随机数据生成令牌并解析
- 对每一组数据，比较用户id与解析id

令牌测试结果

![令牌测试结果](./images/jwt_test.PNG)

#### 3.2.1.2 打包

*utils/TarGzTest.java*

**测试接口**
- targz解压缩
  - 接收：targz字节串
  - 生成：路径到文件的映射
- 单文件压缩
  - 接收：单个文件
  - 生成：targz字节串

**步骤**
- 读取cpython.tar.gz，对其进行解压得所有文件名称与其内容的映射
- 遍历cpython目录，检查映射中是否存在对应的文件且内容正确
- 读取cpython/Python/ast.c
- 压缩ast.c，然后解压，检查该文件内容是否未改变

打包测试结果

![打包测试结果](./images/targz_test.PNG)

单文件打包测试结果

![单文件打包测试结果](./images/single_targz_test.PNG)

#### 3.2.1.3 用户系统

*service/UserServiceTest.java*

**测试接口**
- 令牌签发
  - 接收：用户id
  - 生成：令牌字符串
- 令牌验证
  - 接收：用户id、令牌字符串
  - 生成：如果验证失败，生成ServiceError
- 用户格式
  - 接收：用户信息（名称和密码）
  - 生成：如果用户名或密码格式错误，返回false
- 用户创建
  - 接收：用户信息（名称和密码）
- 用户查询
  - 接收：用户id或用户名
  - 生成：用户（名称和密码）
- 用户修改
  - 接收：密码

**步骤**
- 创建一个格式正确的gooduser
- 用gooduser签发令牌并验证
- 随机生成100个格式非法的用户并验证
- 随机生成50个随机用户。如果非法，跳过之；如果合法，先创建用户，然后将密码反转再修改用户

令牌签发测试结果

![令牌签发测试结果](./images/user_sign.PNG)

用户格式测试结果

![用户格式测试结果](./images/user_format.PNG)

用户修改测试结果

![用户修改测试结果](./images/user_update.PNG)

#### 3.2.1.4 分析系统

*service/ProjectServiceTest.java*

**测试接口**
- 分析算法结果
  - 接收：算法回传结果(json)、文件名到文件分析对象的映射
  - 生成：给映射中的文件分析对象传递分析后的结果
- 查看用户上传
  - 接收：用户id
  - 生成：该用户所属的代码包信息列表
- 查看代码包细节
  - 接收：代码包id
  - 生成：文件名到文件分析对象的映射

**步骤**
- 基于cpython.tar.gz手动创建一个代码包对象
- 读取cpython_out.json模拟算法端返回的结果
- 分析代码包对象并检查异常
- 读取数据库中user0拥有的所有代码包
- 遍历这些代码包，查看它们的细节并于数据库进行比对

代码包
- include
  - a.h
  - b.h
- src
  - a.c
  - b.cpp
- main.cpp

分析json测试结果

![分析json测试结果](./images/proj_analysis.PNG)

查看代码包列表测试结果

![查看代码包列表测试结果](./images/proj_query.PNG)

查看代码包细节测试结果

![查看代码包细节测试结果](./images/proj_fetch.PNG)

### 3.2.2 集成测试

#### 3.2.2.1 用户系统

使用thunder client发送HTTP请求模拟触发REST API

用户登录

```json
{
  "code": 0,
  "msg": "登录成功",
  "data": {
    "user": {
      "id": 1,
      "username": "test0",
      "password": "test0@staticanalyzer"
    },
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjg4MTQ4OTQwLCJleHAiOjE2ODgxNTA3NDB9.nuww1QlEjYGDmQtKnNL6-6ZmU5h7rmqPO8Y7ZLcfRONUAhfWlmpzhnCdT0gayhsUhTlrxc2gUa68y5D8wHd_1g"
  }
}
```

查看密码

```json
{
  "code": 0,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "username": "test0",
    "password": "test0@staticanalyzer"
  }
}
```

修改密码为hahahaha

```json
{
  "code": 0,
  "msg": "修改成功",
  "data": null
}
{
  "code": 0,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "username": "test0",
    "password": "hahahaha"
  }
}
```

#### 3.2.2.2 分析系统

使用thunder client发送HTTP请求模拟触发REST API

完成代码包列表查看

```json
{
  "code": 0,
  "msg": "查询成功",
  "data": [
    {
      "id": 2,
      "timestamp": "2023-06-30 17:47:26",
      "status": "Complete",
      "config": "Framework{level=0}",
      "analyseBrief": [
        {
          "analyseType": "UninitializedVariable",
          "status": "Warning"
        }
      ]
    },
    {
      "id": 1,
      "timestamp": "2023-06-30 17:47:26",
      "status": "Complete",
      "config": "Framework{level=0}",
      "analyseBrief": [
        {
          "analyseType": "UseBeforeDef",
          "status": "Warning"
        },
        {
          "analyseType": "ArrayOutOfBound",
          "status": "Error"
        }
      ]
    }
  ]
}
```

简报查看（简报不显示源文件内容）

```json
{
  "code": 0,
  "msg": "目录查询成功",
  "data": {
    "name": "root",
    "directories": {
      "include": {
        "name": "include",
        "directories": {},
        "files": {
          "a.h": {
            "name": "a.h",
            "src": null,
            "severity": "Pass"
          },
          "b.h": {
            "name": "b.h",
            "src": null,
            "severity": "Pass"
          }
        }
      },
      "src": {
        "name": "src",
        "directories": {},
        "files": {
          "a.c": {
            "name": "a.c",
            "src": null,
            "severity": "Pass"
          },
          "b.cpp": {
            "name": "b.cpp",
            "src": null,
            "severity": "Pass"
          }
        }
      }
    },
    "files": {
      "main.cpp": {
        "name": "main.cpp",
        "src": null,
        "severity": "Warning"
      }
    }
  }
}
```

文件查看

```json
{
  "code": 0,
  "msg": "文件查询成功",
  "data": {
    "name": "main.cpp",
    "src": "#include \"a.h\"\r\n#include \"b.h\"\r\n#include <iostream>\r\n\r\nint main()\r\n{\r\n    std::cout << testA2() << std::endl;  // 0\r\n    std::cout << testA1(0) << std::endl; // 1\r\n    TestB b;\r\n    std::cout << b.testB1(0) << std::endl; // 2\r\n    std::cout << b.testB2() << std::endl;  // 3\r\n    \r\n    int a;\r\n    std::cout << a << std::endl;    // Uninitiated variable\r\n}",
    "analyseResults": [
      {
        "startLine": 13,
        "startColumn": 5,
        "endLine": 13,
        "endColumn": 9,
        "severity": "Warning",
        "message": "Uninitialized variable test.",
        "type": "UninitializedVariable"
      }
    ]
  }
}
```

问题查看

```json
{
  "code": 0,
  "msg": "问题查询成功",
  "data": [
    {
      "startLine": 13,
      "startColumn": 5,
      "endLine": 13,
      "endColumn": 9,
      "severity": "Warning",
      "message": "Uninitialized variable test.",
      "type": "UninitializedVariable",
      "file": "main.cpp"
    }
  ]
}
```

## 3.3 测试总结

本项目测试涵盖全面，随机性强，综合单元测试和集成测试，便于找出漏洞。
缺点是对性能的评测较少。
