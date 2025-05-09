package com.example.liesettyfinalproject

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
//Created by Liesetty
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }//setOnApplyWindowInsetsListener

        setSupportActionBar(findViewById(R.id.toolbar))
    }//onCreate

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)


        return super.onCreateOptionsMenu(menu)

    }//onCreateOptionsMenu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeMenuItem -> {
                findNavController(R.id.fragmentContainerView).navigate(R.id.action_global_homeFragment)
            }

            R.id.inventoryMenuItem -> {
                findNavController(R.id.fragmentContainerView).navigate(R.id.action_global_categoryFragment)
            }

            R.id.aboutMenuItem -> {
                val aboutJson = aboutProjectJson()
                val jsonObject = JSONObject(aboutJson)

                        val aboutMessage = """
                    CreatedBy: ${jsonObject.getString("createdBy")}
                    Project Name: ${jsonObject.getString("projectName")}
                    Purpose: ${jsonObject.getString("purpose")}
                    Description: ${jsonObject.getString("projectDescription")}
                    """.trimIndent()

                val materialAlert = MaterialAlertDialogBuilder(this)
                materialAlert.setTitle("About This Application")

                materialAlert.setMessage(aboutMessage)
                    .setPositiveButton("Close") { dialog, i ->
                        dialog.dismiss()
                    }.create()

                materialAlert.show()

            }
        }//when
        return super.onOptionsItemSelected(item)
    }//onOptionsItemSelected

    fun aboutProjectJson(): String{
        try {
            val inputStream = assets.open("about_project.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer, Charsets.UTF_8)
        }catch (e: Exception){
            e.printStackTrace()
            return ""
        }
    }//aboutProjectJson
}//MainActivity