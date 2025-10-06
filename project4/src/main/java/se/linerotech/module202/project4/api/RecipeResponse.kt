package se.linerotech.module202.project4.api

data class RecipeResponse(val hits: List<Hit>)

data class Hit(val recipe: ApiRecipe)

data class ApiRecipe(
    val label: String,
    val image: String?,
    val ingredientLines: List<String> = emptyList(),
    val calories: Double? = null,
    val totalTime: Double? = null,
    val url: String? = null,
    val cuisineType: List<String>? = null
)
