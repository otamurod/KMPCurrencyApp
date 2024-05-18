package data.remote.api

import domain.api.CurrencyApiService
import domain.model.CurrencyApiResponse
import domain.model.CurrencyCode
import domain.model.CurrencyItemResponse
import domain.repository.PreferencesRepository
import domain.util.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl(
    private val preferences: PreferencesRepository
) : CurrencyApiService {
    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_H2i1YN9Wsx7n4seEQXS701YWRrCzdX8VFxQA2YhU"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
        }
        install(DefaultRequest) {
            headers {
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<CurrencyItemResponse>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            if (response.status.value == 200) {
                println("API RESPONSE: ${response.body<String>()}")
                val apiResponse = Json.decodeFromString<CurrencyApiResponse>(response.body())

                // Filter the data
                val availableCurrencyCodes = apiResponse.data.keys
                    .filter {
                        CurrencyCode.entries.map { currencyCode -> currencyCode.name }
                            .toSet()
                            .contains(it)
                    }

                val availableCurrencies = apiResponse.data.values
                    .filter { currency ->
                        availableCurrencyCodes.contains(currency.code)
                    }

                // Persist a timestamp value
                preferences.saveLastUpdatedTime(apiResponse.meta.lastUpdatedAt)

                // Pass the filtered data
                RequestState.Success(data = availableCurrencies)
            } else {
                println("Error: ${response.status}")
                RequestState.Error(message = "Http Error code: ${response.status}")
            }

        } catch (e: Exception) {
            RequestState.Error(message = e.message.toString())
        }
    }
}