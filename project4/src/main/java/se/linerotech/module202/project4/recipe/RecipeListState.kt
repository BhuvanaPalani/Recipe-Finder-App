package se.linerotech.module202.project4.recipe

sealed class RecipeListState {
    data object Loading : RecipeListState()
    data class Data(val items: List<Recipe>) : RecipeListState()
    data class Error(val message: String) : RecipeListState()
}
