import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
}

group = "inspired"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	val cucumberVersion = "7.15.0"
	val springfoxVersion = "3.0.0"

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")


	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.keycloak:keycloak-spring-boot-starter:14.0.0")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
	implementation("org.springframework.boot:spring-boot-devtools")
	implementation("io.springfox:springfox-swagger2:${springfoxVersion}")
	implementation("io.springfox:springfox-swagger-ui:${springfoxVersion}")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("io.cucumber:cucumber-java:${cucumberVersion}")
	testImplementation("io.cucumber:cucumber-junit:${cucumberVersion}")
	testImplementation("io.cucumber:cucumber-spring:${cucumberVersion}")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
