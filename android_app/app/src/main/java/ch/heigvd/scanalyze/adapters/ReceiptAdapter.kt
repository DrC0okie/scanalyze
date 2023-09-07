package ch.heigvd.scanalyze.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.Shop.Shop
import ch.heigvd.scanalyze.activities.ReceiptDetailActivity
import ch.heigvd.scanalyze.databinding.ItemReceiptBinding
import ch.heigvd.scanalyze.receipt.JsonReceipt
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ReceiptAdapter(private val receipts: Array<JsonReceipt>) :
    RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val receipt = receipts[bindingAdapterPosition]
                val intent = Intent(itemView.context, ReceiptDetailActivity::class.java)
                intent.putExtra("receipt", receipt)
                context.startActivity(intent)
            }
        }

        fun bind(receipt: JsonReceipt) {

            binding.imageViewShopIcon.setImageResource(Shop.func.fromString(receipt.shopName).resourceImage)
            binding.textViewShop.text = receipt.shopBranch ?: ""
            binding.textViewTotal.text = String.format("%.2f", receipt.total?: 0f)

            try {
                binding.textViewDate.text = LocalDateTime
                    .parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            } catch (e: Exception) {
                binding.textViewDate.text = "Date unknown"
            }
        }
    }

    override fun getItemCount() = receipts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Create a new view, which defines the UI of the list item
        val binding = ItemReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Binds each projectItem in the Array with the actual position of the recyclerView
        holder.bind(receipts[position])
    }
}