package xyz.fiwka.budget.discovery

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer
@SpringBootApplication
class DiscoveryApplication

fun main(args: Array<String>) {
    SpringApplication.run(DiscoveryApplication::class.java, *args)
}