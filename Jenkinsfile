pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        SPRING_DATASOURCE_URL      = 'jdbc:postgresql://host.docker.internal:5433/field_service_db'
        SPRING_DATASOURCE_USERNAME = 'postgres'
        SPRING_DATASOURCE_PASSWORD = credentials('field-service-db-password')
        JWT_SECRET                 = 'ci-only-dummy-secret-key-that-is-long-enough-for-hmac-sha384-1234567890'
        MAIL_PASSWORD              = 'dummy'
        TWILIO_ACCOUNT_SID         = 'dummy'
        TWILIO_AUTH_TOKEN          = 'dummy'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build and test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean verify'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
    }
}
