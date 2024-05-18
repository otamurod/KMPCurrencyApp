package di

import com.russhwolf.settings.Settings
import data.local.repository.PreferencesRepositoryImpl
import data.remote.api.CurrencyApiServiceImpl
import domain.api.CurrencyApiService
import domain.repository.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}