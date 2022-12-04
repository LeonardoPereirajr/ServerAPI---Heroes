package com.project.di

import com.project.repository.HeroRepository
import com.project.repository.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module{
    single<HeroRepository> {
        HeroRepositoryImpl()
    }
}