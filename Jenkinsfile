/**
 * Jenkins pipeline to build Android Kotlin project (Random String Generator)
 * Accepts parameters from Jenkins UI and automates build + Firebase upload
 */

pipeline {

    agent any

    tools {
            jdk 'jdk17'
        }

    environment {
        // Project paths
        BUILD_PATH = 'app/build/outputs/apk'
        BUILD_APK_PATH = ''
        DESTINATION_PATH = '/Users/linkan/Desktop/test'
        ANDROID_SDK_PATH = '/Users/linkan/Library/Android/sdk'

        // File references
        MY_PROPERTIES_FILE = "local.properties"
        GRADLE_FILE = 'app/build.gradle.kts'

        // Git & Firebase
        PROJECT_GIT_URL = 'https://github.com/Linkan1992/Random_String_Generator.git'
        APP_ID = '1:1050128836546:android:319e12c85411a6f08fd85b'
        FIREBASE_SERVICE_AC_KEY_PATH = '/Users/linkan/Downloads/totax-2b5f9-firebase-adminsdk-6iuqx-bf59c84cf0.json'
    }

    stages {

        stage('Check property file') {
            steps {
                script {
                    def propFilePath = "${env.WORKSPACE}/${MY_PROPERTIES_FILE}"
                    def propFileContent = """sdk.dir=${ANDROID_SDK_PATH}
VERSION_CODE=4
VERSION_NAME=6.38
RELEASE_NOTES=What's new: - Added new features - Fixed bug - Improved performance - new notification"""

                    if (!fileExists(propFilePath)) {
                        writeFile file: propFilePath, text: propFileContent
                        echo "✅ ${MY_PROPERTIES_FILE} created: ${propFilePath}"
                    } else {
                        echo "ℹ️ ${MY_PROPERTIES_FILE} already exists."
                    }
                }
            }
        }

        stage('Store Value') {
            steps {
                script {
                    def fileContent = readFile("${MY_PROPERTIES_FILE}").trim().split('\n').collectEntries { line ->
                        def (key, value) = line.split('=', 2)
                        [(key.trim()): value.trim()]
                    }

                    echo "📄 Current local.properties:\n${fileContent}"

                    if (params.VERSION_CODE) {
                        fileContent['VERSION_CODE'] = params.VERSION_CODE
                    }
                    if (params.VERSION_NAME) {
                        fileContent['VERSION_NAME'] = params.VERSION_NAME
                    }
                    if (params.RELEASE_NOTES) {
                        // Avoid line breaks breaking file structure
                        fileContent['RELEASE_NOTES'] = params.RELEASE_NOTES.replaceAll('\n', '\t')
                    }

                    writeFile file: "${MY_PROPERTIES_FILE}",
                        text: fileContent.collect { "${it.key}=${it.value}" }.join('\n')
                    echo "📝 Updated ${MY_PROPERTIES_FILE} with parameters."
                }
            }
        }

        stage('Input Parameter') {
            steps {
                script {
                    def fileContent = readFile("${MY_PROPERTIES_FILE}").trim().split('\n').collectEntries { line ->
                        def (key, value) = line.split('=', 2)
                        [(key.trim()): value.trim()]
                    }

                    echo "🧩 Loaded from local.properties:"
                    echo "VERSION_CODE=${fileContent['VERSION_CODE']}"
                    echo "VERSION_NAME=${fileContent['VERSION_NAME']}"
                    echo "RELEASE_NOTES=${fileContent['RELEASE_NOTES']}"

                    properties([
                        parameters([
                            choice(name: 'BUILD_TYPE', choices: ['debug', 'release'], description: 'Select build type'),
                            string(name: 'FIREBASE_TESTERS', defaultValue: 'uat-testers', description: 'Firebase tester group'),
                            string(name: 'VERSION_CODE', defaultValue: "${fileContent['VERSION_CODE']}", description: "Version code (Current: ${fileContent['VERSION_CODE']})"),
                            string(name: 'VERSION_NAME', defaultValue: "${fileContent['VERSION_NAME']}", description: "Version name (Current: ${fileContent['VERSION_NAME']})"),
                            text(name: 'RELEASE_NOTES', defaultValue: "${fileContent['RELEASE_NOTES'].replaceAll('\t', '\n')}", description: 'Release notes:')
                        ])
                    ])
                }
            }
        }

        stage('Checkout') {
            steps {
                script {
                    git branch: 'develop', url: "${PROJECT_GIT_URL}"
                    echo "📦 Checked out develop branch from ${PROJECT_GIT_URL}"
                }
            }
        }

        stage('Preparation') {
            steps {
                script {
                    def buildType = params.BUILD_TYPE ?: 'debug'
                    def buildPath = "${BUILD_PATH}/${buildType}"
                    BUILD_APK_PATH = "${buildPath}/*.apk"

                    sh "mkdir -p ${buildPath}"
                    sh "mkdir -p ${DESTINATION_PATH}"
                    sh "rm -rf ${buildPath}/*"

                    // Make gradlew executable to prevent permission errors
                    sh "chmod +x ./gradlew"

                    echo "🧱 Build folders prepared for ${buildType}"
                }
            }
        }

        stage('Update Version in Gradle File') {
            steps {
                script {
                    def gradleContent = readFile("${GRADLE_FILE}")

                    gradleContent = gradleContent
                        .replaceAll(/versionCode\s*=\s*\d+/, "versionCode = ${params.VERSION_CODE}")
                        .replaceAll(/versionName\s*=\s*\".*?\"/, "versionName = \"${params.VERSION_NAME}\"")

                    writeFile(file: "${GRADLE_FILE}", text: gradleContent)
                    echo "✅ Updated Gradle with versionCode=${params.VERSION_CODE}, versionName=${params.VERSION_NAME}"
                }
            }
        }

         stage('Check Java') {
                    steps {
                        sh 'java -version'
                    }
                }

        stage('Build') {
            steps {
                script {
                    def buildType = params.BUILD_TYPE
                    def buildCommand = "./gradlew assemble${buildType.capitalize()} --stacktrace --info --no-daemon"
                    echo "🏗️ Building ${buildType} APK..."
                    sh buildCommand
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: "${BUILD_PATH}/${params.BUILD_TYPE}/*.apk", allowEmptyArchive: true
                echo "📦 APK archived successfully."
            }
        }

        stage('Pause 3 seconds') {
            steps {
                script {
                    sleep(3)
                    echo "⏸️ Waited 3 seconds before copying."
                }
            }
        }

        stage('Copy to Desktop') {
            steps {
                script {
                    sh "cp ${BUILD_PATH}/${params.BUILD_TYPE}/*.apk ${DESTINATION_PATH}"
                    echo "✅ Copied APK to ${DESTINATION_PATH}"
                }
            }
        }

       /* stage('Upload to Firebase Distribution') {
            steps {
                script {
                    def apkPath = sh(script: "ls ${BUILD_PATH}/${params.BUILD_TYPE}/*.apk | head -n 1", returnStdout: true).trim()
                    echo "🚀 Uploading ${apkPath} to Firebase..."

                    env.GOOGLE_APPLICATION_CREDENTIALS = "${FIREBASE_SERVICE_AC_KEY_PATH}"

                    def firebaseUploadCmd = """
                    firebase appdistribution:distribute "${apkPath}" \
                      --app "${APP_ID}" \
                      --groups "${params.FIREBASE_TESTERS}" \
                      --release-notes "${params.RELEASE_NOTES.replaceAll('"', '\\"')}"
                    """

                    echo "Executing Firebase upload..."
                    sh firebaseUploadCmd
                }
            }
        }*/
    }
}
