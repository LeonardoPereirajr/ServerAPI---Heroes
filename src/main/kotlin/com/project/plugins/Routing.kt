package com.project.plugins

import com.project.routes.getAllHeroes
import com.project.routes.root
import com.project.routes.searchHeroes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
            call.respondText(text = "400: $cause" , status = HttpStatusCode.BadRequest)
        }
    }
    routing {
        root()
        getAllHeroes()
        searchHeroes()
        static("/images") {
            resources("/images")
        }
    }
}

