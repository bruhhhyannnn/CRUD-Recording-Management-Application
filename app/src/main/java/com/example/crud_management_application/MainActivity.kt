package com.example.crud_management_application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            updateDataToFirebase()
        }
        binding.deleteButton.setOnClickListener {
            deleteDataFromFirebase()
        }
    }

    private fun setInProgress(inProgress: Boolean, from: String) {
        if (from == "read") {
            if (inProgress) {
                binding.readProgressBar.visibility = View.VISIBLE
                binding.readTextLayout.visibility = View.GONE
            }
            else {
                binding.readProgressBar.visibility = View.GONE
                binding.readTextLayout.visibility = View.VISIBLE
            }
        }
        else {
            if (inProgress) {
                binding.createProgressBar.visibility = View.VISIBLE
                binding.createButton.visibility = View.GONE
            }
            else {
                binding.createProgressBar.visibility = View.GONE
                binding.createButton.visibility = View.VISIBLE
            }
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
        setInProgress(true, "create")
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
                setInProgress(false, "create")
            }
    }

    private fun readDataFromFirebase() {

        // Read Data Whole
        setInProgress(true, "read")
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

                    setInProgress(false, "read")
                    binding.readTextLayout.visibility = View.VISIBLE

                    Toast.makeText(this, "Data read successfully", Toast.LENGTH_SHORT).show()
                }
            }

        // Read Data Specific
//        setInProgress(true, "read")
//        Firebase.firestore
//            .collection("users")
//            .document("user1")
//            .get()
//            .addOnSuccessListener {
//                if(it.exists()) {
//                    val gender = it.getString("gender")
//
//                    binding.genderRadioGroupRead.check(if (gender == "Male") binding.maleRadioButtonRead.id else binding.femaleRadioButtonRead.id)
//
//                    setInProgress(true, "read")
//                    binding.readTextLayout.visibility = View.VISIBLE
//                }
//            }

    }

    private fun updateDataToFirebase() {

        // Update Specific
//        val data = mapOf(
//            "firstName" to "watol",
//            "gender" to "Female"
//        )
//
//        Firebase.firestore
//            .collection("users")
//            .document("user1")
//            .update(data)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Updated Data", Toast.LENGTH_SHORT).show()
//            }

        // Update Whole
//        val data2 = hashMapOf(
//            "firstName" to "watol big booba"
//        )
//        Firebase.firestore
//            .collection("users")
//            .document("user1")
//            .set(data2)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Updated Whole Data", Toast.LENGTH_SHORT).show()
//            }

        val view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(this)
            .setTitle("Enter Data")
            .setView(view)
        val dialog = builder.create()
        dialog.show()

        // Set Data to Dialog
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

                    view.findViewById<EditText>(R.id.first_name_text).setText(firstName)
                    view.findViewById<EditText>(R.id.middle_initial_text).setText(middleName)
                    view.findViewById<EditText>(R.id.last_name_text).setText(lastName)
                    view.findViewById<EditText>(R.id.age_text).setText(age)
                    view.findViewById<EditText>(R.id.date_of_birth_text).setText(dateOfBirth)
                    view.findViewById<RadioGroup>(R.id.gender_radio_group).check(if (gender == "Male") view.findViewById<RadioButton>(R.id.male_radio_button).id else view.findViewById<RadioButton>(R.id.female_radio_button).id)
                }
            }

        // Update Dialog
        view.findViewById<Button>(R.id.update_button).setOnClickListener {
            val newFirstName = view.findViewById<EditText>(R.id.first_name_text).text.toString()
            val newMiddleInitial = view.findViewById<EditText>(R.id.middle_initial_text).text.toString()
            val newLastName = view.findViewById<EditText>(R.id.last_name_text).text.toString()
            val newAge = view.findViewById<EditText>(R.id.age_text).text.toString()
            val newDateOfBirth = view.findViewById<EditText>(R.id.date_of_birth_text).text.toString()
            val newGender = view.findViewById<RadioGroup>(R.id.gender_radio_group).checkedRadioButtonId
            val newGenderText = if (newGender == R.id.male_radio_button) "Male" else "Female"

            val data = hashMapOf(
                "firstName" to newFirstName,
                "middleName" to newMiddleInitial,
                "lastName" to newLastName,
                "age" to newAge,
                "dateOfBirth" to newDateOfBirth,
                "gender" to newGenderText
            )

            Firebase.firestore
                .collection("users")
                .document("user1")
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Updated Whole Data", Toast.LENGTH_SHORT).show()
                }

            dialog.dismiss()
            readDataFromFirebase()
        }
        view.findViewById<TextView>(R.id.cancel_text_view).setOnClickListener {
            dialog.dismiss()
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

//        // Delete Whole
//        Firebase.firestore
//            .collection("users")
//            .document("user1")
//            .delete()
//            .addOnSuccessListener {
//                Toast.makeText(this, "Deleted whole Data", Toast.LENGTH_SHORT).show()
//            }
    }
}