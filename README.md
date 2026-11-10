<div align="center">

# **省钱兄 JAVA 旧物回收三端开源系统（JAVA + Uni-app + Vue）**

**服务端 · 管理端 · 用户端**

[![License](https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/09/232c51aa-f4a0-4087-b132-c49468621634.png)](LICENSE)
[![JDK](https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/09/43731742-da11-453a-802f-2c18151a8e58.png)](docs/服务端-功能说明.md)
[![Spring Boot](https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/09/5d817cd3-9ead-45ba-8efc-2cb3c9e94dfb.png)](docs/服务端-功能说明.md)
[![Vue](https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/09/a0f29461-d7b4-4289-802a-70d7827eab4e.png)](docs/管理端-运行教程.md)
[![uni-app](https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/09/c06606bd-f322-4e44-a5f2-5a016b9ff848.png)](docs/用户端-运行教程.md)

八年沉淀，分批次开源由 AI 辅助开发的系统源码。  
开源为生态，服务才是根基。欢迎各位开发者一同交流。

</div>

---
⭐ 如果觉得项目不错，请点击右上角 Star 收藏支持！您的 Star 是我们持续开源迭代的最大动力！

## **1. 项目简介**

**省钱兄旧物回收三端开源系统**是一套面向旧衣、家电、数码、奢侈品和书籍的上门回收与寄件回收系统，内置 **品类估价、预约下单、上门取件、结算打款、福利积分**，采用前后端分离架构，服务端、管理端、用户端全套代码开源。

服务端基于 **Spring Boot 2.6.11**（**Java 8**）与 **MyBatis-Plus** 构建，负责接口、数据库，以及短信、对象存储、支付、顺丰寄件等外部能力。  
管理端基于 **Vue 3 + Element Plus + Vite** 开发，供运营人员在电脑浏览器中维护品类价格、订单、取件、结算打款、会员与系统设置。  
用户端基于 **uni-app（Vue 3）** 开发，覆盖 H5 与微信小程序。用户可按品类估价、预约上门或顺丰寄件；回收员可在同一套用户端里查看取件任务并回填成交金额。

