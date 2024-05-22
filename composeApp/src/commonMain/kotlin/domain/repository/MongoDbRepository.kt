package domain.repository

import domain.model.CurrencyItemResponse
import domain.util.RequestState
import kotlinx.coroutines.flow.Flow

interface MongoDbRepository {
    fun configureTheRealm()

    suspend fun insertCurrencyData(currency: CurrencyItemResponse)

    fun readCurrencyData(): Flow<RequestState<List<CurrencyItemResponse>>>

    suspend fun cleanUp()
}