plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.5"
}

java {
    //TODO: idk
    //toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // let me use java 8
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar() // Generate documentation
    withSourcesJar() // Generate source code (not decompiled)
}

// Delegate artifactId value using the extra property
// Allows reusing the value as --> 'project.extra["artifactId"]' <--
// The name value simply throws error. (I don't know why)
val artifactId: String by extra("PvPEvent")

group = "xyz.dysaido"
version = "1.2.11"

repositories {
    mavenCentral() // public maven repo
    mavenLocal() // local maven repo

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "placeholderapi"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    // Spigot, Placeholder, Vault, TAB, (Some Clan/Team plugin?), Multiverse-core
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("org.bstats:bstats-bukkit:3.0.2") // shadowJar like it

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    processResources {
        eachFile {
            expand("version" to project.version) // Placeholder
        }
    }
    build {
        // Extending the build task to depend on shadowJar.
        // This ensures that the plugin is built with the shaded JAR
        // without needing to explicitly call the shadowJar task separately.
        // Summary: Just use build and don't call shadowJar
        dependsOn(named("shadowJar"))

        // Extending the build task to depend on publishToMavenLocal.
        // Summary: ensures that the plugin is published into the local maven repository
        dependsOn(named("publishToMavenLocal"))
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release = 8
    }

    javadoc {
        // Ignore Javadoc errors
        // It can drop any errors sooo use it
        isFailOnError = false

        with(options as StandardJavadocDocletOptions) {
            addStringOption("Xdoclint:none", "-quiet") // Don't show warnings
            addStringOption("encoding", "UTF-8") // Java uses ASCII :(
            addStringOption("charSet", "UTF-8")
        }
    }

    shadowJar {
        archiveClassifier = ""  // Remove "*-all" postfix
        relocate("org.bstats", "xyz.dysaido.pvpevent.libs.org.bstats")
    }

    test {
        useJUnitPlatform()
    }
}

/*
tasks.withType<JavaCompile>().configureEach {
    // Increases build performance because each tasks get a separated JVM
    // Less garbage collection, higher memory usage
    // Suggested to use while using parallel property
    options.isFork = false
}
*/

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.extra["artifactId"] as String
            version = project.version as String

            from(components["java"])
        }
    }
}

configurations {
    testImplementation {
        // Extending the testImplementation configuration from compileOnly
        // to ensure compile-time dependencies are also available during testing.
        // This is useful for cases where some dependencies are needed for
        // compilation but not included in the runtime classpath,
        // yet they are required for unit tests.
        // Summary: we can test with API functions while we don't shade API into the plugin.
        extendsFrom(compileOnly.get())
    }
}
