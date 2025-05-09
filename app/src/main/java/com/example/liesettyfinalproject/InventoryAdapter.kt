package com.example.liesettyfinalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
//Created by Liesetty
class InventoryAdapter(var inventories : ArrayList<InventoryData>, val fragment: CategoryFragment) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val viewGenerator = LayoutInflater.from(parent.context).inflate(R.layout.category_recycler_layout,parent, false)

        return ViewHolder(viewGenerator)
    }//onCreateViewHolder

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = inventories[position]
        holder.catergoryNameButton.text = item.inventoryName
        holder.catergoryNameButton.setOnClickListener {
            fragment.onClickInventory(item)
        }//catergoryNameButton
        holder.categoryDeleteButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance().getReference("categories")
            database.child(item.inventoryId).removeValue()
                .addOnSuccessListener{
                    Toast.makeText(holder.itemView.context, "${item.inventoryName} is deleted",
                        Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e->
                    Toast.makeText(holder.itemView.context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }//categoryDeleteButton

        holder.categoryEditButton.setOnClickListener {
            fragment.updateInventoryName(item)
        }//categoryEditButton
    }//onBindViewHolder

    override fun getItemCount(): Int {
        return inventories.size
    }//getItemCount

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val catergoryNameButton = itemView.findViewById<Button>(R.id.categoryButton)
        val categoryDeleteButton = itemView.findViewById<Button>(R.id.deleteCategory)
        val categoryEditButton = itemView.findViewById<Button>(R.id.editCategory)
    }//ViewHolder

    fun updateList(newList: ArrayList<InventoryData>){
        inventories = newList
        notifyDataSetChanged()
    }//updateList
}//InventoryAdapter