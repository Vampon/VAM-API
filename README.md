<p align="center">
    <img src=./imgs/logo.png/>
</p>
<h1 align="center">VAM API 接口开放平台</h1>
<p align="center"><strong>面向开发者的API开放平台 🛠</strong></p>
<div align="center">
<a href="https://github.com/Vampon/VAM-API" target="_blank">
    <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/Vampon/VAM-API">
</a>
<a href="https://github.com/Vampon/VAM-API" target="_blank">
	<img alt="GitHub forks" src="https://img.shields.io/github/forks/Vampon/VAM-API">
</a>
    <img alt="Maven" src="https://raster.shields.io/badge/Maven-3.9.6-red.svg"/>
<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
        <img alt="" src="https://img.shields.io/badge/JDK-1.8+-green.svg"/>
</a>
    <img alt="SpringBoot" src="https://raster.shields.io/badge/SpringBoot-2.7+-green.svg"/>
</div>

## 项目简介🗺️

VAM API接口开放平台是一个为用户和开发者提供丰富的API接口调用服务的平台

🤝作为『管理员』，可以管理接口和用户，管理接口时可以修改接口信息、上线、添加、发布和修改下线接口。管理用户时可以用户信息、禁用用户和解除用户禁用等。

💻作为『开发者』，可以在线选择所需接口并通过导入[vamapi-client-sdk](https://github.com/Vampon/VAM-API/tree/main/vamapi-client-sdk)快速在项目中集成调用接口的客户端，通过配置客户端的用户资源快速调用接口，减少开发成本，简化开发。

😀作为『用户』，可以查看接口列表，选择感兴趣的接口查看接口文档，在线调用接口，快速查看接口的返回值，判断接口的实现功能。

## 网站导航 🧭

- [**VAM API 后端 🏘️**](https://github.com/Vampon/VAM-API/tree/main/vamapi-backend)
- [**VAM API 前端 🏘**️](https://github.com/Vampon/VAM-API/tree/main/vamapi-frontend-master)
- **[VAM API-SDK](https://github.com/Vampon/VAM-API/tree/main/vamapi-client-sdk)** 🛠
- **[VAM API 接口开放平台 🔗](https://vamapi.cloud/)**
- **[VAM API 开发者文档 📖](https://vampon.github.io/VAM-API-DOC)**

## 目录结构 📑


| 模块                    | 模块说明     |
| ----------------------- | ------------ |
| 🏘️vamapi-backend         | 后端服务模块 |
| 🏘️vamapi-frontend-master | 前端界面     |
| 🕸️vamapi-gateway         | 网关服务模块 |
| 🔗vamapi-interface       | 接口服务模块 |
| 🛠vamapi-client-sdk      | 开发者SDK    |

## 系统架构📋

<img src="./imgs/架构.png" alt="image-20240328223649236" style="zoom: 33%;" />

## 技术选型 🎯

## 后端

- SpringBoot
- SpringCloud-Gateway
- Dubbo
- Maven
- Nacos
- MySQL
- Lombok
- Junit
- Mybatis-plus
- Hutool
- Redis
- Redisson
- commons-email
- 微信支付
- 文心一言

## 前端

- Ant Design Pro Umi
- AntV
- React
- Umi
- axios
- VuePress
- Hexo

## 功能介绍 📋

| **功能**                                                     | **普通用户** | **管理员** |
| ------------------------------------------------------------ | ------------ | ---------- |
| 多语言版本SDK快速接入                                        | ✅            | ✅          |
| **[开发者API在线文档](https://vampon.github.io/VAM-API-DOC)** | ✅            | ✅          |
| 在线调试接口                                                 | ✅            | ✅          |
| 每日签到得V币                                                | ✅            | ✅          |
| 接口大厅搜索接口、浏览接口                                   | ✅            | ✅          |
| 邮箱验证码登录注册                                           | ✅            | ✅          |
| API密钥生成/下载/更新                                        | ✅            | ✅          |
| 更新头像                                                     | ✅            | ✅          |
| 绑定、换绑、解绑邮箱                                         | ✅            | ✅          |
| 订单/充值活动管理                                            | ❌            | ✅          |
| 用户管理、封号解封等                                         | ❌            | ✅          |
| 接口管理、接口发布审核、下架                                 | ❌            | ✅          |
| 统计数据智能BI分析                                           | ❌            | ✅          |

## 功能展示🎯

### 接口页面

![image-20240715214257562](./imgs/image-20240715214257562.png)

### 接口详细界面

![image-20240327215642944](./imgs/image-20240327215642944.png)

![image-20240327215657036](./imgs/image-20240327215657036.png)

![image-20240327215708073](./imgs/image-20240327215708073.png)

![image-20240327215718109](./imgs/image-20240327215718109.png)

### 接口信息智能BI统计分析

![屏幕截图(185)](./imgs/屏幕截图(185).png)

![屏幕截图(184)](./imgs/屏幕截图(184).png)

### 接口管理

![image-20240715214345461](./imgs/image-20240715214345461.png)

### 用户管理

![image-20240715214406075](./imgs/image-20240715214406075.png)

### 个人信息

![image-20240715214438475](./imgs/image-20240715214438475.png)

### 积分商城界面与活动管理

![image-20240715214320315](./imgs/image-20240715214320315.png)



![image-20240715214419980](./imgs/image-20240715214419980.png)

### 登录

![image-20240327215437732](./imgs/image-20240327215437732.png)















