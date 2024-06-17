package com.example.crud_management_application

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crud_management_application.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createButton.setOnClickListener {
            getData()
        }
        binding.readButton.setOnClickListener {
            readDataFromFirebase()
        }
        binding.updateButton.setOnClickListener {
            updateDataToFirebase("watol big booba")
        }
        binding.deleteButton.setOnClickListener {
            deleteDataFromFirebase()
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.createProgressBar.visibility = View.VISIBLE
            binding.createButton.visibility = View.GONE
        }
        else {
            binding.createProgressBar.visibility = View.GONE
            binding.createButton.visibility = View.VISIBLE
        }
    }

    private fun getData() {
        val firstName = binding.firstNameText.text.toString()
        val middleName = binding.middleInitialText.text.toString()
        val lastName = binding.lastNameText.text.toString()
        val age = binding.ageText.text.toString()
        val dateOfBirth = binding.dateOfBirthText.text.toString()
        val sex = binding.genderRadioGroup.checkedRadioButtonId
        val gender = if (sex == binding.maleRadioButton.id) "Male" else "Female"

        addDataToFirebase(firstName, middleName, lastName, age, dateOfBirth, gender)
    }

    private fun addDataToFirebase(firstName: String, middleName: String, lastName: String, age: String, dateOfBirth: String, gender: String) {
        val data = hashMapOf(
            "firstName" to firstName,
            "middleName" to middleName,
            "lastName" to lastName,
            "age" to age,
            "dateOfBirth" to dateOfBirth,
            "gender" to gender
        )

        // add data (with auto id for document)
        setInProgress(true)
        Firebase.firestore
            .collection("users")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show()
                binding.firstNameText.text.clear()
                binding.middleInitialText.text.clear()
                binding.lastNameText.text.clear()
                binding.ageText.text.clear()
                binding.dateOfBirthText.text.clear()
                setInProgress(false)
            }
    }

    private fun readDataFromFirebase() {

        // Read Data Whole
        Firebase.firestore
            .collection("users")
            .document("user1")
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val firstName = it.getString("firstName")
                    val middleName = it.getString("middleName")
                    val lastName = it.getString("lastName")
                    val age = it.getString("age")
                    val dateOfBirth = it.getString("dateOfBirth")
                    val gender = it.getString("gender")

                    binding.firstNameReadText.text = "First Name: $firstName"
                    binding.middleNameReadText.text = "Middle Initial: $middleName"
                    binding.lastNameReadText.text = "Last Name: $lastName"
                    binding.ageReadText.text = "Age: $age"
                    binding.dateOfBirthReadText.text = "Date of Birth: $dateOfBirth"
                    binding.genderRadioGroupRead.check(if (gender == "Male") binding.maleRadioButtonRead.id else binding.femaleRadioButtonRead.id)

                    binding.readTextLayout.visibility = View.VISIBLE
                }
            }

        // Read Data Specific
        Firebase.firestore
            .collection("users")
            .document("user1")
            .get()
            .addOnSuccessListener {
                if(it.exists()) {
                    val gender = it.getString("gender")

                    binding.genderRadioGroupRead.check(if (gender == "Male") binding.maleRadioButtonRead.id else binding.femaleRadioButtonRead.id)

                    binding.readTextLayout.visibility = View.VISIBLE
                }
            }

    }

    private fun updateDataToFirebase(firstName: String) {

        // Update Specific
        val data = mapOf(
            "firstName" to "watol",
            "gender" to "Female"
        )

        Firebase.firestore
            .collection("users")
            .document("user1")
            .update(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Updated Data", Toast.LENGTH_SHORT).show()
            }

        // Update Whole
        val data2 = hashMapOf(
            "firstName" to "watol big booba"
        )
        Firebase.firestore
            .collection("users")
            .document("user1")
            .set(data2)
            .addOnSuccessListener {
                Toast.makeText(this, "Updated Whole Data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteDataFromFirebase() {

        // Delete Specific
        val mapDelete = mapOf(
            "firstName" to FieldValue.delete()
        )
        Firebase.firestore
            .collection("users")
            .document("user1")
            .update(mapDelete)
            .addOnSuccessListener {
                Toast.makeText(this, "Deleted specific Data", Toast.LENGTH_SHORT).show()
            }

        // Delete Whole
        Firebase.firestore
            .collection("users")
            .document("user1")
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Deleted whole Data", Toast.LENGTH_SHORT).show()
            }
    }
}