# Hanabi Remake

> 基于 Hanabi 3.1源码移植<br>
> 非完美移植 使用Claude Opus 4.8辅助

客户端主体代码位于 `cn.hanabi`,通过 `MixinMinecraft` 在游戏启动时自举(`Client.doLogin()` / `Client.Load()`);`com.xybakaqaq.hanabiremake.Hanabi` 是 Forge 的 `@Mod` 入口(模组注册锚点)。

## 环境要求

需要两个 JDK:

- **Java 17** — 用于运行 Gradle 构建(Gradle 8.8 不支持 Java 23+,用 17 即可)
- **Java 8** — 用于编译和运行模组代码(**推荐 Azul Zulu 8**,原因见下)

> 💡 **推荐使用带 JavaFX 的 Azul Zulu 8(非强制)。**
> 云音乐功能依赖 JavaFX。Azul Zulu 8 **本身不一定带 JavaFX**——需要自行下载选择 Azul 提供的**含 JavaFX 的 Zulu 8 构建**(内含 `jfxrt.jar`);本项目正是适配这个 Zulu 8 的 JavaFX 版本。Temurin / 大多数 OpenJDK 8 都不含 JavaFX。
> JavaFX 并不是硬性要求:缺少时云音乐会**自动禁用**、客户端照常运行(见下文)。
> 不过当前源码引用了 JavaFX,所以若改用不带 JavaFX 的 JDK 8 编译,需要自行提供 JavaFX 或移除云音乐模块。
> 目前 `build.gradle.kts` 已把工具链固定为 `JvmVendorSpec.AZUL`,Gradle 会自动选用 Zulu 8;想用别的 JDK 可自行调整该处。

## JavaFX / 网易云音乐说明

云音乐播放器(`cn.hanabi.gui.cloudmusic`)用 `javafx.scene.media` 播放音频。这里有两个坑,均已处理:

1. **编译期** — 源码引用了 JavaFX,需要 JDK 提供它。推荐用**含 JavaFX 的 Azul Zulu 8 构建**;本项目适配的就是该 Zulu 8 自带的 JavaFX(`jfxrt.jar`)版本。`build.gradle.kts` 已把工具链固定为 Azul 厂商,可自行调整。
2. **运行期** — Forge 的 `LaunchClassLoader` **不会**扫描 JDK 扩展目录里的 `jfxrt.jar`,直接运行会报
   `NoClassDefFoundError: javafx/...`。已在 `build.gradle.kts` 中从 Zulu 8 工具链定位其 `jre/lib/ext/jfxrt.jar`,
   作为 `runtimeOnly` 显式加入运行 classpath 解决。

此外 `MusicManager` 带有容错:启动时检测 JavaFX 是否可用,若不可用会打印
`[Hanabi] JavaFX not available - cloud music disabled` 并自动禁用云音乐,而**不会**让整个客户端崩溃。

## IntelliJ IDEA 配置与运行

1. Gradle 面板里把 **Gradle JVM 设为 Java 17**
2. 项目 SDK 设为 **Azul Zulu 8**
3. 同步 Gradle,IDEA 会自动生成名为 `Minecraft Client` 的运行配置,用它启动游戏
4. 改动了依赖(如 `build.gradle.kts`)后,记得先 **Reload Gradle Project** 再运行

> **不要用命令行 `./gradlew runClient`。**
> 本项目用的 loom 0.10 的 `RunGameTask` 在 Gradle 8.7+ 下会因任务属性校验(`property 'main' is missing an annotation`)直接失败。
> 这是模板 / 工具链的已知问题,与移植无关。请用上面 IDEA 生成的运行配置启动。

> **macOS 用户**:可能需要手动移除运行配置中的 `-XstartOnFirstThread` VM 参数。

## 构建

```bash
./gradlew build
```

- 最终产物:`build/libs/hanabiremake-1.0.0.jar`
- `build/intermediates` 下的 jar 为中间产物(未重映射 / 未混淆),不可直接使用