package se.linerotech.module202.project4.api

import se.linerotech.module202.project4.recipe.Recipe

fun RecipeResponse.toDomain(): List<Recipe> =
    hits.map { hit ->
        val r = hit.recipe
        Recipe(
            label = r.label,
            image = r.image,
            ingredientLines = r.ingredientLines ?: emptyList(),
            calories = r.calories,
            totalTime = r.totalTime,
            url = r.url.orEmpty(),
            cuisineType = r.cuisineType ?: emptyList()
        )
    }
