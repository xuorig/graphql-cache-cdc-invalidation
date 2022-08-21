package com.xuorig.cachedgql.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Caffeine
import graphql.ExecutionInput
import graphql.ExecutionResult
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class ResponseCache {
    /**
     * Full response cache
     */
    val cache = Caffeine.newBuilder().maximumSize(10000)
        .build<String, Map<String, Any>>()!!

    /**
     * Invalidation cache where the key is a unique data object key and the values are a list
     * of query hashes that depend on this data access ID.
     */
    val invalidationCache = Caffeine.newBuilder().maximumSize(10000)
        .build<String, Set<String>>()!!

    fun getCachedResponseFromExecutionInput(executionInput: ExecutionInput): Map<String, Any>? {
        val cacheKey = cacheKeyFromInput(executionInput)
        return cache.getIfPresent(cacheKey)
    }

    fun cacheResponse(executionInput: ExecutionInput, executionResult: ExecutionResult) {
        val cacheKey = cacheKeyFromInput(executionInput)
        val dataAccessIds = executionInput.graphQLContext.getOrDefault("data-access-ids", listOf<String>())
        for (id in dataAccessIds) {
            invalidationCache.asMap().compute(id) { _, v ->
                if (v == null) {
                    setOf()
                } else {
                    v + cacheKey
                }
            }
        }
        cache.put(cacheKey, executionResult.toSpecification())
    }

    fun cacheKeyFromInput(executionInput: ExecutionInput): String {
        val mapper = ObjectMapper()
        val variableBytes = mapper.writeValueAsBytes(executionInput.variables)
        val queryBytes = executionInput.query.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(queryBytes + variableBytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}