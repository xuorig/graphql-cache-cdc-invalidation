package com.xuorig.cachedgql.debezium

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import kotlin.text.Charsets.UTF_8

class DebeziumDeserializer: Deserializer<DebeziumEvent> {
    private val objectMapper = ObjectMapper().registerKotlinModule()

    override fun deserialize(topic: String?, data: ByteArray?): DebeziumEvent? {
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to Product"), UTF_8
            ), DebeziumEvent::class.java
        )
    }

    override fun close() {}
}