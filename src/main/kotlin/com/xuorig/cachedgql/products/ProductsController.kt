package com.xuorig.cachedgql.products

import graphql.GraphQLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ProductsController(
    @Autowired val productRepository: ProductRepository
) {
    @QueryMapping
    fun product(context: GraphQLContext, @Argument id: String): Product? {
        val product = productRepository.findById(id.toInt())
        // Here we explicitely put `product-$id` into the data-access-ids.
        // In real life we'd probably want to make something a bit less brittle and ergonomic.
        // An annotation for example.
        context.put("data-access-ids", listOf("product-$id"))
        return product.orElse(null)
    }
}