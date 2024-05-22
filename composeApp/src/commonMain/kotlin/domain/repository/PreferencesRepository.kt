package domain.repository

import domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun saveLastUpdatedTime(lastUpdatedTime: String)
    suspend fun isDataFresh(currentTimeStamp: Long): Boolean
    suspend fun saveSourceCurrencyCode(code: String)
    suspend fun saveTargetCurrencyCode(code: String)
    fun readSourceCurrencyCode(): Flow<CurrencyCode>
    fun readTargetCurrencyCode(): Flow<CurrencyCode>
}