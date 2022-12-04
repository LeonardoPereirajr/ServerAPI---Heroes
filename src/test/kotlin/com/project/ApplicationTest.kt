package com.project

import com.project.models.ApiResponse
import com.project.repository.HeroRepository
import com.project.repository.NEXT_PAGE_KEY
import com.project.repository.PREVIOUS_PAGE_KEY
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `acess Root Endpoint, Assert Correct Information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Welcome to Boruto API", response.content)
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `acess all heroes endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "boruto/heroes").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    sucess = true,
                    message = "OK",
                    prevPage = null,
                    nextPage = 2,
                    heroes = heroRepository.page1
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("expected: $expected")
                println("actual: $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `acess all heroes endpoint, query second page, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "boruto/heroes?page=2").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    sucess = true,
                    message = "OK",
                    prevPage = 1,
                    nextPage = 3,
                    heroes = heroRepository.page2
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("expected: $expected")
                println("actual: $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    fun calculatePage(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 1) {
            prevPage = null
        }
        if (page == 5) {
            nextPage = null
        }
        return mapOf(
            PREVIOUS_PAGE_KEY to prevPage,
            NEXT_PAGE_KEY to nextPage
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `acess all heroes endpoint, query all pages, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )
            pages.forEach { page ->
                handleRequest(HttpMethod.Get, "boruto/heroes?page=$page").apply {
                    assertEquals(
                        expected = HttpStatusCode.OK,
                        actual = response.status()
                    )
                    val expected = ApiResponse(
                        sucess = true,
                        message = "OK",
                        prevPage = calculatePage(page = page)[PREVIOUS_PAGE_KEY],
                        nextPage = calculatePage(page = page)[NEXT_PAGE_KEY],
                        heroes = heroes[page - 1]
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    println("PREV PAGE: ${calculatePage(page = page)[PREVIOUS_PAGE_KEY]}")
                    println("NEXT PAGE: ${calculatePage(page = page)[NEXT_PAGE_KEY]}")
                    println("HEROES: ${heroes[page - 1]}")
                    assertEquals(
                        expected = expected,
                        actual = actual
                    )
                }
            }
        }
    }
    @ExperimentalSerializationApi
    @Test
    fun `acess all heroes endpoint, query not exist page, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "boruto/heroes?page=6").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    sucess = false,
                    message = "Heroes not found",
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("expected: $expected")
                println("actual: $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }
    @ExperimentalSerializationApi
    @Test
    fun `acess all heroes endpoint, query not exist information page, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "boruto/heroes?page=invalid").apply {
                assertEquals(
                    expected = HttpStatusCode.BadRequest,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    sucess = false,
                    message = "Invalid page number",
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("expected: $expected")
                println("actual: $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }
    @ExperimentalSerializationApi
    @Test
    fun `acess search heroes endpoint, query hero name, assert single heroe`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "boruto/heroes?search?name=sa").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes?.size
                assertEquals(
                    expected = 3,
                    actual = actual
                )
            }
        }
    }
}