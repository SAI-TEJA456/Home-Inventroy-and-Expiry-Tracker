package com.example.liesettyfinalproject

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//Created by Liesetty
class CategoryFragment : Fragment() {
    //declaring global values
    private lateinit var  inventoryAdapter: InventoryAdapter
    //lateinit allows a not null property outside constructor
    private val inventoryData = ArrayList<InventoryData>()
    private var editedInventoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }//onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryName = view.findViewById<EditText>(R.id.categoryName)

        val saveButton = view.findViewById<Button>(R.id.saveCategory)

        val inventoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)

        saveButton.setOnClickListener {
            val inventoryName = categoryName.text.toString()

                if (inventoryName.isNotEmpty()) {
                    val database = FirebaseDatabase.getInstance().getReference("categories")
                    //this condition is elvis which if declaration
                    //database.push().key!!- instead or creating a random id i am using last token or key which used to reference
                    val inventoryId = editedInventoryId ?: database.push().key!!
                    val inventoryData = InventoryData(inventoryName, inventoryId)

                    database.child(inventoryId).setValue(inventoryData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Inventory Added", Toast.LENGTH_LONG).show()
                            categoryName.text.clear()
                            editedInventoryId = null
                        }//addOnSuccessListener
                        .addOnFailureListener {
                            val alertBuilder = AlertDialog.Builder(requireActivity())
                            alertBuilder.setMessage("Error: ${it.message}")
                            alertBuilder.setTitle("Invertory Adding Failed")
                            alertBuilder.setPositiveButton("Close") { closeButton: DialogInterface, i: Int ->
                                closeButton.dismiss()
                            }
                            alertBuilder.show()
                        }//addOnFailureListener

                } else{
                    categoryName.error = "Cannot be Empty"
                }//end if/else
        }//setOnClickListener

        inventoryRecyclerView.layoutManager = LinearLayoutManager(context)

        inventoryAdapter = InventoryAdapter(
            inventoryData,
            fragment = this@CategoryFragment
        )
        inventoryRecyclerView.adapter = inventoryAdapter

        //fun to load data from database
        loadCategories()
    }//onViewCreated


    private fun loadCategories() {
        val database = FirebaseDatabase.getInstance().getReference("categories")
        //to update list for each time data changes which update adapter view
        //ValueEvent Listener is an interface with two methods which can receive data changes
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = ArrayList<InventoryData>()
                for (i in snapshot.children){
                    //need to write explaination here
                    val inventoryData = i.getValue(InventoryData::class.java)
                    inventoryData?.let { newList.add(it) }
                }//end for
                inventoryAdapter.updateList(newList)
            }//onDataChange

            override fun onCancelled(error: DatabaseError) {
                val alertBuilder = AlertDialog.Builder(requireActivity())
                alertBuilder.setMessage("Error: ${error.message}")
                alertBuilder.setTitle("Inventory Loading Failed")
                alertBuilder.setPositiveButton("Close") { closeButton: DialogInterface, i: Int ->
                    closeButton.dismiss()
                }
                alertBuilder.show()
            }//onCancelled
        })//addValueEventListener
    }//loadCategories


    //function will be called by adapter particular button to chnage name
    fun updateInventoryName(item: InventoryData){
        val categoryName = view?.findViewById<EditText>(R.id.categoryName)
        categoryName?.setText(item.inventoryName)
        editedInventoryId = item.inventoryId
    }//updateInventoryName

    //called by adapter for event listener by inventory button in recycler view
    fun onClickInventory(item: InventoryData){
        val  bundle = Bundle()
        bundle.apply {
            putString("inventoryId", item.inventoryId)
            putString("inventoryName", item.inventoryName)
        }
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_productFragment, bundle)
    }//onClickInventory
}