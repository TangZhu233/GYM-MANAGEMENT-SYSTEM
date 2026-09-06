# 健身房管理系统 — 项目规范与限界条件

> 本文件是**唯一权威的项目规范**。本文档每次会话开始会自动加载，工作范围、改动边界与协作方式一律以本文为准。
> 工作中新发现的重要约束，**必须追加到本文档**（见文末「约束变更流程」），而不是口头约定。

---

## 1. 项目是什么

一个健身房管理系统（课程/毕设项目），分为**前端 + 后端**两个应用，共用同一个 git 仓库、同一台本机开发。

## 2. 仓库布局（务必记清）

```
D:\GYM-MANAGEMENT-SYSTEM\gym-management-system\   ← git 仓库根
├── CLAUDE.md                      ← 本文档
├── README.md / READMEone.md       ← 项目说明
├── debug.md                       ← 用户自己的排错笔记（勿动/勿删）
├── docker-compose.yml
├── source\
│   ├── frontend\                  ← 前端应用（Vue3 + Vite）
│   │   └── src\pages\*.vue        ← 页面组件
│   ├── gym-management-system\     ← 后端应用（Spring Boot）
│   │   └── src\main\{java,resources}\
│   └── gym_management_system.sql  ← 数据库全量建库脚本（重要）
└── report_screenshots\ report_vector_figures\   ← 报告素材，勿当源码改
```

- **`target/`（后端编译产物）、`node_modules/`（前端依赖）** 都是 git 忽略的生成物，永远不作为源码讨论或提交。
- 用户本人未跟踪的笔记文件（`debug.md`、`READMEone.md` 等）**不得删除、覆盖、重命名**，除非用户明确要求。

## 3. 技术栈

| 端 | 技术 | 要点 |
|---|---|---|
| 后端 | Spring Boot 3.5.11（parent）、Java 17、Maven | MyBatis + MySQL 8、Lombok |
| 前端 | Vue 3 + Vite + TypeScript | Element Plus、axios、vue-router（hash 模式）、echarts |

## 4. 本地如何运行

