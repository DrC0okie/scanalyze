package ch.heigvd.scanalyze.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.Product
import ch.heigvd.scanalyze.databinding.ItemReceiptDetailBinding

class ReceiptDetailAdapter(private val products: ArrayList<Product>?) :
    RecyclerView.Adapter<ReceiptDetailAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReceiptDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.abbreviatedName
            binding.textViewQuantity.text = String.format("%.2f", product.quantity)
            binding.textViewUnitPrice.text = String.format("%.2f", product.unitPrice)
            binding.textViewProductPrice.text =
                String.format("%.2f", (product.quantity * product.unitPrice))
        }
    }

    override fun getItemCount(): Int {
        return products?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReceiptDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Binds each projectItem in the Array with the actual position of the recyclerView
        holder.bind(products?.get(position) ?: Product(0, "null", 0.0, 0.0, 0.0))
    }
}