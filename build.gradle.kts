plugins {
    id("groovy")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.8")
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
//    implementation("com.lmax:disruptor:4.0.0.RC1")
    implementation("com.lmax:disruptor:3.4.2")
    implementation("com.google.guava:guava:31.1-jre")
}

tasks.test {
    useJUnitPlatform()
}