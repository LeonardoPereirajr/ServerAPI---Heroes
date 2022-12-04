package com.project.plugins


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*


fun Application.configureStatusPages() {
    install(StatusPages) {
    }
}
