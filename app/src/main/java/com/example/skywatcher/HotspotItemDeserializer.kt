package com.example.skywatcher
import com.google.gson.*
import java.lang.reflect.Type

//Semi Junk Code Please Ignore
class HotspotItemDeserializer : JsonDeserializer<HotspotItem> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HotspotItem {
        val jsonArray = json.asJsonArray

        return HotspotItem(
            locId = jsonArray[0].asString,
            countryCode = jsonArray[1].asString,
            subnational1Code = jsonArray[2].asString,
            subnational2Code = if (jsonArray[3].isJsonNull) null else jsonArray[3].asString,
            latitude = jsonArray[4].asDouble,
            longitude = jsonArray[5].asDouble,
            locationName = jsonArray[6].asString,
            observationDate = jsonArray[7].asString,
            count = jsonArray[8].asInt
        )
    }
}