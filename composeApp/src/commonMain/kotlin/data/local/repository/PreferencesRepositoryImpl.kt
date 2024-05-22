package data.local.repository

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.model.CurrencyCode
import domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class PreferencesRepositoryImpl(
    private val settings: Settings
) : PreferencesRepository {
    companion object {
        const val TIME_STAMP_KEY = "lastUpdatedTime"
        const val SOURCE_CURRENCY_KEY = "sourceCurrency"
        const val TARGET_CURRENCY_KEY = "targetCurrency"

        val DEFAULT_SOURCE_CURRENCY = CurrencyCode.USD.name
        val DEFAULT_TARGET_CURRENCY = CurrencyCode.EUR.name
    }

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()
    override suspend fun saveLastUpdatedTime(lastUpdatedTime: String) {
        flowSettings.putLong(
            key = TIME_STAMP_KEY,
            value = Instant.parse(lastUpdatedTime).toEpochMilliseconds()
        )
    }

    override suspend fun isDataFresh(currentTimeStamp: Long): Boolean {
        val savedTimeStamp = flowSettings.getLong(
            key = TIME_STAMP_KEY,
            defaultValue = 0L
        )

        return if (savedTimeStamp != 0L) {
            val currentInstant = Instant.fromEpochMilliseconds(currentTimeStamp)
            val savedInstant = Instant.fromEpochMilliseconds(savedTimeStamp)

            val currentDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDateTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())

            val daysDifference = currentDateTime.date.dayOfYear - savedDateTime.date.dayOfYear
            daysDifference < 1
        } else {
            false
        }
    }

    override suspend fun saveSourceCurrencyCode(code: String) {
        flowSettings.putString(
            key = SOURCE_CURRENCY_KEY,
            value = code
        )
    }

    override suspend fun saveTargetCurrencyCode(code: String) {
        flowSettings.putString(
            key = TARGET_CURRENCY_KEY,
            value = code
        )
    }

    override fun readSourceCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = SOURCE_CURRENCY_KEY,
            defaultValue = DEFAULT_SOURCE_CURRENCY
        ).map { CurrencyCode.valueOf(it) }
    }

    override fun readTargetCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = TARGET_CURRENCY_KEY,
            defaultValue = DEFAULT_TARGET_CURRENCY
        ).map { CurrencyCode.valueOf(it) }
    }
}