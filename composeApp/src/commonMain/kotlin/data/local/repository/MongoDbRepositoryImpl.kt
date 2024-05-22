package data.local.repository

import domain.model.CurrencyItemResponse
import domain.repository.MongoDbRepository
import domain.util.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoDbRepositoryImpl : MongoDbRepository {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(CurrencyItemResponse::class)
            ).compactOnLaunch()
                .build()

            realm = Realm.open(config)
        }
    }

    override suspend fun insertCurrencyData(currency: CurrencyItemResponse) {
        realm?.write { copyToRealm(currency) }
    }

    override fun readCurrencyData(): Flow<RequestState<List<CurrencyItemResponse>>> {
        return realm?.query<CurrencyItemResponse>()?.asFlow()
            ?.map { result ->
                RequestState.Success(data = result.list)
            } ?: flow { RequestState.Error(message = "Realm not configured!") }
    }

    override suspend fun cleanUp() {
        realm?.write {
            val currencyCollection = this.query<CurrencyItemResponse>()
            delete(currencyCollection)
        }
    }
}