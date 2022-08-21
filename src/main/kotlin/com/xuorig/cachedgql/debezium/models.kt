package com.xuorig.cachedgql.debezium

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DebeziumEvent(
    val payload: DebeziumPayload
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DebeziumPayload(
  val before: Map<String, Any>,
  val after: Map<String, Any>
)