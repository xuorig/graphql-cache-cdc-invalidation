package com.xuorig.cachedgql.cache

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQLError
import org.springframework.graphql.ExecutionGraphQlResponse
import org.springframework.graphql.ResponseError
import org.springframework.graphql.ResponseField

/**
 * A cached GraphQL response which override getData and friends with a precomputed result
 */
class CachedResponse(val requestInput: ExecutionInput, val cachedResult: Map<String, Any>): ExecutionGraphQlResponse {
    override fun isValid(): Boolean {
        return true
    }

    override fun <T : Any?> getData(): T? {
        return executionResult.getData() as T
    }

    override fun getErrors(): MutableList<ResponseError> {
        return mutableListOf()
    }

    override fun field(path: String): ResponseField {
        TODO("Not yet implemented")
    }

    override fun getExtensions(): MutableMap<Any, Any> {
        return mutableMapOf("cached" to true)
    }

    override fun toMap(): MutableMap<String, Any> {
        return executionResult.getData() as MutableMap<String, Any>
    }

    override fun getExecutionInput(): ExecutionInput {
        return requestInput
    }

    override fun getExecutionResult(): ExecutionResult {
        return object : ExecutionResult {
            override fun getErrors(): MutableList<GraphQLError> {
                return mutableListOf()
            }

            override fun <T : Any?> getData(): T {
                return cachedResult["data"] as T
            }

            override fun isDataPresent(): Boolean {
                return true
            }

            override fun getExtensions(): MutableMap<Any, Any> {
                return mutableMapOf("cached" to true)
            }

            override fun toSpecification(): Map<String, Any> {
                return mapOf(
                    "data" to cachedResult["data"] as Map<String, Any>,
                    "extensions" to mapOf("cached" to true)
                )
            }
        }
    }
}