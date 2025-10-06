package se.linerotech.module202.project4.recipe

import se.linerotech.module202.project4.api.NetworkModule
import se.linerotech.module202.project4.api.toDomain

class RecipeRepository {
    private val api = NetworkModule.api

    suspend fun getBalancedFeed(): List<Recipe> =
        api.searchRecipes(
            q = null, // omit query per spec
            diet = "balanced"
        ).toDomain()

    suspend fun search(
        query: String,
        diet: String?,
        cuisine: String?,
        meal: String?,
        dish: String?,
        health: String?
    ): List<Recipe> {
        val q0 = query.trim()
        var list = api.searchRecipes(
            q = q0.ifEmpty { null },
            diet = diet,
            cuisine = cuisine,
            meal = meal,
            dish = dish,
            health = health
        ).toDomain()

        if (list.isEmpty()) {
            val q1 = q0.replace(Regex("\\brecipes?\\b", RegexOption.IGNORE_CASE), "").trim()
            if (q1.isNotEmpty() && q1 != q0) {
                list = api.searchRecipes(
                    q = q1,
                    diet = diet,
                    cuisine = cuisine,
                    meal = meal,
                    dish = dish,
                    health = health
                ).toDomain()
            }
        }

        if (list.isEmpty() && q0.isNotEmpty()) {
            list = api.searchRecipes(
                q = q0,
                diet = null,
                cuisine = null,
                meal = null,
                dish = null,
                health = null
            ).toDomain()
        }

        return list
    }
}
