# TradingPlatform

#### 介绍
本项目是一个前后端分离的电商平台，采用Vue构建前端界面，Spring Boot搭建后端服务，并引入Redis提升系统性能。平台分为管理员和用户两大角色，管理员可通过后台系统进行商品添加、修改等操作，并实时记录操作日志，便于追踪与管理。用户界面则提供购物车、付费及商品选择等功能，确保用户能够流畅地完成购物流程。

This project is a front-end and back-end separated e-commerce platform. The front-end interface is built with Vue, the back-end service is constructed with Spring Boot, and Redis is introduced to improve system performance. The platform is divided into two major roles: administrators and users. Administrators can add, modify and other operations on commodities through the background system, and record operation logs in real time for easy tracking and management. The user interface provides functions such as shopping cart, payment and commodity selection to ensure that users can complete the shopping process smoothly.

#### 软件架构
前端：Vue3 + Element Plus + ECharts
后端：Spring Boot + MyBatis Plus
数据库：MySQL（业务数据） + Redis（缓存/会话管理）
部署：Nginx（反向代理）

##### 1. 管理员系统模块
功能亮点：
数据可视化看板（ECharts动态图表）
商品全生命周期管理（上架/编辑/库存跟踪）
订单状态实时监控与操作日志审计
![管理员主界面](https://foruda.gitee.com/images/1746713931891703264/fc675545_13265925.png "屏幕截图 2025-05-07 220715.png")
![管理员子界面](https://cdn.jsdelivr.net/gh/Frimiku/picture_cloud/trading1.png)


##### 2. 用户端模块
功能亮点：
JWT令牌无状态登录认证
购物车实时同步（Redis缓存保障高性能）
订单全流程追踪（待支付/发货中/已完成）
![用户主界面](https://cdn.jsdelivr.net/gh/Frimiku/picture_cloud/Trading2.png)
![购物车界面](https://cdn.jsdelivr.net/gh/Frimiku/picture_cloud/Trading3.png)
