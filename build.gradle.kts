import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

buildscript {
  dependencies {
    val kotlinVersion = "1.8.10"
//    val kspVersion = System.getenv("MOSHI_KSP_VERSION")
//      ?: libs.versions.ksp.get()
    classpath(kotlin("gradle-plugin", version = kotlinVersion))
//    classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kspVersion")
    classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.8.10-1.0.9")

    // https://github.com/melix/japicmp-gradle-plugin/issues/36
    classpath("com.google.guava:guava:31.1-jre")
  }
}

plugins {
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.japicmp) apply false
}

allprojects {
  group = "com.github.raoulsson"  // Use your GitHub username
  version = "2.0.2-raoulsson"     // Use the specific version or tag

  repositories {
    mavenCentral()
  }
}

subprojects {
  // Apply with "java" instead of just "java-library" so kotlin projects get it too
  pluginManager.withPlugin("java") {
    configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
      }
    }
    if (project.name != "records-tests") {
      tasks.withType<JavaCompile>().configureEach {
        options.release.set(8)
      }
    }
  }

  pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    tasks.withType<KotlinCompile>().configureEach {
      kotlinOptions {
        // TODO re-enable when no longer supporting multiple kotlin versions
//        @Suppress("SuspiciousCollectionReassignment")
//        freeCompilerArgs += listOf("-progressive")
        jvmTarget = libs.versions.jvmTarget.get()
      }
    }

//    configure<KotlinProjectExtension> {
//      if (project.name != "examples") {
//        explicitApi()
//      }
//    }
  }
}

allprojects {
  tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
      reportUndocumented.set(false)
      skipDeprecated.set(true)
      jdkVersion.set(8)
      perPackageOption {
        matchingRegex.set("com\\.squareup.moshi\\.internal.*")
        suppress.set(true)
      }
    }
    if (name == "dokkaHtml") {
      outputDirectory.set(rootDir.resolve("docs/1.x"))
      dokkaSourceSets.configureEach {
        skipDeprecated.set(true)
        externalDocumentationLink {
          url.set(URL("https://square.github.io/okio/2.x/okio/"))
        }
      }
    }
  }

//  plugins.withId("com.vanniktech.maven.publish.base") {
//    configure<MavenPublishBaseExtension> {
//      publishToMavenCentral(SonatypeHost.S01)
//      signAllPublications()
//      pom {
//        description.set("A modern JSON API for Android and Java")
//        name.set(project.name)
//        url.set("https://github.com/square/moshi/")
//        licenses {
//          license {
//            name.set("The Apache Software License, Version 2.0")
//            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
//            distribution.set("repo")
//          }
//        }
//        scm {
//          url.set("https://github.com/square/moshi/")
//          connection.set("scm:git:git://github.com/square/moshi.git")
//          developerConnection.set("scm:git:ssh://git@github.com/square/moshi.git")
//        }
//        developers {
//          developer {
//            id.set("square")
//            name.set("Square, Inc.")
//          }
//        }
//      }
//    }
//  }
}
