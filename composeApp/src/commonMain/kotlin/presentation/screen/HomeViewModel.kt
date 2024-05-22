package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.api.CurrencyApiService
import domain.model.CurrencyItemResponse
import domain.model.RateStatus
import domain.repository.MongoDbRepository
import domain.repository.PreferencesRepository
import domain.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val currencyApiService: CurrencyApiService,
    private val mongoDbRepository: MongoDbRepository
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _sourceCurrency: MutableState<RequestState<CurrencyItemResponse>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<CurrencyItemResponse>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<CurrencyItemResponse>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<CurrencyItemResponse>> = _targetCurrency


    private var _allCurrencies = mutableStateListOf<CurrencyItemResponse>()
    val allCurrencies: List<CurrencyItemResponse> = _allCurrencies

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }
        }
    }

    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferencesRepository.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _sourceCurrency.value =
                        RequestState.Error(message = "Couldn't find the selected currency.")
                }
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferencesRepository.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _targetCurrency.value =
                        RequestState.Error(message = "Couldn't find the selected currency.")
                }
            }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCache = mongoDbRepository.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("$TAG: Database is full")
                    _allCurrencies.clear()
                    _allCurrencies.addAll(localCache.getSuccessData())
                    if (!preferencesRepository.isDataFresh(
                            Clock.System.now().toEpochMilliseconds()
                        )
                    ) {
                        println("HomeViewModel: DATA NOT FRESH")
                        cacheTheData()
                    } else {
                        println("HomeViewModel: DATA IS FRESH")
                    }
                } else {
                    println("HomeViewModel: DATABASE NEEDS DATA")
                    cacheTheData()
                }
            } else if (localCache.isError()) {
                println("HomeViewModel: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
            }

            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun cacheTheData() {
        val fetchedData = currencyApiService.getLatestExchangeRates()
        if (fetchedData.isSuccess()) {
            mongoDbRepository.cleanUp()
            fetchedData.getSuccessData().forEach {
                println("HomeViewModel: ADDING ${it.code}")
                mongoDbRepository.insertCurrencyData(it)
            }
            println("HomeViewModel: UPDATING _allCurrencies")
            _allCurrencies.clear()
            _allCurrencies.addAll(fetchedData.getSuccessData())
        } else if (fetchedData.isError()) {
            println("HomeViewModel: FETCHING FAILED ${fetchedData.getErrorMessage()}")
        }
    }

    private suspend fun getRateStatus() {
        _rateStatus.value = if (
            preferencesRepository.isDataFresh(
                currentTimeStamp = Clock.System.now().toEpochMilliseconds()
            )
        ) RateStatus.Fresh
        else RateStatus.Stale
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}