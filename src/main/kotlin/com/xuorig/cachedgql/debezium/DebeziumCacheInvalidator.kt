package com.xuorig.cachedgql.debezium

import com.xuorig.cachedgql.cache.ResponseCache
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class DebeziumCacheInvalidator(
    @Autowired val responseCache: ResponseCache
) {
    val LOG = LoggerFactory.getLogger(DebeziumCacheInvalidator::class.java)!!

    /**
     * In the real world we'd configure listeners for all table topics, here we
     * only listen to the `products` topic.
     */
    @KafkaListener(id = "productsListener", topics = ["dbserver1.inventory.products"])
    fun listen(event: DebeziumEvent) {
        val itemId = event.payload.after["id"] as Int
        val toInvalidate = responseCache.invalidationCache.get("product-$itemId") { setOf() }
        LOG.info("Invalidating queries depending on data: [${toInvalidate.joinToString { "," }}]")
        responseCache.cache.invalidateAll(toInvalidate)
    }
}