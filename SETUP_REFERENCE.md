# Complete Android Temi SDK Setup - Reference Guide

## Summary of Changes

This document provides a complete reference of all files configured for the Temi Robot SDK Android project.

---

## 1. settings.gradle.kts

**Location**: `/settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.robotemi.com/repository/maven-public/")
    }
}

rootProject.name = "AlliswellTemi"
include(":app")
```

**Key Points:**
- Temi Maven repository configured
- Dependency resolution management enabled
- Project name set to "AlliswellTemi"

---

## 2. build.gradle.kts (Project Level)

**Location**: `/build.gradle.kts`

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
```

**Key Points:**
- Plugins defined but NOT applied (apply false)
- No duplicate plugin declarations
- Versions explicitly specified

---

## 3. app/build.gradle.kts

**Location**: `/app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

kotlin {
    jvmToolchain(8)
}

android {
    namespace = "com.example.alliswelltemi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.alliswelltemi"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Temi Robot SDK
    implementation("com.robotemi:sdk:1.135.0")

    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

**Key Points:**
- Kotlin JVM Toolchain set to 8
- ViewBinding enabled
- All required Temi SDK dependencies
- Android API levels: min 26, target 34, compile 34

---

## 4. AndroidManifest.xml

**Location**: `/app/src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AlliswellTemi">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

**Key Points:**
- INTERNET, RECORD_AUDIO, CAMERA permissions declared
- MainActivity declared as launcher activity
- android:exported="true" for Android 12+

---

## 5. MainActivity.kt

**Location**: `/app/src/main/java/com/example/alliswelltemi/MainActivity.kt`

```kotlin
package com.example.alliswelltemi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnRobotReadyListener

class MainActivity : AppCompatActivity(), OnRobotReadyListener {

    private lateinit var robot: Robot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Temi Robot SDK
        Robot.getInstance().addOnRobotReadyListener(this)
    }

    override fun onRobotReady(robot: Robot?) {
        this.robot = robot ?: return

        // Make Temi speak the test message
        this.robot.speak(
            com.robotemi.sdk.TtsRequest.Builder()
                .setLanguage("en-US")
                .setText("Hello, I am Temi and I am ready")
                .build()
        )
    }

    override fun onPause() {
        super.onPause()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }

    override fun onResume() {
        super.onResume()
        Robot.getInstance().addOnRobotReadyListener(this)
    }
}
```

**Key Points:**
- Implements OnRobotReadyListener for Temi SDK
- Robot initialization in onCreate()
- Speech synthesis in onRobotReady()
- Proper lifecycle management (pause/resume)

---

## 6. activity_main.xml

**Location**: `/app/src/main/res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textSize="24sp"
        android:textStyle="bold" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Temi Robot SDK Initialized"
        android:textSize="16sp" />

</LinearLayout>
```

**Key Points:**
- Simple, clean layout
- ViewBinding compatible
- Centered content with padding

---

## Implementation Checklist

✅ **Project Configuration**
- [ ] Language: Kotlin
- [ ] Min SDK: 26
- [ ] Target SDK: 34
- [ ] Compile SDK: 34
- [ ] ViewBinding: Enabled

✅ **Gradle Configuration**
- [ ] settings.gradle.kts configured with Temi maven repo
- [ ] build.gradle.kts has plugin definitions without apply
- [ ] app/build.gradle.kts applies plugins and includes dependencies
- [ ] No duplicate plugin declarations

✅ **Android Configuration**
- [ ] AndroidManifest.xml has permissions (INTERNET, RECORD_AUDIO, CAMERA)
- [ ] MainActivity activity declared and exported
- [ ] Intent filter for MAIN/LAUNCHER

✅ **Code Implementation**
- [ ] MainActivity.kt implements OnRobotReadyListener
- [ ] Robot SDK initialized in onCreate()
- [ ] Temi speaks test message in onRobotReady()
- [ ] Lifecycle listeners properly managed

✅ **Resources**
- [ ] activity_main.xml layout created
- [ ] strings.xml has app_name (from existing project)

---

## Build & Run Commands

```bash
# Clean and rebuild
./gradlew clean build

# Install on connected device
./gradlew installDebug

# Run app
./gradlew installDebug && adb shell am start -n com.example.alliswelltemi/.MainActivity

# View logs
adb logcat | grep -i temi

# Build release
./gradlew bundleRelease
```

---

## Expected Behavior

1. App launches and displays MainActivity
2. Temi Robot SDK initializes
3. onRobotReady() callback fires
4. Robot speaks: "Hello, I am Temi and I am ready"
5. App displays "AlliswellTemi" and "Temi Robot SDK Initialized"

---

## Troubleshooting

**Issue**: Build fails with "Unresolved reference 'MainActivity'"
- **Solution**: Ensure MainActivity.kt is in correct package: `com/example/alliswelltemi/`

**Issue**: Gradle won't sync
- **Solution**: Run `./gradlew clean` and invalidate caches in Android Studio

**Issue**: Robot SDK not found
- **Solution**: Verify internet connection and Temi maven repository URL is correct

**Issue**: App crashes on startup
- **Solution**: Check logcat for errors, ensure all permissions are granted

---

## Project Structure

```
AlliswellTemi/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── local.properties
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/alliswelltemi/
│       │   └── MainActivity.kt
│       └── res/
│           ├── drawable/
│           ├── mipmap-*/
│           ├── layout/
│           │   └── activity_main.xml
│           ├── values/
│           │   ├── colors.xml
│           │   ├── strings.xml
│           │   └── themes.xml
│           └── xml/
│               ├── backup_rules.xml
│               └── data_extraction_rules.xml
└── gradle/
    └── wrapper/
        └── gradle-wrapper.jar
```

---

## Dependencies Summary

| Dependency | Version | Repository |
|-----------|---------|-----------|
| com.robotemi:sdk | 1.135.0 | maven.robotemi.com |
| androidx.core:core-ktx | 1.12.0 | mavenCentral |
| androidx.appcompat:appcompat | 1.6.1 | google |
| com.google.android.material:material | 1.11.0 | google |
| androidx.lifecycle:lifecycle-runtime-ktx | 2.6.2 | google |
| junit:junit | 4.13.2 | mavenCentral |
| androidx.test.ext:junit | 1.1.5 | google |
| androidx.test.espresso:espresso-core | 3.5.1 | google |

---

## Notes

- All code follows Android and Kotlin best practices
- ViewBinding is enabled for type-safe view access
- Robot listener is properly managed in lifecycle callbacks
- No Groovy syntax used - pure Kotlin DSL
- Production-ready and minimal configuration

---

**Generated**: April 15, 2026
**Status**: ✅ Complete and Ready for Development

