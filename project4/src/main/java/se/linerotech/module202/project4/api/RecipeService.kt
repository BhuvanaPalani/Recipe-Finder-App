package se.linerotech.module202.project4.api

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("api/recipes/v2")
    suspend fun searchRecipes(
        @Query("type") type: String = "public",
        @Query("q") q: String? = null,
        @Query("diet") diet: String? = null,
        @Query("cuisineType") cuisine: String? = null,
        @Query("mealType") meal: String? = null,
        @Query("dishType") dish: String? = null,
        @Query("health") health: String? = null
    ): RecipeResponse
}
