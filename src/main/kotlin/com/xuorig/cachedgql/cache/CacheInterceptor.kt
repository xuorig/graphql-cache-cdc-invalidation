package com.xuorig.cachedgql.cache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Spring GraphQL interceptor that returns cached GraphQL responses.
 */
@Component
class CacheInterceptor : WebGraphQlInterceptor {
    @Autowired
    lateinit var responseCache: ResponseCache

    override fun intercept(
        request: WebGraphQlRequest,
        chain: WebGraphQlInterceptor.Chain
    ): Mono<WebGraphQlResponse> {
        val cachedResponse = responseCache.getCachedResponseFromExecutionInput(request.toExecutionInput())

        if (cachedResponse != null) {
            return Mono.just(
                WebGraphQlResponse(CachedResponse(request.toExecutionInput(), cachedResponse))
            )
        }

        return chain.next(request).map {
            responseCache.cacheResponse(it.executionInput, it.executionResult)
            it
        }
    }
}