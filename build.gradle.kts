
import groovy.lang.Closure
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.11"
    id("org.jetbrains.dokka")
    maven
}

group = "com.teamwizardry.mirror"
version = "0.0.1"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.4.0-M1")
    testCompile(kotlin("reflect"))
    testCompile(files("noParamNames/out"))
}

java.sourceSets {
    getByName("main").java.srcDirs("src/samples/kotlin")
    getByName("main").java.srcDirs("src/samples/java")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.javaParameters = true
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<DokkaTask> {
    val out = "$projectDir/docs"
    outputFormat = "html"
    outputDirectory = out
    jdkVersion = 8
    doFirst {
        println("Cleaning doc directory $out...")
        project.delete(fileTree(out))
    }

    kotlinTasks(Any().dokkaDelegateClosureOf<Any?> { emptyList<Any?>() })

    sourceDirs = listOf("src/main/kotlin").map { projectDir.resolve(it) }
    samples = listOf("src/samples/java", "src/samples/kotlin")
    includes = projectDir.resolve("src/main/docs").walkTopDown()
            .filter { it.isFile }
            .toList()
}

fun <T> Any.dokkaDelegateClosureOf(action: T.() -> Unit) = object : Closure<Any?>(this, this) {
    @Suppress("unused") // to be called dynamically by Groovy
    fun doCall() = org.gradle.internal.Cast.uncheckedCast<T>(delegate)!!.action()
}
