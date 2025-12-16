<<<<<<< Updated upstream
# Vue 3 + Vite

This template should help get you started developing with Vue 3 in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about IDE Support for Vue in the [Vue Docs Scaling up Guide](https://vuejs.org/guide/scaling-up/tooling.html#ide-support).
=======
# 智慧医院就诊管理平台

## 项目结构

```
hospital
├─ frontend/         # Vite + Vue3 + TypeScript 前端
└─ backend/          # Spring Boot 3 后端
```

### 前端栈

- Vite + Vue 3 + TypeScript
- Vue Router、Pinia、Axios
- Element Plus 组件库

主要页面：仪表盘、登录/注册、就诊人档案增删改查（示例数据，等待对接后端）。

### 后端栈

- Spring Boot 3 + Spring Web + Spring Data JPA + Validation
- Spring Security（当前放开权限，后续可接入 JWT）
- H2 内存数据库（便于本地验证，切换 MySQL 只需修改 `application.yml`）

提供的 REST 接口：

| Method | Path                | 说明             |
|--------|--------------------|------------------|
| POST   | `/api/auth/register` | 注册并返回 Token |
| POST   | `/api/auth/login`    | 登录             |
| GET    | `/api/patients`      | 查询就诊人 (需 `X-USER-ID`) |
| POST   | `/api/patients`      | 新增就诊人       |
| PUT    | `/api/patients/{id}` | 更新就诊人       |
| DELETE | `/api/patients/{id}` | 删除就诊人       |

> 为了在没有完整认证的情况下便于联调，患者接口通过 `X-USER-ID` 请求头识别当前用户，可在登录/注册响应中拿到 `userId` 后直接携带。

## 环境要求

- Node.js 18+
- npm / pnpm / yarn（二者任选其一）
- JDK 17
- Maven 3.9+

## 本地启动

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认监听 `http://localhost:8080`，内置 H2 控制台 `http://localhost:8080/h2-console`。

### 2. 启动前端

```bash
cd frontend
npm install        # 首次需要
npm run dev
```

Vite 默认监听 `http://localhost:5173`，并通过代理将 `/api` 流量转发至 `http://localhost:8080`。

## 后续迭代建议

- 对接真实短信网关、验证码有效期校验。
- 使用 JWT + Spring Security Filter 保证接口安全，并与前端路由守卫联动。
- 依据用例图补充角色（医生、护士、药师）及其权限范围。
- 将 H2 替换为 MySQL/SQL Server，并完善数据迁移脚本。
>>>>>>> Stashed changes
