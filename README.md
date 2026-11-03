# 健身房管理系统

本项目是数据库原理课程实验作业，围绕健身房日常运营场景实现会员管理、员工管理、器材管理、课程管理、课程报名、课程评价与运营分析等功能。系统采用前后端分离架构：前端使用 Vue 3 + Vite + Element Plus，后端使用 Spring Boot + MyBatis，数据库使用 MySQL。

## 项目功能

- 管理员登录：管理员账号校验、Session 登录态维护。
- 会员管理：会员分页查询、新增、修改、删除、按会员卡查询；可维护身高体重等身体数据（选填），并管理「购买课时 / 剩余课时」——修改购买课时会自动把差额补到剩余课时，且约束 0 ≤ 剩余 ≤ 购买。
- 员工管理：员工信息查询、新增、修改、删除；职务为「健身教练」的员工可勾选「所教课程」——一门课可有多位教练任教，解雇名下仍有任教科目的员工会被拦截。
- 器材管理：器材查询、新增、修改、删除，支持位置和状态筛选。
- 课程管理：课程查询、新增、删除，显示课程容量、教练、价格、报名人数等信息；一门课可挂多位任课教练（课程上可直接「改教练」），无教练的课程显示「待安排」；支持按教练筛选课程及其报名记录。
- 课程报名记录：按课程 / 按教练查询报名记录；每条记录显示该会员报名时选择的教练。
- 会员端功能：会员登录、个人信息查看与修改、课程报名、已报名课程查询；报名时需从该课程任课教练中选择一位（无教练的课程不可报名），「我的课程」显示所选教练；卡内课时与资料实时取自数据库（管理员后台修改后会员端即时可见）。
- 健康管理（会员端）：健康评估（BMI 实时计算与分级刻度）、运动打卡（日历 / 连续 / 累计）、饮食推荐（按 BMI 估算全天热量与三餐），以及健康助手（AI 问答，需配置 `AI_API_KEY`）。
- 营业状态：管理员可切换「营业中 / 已打烊」，全站显示状态提示；打烊仅作提示，不拦截线上操作。
- 运营分析：课程报名排行、评分排行、统计卡片和课程分析图表展示。
- 数据库实验接口：保留若干用于课程实验演示的查询、视图和业务操作接口。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 前端 | Vue 3、TypeScript、Vite、Vue Router、Axios、Element Plus、ECharts |
| 后端 | Spring Boot 3、MyBatis、Lombok、Maven |
| 数据库 | MySQL 8 |
| 部署 | Docker、Docker Compose、Nginx |

## 目录结构

```text
.
├── README.md
├── docker-compose.yml
├── source/
│   ├── frontend/                  # Vue 前端项目
│   ├── gym-management-system/     # Spring Boot 后端项目
│   └── gym_management_system.sql  # 数据库初始化脚本
├── report_screenshots/            # 实验报告运行截图
└── report_vector_figures/         # 系统结构图、技术架构图、ER 图
```

## 系统设计图

### 系统功能结构

![系统功能结构](report_vector_figures/figure1-1-system-function-structure.png)

### 技术架构

![技术架构](report_vector_figures/figure2-1-technology-architecture.png)

### 总体 ER 图

![总体 ER 图](report_vector_figures/ppt_er/manual_final/figure3-5-total-er.png)

## 系统运行截图

### 管理员登录

![管理员登录](report_screenshots/fig6-1-admin-login.png)

### 会员管理

![会员管理](report_screenshots/fig6-2-member-management.png)

### 课程管理

![课程管理](report_screenshots/fig6-3-course-management.png)

### 课程运营分析

![课程运营分析](report_screenshots/fig6-4-course-analysis.png)

![课程运营气泡图](report_screenshots/fig6-4b-course-analysis-bubble.png)

### 会员端功能

#### 会员登录

![会员登录](report_screenshots/fig8-1-user-login.png)

#### 会员主页

![会员主页](report_screenshots/fig8-2-user-main.png)

#### 个人信息与修改

![个人信息](report_screenshots/fig8-3-user-info.png)

![修改个人信息](report_screenshots/fig8-4-user-update-info.png)

#### 报名选课（选择任课教练）

![报名选课](report_screenshots/fig6-5-user-course.png)

![选择任课教练](report_screenshots/fig8-5-user-apply-class-coach.png)

#### 我的课程与评分

![我的课程](report_screenshots/fig8-6-user-my-class.png)

![课程评分](report_screenshots/fig8-7-user-rating.png)

#### 健康管理

![健康评估](report_screenshots/fig8-8-user-health-bmi.png)

![运动打卡](report_screenshots/fig8-9-user-checkin.png)

![饮食推荐](report_screenshots/fig8-10-user-diet.png)

![健康助手](report_screenshots/fig8-11-user-chat.png)

## 快速启动：Docker Compose

