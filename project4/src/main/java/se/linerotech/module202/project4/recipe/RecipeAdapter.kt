package se.linerotech.module202.project4.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import se.linerotech.module202.project4.R
import se.linerotech.module202.project4.databinding.ItemRecipeCardBinding

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.VH>() {

    private val data = mutableListOf<Recipe>()

    fun submit(items: List<Recipe>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemRecipeCardBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemRecipeCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = data[position]

        holder.b.textViewTitle.text = r.label

        val url = r.image?.takeIf { it.isNotBlank() } ?: "https://picsum.photos/400/300"
        Glide.with(holder.itemView.context)
            .load(url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(holder.b.imageViewRecipe)

        holder.b.textIngredients.text = "${r.ingredientLines.size} ingredients"

        val calories = r.calories?.toInt() ?: 0
        holder.b.textCalories.text = "$calories calories"

        val minutes = r.totalTime?.toInt() ?: 0
        holder.b.textTime.text = "$minutes minutes"

        val cuisine = r.cuisineType.firstOrNull().orEmpty()
        holder.b.textCuisine.text = if (cuisine.isBlank()) "—" else cuisine

        holder.itemView.setOnClickListener { onClick(r) }
    }

    override fun getItemCount(): Int = data.size
}
