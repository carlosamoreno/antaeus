plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":pleo-antaeus-data"))
    api(project(":pleo-antaeus-models"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
    implementation("com.rabbitmq:amqp-client:5.9.0")
    implementation("com.google.code.gson:gson:2.8.5")
}