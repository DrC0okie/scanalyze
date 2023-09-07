package ch.heigvd.scanalyze.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.databinding.ItemReceiptDetailBinding
import ch.heigvd.scanalyze.receipt.JsonProduct
import ch.heigvd.scanalyze.receipt.JsonReceipt

class ReceiptDetailAdapter(private val receipt: JsonReceipt?) :
    RecyclerView.Adapter<ReceiptDetailAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReceiptDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: JsonProduct) {
            binding.textViewProductName.text = product.name
            binding.textViewQuantity.text = String.format("%.2f", product.quantity)
            binding.textViewUnitPrice.text = String.format("%.2f", product.unitPrice)
            binding.textViewProductPrice.text = String.format("%.2f", product.totalPrice)
        }
    }

    override fun getItemCount(): Int {
        return receipt?.products?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReceiptDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Binds each item in the Array with the actual position of the recyclerView
        holder.bind(receipt?.products?.get(position) ?: JsonProduct("null", 0f, 0f, 0f, 0f))
    }
}