本仓库为服务端、管理端、用户端全套源码。线上 H5 演示见 [https://jwhs.xianmxkj.com](https://jwhs.xianmxkj.com)（与仓库用户端同源）。底部四个主入口是 **首页、福利中心、订单、我的**。

- **官方网站开源计划**：[https://www.xiansqx.com](https://www.xiansqx.com)
- **开源计划**：[https://opensource.xiansqx.cn](https://opensource.xiansqx.cn)
- **官方文档**：[功能说明与运行教程](docs/服务端-运行教程.md)
- **Gitee 仓库**：[https://gitee.com/maxiaodong1996/sqx-jiuwuhuishou.git](https://gitee.com/maxiaodong1996/sqx-jiuwuhuishou.git)
- **GitHub 仓库**：[https://github.com/yuyang7816/sqx-jiuwuhuishou.git](https://github.com/yuyang7816/sqx-jiuwuhuishou.git)

## **2. 移动端界面预览**

截图来自本项目线上 H5 演示站 [https://jwhs.xianmxkj.com](https://jwhs.xianmxkj.com)（与 `jiuwuhuishou-user` 构建产物一致），手机视口截取。

<div align="center">

| 首页 | 家电回收 | 数码估价 | 奢侈品 | 回收规则 |
| :---: | :---: | :---: | :---: | :---: |
| <img src="https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/04/6ed5854e-e00e-4d65-ab18-210e0342f635.png" width="160" alt="首页" /> | <img src="https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/04/e70a2a69-108d-4c2b-8ac5-6c607b46314d.png" width="160" alt="家电回收" /> | <img src="https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/04/935edf68-f68a-4498-9ad4-4e03137c5f3a.png" width="160" alt="数码估价" /> | <img src="https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/04/b917b76b-a594-474c-b645-c38377390061.png" width="160" alt="奢侈品" /> | <img src="https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/11/04/2325c5f5-ae9d-4eae-b95b-796929af3d98.png" width="160" alt="回收规则" /> |

</div>

## **3. 开源说明**

> **【省钱兄科技｜开源计划启动】**
>
> 八年沉淀，我们将分批次开源全套由 AI 辅助开发的系统源码。  
> 本项目采用 **Apache 2.0** 许可协议，源码供学习、技术研究、商业使用、二次开发与参考使用。  
> **开源为生态，服务才是根基；** 欢迎各位开发者一同交流。

## **4. 快速开始**

建议顺序：**先服务端，再管理端，最后用户端。** 管理端和用户端都要等服务端已经启动，才能登录和加载数据。

**说明：** 仓库里**不包含** `node_modules`（体积大、随系统生成）。克隆或下载后，在 **`jiuwuhuishou-admin`** 目录下**必须先执行一次** `npm install`，再启动开发命令。用户端用 HBuilderX 打开 `jiuwuhuishou-user`。详细步骤见下方与 [docs](docs/管理端-运行教程.md) 中的运行教程。

`application.yml` 里当前激活的是 `dev`。请确认数据源指向本机库，不要改成 `prod`。配置里 `sqx.redis.open` 为 `false`，本地可以先不装 Redis。

### **4.1 服务端**

安装 **JDK 8**、**Maven**、**MySQL 8**。新建数据库 `jwhuishou`（字符集 `utf8mb4`），再按版本号从小到大导入 [`jiuwuhuishou-server/sql`](jiuwuhuishou-server/sql) 里的 `V*.sql`。这些脚本只建回收业务表和后续变更，不会自己建库，也不包含一套从零创建管理员、角色、菜单的完整系统库。

[`application.yml`](jiuwuhuishou-server/sqx-application/src/main/resources/application.yml) 中 `spring.profiles.active` 已经是 **`dev`**。请按本机环境修改 [`application-dev.yml`](jiuwuhuishou-server/sqx-application/src/main/resources/application-dev.yml) 里的数据库账号密码：库名保持 `jwhuishou`，把占位的「用户名」「密码」改成你自己的。

在 `jiuwuhuishou-server` 目录执行 Maven 启动。启动类是 `com.sqx.SqxApplication`。控制台出现「旧物回收项目启动成功...」后，默认 **端口 9135**，上下文路径 **`/sqx_fast`**。接口文档：`http://127.0.0.1:9135/sqx_fast/doc.html`（Knife4j）。

更完整的模块划分与接口约定见 [服务端功能说明](docs/服务端-功能说明.md)。

### **4.2 管理端**

安装 **Node.js 18+**（见 `jiuwuhuishou-admin/package.json` 的 `engines`）。本机联调前，打开 `jiuwuhuishou-admin/src/utils/tool.js`，把 `getApiBaseUrl` 里现在写死的线上地址改成 `http://127.0.0.1:9135/sqx_fast/`。只改 `.env.development` 不会生效。

进入 `jiuwuhuishou-admin` 目录，**首次运行前**安装依赖并启动：

```bash
cd jiuwuhuishou-admin
npm install
npm run dev
```

`npm install` 会根据 `package.json` / `package-lock.json` 在本机生成 `node_modules`，只需在依赖变更或新克隆后执行。

开发站点默认端口 **5176**。

演示管理端账号：`admin` / `123456`（登录页注释里的账号；空库只导入 `sql` 时这个管理员还不存在，以库中数据为准）。

### **4.3 用户端**

**HBuilderX（推荐）：** 打开 `jiuwuhuishou-user` 目录，运行到浏览器或微信开发者工具。应用面向旧物回收，底部四个入口：**首页、福利中心、订单、我的**。

开发时请确认 [`jiuwuhuishou-user/common/config.js`](jiuwuhuishou-user/common/config.js) 中的接口地址指向本机或已部署的服务端。文件里现在生效的是线上地址 `https://jwhs.xianmxkj.com/sqx_fast`。本机联调时，把接口改成 `http://127.0.0.1:9135/sqx_fast`，WebSocket 改成 `ws://127.0.0.1:9135/sqx_fast/`。`manifest.json` 里 H5 开发端口写的是 **8852**，以 HBuilderX 控制台打印的地址为准。

微信小程序需把 `manifest.json` 里的 AppId 换成你自己的。短信、微信、支付、对象存储、顺丰等密钥需自行申请，并在管理端相关配置页填写。`sqx.verify-code.test-mode` 目前是 `true`，短信验证码固定为 **888888**。

## **5. 文档**

| 想做的事 | 打开哪一份 |
|---|---|
| 把服务端跑起来 | [服务端运行教程](docs/服务端-运行教程.md) |
| 看服务端能做什么 | [服务端功能说明](docs/服务端-功能说明.md) |
| 把管理端跑起来 | [管理端运行教程](docs/管理端-运行教程.md) |
| 看管理端能做什么 | [管理端功能说明](docs/管理端-功能说明.md) |
| 把用户端跑起来 | [用户端运行教程](docs/用户端-运行教程.md) |
| 看用户端能做什么 | [用户端功能说明](docs/用户端-功能说明.md) |

## **6. 目录结构**

仓库根目录只放三端代码、说明和协议。编译出来的 `target`、`dist`、`unpackage`、`node_modules` 是运行时生成的，不需要改。

### **6.1 仓库根目录**

| 路径 | 是什么 |
|---|---|
| `jiuwuhuishou-server` | **服务端**。接口、数据库脚本、支付、短信、寄件等能力都在这里 |
| `jiuwuhuishou-admin` | **管理端**。运营人员在电脑浏览器里打开的后台 |
| `jiuwuhuishou-user` | **用户端**。用户预约回收，回收员也在这里做取件，也可在浏览器 H5 预览 |
| `docs` | 功能说明、运行教程与 README 预览截图 |
| `LICENSE` | Apache License 2.0 全文 |

### **6.2 服务端 `jiuwuhuishou-server`**

| 路径 | 是什么 |
|---|---|
| `sqx-application` | 启动入口与应用配置。主程序在 `com.sqx.SqxApplication` |
| `sqx-modules/sqx-app` | 旧物回收业务接口：品类估价、订单、取件、寄件、结算打款、福利、钱包、会员、分销、客服 |
| `sqx-modules/sqx-system` | 系统模块：管理员、角色、菜单、配置、权限 |
| `sqx-modules/sqx-generator` | 按数据表生成代码的工具，日常运行用不到 |
| `sqx-common` | 公共组件：Web、MyBatis、Redis、短信、支付、文件、文档等 |
| `sqx-application/src/main/resources` | `application.yml` 与各环境配置 |
| `sql` | 回收业务版本脚本，从 `V1__recycle_init.sql` 起按版本号导入。导入前先建库 `jwhuishou` |
| `scripts` | 本机执行某一版 SQL、释放端口等辅助脚本 |

### **6.3 管理端 `jiuwuhuishou-admin`**

| 路径 | 是什么 |
|---|---|
| `src/views/DashboardView.vue` | 数据中心 |
| `src/views/business` | 回收订单、改约、异常、取件、结算、打款、争议、评价、品类和规则、首页装修、福利、客服、钱包、分销、提现 |
| `src/views/user` | 用户列表、用户详情 |
| `src/views/system` | 管理员、角色、菜单、权限、字典、系统配置、操作日志 |
| `src/views/LoginView.vue` | 登录页，含验证码 |
| `src/router` | 页面地址和菜单对应关系 |
| `src/utils/tool.js` | 管理端实际请求的服务端地址。本机联调改这里 |
| `src/api` | 每个页面调用服务端的接口 |

### **6.4 用户端 `jiuwuhuishou-user`**

| 路径 | 是什么 |
|---|---|
| `pages/index` | 首页：轮播、成交公告、品类入口、推荐分佣 |
| `pages/welfare` | 福利中心：签到、金币、任务 |
| `pages/order` | 订单列表（全部、进行中、待确认、已完成） |
| `pages/my` | 我的：余额、会员、邀请、设置；员工账号在这里进入取件 |
| `packageRecycle` | 家电估价、旧衣和书籍预约、数码与奢侈品搜索、服务范围 |
| `packageCommon` | 登录注册、订单详情、改约、结算确认、打款、评价、取件、地址、钱包、提现、客服 |
| `common` | 接口地址、登录态、请求封装等公共逻辑。本机或线上地址在 `common/config.js` |
| `components` | 页面里重复使用的组件 |
| `static` | 图标与默认图片 |
| `pages.json` | 页面路由与底部导航（首页 / 福利中心 / 订单 / 我的） |
| `manifest.json` | 应用名称、小程序 AppId、H5 等配置 |

界面组件使用 **uView Plus**。用户下单和回收员取件都在用户端完成。

## **7. 核心特性与技术栈**

### **7.1 核心特性**

· **底部四个入口：首页、福利中心、订单、我的**  
· **家电按型号估价，数码和奢侈品按品牌搜索，旧衣和书籍按重量预约**  
· **履约可选上门回收或顺丰寄件，邮寄单可看运单和物流**  
· **订单全流程：进行中、待确认、结算确认、价格争议、评价、打款**  
· **签到领金币、钱包余额、微信或支付宝提现、会员权益、邀请分佣**  
· **回收员查看取件任务，上门后回填成交金额**  
· **在线客服、帮助中心、消息通知**  
· **管理端品类价格、订单、取件、结算打款、福利和系统配置**  
· **短信验证码、微信登录、在线客服**

业务细节见 [服务端功能说明](docs/服务端-功能说明.md) 与 Knife4j `doc.html`。

版本以仓库里的配置文件为准。没写死版本号的，按上文快速开始安装对应大版本即可。

### **7.2 服务端**

| 类别 | 技术 | 版本 | 用来做什么 |
|---|---|---|---|
| 语言 | **Java** | **1.8** | 整个服务端。不能换成 11 或 17 |
| 框架 | **Spring Boot** | **2.6.11** | 提供网页接口，端口 `9135`，路径 `/sqx_fast` |
| 持久化 | **MyBatis-Plus** | **3.5.3.2** | 读写 MySQL |
| 构建 | **Maven** | 3.6 及以上 | 下载依赖、编译、启动 |
| 数据库 | **MySQL** | 8.0 及以上 | 业务数据。库名 `jwhuishou`，脚本在 `jiuwuhuishou-server/sql` |
| 连接池 | **Druid** | 1.2.19 | 管理数据库连接 |
| 缓存 | **Redis / Redisson** | 可选 | 本地 `sqx.redis.open` 为 `false`，第一次启动可以先不装 |
| 管理端登录 | **Apache Shiro** | 1.4.0 | 后台账号、角色、菜单权限 |
| 用户端登录 | **JWT**（jjwt） | 0.9.1 | 用户登录后的凭证 |
| 接口文档 | **Knife4j / SpringDoc** | Knife4j 4.4.0，SpringDoc 1.7.0 | 浏览器打开 `doc.html` |
| 登录验证码 | **Kaptcha** | 0.0.9 | 管理端登录页上的图片验证码 |
| 对象存储 | **阿里云 OSS** 等（可选） | 3.4.0 | 不填密钥时图片可存本机路径 |
| 支付 | 微信支付、支付宝等 | 随工程 | 开通会员、提现。密钥要自己申请 |
| 寄件 | 顺丰 / 物流查询 | 可配置 | 邮寄回收的运单和轨迹 |

### **7.3 管理端**

| 类别 | 技术 | 版本 | 用来做什么 |
|---|---|---|---|
| 运行环境 | **Node.js** | **18+** | 安装依赖、启动开发网站 |
| 页面框架 | **Vue** | **3.5** | 后台每一个页面 |
| 构建 | **Vite** | **5.4** | 开发时热更新，默认端口 `5176` |
| 界面组件 | **Element Plus** | **2.11** | 表格、表单、弹窗、菜单 |
| 页面跳转 | **Vue Router** | **4.5** | 登录页、数据中心和各业务页的地址 |
| 状态 | **Pinia** | **3.0** | 记住登录等信息 |
| 请求 | **Axios** | **1.9** | 把操作发给服务端 |
| 图表 | **ECharts** | **6.1** | 数据中心上的图 |

### **7.4 用户端**

| 类别 | 技术 | 版本 | 用来做什么 |
|---|---|---|---|
| 框架 | **uni-app** | Vue 3 | 同一套页面可运行到浏览器、微信小程序 |
| 开发工具 | **HBuilderX** | 正式版 | 编译并运行到 Chrome 或微信开发者工具 |
| 界面组件 | **uView Plus** | 3.8 | 按钮、输入框、轮播、弹窗。不负责付款 |
| 浏览器预览 | H5 | 开发端口 `8852`，以控制台为准 | 电脑上看用户端 |
| 微信小程序 | 微信开发者工具 | 可选 | 要自己的小程序 AppId 才能真机预览 |

## **8. 开源协议**

<div align="center">

本仓库使用 **Apache License 2.0**

全文见 [LICENSE](LICENSE)

</div>

## **9. 社区与支持**

运行、二次开发或商务合作，可以联系 **省钱兄科技**。

使用过程中如果接口起不来、页面打不开，先按本文 **4. 快速开始** 和 `docs` 里的运行教程排查。短信、微信、支付、对象存储、顺丰等密钥需要自行申请，并在管理端系统配置中填写。仍解决不了时，可通过官方渠道说明你的环境和报错。

当前项目正在测试中，如遇到 BUG，可自行修复或者留言反馈，我们将第一时间进行版本迭代。  
欢迎一起交流学习。开源是把源码开放出来，具体落地、定制和长期维护仍由服务来承接。

<div align="center">

<img src="docs/images/community-qr.jpg" alt="省钱兄科技二维码" width="220" />

**省钱兄科技**

</div>
