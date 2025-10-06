package se.linerotech.module202.project4.recipe

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import se.linerotech.module202.project4.R
import se.linerotech.module202.project4.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!
    private val vm: HomeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RecipeAdapter { recipe -> openDetail(recipe) }
        b.recyclerViewRecipes.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerViewRecipes.adapter = adapter

        setupChipPopups()
        renderSelectedFilters()

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("clearFilters")
            ?.observe(viewLifecycleOwner) { clear ->
                if (clear == true) {
                    findNavController().currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<Boolean>("clearFilters")

                    resetFilters()
                    vm.loadBalancedFeed()
                }
            }

        b.textInputLayoutSearch.endIconMode =
            com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
        b.textInputLayoutSearch.setEndIconDrawable(R.drawable.search)
        b.textInputLayoutSearch.setEndIconOnClickListener {
            navigateToResults()
        }

        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        b.recyclerViewRecipes.addItemDecoration(divider)
        b.editTextSearch.setOnEditorActionListener { _, actionId, event ->
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER ||
                actionId == EditorInfo.IME_ACTION_SEARCH
            if (isEnter) {
                navigateToResults()
                true
            } else {
                false
            }
        }

        vm.state.observe(viewLifecycleOwner) { state ->
            b.textViewSubTitle.isVisible =
                state is RecipeListState.Data && b.editTextSearch.text.isNullOrBlank()

            when (state) {
                is RecipeListState.Loading -> {
                    b.recyclerViewRecipes.isVisible = false
                    Snackbar.make(b.root, "Loading…", Snackbar.LENGTH_SHORT).show()
                }
                is RecipeListState.Data -> {
                    b.recyclerViewRecipes.isVisible = true
                    adapter.submit(state.items)
                    if (b.editTextSearch.text.isNullOrBlank()) {
                        b.textViewSubTitle.text = "Balanced Recipes (${state.items.size})"
                    }
                }
                is RecipeListState.Error -> {
                    b.recyclerViewRecipes.isVisible = false
                    Snackbar.make(b.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
        vm.loadBalancedFeed()
    }

    private fun currentQuery(): String = b.editTextSearch.text?.toString().orEmpty()

    private fun cleanedQuery(): String =
        currentQuery()
            .replace(Regex("\\brecipes?\\b", RegexOption.IGNORE_CASE), "")
            .trim()

    private fun openDetail(recipe: Recipe) {
        val args = bundleOf(
            "arg_title" to recipe.label,
            "arg_image" to (recipe.image ?: ""),
            "arg_ingredients" to recipe.ingredientLines.toTypedArray(),
            "arg_calories" to (recipe.calories ?: 0.0),
            "arg_time" to (recipe.totalTime ?: 0.0)
        )
        findNavController().navigate(R.id.detailFragment, args)
    }

    private fun resetFilters() {
        vm.selectedDiet = null
        vm.selectedCuisine = null
        vm.selectedMeal = null
        vm.selectedDish = null
        vm.selectedHealth = null

        b.chipDiet.text = getString(R.string.diet)
        b.chipCuisine.text = getString(R.string.cuisine)
        b.chipMeal.text = getString(R.string.meal)
        b.chipDish.text = getString(R.string.dish)
        b.chipHealth.text = getString(R.string.health)

        toggleChipIcon(b.chipDiet, false)
        toggleChipIcon(b.chipCuisine, false)
        toggleChipIcon(b.chipMeal, false)
        toggleChipIcon(b.chipDish, false)
        toggleChipIcon(b.chipHealth, false)

        b.editTextSearch.setText("")
        b.textViewSubTitle.text = getString(R.string.balanced_recipes)
    }

    private fun navigateToResults() {
        val args = bundleOf(
            "q" to cleanedQuery(),
            "diet" to vm.selectedDiet,
            "cuisine" to vm.selectedCuisine,
            "meal" to vm.selectedMeal,
            "dish" to vm.selectedDish,
            "health" to vm.selectedHealth
        )
        findNavController().navigate(R.id.searchResultFragment, args)
    }

    private fun setupChipPopups() {
        b.chipDiet.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor).apply {
                menuInflater.inflate(R.menu.menu_diet, menu)
                setOnMenuItemClickListener { item ->
                    vm.selectedDiet = when (item.itemId) {
                        R.id.m_diet_balanced -> "balanced"
                        R.id.m_diet_high_fiber -> "high-fiber"
                        R.id.m_diet_high_protein -> "high-protein"
                        R.id.m_diet_low_carb -> "low-carb"
                        R.id.m_diet_low_fat -> "low-fat"
                        R.id.m_diet_low_sodium -> "low-sodium"
                        else -> null
                    }
                    renderSelectedFilters()
                    toggleChipIcon(b.chipDiet, false)
                    true
                }
            }
            toggleChipIcon(b.chipDiet, true)
            popup.setOnDismissListener { toggleChipIcon(b.chipDiet, false) }
            popup.show()
        }

        b.chipCuisine.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor).apply {
                menuInflater.inflate(R.menu.menu_cuisine, menu)
                setOnMenuItemClickListener { item ->
                    vm.selectedCuisine = when (item.itemId) {
                        R.id.m_cuisine_american -> "American"
                        R.id.m_cuisine_asian -> "Asian"
                        R.id.m_cuisine_french -> "French"
                        R.id.m_cuisine_indian -> "Indian"
                        R.id.m_cuisine_italian -> "Italian"
                        else -> null
                    }
                    renderSelectedFilters()
                    toggleChipIcon(b.chipCuisine, false)
                    true
                }
            }
            toggleChipIcon(b.chipCuisine, true)
            popup.setOnDismissListener { toggleChipIcon(b.chipCuisine, false) }
            popup.show()
        }

        b.chipMeal.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor).apply {
                menuInflater.inflate(R.menu.menu_meal, menu)
                setOnMenuItemClickListener { item ->
                    vm.selectedMeal = when (item.itemId) {
                        R.id.m_meal_breakfast -> "Breakfast"
                        R.id.m_meal_lunch -> "Lunch"
                        R.id.m_meal_snack -> "Snack"
                        R.id.m_meal_teatime -> "Teatime"
                        else -> null
                    }
                    renderSelectedFilters()
                    toggleChipIcon(b.chipMeal, false)
                    true
                }
            }
            toggleChipIcon(b.chipMeal, true)
            popup.setOnDismissListener { toggleChipIcon(b.chipMeal, false) }
            popup.show()
        }

        b.chipDish.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor).apply {
                menuInflater.inflate(R.menu.menu_dish, menu)
                setOnMenuItemClickListener { item ->
                    vm.selectedDish = when (item.itemId) {
                        R.id.m_dish_main -> "Main course"
                        R.id.m_dish_side -> "Side dish"
                        R.id.m_dish_dessert -> "Dessert"
                        R.id.m_dish_soup -> "Soup"
                        R.id.m_dish_salad -> "Salad"
                        else -> null
                    }
                    renderSelectedFilters()
                    toggleChipIcon(b.chipDish, false)
                    true
                }
            }
            toggleChipIcon(b.chipDish, true)
            popup.setOnDismissListener { toggleChipIcon(b.chipDish, false) }
            popup.show()
        }

        b.chipHealth.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor).apply {
                menuInflater.inflate(R.menu.menu_health, menu)
                setOnMenuItemClickListener { item ->
                    vm.selectedHealth = when (item.itemId) {
                        R.id.m_health_peanut_free -> "peanut-free"
                        R.id.m_health_tree_nut_free -> "tree-nut-free"
                        R.id.m_health_alcohol_free -> "alcohol-free"
                        R.id.m_health_vegan -> "vegan"
                        R.id.m_health_vegetarian -> "vegetarian"
                        else -> null
                    }
                    renderSelectedFilters()
                    toggleChipIcon(b.chipHealth, false)
                    true
                }
            }
            toggleChipIcon(b.chipHealth, true)
            popup.setOnDismissListener { toggleChipIcon(b.chipHealth, false) }
            popup.show()
        }
    }

    private fun toggleChipIcon(chip: Chip, expanded: Boolean) {
        chip.setChipIconResource(
            if (expanded) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down
        )
    }

    private fun renderSelectedFilters() {
        b.chipDiet.text = vm.selectedDiet?.let { "Diet: $it" } ?: "Diet"
        b.chipCuisine.text = vm.selectedCuisine?.let { "Cuisine: $it" } ?: "Cuisine"
        b.chipMeal.text = vm.selectedMeal?.let { "Meal: $it" } ?: "Meal"
        b.chipDish.text = vm.selectedDish?.let { "Dish: $it" } ?: "Dish"
        b.chipHealth.text = vm.selectedHealth?.let { "Health: $it" } ?: "Health"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    override fun onResume() {
        super.onResume()
        resetFilters()
        vm.loadBalancedFeed()
    }
}
