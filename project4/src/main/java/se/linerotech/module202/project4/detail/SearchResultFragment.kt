package se.linerotech.module202.project4.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import se.linerotech.module202.project4.R
import se.linerotech.module202.project4.databinding.FragmentSearchResultBinding
import se.linerotech.module202.project4.recipe.HomeViewModel
import se.linerotech.module202.project4.recipe.RecipeAdapter

class SearchResultFragment : Fragment() {

    private var _b: FragmentSearchResultBinding? = null
    private val b get() = _b!!

    private val vm: HomeViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentSearchResultBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.toolbar.setNavigationOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("clearFilters", true)
            findNavController().navigateUp()
        }

        adapter = RecipeAdapter { recipe ->
            val args = Bundle().apply {
                putString("arg_title", recipe.label)
                putString("arg_image", recipe.image ?: "")
                putStringArray("arg_ingredients", recipe.ingredientLines.toTypedArray())
                putDouble("arg_calories", recipe.calories ?: 0.0)
                putDouble("arg_time", recipe.totalTime ?: 0.0)
            }
            findNavController().navigate(R.id.detailFragment, args)
        }
        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerView.adapter = adapter

        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        b.recyclerView.addItemDecoration(divider)
        val q = arguments?.getString("q").orEmpty()
        val diet = arguments?.getString("diet")
        val cuisine = arguments?.getString("cuisine")
        val meal = arguments?.getString("meal")
        val dish = arguments?.getString("dish")
        val health = arguments?.getString("health")
        vm.searchWithFilters(q, diet, cuisine, meal, dish, health) { items ->
            b.toolbar.title = if (q.isBlank()) "Results" else "Results for \"$q\""
            adapter.submit(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
