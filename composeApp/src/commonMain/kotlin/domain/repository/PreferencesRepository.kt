package domain.repository

interface PreferencesRepository {
    suspend fun saveLastUpdatedTime(lastUpdatedTime: String)
    suspend fun isDataFresh(currentTimeStamp: Long): Boolean
}