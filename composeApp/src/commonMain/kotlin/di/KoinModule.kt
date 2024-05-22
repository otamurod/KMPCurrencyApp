package di

import com.russhwolf.settings.Settings
import data.local.repository.MongoDbRepositoryImpl
import data.local.repository.PreferencesRepositoryImpl
import data.remote.api.CurrencyApiServiceImpl
import domain.api.CurrencyApiService
import domain.repository.MongoDbRepository
import domain.repository.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.HomeViewModel

val appModule = module {
    single { Settings() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(settings = get()) }
    single<MongoDbRepository> { MongoDbRepositoryImpl() }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
    factory {
        HomeViewModel(
            preferencesRepository = get(),
            currencyApiService = get(),
            mongoDbRepository = get()
        )
    }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}