# 1 简介

该项目灵感来源于，使用 `阿里云 kubernetes` 以及 `阿里云 容器镜像服务` 时，通过触发器可以让服务实现滚动更新的操作。

奈何知识受限，目前只掌握到 `docker swarm`，以及自用服务器上 `kubernetes` 没有太大必要。所以想通过 `docker swarm` 的方式实现类似滚动更新的操作。

此项目仅供学习参考。

# 2 项目说明

本项目可通过 `部署 jar 包` or `docker 容器中运行` 方式进行启动。

## 2.1 前提说明

由于 code 限制，目前的实现方式为 将镜像推送到 [阿里云容器镜像服务](https://cr.console.aliyun.com/cn-hangzhou/instance/dashboard)
，通过该平台的触发器回调 API，通知该项目对相应的服务进行滚动更新。

## 2.2 模块说明

### 2.2.1 用户模块

由于该项目涉及到回调，所以会暴露于外网。则需要搭建个的用户模块，由于不是主业务，较为简单，可注册、登录。

### 2.2.2 脚本模块

通过配置，实现滚动更新操作。也可创建服务、删除服务、更新服务。

## 2.3 待优化部分

* `docker 容器中运行` 首次需进入容器内部，需进行 [阿里云容器服务授权](https://cr.console.aliyun.com/cn-shanghai/instance/credentials)
* 创建服务、删除服务、修改服务 需进行消息推送

# 3 API 说明

`${baseUrl}` 为基地址。

## 3.1 鉴权模块

### 3.1.1 注册

```http request
POST ${baseUrl}/web/user/login/registered
```

* 请求参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| username | String | Y    | 用户名 |
| password | String | Y    | 密码   |
| name     | String | Y    | 名称   |
| phone    | String | Y    | 手机号 |

> WARN
>
> username 全局唯一

* 响应参数

| 参数名    | 类型    | 必选 | 描述     |
| --------- | ------- | ---- | -------- |
| id        | Long    | Y    | 用户ID   |
| role      | Enum    | Y    | 角色     |
| username  | String  | Y    | 用户名   |
| name      | String  | Y    | 名称     |
| phone     | String  | Y    | 手机号   |
| isDisable | Integer | Y    | 是否禁用 |

* 示例

```text
http request:

POST localhost:8080/web/user/login/registered

request body:

{
    "username": "aatrox",
    "password": "123456",
    "name": "aatrox",
    "phone": "15159845510"
}

response body:

{
    "code": 0,
    "message": "成功",
    "data": {
        "id": 25965724757803008,
        "role": "USER",
        "username": "aatrox",
        "name": "aatrox",
        "phone": "15159845510",
        "isDisable": 0
    }
}
```

### 3.1.2 登录

```http request
POST ${baseUrl}/web/user/login
```

* 请求参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| username | String | Y    | 用户名 |
| password | String | Y    | 密码   |

* 响应参数

| 参数名        | 类型   | 必选 | 描述          |
| ------------- | ------ | ---- | ------------- |
| id            | Long   | Y    | 用户ID        |
| role          | Enum   | Y    | 角色          |
| username      | String | Y    | 用户名        |
| name          | String | Y    | 名称          |
| phone         | String | Y    | 手机号        |
| authorization | String | Y    | authorization |
| refreshToken  | String | Y    | refreshToken  |

> TIPS
>
> `authorization` 用于 `request header authorization` 参数值。后续需要鉴权的API通过此参数进行鉴权，有效期为两小时，需配合 `refresh 登录 API` 进行定时刷新 `authorization`。
>
> `refreshToken` 用于 `refresh 登录 API` 请求参数，登录后会返回新的 `authorization` 和 `refreshToken`。

* 示例

```http request
POST localhost:8080/web/user/login

request body:

{
    "username": "aatrox",
    "password": "123456"
}

response body:

{
    "code": 0,
    "message": "成功",
    "data": {
        "id": 25965724757803008,
        "role": "USER",
        "username": "aatrox",
        "name": "aatrox",
        "phone": "15159845510",
        "authorization": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiVVNFUiIsIkFDVElWRSI6ImxvY2FsIiwiQVBQTElDQVRJT04iOiJjbG91ZCIsImlzcyI6IuW9reajruixqiIsIklEIjoiMjU5NjU3MjQ3NTc4MDMwMDgiLCJleHAiOjE2MTkwODQ2MzIsImlhdCI6MTYxOTA3NzQzMn0.xTgzRc6Oon2A6DqlZza5zohsh_Br-yh59QfhkF8Yf-s",
        "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJBQ1RJVkUiOiJsb2NhbCIsIkFQUExJQ0FUSU9OIjoiY2xvdWQiLCJpc3MiOiLlva3mo67osaoiLCJJRCI6IjI1OTY1NzI0NzU3ODAzMDA4IiwiZXhwIjoxNjE5NjgyMjMyLCJpYXQiOjE2MTkwNzc0MzJ9.mdxGGCsBgNMwVE4Hyxb2SFGt_uEL5I70A6qsxzqDTuI"
    }
}
```

### 3.1.3 refresh 登录

```http request
POST ${baseUrl}/web/user/login/refresh
```

* 请求参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| refreshToken | String | Y    | refreshToken |

* 响应参数

| 参数名        | 类型   | 必选 | 描述          |
| ------------- | ------ | ---- | ------------- |
| id            | Long   | Y    | 用户ID        |
| role          | Enum   | Y    | 角色          |
| username      | String | Y    | 用户名        |
| name          | String | Y    | 名称          |
| phone         | String | Y    | 手机号        |
| authorization | String | Y    | authorization |
| refreshToken  | String | Y    | refreshToken  |

> TIPS
>
> `authorization` 用于 `request header authorization` 参数值。后续需要鉴权的API通过此参数进行鉴权，有效期为两小时，需配合 `refresh 登录 API` 进行定时刷新 `authorization`。
>
> `refreshToken` 用于 `refresh 登录 API` 请求参数，登录后会返回新的 `authorization` 和 `refreshToken`。

* 示例

```http request
POST localhost:8080/web/user/login/refresh

request body:

{
    "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJBQ1RJVkUiOiJsb2NhbCIsIkFQUExJQ0FUSU9OIjoiY2xvdWQiLCJpc3MiOiLlva3mo67osaoiLCJJRCI6IjI1OTY1NzI0NzU3ODAzMDA4IiwiZXhwIjoxNjE5NjgyMjMyLCJpYXQiOjE2MTkwNzc0MzJ9.mdxGGCsBgNMwVE4Hyxb2SFGt_uEL5I70A6qsxzqDTuI"
}

response body:

{
    "code": 0,
    "message": "成功",
    "data": {
        "id": 25965724757803008,
        "role": "USER",
        "username": "aatrox",
        "name": "aatrox",
        "phone": "15159845510",
        "authorization": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiVVNFUiIsIkFDVElWRSI6ImxvY2FsIiwiQVBQTElDQVRJT04iOiJjbG91ZCIsImlzcyI6IuW9reajruixqiIsIklEIjoiMjU5NjU3MjQ3NTc4MDMwMDgiLCJleHAiOjE2MTkwODUyNDUsImlhdCI6MTYxOTA3ODA0NX0.v_XkxeVeK9porIWm5EG5XvVNfrPxDBXbsFTp4i9WkpU",
        "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJBQ1RJVkUiOiJsb2NhbCIsIkFQUExJQ0FUSU9OIjoiY2xvdWQiLCJpc3MiOiLlva3mo67osaoiLCJJRCI6IjI1OTY1NzI0NzU3ODAzMDA4IiwiZXhwIjoxNjE5NjgyODQ1LCJpYXQiOjE2MTkwNzgwNDV9.j6xXcSpiis7ENeV0aGgdWteYpwXTfrC4hbNGdyWQOVc"
    }
}
```

## 3.2 脚本模块

### 3.2.1 保存脚本信息（鉴权）

```http request
POST ${baseUrl}/web/user/script
```

* 请求参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| serviceName | String | Y    | 服务名称 |
| dockerComposeContent | String | Y    | docker compose 编排文件内容 |
| isAutoUpdate | Integer | Y    | 是否自动更新 |

* 示例

```http request
POST localhost:8080/web/user/script

request body:

{
    "serviceName": "halo",
    "dockerComposeContent": "xxx",
    "isAutoUpdate": 1
}

response body:

{
    "code": 0,
    "message": "成功"
}
```

### 3.2.2 修改脚本信息（鉴权）

```http request
PUT ${baseUrl}/web/user/script/${scriptId}
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| scriptId | Long | Y    | 脚本ID |

* 请求参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| dockerComposeContent | String | N    | docker compose 编排文件内容 |
| isAutoUpdate | Integer | N    | 是否自动更新 |

* 示例

```http request
POST localhost:8080/web/user/script/25970351624568832

request body:

{
    "dockerComposeContent": "yyy",
    "isAutoUpdate": 1
}
response body:

{
    "code": 0,
    "message": "成功"
}
```

### 3.2.3 删除脚本信息（鉴权）

```http request
DELETE ${baseUrl}/web/user/script/${scriptId}
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| scriptId | Long | Y    | 脚本ID |

* 示例

```http request
DELETE localhost:8080/web/user/script/25970351624568832

response body:

{
    "code": 0,
    "message": "成功"
}
```

### 3.2.4 查询脚本信息（鉴权）

```http request
GET ${baseUrl}/web/user/script
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| serviceName | String | N    | 服务名称 |

* 响应参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| serviceName | String | Y    | 服务名称 |
| dockerComposeContent | String | Y    | docker compose 编排文件内容 |
| baseStartScript | Array<String> | Y    | 基础运行服务脚本 |
| startScript | String | Y    | 运行服务脚本 |
| downScript | String | Y    | 关闭服务脚本 |
| updateScript | String | Y    | 更新服务脚本 |
| id | Long | Y    | 脚本ID |
| isAutoUpdate | Integer | Y    | 是否自动更新 |
| autoUpdateUrl | String | Y    | 自动更新回调地址 |

* 示例

```http request
GET localhost:8080/web/user/script

response body:

{
    "code": 0,
    "message": "成功",
    "data": {
        "content": [
            {
                "serviceName": "halo",
                "dockerComposeContent": "xxx",
                "baseStartScript": [
                    "rm -rf ~/docker-deploy-script/halo/docker-compose.yml",
                    "mkdir -p ~/docker-deploy-script/halo",
                    "echo \"xxx\" > ~/docker-deploy-script/halo/docker-compose.yml"
                ],
                "startScript": "docker stack deploy -c ~/docker-deploy-script/halo/docker-compose.yml halo",
                "downScript": "docker stack down halo",
                "updateScript": "docker service update --image {0}:{1} halo_halo",
                "id": 25972445886693376,
                "isAutoUpdate": 1,
                "autoUpdateUrl": "http://localhost:8080/api/autoUpdate/25965724757803008/halo"
            }
        ],
        "totalPages": 1,
        "totalElements": 1,
        "pageNumber": 1,
        "pageSize": 20
    }
}
```

### 3.2.5 查询脚本详情（鉴权）

```http request
GET ${baseUrl}/web/user/script/{scriptId}
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| scriptId | Long | Y    | 脚本ID |

* 响应参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| serviceName | String | Y    | 服务名称 |
| dockerComposeContent | String | Y    | docker compose 编排文件内容 |
| baseStartScript | Array<String> | Y    | 基础运行服务脚本 |
| startScript | String | Y    | 运行服务脚本 |
| downScript | String | Y    | 关闭服务脚本 |
| updateScript | String | Y    | 更新服务脚本 |
| id | Long | Y    | 脚本ID |
| isAutoUpdate | Integer | Y    | 是否自动更新 |
| autoUpdateUrl | String | Y    | 自动更新回调地址 |

* 示例

```http request
GET localhost:8080/web/user/script/25972445886693376

response body:

{
    "code": 0,
    "message": "成功",
    "data": {
        "serviceName": "halo",
        "dockerComposeContent": "xxx",
        "baseStartScript": [
            "rm -rf ~/docker-deploy-script/halo/docker-compose.yml",
            "mkdir -p ~/docker-deploy-script/halo",
            "echo \"xxx\" > ~/docker-deploy-script/halo/docker-compose.yml"
        ],
        "startScript": "docker stack deploy -c ~/docker-deploy-script/halo/docker-compose.yml halo",
        "downScript": "docker stack down halo",
        "updateScript": "docker service update --image {0}:{1} halo_halo",
        "id": 25972445886693376,
        "isAutoUpdate": 1,
        "autoUpdateUrl": "http://localhost:8080/api/autoUpdate/25965724757803008/halo"
    }
}
```

### 3.2.6 创建服务（鉴权）

```http request
PUT ${baseUrl}/web/user/script/{scriptId}/createService
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| scriptId | Long | Y    | 脚本ID |

* 响应参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| data | String | Y    | 指令执行结果 |

* 示例

```http request
PUT localhost:8080/web/user/script/25972445886693376/createService

response body:

{
    "code": 0,
    "message": "成功",
    "data": "Creating service halo_halo"
}
```

### 3.2.7 删除服务（鉴权）

```http request
PUT ${baseUrl}/web/user/script/{scriptId}/removeService
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| scriptId | Long | Y    | 脚本ID |

* 响应参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| data | String | Y    | 指令执行结果 |

* 示例

```http request
PUT localhost:8080/web/user/script/25972445886693376/removeService

response body:

{
    "code": 0,
    "message": "成功",
    "data": "Removing service halo_halo"
}
```

### 3.2.8 更新服务（鉴权）

```http request
PUT ${baseUrl}/web/user/script/{scriptId}/updateService
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| scriptId | Long | Y    | 脚本ID |

* 请求参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| repository | String | Y    | 镜像 |
| tag | String | Y    | 标签 |

* 响应参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| data | Array<String> | Y    | 指令执行结果 |

* 示例

```http request
PUT localhost:8080/web/user/script/25972445886693376/updateService

request body:

{
    "repository": "halohub/halo",
    "tag": "1.4.7"
}

response body:

{
    "code": 0,
    "message": "成功",
    "data": [
        "halo_halo",
        "overall progress: 0 out of 1 tasks",
        "1/1:  ",
        "overall progress: 1 out of 1 tasks",
        "verify: Waiting 5 seconds to verify that tasks are stable...",
        "verify: Waiting 4 seconds to verify that tasks are stable...",
        "verify: Waiting 3 seconds to verify that tasks are stable...",
        "verify: Waiting 2 seconds to verify that tasks are stable...",
        "verify: Waiting 1 seconds to verify that tasks are stable...",
        "verify: Service converged"
    ]
}
```

## 3.3 API 模块

### 3.3.1 镜像自动更新（阿里云触发器调用）

```http request
POST ${baseUrl}/api/autoUpdate/{userId}/{serviceName}
```

* URL参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| userId | Long | Y    | 用户ID |
| serviceName | String | Y    | 服务名称 |

* 请求参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| push_data.tag | String | Y    | 标签 |
| repository.name | String | Y    | 镜像名称 |
| repository.namespace | String | Y    | 命名空间 |
| repository.region | String | Y    | 地区 |

* 响应参数

| 参数名   | 类型   | 必选 | 描述   |
| -------- | ------ | ---- | ------ |
| data | Array<String> | Y    | 指令执行结果 |

* 示例

```http request
POST localhost:8080/api/autoUpdate/25895455430971392/demo

request body:

{
    "push_data": {
        "digest": "sha256:179b742913762a68ae7344a927b105f951e7a82adc544a92bf212f1dee3f2656",
        "pushed_at": "2021-04-22 13:36:43",
        "tag": "prod-1.4.7"
    },
    "repository": {
        "date_created": "2021-04-18 13:15:40",
        "name": "halo",
        "namespace": "pongsky",
        "region": "cn-shanghai",
        "repo_authentication_type": "NO_CERTIFIED",
        "repo_full_name": "pongsky/halo",
        "repo_origin_type": "NO_CERTIFIED",
        "repo_type": "PRIVATE"
    }
}

response body:

{
    "code": 0,
    "message": "成功",
    "data": [
        "demo_demo",
        "overall progress: 0 out of 1 tasks",
        "1/1:  ",
        "1/1: No such image: registry.cn-shanghai.aliyuncs.com/pongsky/halo:prod-1.4.7",
        "overall progress: 1 out of 1 tasks",
        "verify: Waiting 5 seconds to verify that tasks are stable...",
        "verify: Waiting 4 seconds to verify that tasks are stable...",
        "verify: Waiting 3 seconds to verify that tasks are stable...",
        "verify: Waiting 2 seconds to verify that tasks are stable...",
        "verify: Waiting 1 seconds to verify that tasks are stable...",
        "verify: Service converged",
        "image registry.cn-shanghai.aliyuncs.com/pongsky/halo:prod-1.4.7 could not be accessed on a registry to record",
        "its digest. Each node will access registry.cn-shanghai.aliyuncs.com/pongsky/halo:prod-1.4.7 independently,",
        "possibly leading to different nodes running different",
        "versions of the image."
    ]
}
```

# 4 使用说明

1. 部署项目
2. 调用 注册 API，注册信息
3. 调用 登录 API，获取访问凭证
4. 调用 创建脚本 API，系统添加脚本信息
5. 调用 查询脚本 API，获取 脚本ID、自动更新URL 等信息
6. 调用 创建服务 API，进行服务部署
7. 将 自动更新 URL，配置到 阿里云镜像的触发器地址，并保证能外网访问该 URL

> TODO 后续补充图文教程