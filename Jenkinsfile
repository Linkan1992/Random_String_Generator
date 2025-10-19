/**
 * Jenkins pipeline to build Android Kotlin project (Random String Generator)
 * Accepts parameters from Jenkins UI and automates build + Firebase upload
 */

pipeline {

    agent any

    tools {
            jdk 'My JDK 17'
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
        APP_ID = '1:294722677248:android:2404a46b35875aea3ac6e5'
        FIREBASE_SERVICE_AC_KEY_PATH = '/Users/linkan/Downloads/loginfirebase-b7d06-firebase-adminsdk-fbsvc-5fa0ac06bd.json'

        // Firebase CLI paths
        NODE_PATH = '/opt/homebrew/bin/node'
        FIREBASE_PATH = '/opt/homebrew/bin/firebase'
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
                            string(name: 'FIREBASE_TESTERS', defaultValue: 'rbl-android-unofficial', description: 'Firebase tester group'),
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

        /* stage('Copy to Desktop') {
            steps {
                script {
                    sh "cp ${BUILD_PATH}/${params.BUILD_TYPE} *//*.apk ${DESTINATION_PATH}"
                    echo "✅ Copied APK to ${DESTINATION_PATH}"
                }
            }
        } */

       /* stage('Upload to Firebase Distribution') {
            steps {
                script {
                    def apkPath = sh(script: "ls ${BUILD_PATH}/${params.BUILD_TYPE} *//*.apk | head -n 1", returnStdout: true).trim()
                    echo "🚀 Uploading ${apkPath} to Firebase..."

                    env.GOOGLE_APPLICATION_CREDENTIALS = "${FIREBASE_SERVICE_AC_KEY_PATH}"

                    def firebaseUploadCmd = """
                    /opt/homebrew/bin/node /opt/homebrew/bin/firebase appdistribution:distribute "${apkPath}" \
                      --app "${APP_ID}" \
                      --testers "${params.FIREBASE_TESTERS}" \
                      --release-notes "${params.RELEASE_NOTES.replaceAll('"', '\\"')}"
                    """

                    echo "Executing Firebase upload..."
                    sh firebaseUploadCmd
                }
            }
        } */

        stage('Upload to Firebase Distribution') {
            steps {
                script {
                    def apkPath = sh(script: "ls ${BUILD_PATH}/${params.BUILD_TYPE}/*.apk | head -n 1", returnStdout: true).trim()

                    env.GOOGLE_APPLICATION_CREDENTIALS = "${FIREBASE_SERVICE_AC_KEY_PATH}"

                    // Parse input list (comma-separated)
                    def testersInput = params.FIREBASE_TESTERS ?: ''
                    def entries = testersInput.split(',').collect { it.trim() }.findAll { it }

                    // Separate emails and group names
                    def emailList = entries.findAll { it.contains('@') }
                    def groupList = entries.findAll { !it.contains('@') }

                    // Prepare CLI arguments
                    def emailArg = emailList ? "--testers \"${emailList.join(',')}\"" : ""
                    def groupArg = groupList ? "--groups \"${groupList.join(',')}\"" : ""

                    // Escape release notes
                    def releaseNotesEscaped = params.RELEASE_NOTES.replaceAll('"', '\\"')

                    // Build Firebase upload command
                    def firebaseUploadCmd = """
                        ${env.NODE_PATH} ${env.FIREBASE_PATH} appdistribution:distribute "${apkPath}" \
                          --app "${APP_ID}" \
                          ${emailArg} \
                          ${groupArg} \
                          --release-notes "${releaseNotesEscaped}"
                    """.trim()

                    echo "👥 Groups: ${groupList}"
                    echo "📧 Testers: ${emailList}"
                    echo "🚀 Uploading ${apkPath} to Firebase..."
                    sh firebaseUploadCmd
                }
            }
        }


        stage('Commit & Push Updated Gradle Version') {
          when { expression { currentBuild.currentResult == 'SUCCESS' } }
          steps {
            script {
              echo "Preparing to commit updated Gradle version..."

              sh '''
                git config user.name "Linkan Chauhan CI"
                git config user.email "linkanchauhan@gmail.com"
              '''

              // ensure branch
              sh 'git fetch origin'
              sh "git checkout develop"

              // add files if changed
              sh """
                if [ -n "$(git status --porcelain ${GRADLE_FILE} || true)" ]; then
                  git add ${GRADLE_FILE}
                  git commit -m "uto-update versionCode=${params.VERSION_CODE}, versionName=${params.VERSION_NAME} [Jenkins Build #${BUILD_NUMBER}]"
                  echo "Committed changes"
                else
                  echo "No changes to commit for ${GRADLE_FILE}"
                fi
              """

              // push using token from Jenkins credentials
              withCredentials([string(credentialsId: 'GITHUB_PAT', variable: 'GIT_TOKEN')]) {
                sh """
                  git remote set-url origin https://${GIT_TOKEN}@github.com/Linkan1992/Random_String_Generator.git
                  git push origin develop || ( echo "Push failed"; exit 1 )
                """
              }

              echo "Pushed updated Gradle version to GitHub successfully."
            }
          }
        }


    }
}
