plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.dsprenkels.sss"
    compileSdk = 34

    ndkVersion = "25.2.9519653"

    defaultConfig {
        minSdk = 26

        ndk {
            ldLibs?.add("log")
        }

        externalNativeBuild {
            cmake {
                // explicitly build libs
                arguments.add("-DANDROID_STL=c++_shared")
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    externalNativeBuild {
        cmake {
            version = "3.22.1"
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("com.android.support.test:testing-support-lib:0.1")
}

if (!ext.has("signing.keyId") || ext["signing.keyId"].toString().trim().isEmpty()) ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
if (!ext.has("signing.key") || ext["signing.key"].toString().isEmpty()) ext["signing.key"] = System.getenv("SIGNING_KEY")
if (!ext.has("signing.password") || ext["signing.password"].toString().isEmpty()) ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
if (!ext.has("ossrhUsername") || ext["ossrhUsername"].toString().isEmpty()) ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
if (!ext.has("ossrhPassword") || ext["ossrhPassword"].toString().isEmpty()) ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
//println("Extras ${extra.properties.map { it.key + ":" + it.value }}")

val PUBLISH_GROUP_ID = "com.joinself"
val PUBLISH_VERSION = "1.0.0-SNAPSHOT"
group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

publishing {
    publications {
        register<MavenPublication>("release"){
            artifactId = "shamirsecretsharing"
            groupId = PUBLISH_GROUP_ID
            version = PUBLISH_VERSION

            afterEvaluate {
                from(components["release"])
            }
            pom {
                name = "shamirsecretsharing"
                description = "Self Android shamir secret sharing"
                url = "https://github.com/joinself/sss-android"
                licenses {
                    license {
                        name = "The MIT License"
                        url = "https://github.com/joinself/self-omemo-jni/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "joinself"
                        name = "Dev Ops"
                        email = "ops@joinself.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/joinself/sss-android.git"
                    developerConnection = "scm:git:ssh://github.com/joinself/sss-android.git"
                    url = "https://github.com/joinself/sss-android"
                }
            }
        }

        repositories {
            //:omemo:publishReleaseToMyRepoRepository
            maven {
                name = "MyRepo"
                val releasesRepoUrl = layout.buildDirectory.dir("repos/releases")
                val snapshotsRepoUrl = layout.buildDirectory.dir("repos/snapshots")
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            }
            // :omemo:publishReleaseToSonatypeRepository
            maven {
                name = "sonatype"
                val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                println("version ${version.toString()}")
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                credentials {
                    username = rootProject.ext["ossrhUsername"].toString()
                    password = rootProject.ext["ossrhPassword"].toString()
                }
            }
        }
    }
}

signing {
    if (rootProject.ext["signing.keyId"].toString().isNotBlank()) {
        useInMemoryPgpKeys(
            rootProject.ext["signing.keyId"].toString(),
            rootProject.ext["signing.key"].toString(),
            rootProject.ext["signing.password"].toString(),
        )
        sign(publishing.publications)
    }
}
