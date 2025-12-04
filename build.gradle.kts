// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
}

subprojects {
    apply(plugin = "maven-publish")

    afterEvaluate {
        // Only configure publishing for Android library modules
        if (plugins.hasPlugin("com.android.library")) {
            // Configure the publishing extension
            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("release") {
                        // Get the release component from Android
                        from(components["release"])

                        // Customize these for your project
                        groupId = "com.github.chiali-achraf"
                        artifactId = project.name // This will be :linechart, :common, etc.
                        version = "1.0.0"


                    }
                }
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}