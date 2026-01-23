import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.components:components-resources:1.10.0")
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("me.friwi:jcefmaven:141.0.10")
            implementation("com.formdev:flatlaf:3.7")
            implementation("com.formdev:flatlaf-extras:3.7")
        }
    }
}


compose.desktop {
    application {
        mainClass = "io.github.feather_browser.feather.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "FeatherBrowser"
            packageVersion = "1.0.0"

            windows {
                console = true
            }
        }

        buildTypes.release {
            proguard {
                isEnabled = false
            }
        }
    }
}
