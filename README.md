# 云记事本 - 企业级 Java Swing 桌面应用

基于 Java 17 的多模块企业级云记事本桌面应用，采用 MVC 分层架构，支持笔记管理、标签分类、图片附件、回收站、搜索、导出等功能。

## 技术栈

| 层面 | 技术 |
|------|------|
| 语言 | Java 17 |
| UI 框架 | Java Swing + FlatLaf（现代扁平化主题） |
| 构建 | Maven 多模块 |
| 数据库 | MySQL 8.0 |
| 连接池 | HikariCP |
| 日志 | SLF4J + Logback |
| 测试 | JUnit 5 |
| 容器化 | Docker Compose（可选） |

## 项目结构

```
├── pom.xml                        # 父 POM（统一版本管理）
├── schema.sql                     # 数据库建表脚本
├── docker-compose.yml             # Docker MySQL 容器
├── .env / .env.template           # 数据库配置
├── mvnw / mvnw.cmd                # Maven Wrapper
│
├── notepad-common/                # 通用模块
│   └── common/
│       ├── constant/              # 全局常量
│       ├── exception/             # 异常体系（ErrorCode/BusinessException/ServiceException）
│       ├── result/                # 统一返回封装
│       └── util/                  # 工具类（StringUtils/ImageUtils）
│
├── notepad-model/                 # 数据模型模块
│   └── model/
│       ├── entity/                # 实体类（Note/NoteImage/Tag）
│       ├── dto/                   # 数据传输对象
│       └── vo/                    # 视图对象
│
├── notepad-dao/                   # 数据访问模块
│   └── dao/
│       ├── pool/                  # HikariCP 连接池
│       ├── repository/            # Repository 接口 + 实现
│       └── mapper/                # ResultSet 映射
│
├── notepad-service/               # 业务逻辑模块
│   └── service/
│       ├── NoteService            # 笔记服务接口
│       ├── TagService             # 标签服务接口
│       ├── ExportService          # 导出服务接口
│       └── impl/                  # 服务实现
│
├── notepad-ui/                    # 界面模块
│   └── ui/
│       ├── theme/                 # FlatLaf 主题配置
│       ├── frame/                 # 主窗口
│       ├── panel/                 # 面板组件（搜索/标签/列表/编辑/图片）
│       ├── renderer/              # 列表渲染器
│       └── handler/               # 事件处理器
│
├── notepad-bootstrap/             # 启动模块
│   └── Bootstrap.java             # 程序入口
│
└── notepad-test/                  # 测试模块
    └── JUnit 5 单元测试
```

## 模块依赖关系

```
notepad-bootstrap
    ├── notepad-ui
    │   └── notepad-service
    │       └── notepad-dao
    │           └── notepad-model
    │               └── notepad-common
    └── notepad-test
```

## 功能特性

- **笔记管理**：新建、编辑、保存、删除笔记
- **标签系统**：创建标签、给笔记绑定标签、按标签筛选
- **图片附件**：多图上传、缩略图预览
- **回收站**：软删除、还原、永久删除
- **关键字搜索**：标题 + 内容模糊搜索
- **导出 HTML**：将笔记导出为 HTML 文件
- **快捷键**：Ctrl+S 快速保存
- **异步加载**：SwingWorker 异步执行数据库操作，UI 不卡顿
- **异常处理**：分层异常体系，友好错误提示
- **日志系统**：SLF4J + Logback，分级日志输出到文件

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+（或使用项目自带的 Maven Wrapper）

### 1. 创建数据库

在 MySQL 中执行建表脚本：

```bash
mysql -u root -p < schema.sql
```

或手动执行 `schema.sql` 中的 SQL 语句。

### 2. 配置数据库连接

复制 `.env.template` 为 `.env`，修改数据库配置：

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=cloudnote
DB_USER=root
DB_PASSWORD=your_password
```

### 3. 编译项目

```bash
# 使用 Maven Wrapper（推荐）
./mvnw clean compile

# 或使用本地 Maven
mvn clean compile
```

### 4. 运行项目

在 IDEA 中运行 `com.cloudnote.Bootstrap` 类的 `main` 方法。

或使用命令行：

```bash
./mvnw exec:java -Dexec.mainClass="com.cloudnote.Bootstrap" -pl notepad-bootstrap
```

### 5. 使用 Docker（可选）

```bash
docker-compose up -d
```

启动 MySQL 容器，自动执行 `schema.sql` 初始化数据库。

## 数据库设计

### ER 图

```
notes (笔记表)
├── id (PK)
├── title
├── content
├── category
├── create_time
├── update_time
└── is_deleted

note_images (图片表)
├── id (PK)
├── note_id (FK → notes.id)
├── image_path
├── image_name
└── upload_time

tags (标签表)
├── id (PK)
├── name (UNIQUE)
└── color

note_tags (关联表)
├── note_id (FK → notes.id)
└── tag_id (FK → tags.id)
```

## 架构亮点

### 1. 多模块分层架构

清晰的模块边界，各模块职责单一，便于维护和扩展。

### 2. HikariCP 连接池

高性能数据库连接池，自动管理连接生命周期，避免频繁创建/销毁连接。

### 3. N+1 查询优化

批量查询笔记的图片和标签，使用 `IN` 子句替代循环单条查询。

### 4. SwingWorker 异步

所有数据库操作在后台线程执行，不阻塞 Swing 事件分发线程（EDT），UI 保持流畅。

### 5. 异常分层处理

```
Repository 层 → 捕获 SQLException，记录日志，抛出 ServiceException
Service 层   → 校验参数，抛出 BusinessException
UI 层        → 捕获异常，弹出友好错误提示
```

### 6. 外部化配置

数据库密码通过 `.env` 文件配置，不提交到版本控制，安全可靠。

## 项目截图

（可在此处添加项目截图）

## 开发工具

- IntelliJ IDEA 2024.3
- DataGrip（数据库管理）
- Git

## 许可证

MIT License
