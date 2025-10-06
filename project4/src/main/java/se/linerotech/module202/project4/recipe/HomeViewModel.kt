package se.linerotech.module202.project4.recipe

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RecipeRepository()
    var selectedDiet: String? = null
    var selectedCuisine: String? = null
    var selectedMeal: String? = null
    var selectedDish: String? = null
    var selectedHealth: String? = null

    private val _state = MutableLiveData<RecipeListState>()
    val state: LiveData<RecipeListState> = _state

    fun loadBalancedFeed() = viewModelScope.launch {
        _state.value = RecipeListState.Loading
        try {
            val items = repo.getBalancedFeed()
            _state.value = RecipeListState.Data(items)
        } catch (e: Exception) {
            _state.value = RecipeListState.Error(e.message ?: "Failed to load")
        }
    }

    fun searchOnHome(q: String) = viewModelScope.launch {
        _state.value = RecipeListState.Loading
        try {
            val items = repo.search(
                query = q,
                diet = selectedDiet,
                cuisine = selectedCuisine,
                meal = selectedMeal,
                dish = selectedDish,
                health = selectedHealth
            )
            _state.value = RecipeListState.Data(items)
        } catch (e: Exception) {
            _state.value = RecipeListState.Error(e.message ?: "Failed to search")
        }
    }

    fun searchWithFilters(
        q: String,
        diet: String?,
        cuisine: String?,
        meal: String?,
        dish: String?,
        health: String?,
        onResult: (List<Recipe>) -> Unit
    ) = viewModelScope.launch {
        try {
            val cleaned = q.replace(Regex("\\brecipes?\\b", RegexOption.IGNORE_CASE), "").trim()
            val items = repo.search(
                query = cleaned,
                diet = diet,
                cuisine = cuisine,
                meal = meal,
                dish = dish,
                health = health
            )
            onResult(items)
        } catch (e: Exception) {
            onResult(emptyList())
        }
    }
}
