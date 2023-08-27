package ch.heigvd.scanalyze

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceiptAdapter (val receipts: Array<Receipt>):RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {

    //  The viewHolder is responsible to bind each element of the RecyclerView to its corresponding
    // collection element (here an Array<ProjectItem>)
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val shopImage: ImageView
        private val receiptDate: TextView
        private val shopBranch: TextView
        private val receiptTotal: TextView

        init {
            shopImage = view.findViewById(R.id.image_view_shop_icon)
            receiptDate = view.findViewById(R.id.text_view_date)
            shopBranch = view.findViewById(R.id.text_view_shop)
            receiptTotal = view.findViewById(R.id.text_view_total)

            //On click start activity receiptDetail
            itemView.setOnClickListener {
                val context = itemView.context
                val receipt = receipts[bindingAdapterPosition]
                val intent = Intent(itemView.context, ReceiptDetailActivity::class.java)
                intent.putExtra("receipt", receipt)
                context.startActivity(intent)
            }
        }

        fun bind(receipt: Receipt){
            shopImage.setImageResource(receipt.shop.resourceImage)
            receiptDate.text = receipt.getFormattedDate()
            shopBranch.text = receipt.shopBranch
            receiptTotal.text = receipt.totalPrice.toString()
        }
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receipt, parent ,false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Binds each projectItem in the Array with the actual position of the recyclerView
        holder.bind(receipts[position])
    }

}