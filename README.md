# 记账助手 Android App

## 项目结构

```
finance-android/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/finance/app/
│       │   ├── data/                    # 数据层
│       │   │   ├── local/               # 本地（Room DAO、数据库、偏好）
│       │   │   │   ├── dao/             # CategoryDao, ExpenseDao, IncomeDao
│       │   │   │   ├── FinanceDatabase.kt
│       │   │   │   └── PreferencesManager.kt
│       │   │   ├── model/               # 数据模型（Entity、API 模型）
│       │   │   ├── remote/              # 网络（ApiService、DTO）
│       │   │   │   └── dto/
│       │   │   └── repository/           # 仓库（Auth, Category, Expense, Income, AI）
│       │   ├── di/                      # 依赖注入（AppContainer, NetworkModule）
│       │   ├── presentation/            # UI 层
│       │   │   ├── navigation/          # NavGraph
│       │   │   ├── screen/              # 界面（auth/LoginScreen, home/HomeScreen）
│       │   │   ├── theme/               # Theme.kt
│       │   │   └── viewmodel/           # ViewModel（Auth, Expense, Income, AI）
│       │   ├── util/                    # 工具（DateUtils, Resource）
│       │   ├── FinanceApplication.kt
│       │   └── MainActivity.kt
│       └── res/                         # 资源（drawable, mipmap, values, xml）
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── clean-build.sh                       # 清除缓存与构建
├── build-apk.sh                         # 快速打包 APK
└── README.md
```

## 编译方式

### 环境要求

- **JDK**：建议 JDK 17 及以上（与 Kotlin 2.x 及 Android Gradle Plugin 8.x 兼容）
- **Android SDK**：需安装 Android SDK，并配置 `ANDROID_HOME` 或 `local.properties` 中的 `sdk.dir`

### 本地配置（local.properties）

`local.properties` 不纳入版本控制（含本机路径与密钥信息）。首次克隆或新环境构建时，请**复制模板生成**：

```bash
cp local.properties.dev local.properties
```

然后按本机情况修改 `local.properties` 中的值。字段含义如下：

| 字段 | 含义 |
|------|------|
| `sdk.dir` | Android SDK 的安装路径，Gradle 编译时使用（如 `/Users/xxx/Library/Android/sdk`） |
| `RELEASE_STORE_FILE` | Release 签名用的 keystore 路径（见下方「Release keystore 的生成与存放」） |
| `RELEASE_STORE_PASSWORD` | 上述 keystore 的密码 |
| `RELEASE_KEY_ALIAS` | 签名密钥的别名（如 `finance-app`） |
| `RELEASE_KEY_PASSWORD` | 上述密钥的密码 |
| `BASE_URL` | 后端 API 基础地址。模拟器访问本机服务常用 `http://10.0.2.2:8080`；真机请改为本机 IP（如 `http://192.168.1.100:8080`） |

#### Release keystore 的生成与存放

**生成 keystore**（需已安装 JDK，使用 `keytool`）：

```bash
keytool -genkey -v -keystore release.keystore -alias finance-app -keyalg RSA -keysize 2048 -validity 10000
```

按提示输入 keystore 密码、密钥密码、姓名/单位等信息；其中 **alias**（如 `finance-app`）和密码需与 `local.properties` 中的 `RELEASE_KEY_ALIAS`、`RELEASE_STORE_PASSWORD`、`RELEASE_KEY_PASSWORD` 一致。

**存放位置**：

- **推荐**：放在**项目根目录**（与 `build.gradle.kts`、`settings.gradle.kts` 同级），例如：
  ```
  finance-android/
  ├── release.keystore    ← 放这里
  ├── build.gradle.kts
  ├── local.properties
  └── ...
  ```
  此时 `local.properties` 中填写：`RELEASE_STORE_FILE=release.keystore`（相对路径，相对于项目根目录）。
- **其他位置**：若放在别处，`RELEASE_STORE_FILE` 需写**绝对路径**，例如：`RELEASE_STORE_FILE=/path/to/release.keystore`。

**注意**：`release.keystore` 和 `local.properties` 均不要提交到版本控制；丢失 keystore 或密码将无法对已上架应用进行更新。

### 命令行编译

在项目根目录下执行：

| 命令 | 说明 |
|------|------|
| `./gradlew assembleDebug` | 编译 Debug 包（未签名，用于开发调试） |
| `./gradlew assembleRelease` | 编译 Release 包（需配置签名，见 `RELEASE_SIGNING.md`） |
| `./gradlew clean` | 清理构建产物 |
| `./gradlew clean assembleDebug` | 先清理再编译 Debug 包 |
| `./gradlew installDebug` | 编译并安装 Debug 包到已连接设备/模拟器 |
| `./gradlew installRelease` | 编译并安装 Release 包到已连接设备/模拟器 |

**Windows** 下将 `./gradlew` 改为 `gradlew.bat`，例如：`gradlew.bat assembleDebug`。

### 快捷脚本

可使用项目根目录下的脚本简化操作：

| 脚本 | 说明 |
|------|------|
| `./clean-build.sh` | 清除缓存与构建产物（相当于深度 clean） |
| `./build-apk.sh` | 快速打包，生成 Debug APK |

使用前请为脚本添加执行权限：`chmod +x clean-build.sh build-apk.sh`。

### 输出位置

- **Debug APK**：`app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**：`app/build/outputs/apk/release/app-release.apk`（配置签名后）

### 使用 Android Studio 编译

1. 用 Android Studio 打开本项目根目录。
2. 等待 Gradle 同步完成。
3. 菜单 **Build → Make Project**（或快捷键）编译；**Run → Run 'app'** 运行到设备或模拟器。
