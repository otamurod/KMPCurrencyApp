package domain.api

import domain.model.CurrencyItemResponse
import domain.util.RequestState

interface CurrencyApiService {
    suspend fun getLatestExchangeRates(): RequestState<List<CurrencyItemResponse>>
}