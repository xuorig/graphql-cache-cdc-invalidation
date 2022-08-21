package com.xuorig.cachedgql.cache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class Cache(
    val items: List<CacheItem>
)

data class CacheItem(
    val key: String,
    val value: String
)

@Controller
class CacheController(
    @Autowired
    val rspCache: ResponseCache
) {
    @QueryMapping
    fun responseCache(): Cache {
        return Cache(rspCache.cache.asMap().entries.map {
            CacheItem(it.key, it.value.toString())
        })
    }

    @QueryMapping
    fun invalidationCache(): Cache {
        return Cache(rspCache.invalidationCache.asMap().entries.map {
            CacheItem(it.key, it.value.toString())
        })
    }
}