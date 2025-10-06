package se.linerotech.module202.project4.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.linerotech.module202.project4.databinding.ItemIngredientBinding

class IngredientAdapter :
    RecyclerView.Adapter<IngredientAdapter.IngredientVH>() {

    private val items = mutableListOf<String>()

    fun submit(list: List<String>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class IngredientVH(val b: ItemIngredientBinding) :
        RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientVH {
        val b = ItemIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientVH(b)
    }

    override fun onBindViewHolder(holder: IngredientVH, position: Int) {
        holder.b.textViewIngredient.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}
