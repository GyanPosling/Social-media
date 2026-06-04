plugins {
	kotlin("jvm") version "1.9.25" apply false
	kotlin("plugin.spring") version "1.9.25" apply false
	kotlin("plugin.jpa") version "1.9.25" apply false
	id("org.springframework.boot") version "3.5.14" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.socialmedia"
	version = "0.0.1-SNAPSHOT"
}

subprojects {
	plugins.withId("org.jetbrains.kotlin.jvm") {
		extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
			compilerOptions {
				freeCompilerArgs.addAll("-Xjsr305=strict")
			}
		}
	}

	plugins.withId("java") {
		extensions.configure<JavaPluginExtension> {
			toolchain {
				languageVersion = JavaLanguageVersion.of(21)
			}
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
