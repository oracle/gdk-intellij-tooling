/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.8.21"
  id("org.jetbrains.intellij") version "1.17.2"
}

group = "cloud.graal.gdk.plugin"
version = "1.8.0"

repositories {
  mavenLocal()
  mavenCentral()
  maven(url = "https://maven.oracle.com/public/")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2023.3.4")
  type.set("IU") // Target IDE Platform

  plugins.set(listOf("com.intellij.java", "org.jetbrains.plugins.gradle", "org.jetbrains.idea.maven"))
}

dependencies {
  implementation("cloud.graal.gdk:gdk-core:4.7.3.2") {
        exclude(group="org.slf4j", module="slf4j-api")
  }

  var micronautVersion = "4.7.10"

  runtimeOnly("io.micronaut:micronaut-http-server:$micronautVersion") {
        exclude(group="org.slf4j", module="slf4j-api")
  }

  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}


tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("233.3")
    untilBuild.set("")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  buildSearchableOptions {
    enabled = false
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}

tasks.test {
  useJUnitPlatform {
    include("cloud/graal/plugin/gdk/*")
    includeEngines("junit-jupiter")
  }
}
