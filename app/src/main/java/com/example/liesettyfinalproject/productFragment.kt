package com.example.liesettyfinalproject

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//Created by Liesetty
class productFragment : Fragment() {

    private lateinit var productAdapter: InventoryProductAdapter
    private val productData = ArrayList<ProductData>()
    private var editProductId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }//onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inventoryName = arguments?.getString("inventoryName")
        val inventoryId = arguments?.getString("inventoryId")

        val categoryTitle = view.findViewById<TextView>(R.id.productTextView)
        categoryTitle.text = inventoryName

        val productNameEdit = view.findViewById<EditText>(R.id.productName)
        val productQuantityEdit = view.findViewById<EditText>(R.id.productQuantity)
        val productExpiryDateEdit = view.findViewById<EditText>(R.id.expiryDate)

        val saveProduct = view.findViewById<Button>(R.id.saveProductButton)

        val productRecyclerView = view.findViewById<RecyclerView>(R.id.productRecyclerView)

        productExpiryDateEdit.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH)
            val day = calender.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(),
                {_, selectedYear, selectedMonth, selectedDay ->
                    val formatDate = "${selectedMonth + 1}/${selectedDay}/${selectedYear}"
                    productExpiryDateEdit.setText(formatDate)
                }, year, month, day)
            datePicker.show()
        }//productExpiryDateEdit

        saveProduct.setOnClickListener {
            val productName = productNameEdit.text.toString()
            val productQuantity = productQuantityEdit.text.toString()
            val productExpiryDate = productExpiryDateEdit.text.toString()
            if (productName.isNotEmpty())
            {
                if (productQuantity.isNotEmpty())
                {
                    if (productExpiryDate.isNotEmpty())
                    {
                        val database = FirebaseDatabase.getInstance().getReference("products")
                        val productId = editProductId ?: database.push().key!!

                        val productData = ProductData(
                            productId,
                            inventoryId ?: "",
                            productName,
                            productQuantity,
                            productExpiryDate
                        )

                        database.child(productId).setValue(productData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Product added to inventory", Toast.LENGTH_LONG).show()
                                productNameEdit.text.clear()
                                productQuantityEdit.text.clear()
                                productExpiryDateEdit.text.clear()
                                editProductId = null
                            }.addOnFailureListener {
                                val alertBuilder = AlertDialog.Builder(requireActivity())
                                alertBuilder.setMessage("Error: ${it.message}")
                                alertBuilder.setTitle("Product Adding Failed")
                                alertBuilder.setPositiveButton("Close") { closeButton: DialogInterface, i: Int ->
                                    closeButton.dismiss()
                                }
                                alertBuilder.show()
                            }

                    }else{
                        productExpiryDateEdit.error = "Value cannot be empty"
                    }

                }else{
                    productQuantityEdit.error = "Value cannot be empty"
                }

            }else{
                productNameEdit.error = "Value cannot be empty"
            }
        }//saveProduct

        productRecyclerView.layoutManager = LinearLayoutManager(context)

        productAdapter = InventoryProductAdapter(
            productData,
            fragment = this@productFragment
        )
        productRecyclerView.adapter = productAdapter

        loadExistingProducts()
    }//onViewCreated

    private fun loadExistingProducts() {
        val inventoryId = arguments?.getString("inventoryId")
        val database = FirebaseDatabase.getInstance().getReference("products")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = ArrayList<ProductData>()
                for (i in snapshot.children){
                    val product = i.getValue(ProductData::class.java)
                    product?.let {
                    if (it.inventoryId == inventoryId){
                        newList.add(it)
                        Log.d("Loading Data:", "Received Data: ${inventoryId} From Firebase:  ProductId: ${it.productId} InventoryId: ${it.inventoryId}")
                    }
                    }
                }
                productAdapter.updateList(newList)
            }//onDataChange

            override fun onCancelled(error: DatabaseError) {
                val alertBuilder = AlertDialog.Builder(requireActivity())
                alertBuilder.setMessage("Error: ${error.message}")
                alertBuilder.setTitle("Product Loading Failed")
                alertBuilder.setPositiveButton("Close") { closeButton: DialogInterface, i: Int ->
                    closeButton.dismiss()
                }
                alertBuilder.show()
            }//onCancelled


        })//addValueEventListener
    }//loadExistingProducts

    fun updateProduct(product: ProductData){
        val productNameEdit = view?.findViewById<EditText>(R.id.productName)
        val productQuantityEdit = view?.findViewById<EditText>(R.id.productQuantity)
        val productExpiryDateEdit = view?.findViewById<EditText>(R.id.expiryDate)

        productNameEdit?.setText(product.productName)
        productQuantityEdit?.setText(product.productQuantity)
        productExpiryDateEdit?.setText(product.productExpiryDate)
        editProductId = product.productId
    }//updateProduct
}