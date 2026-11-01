# ABB 逐行讲解（Android 离线阅读器）

把 `ABB Digital Twin`（C# / WinForms / ABB PCSDK 6.08）这个程序的**逐行中文讲解**做成了一个安卓 App：
930 行源码、每行一句讲解、87 个代码块、15 条问题与 15 条跨文件调用关系，全部离线。

## 它是什么

- 一个 `WebView` 壳 + 一个自包含的 `assets/index.html`（约 250 KB，样式/脚本/数据全在里面）。
- **零权限、零网络**：`AndroidManifest.xml` 里没有任何 `<uses-permission>`，装好就能用，飞机上也能看。
- 无 AndroidX、无第三方依赖，只有 `MainActivity.java` 一个类。

## 怎么出 APK（在 GitHub 上，不需要本机装 Android SDK）

1. 把本仓库推到 GitHub（或直接用网页 Upload files 上传整个目录）。
2. 打开仓库 → **Actions** → 左侧 **Build APK** → 右上 **Run workflow** → 运行。
3. 等 2~4 分钟，跑完后：
   - **Artifacts** 里下载 `ABB-Reader-apk`（zip，里面是 `ABB-Reader-debug.apk`）；
   - 或者打个 tag（`git tag v1.0 && git push --tags`）→ 自动生成 **Release**，手机浏览器直接点链接下载 APK。
4. 手机安装：允许"安装未知来源应用"，装完桌面出现「ABB 逐行讲解」。

> debug 版用 Android 自动生成的调试密钥签名，可以直接安装；不需要也不应该把签名密钥放进仓库。

## 版本约定（别乱改）

| 组件 | 版本 | 原因 |
| --- | --- | --- |
| AGP | 8.5.2 | 官方对应表：Gradle 最低/默认 8.7、JDK 17、最高 API 34 |
| Gradle | 8.7 | 由 `gradle/actions/setup-gradle@v4` 在云端下载，仓库里**没有** wrapper jar |
| JDK | 17 | AGP 8.5 的默认与最低 JDK |
| compileSdk / targetSdk | 34 | |
| minSdk | 24 | Android 7.0 起；WebView 支持所需的 ES6 语法 |

## 怎么改内容

讲解内容不在这里手改，回到上一级目录改源文件后重新生成：

```
_mobile\
├─ shell.html               页面骨架（CSS + JS 交互，占位符 __DATA__）
├─ src\*.cs                 7 个源文件（从原 zip 逐字节抽出）
├─ notes\*.json             逐行/逐块讲稿
├─ content\*.json           总览、概念、问题清单、块关系
└─ _tools\build.py          组装 + 校验 → 讲解器-移动版.html → 本仓库 assets/index.html
```

改完跑一次 `python _tools\build.py`，它会把成品同时写到：
- `_mobile\讲解器-移动版.html`（电脑上双击就能看）
- `repo\app\src\main\assets\index.html`（打进 APK 的那份）

然后 commit & push，Actions 自动重编。

## 目录

```
app/src/main/
├─ AndroidManifest.xml
├─ java/com/abbdt/reader/MainActivity.java
├─ assets/index.html                ← 讲解器（构建产物，由 build.py 生成）
└─ res/                            图标、字符串、主题
```

## 交互用法（App 内）

- 底部五个页签：总览 / 逐行 / 概念 / 模拟 / 问题。
- 「逐行」里：上方切换 7 个源文件；点任意一行 → 底部弹出该行讲解（可"上一行/下一行"连续读）。
- ⚙ 里可开「行下直接显示」，每行代码下面直接印讲解，适合通读；还有字号 A−/A+ 与「本文件块地图」。
- 🔍 搜索代码和讲解文本，点结果直接跳到那一行。
- 「模拟」按 `Form1.Designer.cs` 的真实坐标还原界面，可切「变位机/外轴」「断网」复现两个典型 bug。
