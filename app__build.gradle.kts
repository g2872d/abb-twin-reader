plugins {
    id("com.android.application")
}

android {
    namespace = "com.abbdt.reader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.abbdt.reader"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            // 产物名固定，便于 workflow 里取文件
            applicationIdSuffix = ""
        }
        getByName("release") {
            isMinifyEnabled = false
            // 不配置签名：workflow 只出 debug APK（自带 debug 签名，可直接安装）
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 把 assets 里的 index.html 原样打包，不做任何压缩转换
    androidResources {
        noCompress += listOf("html")
    }
}

dependencies {
    // 刻意零依赖：不用 AndroidX、不用第三方库，构建快、体积小、离线可用
}
