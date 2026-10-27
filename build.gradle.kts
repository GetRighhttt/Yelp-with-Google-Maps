plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.parcelize) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

val ktlint =
    configurations.create("ktlint") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.SHADOWED))
        }
    }

dependencies {
    ktlint(libs.ktlint)
}

tasks.register<JavaExec>("formatKotlin") {
    group = "formatting"
    description = "Formats Kotlin sources and build scripts using .editorconfig."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args("--format", "**/src/**/*.kt", "*.gradle.kts", "app/*.gradle.kts")
}

tasks.register<JavaExec>("checkKotlinFormat") {
    group = "verification"
    description = "Checks Kotlin source and build script formatting."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args("**/src/**/*.kt", "*.gradle.kts", "app/*.gradle.kts")
}
