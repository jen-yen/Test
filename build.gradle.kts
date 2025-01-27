import org.sourcegrade.jagr.gradle.task.grader.GraderRunTask

plugins {
    java
    application
    alias(libs.plugins.style)
    alias(libs.plugins.jagr.gradle)
    alias(libs.plugins.javafx)
    alias(libs.plugins.algomate)
}

version = file("version").readLines().first()


exercise {
    assignmentId.set("hProjekt")
}

submission {
    // ACHTUNG!
    // Setzen Sie im folgenden Bereich Ihre TU-ID (NICHT Ihre Matrikelnummer!), Ihren Nachnamen und Ihren Vornamen
    // in Anführungszeichen (z.B. "ab12cdef" für Ihre TU-ID) ein!
    // BEISPIEL:
    // studentId = "ab12cdef"
    // firstName = "sol_first"
    // lastName = "sol_last"
    studentId = ""
    firstName = ""
    lastName = ""

    // Optionally require own tests for mainBuildSubmission task. Default is false
    requireTests = false
}


dependencies {
    implementation(libs.annotations)
    implementation(libs.algoutils.student)
    implementation(libs.algoutils.tutor)
    testImplementation(libs.junit.core)
}

application {
    mainClass.set("hProjekt.Main")
}

tasks {
    val runDir = File("build/run")
    withType<JavaExec> {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
    }
    test {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
        useJUnitPlatform()
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<GraderRunTask> {
        doFirst {
            throw GradleException("Public tests will be released soon.")
        }
    }
    javadoc {
        options.jFlags?.add("-Duser.language=en")
        options.optionFiles = mutableListOf(project.file("src/main/javadoc.options"))
    }
}

javafx {
    version = "23"
    modules("javafx.controls", "javafx.graphics", "javafx.base", "javafx.fxml", "javafx.swing", "javafx.media")
}
