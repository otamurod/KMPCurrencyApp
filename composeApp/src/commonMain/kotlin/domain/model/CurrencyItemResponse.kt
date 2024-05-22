package domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mongodb.kbson.ObjectId

@Serializable
open class CurrencyItemResponse : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()

    @SerialName("code")
    var code: String = ""

    @SerialName("value")
    var value: Double = 0.0
}