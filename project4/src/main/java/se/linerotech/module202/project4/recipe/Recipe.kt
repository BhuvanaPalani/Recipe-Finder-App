package se.linerotech.module202.project4.recipe

data class Recipe(
    val label: String,
    val image: String?,
    val ingredientLines: List<String>,
    val calories: Double?,
    val totalTime: Double?,
    val url: String = "",
    val ingredients: List<String> = emptyList(),
    val cuisineType: List<String> = emptyList()
)