推荐使用 Docker Compose 启动完整环境，包含 MySQL、后端服务和前端页面。

### 环境要求

- Docker
- Docker Compose

### 启动

```bash
docker compose up -d --build
```

启动完成后访问：

- 前端页面：http://localhost:5173
- 后端接口：http://localhost:8080
- MySQL：localhost:3306

> 注意：如果本机已安装并运行 MySQL（占用 3306），Compose 中的 `mysql` 服务会因端口冲突无法启动。请先停止本机 MySQL 服务，或把 `docker-compose.yml` 中的 `"3306:3306"` 改为 `"3307:3306"`（只影响宿主机访问，容器之间仍通过 `mysql:3306` 连接）。

数据库初始化脚本会在 MySQL 容器首次创建时自动执行：

```text
source/gym_management_system.sql
```

如果修改过初始化 SQL 后需要重新导入数据，可以删除 Compose 数据卷后重新启动：

```bash
docker compose down -v
docker compose up -d --build
```

### 停止

```bash
docker compose down
```

## 本地开发运行

如果不使用 Docker，也可以分别启动数据库、后端和前端。

### 环境要求

- JDK 17+
- Maven（或直接使用仓库自带的 Maven Wrapper `mvnw`）
- Node.js 20.19+ 或 22.12+（Vite 8 要求，低版本启动会报错）
- MySQL 8

### 1. 初始化数据库

先在本地 MySQL 中创建并导入数据库脚本：

```bash
mysql -uroot -p123456 < source/gym_management_system.sql
```

默认数据库配置：

```yaml
database: gym_db
username: root
password: 123456
port: 3306
```

如需修改数据库连接，可设置环境变量：

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gym_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=123456
```

### 2. 启动后端

```powershell
cd source/gym-management-system
.\mvnw.cmd spring-boot:run
```

后端默认运行在：

```text
http://localhost:8080
```

### 3. 启动前端

```bash
D:
cd D:\GYM-MANAGEMENT-SYSTEM\gym-management-system\source\frontend
npm install        # 首次运行或依赖变更后执行
npm run dev
```

> Node 版本需为 20.19+ 或 22.12+。本机默认是 Node 16 时，可先执行 `nvm use 22.12.0` 切换，`node -v` 确认无误后再启动。

前端默认运行在：

```text
http://localhost:5173
```

开发模式下，Vite 会把 `/api` 请求代理到 `http://localhost:8080`。

## 测试账号

| 角色 | 账号 | 密码 | 登录地址 |
| --- | --- | --- | --- |
| 管理员 | 1001 | 123456 | http://localhost:5173 |
| 会员 | 2026001 | 123456 | http://localhost:5173/#/toUserLogin |

## 主要接口

| 模块 | 接口前缀 | 说明 |
| --- | --- | --- |
| 登录 | `/api` | 管理员登录、会员登录、退出登录、主页数据 |
| 会员 | `/api/member` | 会员查询、新增、修改、删除（课时 / 身高体重） |
| 员工 | `/api/employee` | 员工查询、新增、修改、删除（健身教练所教课程名单、解雇守卫） |
| 器材 | `/api/equipment` | 器材查询、新增、修改、删除 |
| 课程 | `/api/class` | 课程查询、报名记录、按教练筛选、运营分析、任教教练名单维护（updateClassCoaches） |
| 会员端 | `/api/user` | 会员信息、报名（需选教练）、退课、已报名课程 |
| 健康管理 | `/api/user/health` | BMI 评估、运动打卡、饮食推荐 |
| 健康助手 | `/api/user/chat` | AI 健康问答（需配置 `AI_API_KEY`） |
| 营业状态 | `/api/business` | 查询营业状态、管理员切换营业 / 打烊 |
| 实验接口 | `/api/experiment` | 数据库课程实验查询与业务操作 |

## 数据库说明

核心数据表包括：

- `admin`：管理员账号信息。
- `member`：会员基本信息、身高体重等身体数据与会员卡课时信息。
- `employee`：员工和教练信息（`position` 为 `健身教练` 即视为教练）。
- `equipment`：健身器材信息。
- `class_table`：课程信息（`coach_id` 为其主教练，多教练见 `class_coach`）。
- `class_record`：会员课程报名记录（`coach_id` 记录会员报名时选择的教练）。
- `class_coach`：课程与任课教练的多对多关系（一门课可有多位教练）。
- `class_equipment`：课程与器材的关联关系。
- `gym_checkin`：会员每日运动打卡记录（每人每天一条）。
- `business_status`：全站营业状态配置（单行表：营业中 / 已打烊）。

数据库相关补充脚本位于：

```text
source/gym-management-system/src/main/resources/db/
```

## 打包与构建

前端构建：

```bash
cd source/frontend
npm run build
```

后端构建：

```powershell
cd source/gym-management-system
.\mvnw.cmd -DskipTests package
```
