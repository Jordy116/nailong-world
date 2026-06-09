# 🐉 奶龍世界 — Nailong World

**奶龍世界** 是一款以「奶龍」為主題的 Android 娛樂與社區平台 App，採用 **Kotlin + Jetpack Compose** 原生開發，提供流暢的現代化 UI 體驗。

---

## 📱 功能模塊

| 模塊 | 說明 |
|------|------|
| **主頁** | 奶龍藝術館標題、直播間快捷入口、熱門遊戲推薦、推介內容、每日簽到 |
| **遊戲** | 遊戲列表（熱門 / 推薦 / 最新），分類篩選與卡片展示 |
| **社群** | 社區動態牆，用戶發帖互動（原型階段使用模擬數據） |
| **我的** | 個人資料、連續簽到統計、收藏管理、成就與設定 |

---

## 🛠 技術棧

| 技術 | 版本 |
|------|------|
| 語言 | Kotlin 1.9.22 |
| UI | Jetpack Compose (BOM 2024.01.00) |
| 架構 | MVVM (ViewModel + StateFlow) |
| 導航 | Navigation Compose |
| 圖片加載 | Coil 2.5.0 |
| 網絡請求 | Retrofit 2.9.0 + OkHttp 4.12.0 |
| 依賴管理 | Gradle 8.4 + Kotlin DSL |
| 最低支持 | Android 8.0 (API 26) |
| 目標 SDK | Android 14 (API 34) |

---

## 🚀 本地開發環境配置

### 前置要求

- **Android Studio** Hedgehog (2023.1.1) 或更新版本
- **JDK 17**（Android Studio 內置或手動安裝）
- **Android SDK** 35+

### 克隆與運行

```bash
# 1. 克隆專案
git clone https://github.com/<你的組織>/nailong-world.git
cd nailong-world

# 2. 用 Android Studio 打開專案（File → Open → 選擇 nailong-world 目錄）
#    Android Studio 會自動下載 Gradle Wrapper 和依賴

# 3. 同步 Gradle（點擊 Sync Now）

# 4. 連接設備或啟動模擬器，點擊 Run ▶
```

> ⚠️ 如果遇到 SDK 路徑問題，請在專案根目錄創建 `local.properties` 文件：
> ```
> sdk.dir=/path/to/your/Android/Sdk
> ```

### 命令行構建（可選）

```bash
# 確保 gradlew 有執行權限
chmod +x gradlew

# 運行 lint 檢查
./gradlew lintDebug

# 編譯 Debug APK
./gradlew assembleDebug

# APK 產物在 app/build/outputs/apk/debug/app-debug.apk
```

---

## 🤖 CI/CD：GitHub Actions 自動構建

本專案已配置完整的 **GitHub Actions** 工作流，每次推送代碼或發起 PR 到 `main` / `developer` 分支時，會自動執行：

1. ✅ 檢出代碼
2. ✅ 配置 JDK 17
3. ✅ 快取 Gradle 依賴（加速後續構建）
4. ✅ 授予 Gradle 執行權限
5. ✅ 運行 Lint 檢查
6. ✅ 編譯 Debug APK
7. ✅ 上傳 APK 到 GitHub Artifacts

### 觸發條件

| 事件 | 分支 |
|------|------|
| `git push` | `main`, `developer` |
| Pull Request | target: `main`, `developer` |

### 下載 APK

構建完成後：

1. 打開 GitHub 倉庫頁面 → **Actions** 標籤頁
2. 點擊最新的成功工作流運行記錄
3. 在 **Artifacts** 區域點擊 `nailong-world-debug` 下載 APK
4. 將 APK 傳入手機安裝即可測試

> APK 會保留 7 天，過期後自動刪除。

---

## 📁 專案目錄結構

```
nailong-world/
├── .github/workflows/
│   └── android-build.yml          # CI/CD 工作流配置
├── app/
│   ├── src/main/
│   │   ├── java/com/nailong/world/
│   │   │   ├── MainActivity.kt           # 入口 Activity + 底部導航
│   │   │   ├── ui/
│   │   │   │   ├── home/HomeScreen.kt    # 主頁面
│   │   │   │   ├── game/GameScreen.kt    # 遊戲頁面
│   │   │   │   ├── community/CommunityScreen.kt  # 社群頁面
│   │   │   │   ├── profile/ProfileScreen.kt      # 我的頁面
│   │   │   │   ├── navigation/Navigation.kt      # 底部導航定義
│   │   │   │   ├── components/SharedComponents.kt # 共用 UI 組件
│   │   │   │   └── theme/               # 主題（Color / Type / Theme）
│   │   │   ├── data/
│   │   │   │   ├── model/Models.kt      # 數據模型
│   │   │   │   └── repository/NailongRepository.kt # 資料倉儲（API 接口）
│   │   │   └── viewmodel/               # ViewModel
│   │   ├── res/
│   │   │   ├── values/strings.xml       # 字符串資源（繁體中文）
│   │   │   ├── values/colors.xml        # 顏色資源
│   │   │   └── values/themes.xml        # 主題樣式
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                 # App 模塊構建配置
│   └── proguard-rules.pro               # ProGuard 混淆規則
├── build.gradle.kts                     # 根構建配置
├── settings.gradle.kts                  # 項目設置
├── gradle.properties                    # Gradle 屬性
├── gradlew / gradlew.bat               # Gradle Wrapper
├── gradle/wrapper/                      # Gradle Wrapper JAR & 配置
└── README.md                            # 本文件
```

---

## 🤝 團隊協作規範

### 分支策略

| 分支 | 用途 |
|------|------|
| `main` | 穩定版，僅合入經過 Review 的代碼 |
| `developer` | 開發主分支，日常集成 |
| `feature/*` | 功能分支（例如 `feature/live-stream`） |
| `fix/*` | 修復分支（例如 `fix/crash-on-home`） |

### 提交信息格式

```
[模塊] 簡短描述

例：
[Home] 完成直播間快捷入口卡片
[Game] 新增遊戲分類篩選功能
[CI] 修復 Gradle 快取 key 生成邏輯
```

### PR 流程

1. 從 `developer` 創建功能分支
2. 開發完成後發起 PR 到 `developer`
3. CI 自動構建檢查通過後 Review
4. Merge 到 `developer`（必要時 squash）

---

## 📄 許可證

本項目僅供學習與交流使用。奶龍相關 IP 歸屬於原作者。

---

開發愉快！🐉✨
