package ch.heigvd.scanalyze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceiptDetailAdapter(val products: ArrayList<Product>?) :
    RecyclerView.Adapter<ReceiptDetailAdapter.ViewHolder>() {

    //  The viewHolder is responsible to bind each element of the RecyclerView to its corresponding
    // collection element (here an Array<ProjectItem>)
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val productName: TextView
        private val productQuantity: TextView
        private val productUnitPrice: TextView
        private val productTotal: TextView

        init {
            productName = view.findViewById(R.id.text_view_product_name)
            productQuantity = view.findViewById(R.id.text_view_quantity)
            productUnitPrice = view.findViewById(R.id.text_view_unit_price)
            productTotal = view.findViewById(R.id.text_view_product_price)

        }

        fun bind(product: Product) {
            productName.text = product.abbreviatedName
            productQuantity.text = String.format("%.2f", product.quantity)
            productUnitPrice.text = String.format("%.2f", product.unitPrice)
            productTotal.text = String.format("%.2f", (product.quantity * product.unitPrice))
        }
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receipt_detail, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Binds each projectItem in the Array with the actual position of the recyclerView
        holder.bind(products?.get(position) ?: Product(0, "null", 0.0, 0.0, 0.0))
    }

}