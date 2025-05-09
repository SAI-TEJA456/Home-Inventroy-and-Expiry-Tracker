package com.example.liesettyfinalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
//Created by Liesetty
class InventoryProductAdapter(var products: ArrayList<ProductData>,val fragment: productFragment): RecyclerView.Adapter<InventoryProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val viewGenerator = LayoutInflater.from(parent.context).inflate(R.layout.product_recycler_layout, parent, false)

        return ViewHolder(viewGenerator)
    }//onCreateViewHolder

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val product = products[position]
        holder.productName.text = product.productName
        holder.productQuantity.text = product.productQuantity
        holder.productExpiry.text = product.productExpiryDate

        holder.deleteProductButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance().getReference("products")
            database.child(product.productId).removeValue()
                .addOnSuccessListener{
                    Toast.makeText(holder.itemView.context, "${product.productName} is deleted",
                        Toast.LENGTH_LONG).show()
                }//addOnSuccessListener
                .addOnFailureListener { e->
                    Toast.makeText(holder.itemView.context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }//addOnFailureListener
        }//deleteProductButton

        holder.editProductButton.setOnClickListener {
            fragment.updateProduct(product)
        }//editProductButton
    }//onBindViewHolder

    override fun getItemCount(): Int {
        return products.size
    }//getItemCount

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val productName = itemView.findViewById<TextView>(R.id.productName)
        val productQuantity = itemView.findViewById<TextView>(R.id.productQuantity)
        val productExpiry = itemView.findViewById<TextView>(R.id.productDate)
        val deleteProductButton = itemView.findViewById<Button>(R.id.deleteProduct)
        val editProductButton = itemView.findViewById<Button>(R.id.editProduct)
    }//ViewHolder

    fun updateList(newList: ArrayList<ProductData>)
    {
        products = newList
        notifyDataSetChanged()
    }//updateList
}//InventoryProductAdapter
