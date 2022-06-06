package com.example.base.di

import com.example.base.utils.NumberUtils.createNumber
import com.example.base.utils.toDate
import com.example.base.utils.toStringFormat
import com.google.gson.Gson
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.IOException
import java.util.*

object SerializationModule {
    lateinit var module: Module
        private set

    fun init() {
        this.module = module {
            single { provideGson() }
        }
    }

    private fun provideGson(): Gson {
        val ser: JsonSerializer<Date?> = JsonSerializer { src, _, _ ->
            if (src == null) null
            else JsonPrimitive(src.toStringFormat())
        }

        val deser: JsonDeserializer<Date?> = JsonDeserializer { json, _, _ ->
            json?.asString?.toDate()
        }
        return Gson()
            .newBuilder()
            .registerTypeAdapter(Date::class.java, ser)
            .registerTypeAdapter(Date::class.java, deser)
            .registerTypeAdapter(Int::class.java, EmptyStringToNumberTypeAdapter())
            .registerTypeAdapter(Long::class.java, EmptyStringToNumberTypeAdapter())
            .registerTypeAdapter(Float::class.java, EmptyStringToNumberTypeAdapter())
            .registerTypeAdapter(Double::class.java, EmptyStringToNumberTypeAdapter())
            .setLenient()
            .create()
    }

    class EmptyStringToNumberTypeAdapter : TypeAdapter<Number>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, number: Number?) {
            jsonWriter.value(number)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): Number? {
            if (jsonReader.peek() === JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }
            return try {
                val value: String = jsonReader.nextString()
                if ("" == value) {
                    0
                } else createNumber(value)
            } catch (e: NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }
    }
}