**后端**（端口 8080，在 `source\gym-management-system\` 下）：
```
mvn clean compile        # 首次/改动后先编译（缺这一步会出现“找不到主类”）
mvn spring-boot:run      # 启动，或 IDEA 里运行 GymManagementSystemApplication
```
- 数据源默认：`localhost:3306/gym_db`，账号 `root` / 密码 `123456`（见 `application.yml`，支持环境变量覆盖）。
- **MySQL 必须先启动**（本机服务名 `MySQL80`），且 `gym_db` 库存在。
- 端口被占用时先查：`netstat -ano | findstr :8080`，确认占用进程后再决定，**不要盲目 kill**。

**前端**（端口 5173，在 `source\frontend\` 下）：
```
npm run dev
```
- Vite 代理：请求 `/api/**` 转发到 `http://localhost:8080`（见 `vite.config.ts`）。
- 后端 CORS 只放行 `http://localhost:5173` / `5174`。
- 生产部署：nginx 监听 80，把 `/api/` 代理到后端容器 `backend:8080`（见 `nginx.conf`、`Dockerfile`）。

## 5. 后端代码约定（新代码必须贴合既有风格）

- **分层**：`controller → service(+impl) → mapper`，数据对象在 `pojo`。
  - 控制器：`com.gym.controller.ApiXxxController`，`@RestController` + `@RequestMapping("/api/xxx")`。
  - 服务接口：`com.gym.service.XxxService`；实现：`service/impl/XxxServiceImpl`（`@Service`）。
  - 映射：接口 `com.gym.mapper.XxxMapper` + XML 在 `resources/mapper/XxxMapper.xml`（主类有 `@MapperScan("com.gym.mapper")`）。
  - 数据类：`com.gym.pojo`（如 `Member`）。MyBatis 开启下划线→驼峰自动映射。
- **接口风格**：
  - URL 用「动作」语义，如 `/api/member/selMember`、`/api/member/addMember`、`/api/member/search`。
  - 返回值一般为 `Map<String,Object>`，固定带 `success` 布尔字段；错误处理参考现有代码。
  - 前端提交用 `application/x-www-form-urlencoded`，所以 **POST 新增/更新接口用表单参数直接绑定实体**（如 `addMember(Member member)`），不要改成 JSON body（前端 `client.ts` 的 `postForm` 是按表单发的）。
- **鉴权**：会话 Cookie 方式。`SessionAuthInterceptor` 拦截 `/api/**`，白名单仅 `/api/adminLogin`、`/api/userLogin`、`/api/logout`。
- 业务号生成等逻辑在 Controller 层已有先例（如会员号 `2026 + 4位流水`），改动时沿用，不另造机制。

## 6. 前端代码约定

- 页面组件放 `src/pages/`，命名 `<领域><动作>`（如 `MemberSelMember.vue`、`MemberToAddMember.vue`），**路径、组件名、后端接口保持对应**，别乱起名。
- 路由在 `src/router/index.ts`，**hash 模式**。受保护路由带 `meta: { requiresAuth: true, role: 'admin'|'user' }`。
  - 角色校验：`beforeEach` 请求 `/api/toAdminMain` 或 `/api/toUserMain` 探测会话，失败跳回对应登录页。登录页：管理员 `/`（AdminLogin），用户 `/toUserLogin`（UserLogin）。
- 请求统一走 `src/api/client.ts`（axios，`withCredentials: true`）：普通 GET 用 `api.get`，表单提交用导出的 `postForm(url, data)`。
- UI 用 **Element Plus**；页面整体结构（顶栏标题 + 筛选卡片 + 表格 + 分页/操作列）参考 `MemberSelMember.vue` 等成熟页面。
- 语言：现有页面脚本注释用中文；`.vue` 用 `<script setup>`。

## 7. 数据库约定

- 权威 schema 脚本：`source\gym_management_system.sql`；后端另有增量脚本目录 `src/main/resources/db/*.sql`。
- **任何表结构 / 字段 / 初始化数据的改动：先与用户确认，并同步到 SQL 脚本**，不能只改代码而让脚本失配。

## 8. 限界条件（铁律）——每次动手前自检

1. **单一目标**：一次只解决一个明确问题；不顺手重构、不改与当前任务无关的代码/文件。
2. **先看后动**：删除/覆盖任何文件前，先查看目标内容；若与用户描述不符、或并非我所创建，先提出再处理，不擅自删。
3. **不擅动 git**：不 `commit` / `push` / `reset` / `rebase` / 改历史，除非用户明确要求。新增的未跟踪笔记文件保持不动。
4. **不动生成物**：`target/`、`node_modules/`、`dist/` 只在必要构建命令里出现，不作为「代码改动」。
5. **并行编辑防冲突**：用户可能正在 VS Code / IDEA 里改文件。动手前**明确说出我将改哪些文件**；绝不与用户正在编辑的同一文件同时写，避免互相覆盖。
6. **数据库为单一事实来源**：见第 7 节，schema 改动必须确认 + 同步脚本。
7. **可逆优先**：运行/调试命令优先选择不破坏现状的（查端口先于 kill；`clean` 前确认没有依赖 `target` 的运行）。
8. **如实报告**：改了什么、验证结果如何，说清楚；失败不隐瞒，跳过就明说跳过。

## 9. 协作流程（默认）

1. 用户描述问题/现象（哪个页面、什么操作、什么报错）。
2. 我先定位，说清改动方案与**涉及文件清单** → 用户确认。
3. 我修改 → 给出验证方式并尽量当场验证 → 如实汇报结果。
4. 用户可用自己的编辑器（VS Code / IDEA）复查，界面用浏览器验证。

> 特例：用户说「直接修」「你自己看着办」等，表示已授权，可省略第 2 步确认，但仍遵守第 8 节铁律，并汇报改动文件。

## 10. 约束变更流程

出现新的重要约束（新的项目事实、新的禁区、环境变化），**由发现方在本文件对应小节追加一行或新增小节**，并注明日期，作为后续所有会话的固定依据。

---
_初始版本：2026-09-06_
