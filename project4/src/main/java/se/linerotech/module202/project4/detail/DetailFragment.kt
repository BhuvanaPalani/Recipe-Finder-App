package se.linerotech.module202.project4.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import se.linerotech.module202.project4.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _b: FragmentDetailBinding? = null
    private val b get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentDetailBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val title = requireArguments().getString("arg_title").orEmpty()
        val image = requireArguments().getString("arg_image").orEmpty()
        val ingredients = requireArguments().getStringArray("arg_ingredients") ?: emptyArray()
        val calories = requireArguments().getDouble("arg_calories", 0.0)
        val time = requireArguments().getDouble("arg_time", 0.0)

        b.toolbar.title = title
        b.textViewTitle.text = title
        b.textViewCalories.text = "${calories.toInt()} calories"
        b.textViewTime.text = "${time.toInt()} minutes"

        Glide.with(this)
            .load(image)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(b.imageViewHeader)

        Glide.with(this)
            .load(image)
            .into(b.imageViewPhoto)

        val parent = b.ingredientsContainer
        parent.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        ingredients.forEach { text ->
            val row = se.linerotech.module202.project4.databinding.ItemIngredientBinding
                .inflate(inflater, parent, false)
            row.textViewIngredient.text = text
            parent.addView(row.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
