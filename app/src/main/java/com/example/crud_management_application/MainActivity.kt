package com.example.crud_management_application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_management_application.adapters.UsersAdapter
import com.example.crud_management_application.data_models.UserModel
import com.example.crud_management_application.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UsersAdapter
    private lateinit var userList: ArrayList<UserModel>

    private lateinit var view: View
    private lateinit var dialog: AlertDialog

    private lateinit var binding: ActivityMainBinding

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

    private fun setRecyclerView(view: View) {
        // Set RecyclerView
        recyclerView = view.findViewById(R.id.read_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
    }

    private fun setDataToAdapter() {
        // Set Adapter
        userAdapter = UsersAdapter(userList)
        recyclerView.adapter = userAdapter
    }

    private fun getData() {
        val firstName = binding.firstNameText.text.toString()
        val middleInitial = binding.middleInitialText.text.toString()
        val lastName = binding.lastNameText.text.toString()
        val age = binding.ageText.text.toString()
        val dateOfBirth = binding.dateOfBirthText.text.toString()
        val sex = binding.genderRadioGroup.checkedRadioButtonId
        val gender = if (sex == binding.maleRadioButton.id) "Male" else "Female"

        val data = hashMapOf(
            "firstName" to firstName,
            "middleInitial" to middleInitial,
            "lastName" to lastName,
            "age" to age,
            "dateOfBirth" to dateOfBirth,
            "gender" to gender
        )
        addDataToFirebase(data)
    }

    private fun addDataToFirebase(data: HashMap<String, String>) {
        // add data (with auto id for document)
        setInProgress(true)
        Firebase.firestore
            .collection("users")
            .add(data)
            .addOnSuccessListener {
                val userId = mapOf(
                    "userID" to it.id
                )
                Firebase.firestore
                    .collection("users")
                    .document(it.id)
                    .update(userId)
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
    }

    private fun readUsersFromFirebase() {
        // Call the layout inflater for the custom dialog layout
        view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_read, null)

        setRecyclerView(view)

        // Set Dialog
        val builder = AlertDialog.Builder(this)
            .setTitle("Read Users")
            .setView(view)
        dialog = builder.create()
        dialog.show()

        view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
        view.findViewById<RecyclerView>(R.id.read_recycler_view).visibility = View.GONE
        Firebase.firestore
            .collection("users")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val user = document.toObject(UserModel::class.java)
                        userList.add(user)
                    }
                    setDataToAdapter()
                    userAdapter.notifyDataSetChanged()
                    view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
                    view.findViewById<RecyclerView>(R.id.read_recycler_view).visibility = View.VISIBLE
                }
            }
    }

    private fun readDataFromFirebase() {
        readUsersFromFirebase()

        view.findViewById<Button>(R.id.read_button).setOnClickListener {
            val userID = userAdapter.getSelectedUserID()
            Firebase.firestore
                .collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(UserModel::class.java)
                        if (user != null) {
                            updateUI(user)
                        }
                    }
                }
            dialog.dismiss()
        }
        view.findViewById<TextView>(R.id.cancel_text_view).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun updateUI(user: UserModel) {
        binding.firstNameReadText.text = "First Name: ${user.firstName}"
        binding.middleNameReadText.text = "Middle Initial: ${user.middleInitial}"
        binding.lastNameReadText.text = "Last Name: ${user.lastName}"
        binding.ageReadText.text = "Age: ${user.age}"
        binding.dateOfBirthReadText.text = "Date of Birth: ${user.dateOfBirth}"
        binding.genderRadioGroupRead.check(if (user.gender == "Male") binding.maleRadioButtonRead.id else binding.femaleRadioButtonRead.id)

        binding.readTextLayout.visibility = View.VISIBLE

        Toast.makeText(this, "Data read successfully", Toast.LENGTH_SHORT).show()
    }

    private fun updateDataToFirebase() {
        readUsersFromFirebase()

        view.findViewById<Button>(R.id.read_button).text = "Update"
        view.findViewById<Button>(R.id.read_button).setOnClickListener {

            val userID = userAdapter.getSelectedUserID()

            // Call the layout inflater for the custom dialog layout
            val updateView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_update, null)

            // Set Dialog
            val builder = AlertDialog.Builder(this)
                .setTitle("Update Data")
                .setView(updateView)
            val dialog = builder.create()
            dialog.show()

            // Set Data to Dialog
            Firebase.firestore
                .collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener {document ->
                    if (document.exists()) {
                        val user = document.toObject(UserModel::class.java)
                        if(user != null) {
                            updateView.findViewById<EditText>(R.id.first_name_text).setText(user.firstName)
                            updateView.findViewById<EditText>(R.id.middle_initial_text).setText(user.middleInitial)
                            updateView.findViewById<EditText>(R.id.last_name_text).setText(user.lastName)
                            updateView.findViewById<EditText>(R.id.age_text).setText(user.age)
                            updateView.findViewById<EditText>(R.id.date_of_birth_text).setText(user.dateOfBirth)
                            updateView.findViewById<RadioGroup>(R.id.gender_radio_group).check(if (user.gender == "Male") updateView.findViewById<RadioButton>(R.id.male_radio_button).id else updateView.findViewById<RadioButton>(R.id.female_radio_button).id)
                        }
                    }
                }

            // Update OnClick Dialog
            updateView.findViewById<Button>(R.id.update_button).setOnClickListener {
                val newFirstName = updateView.findViewById<EditText>(R.id.first_name_text).text.toString()
                val newMiddleInitial = updateView.findViewById<EditText>(R.id.middle_initial_text).text.toString()
                val newLastName = updateView.findViewById<EditText>(R.id.last_name_text).text.toString()
                val newAge = updateView.findViewById<EditText>(R.id.age_text).text.toString()
                val newDateOfBirth = updateView.findViewById<EditText>(R.id.date_of_birth_text).text.toString()
                val newGender = updateView.findViewById<RadioGroup>(R.id.gender_radio_group).checkedRadioButtonId
                val newGenderText = if (newGender == R.id.male_radio_button) "Male" else "Female"

                val data = mapOf(
                    "firstName" to newFirstName,
                    "middleInitial" to newMiddleInitial,
                    "lastName" to newLastName,
                    "age" to newAge,
                    "dateOfBirth" to newDateOfBirth,
                    "gender" to newGenderText
                )

                Firebase.firestore
                    .collection("users")
                    .document(userID)
                    .update(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Updated Whole Data", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
                this.dialog.dismiss()
            }
            updateView.findViewById<TextView>(R.id.cancel_text_view).setOnClickListener {
                dialog.dismiss()
            }
        }
        view.findViewById<TextView>(R.id.cancel_text_view).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun deleteDataFromFirebase() {
        readUsersFromFirebase()

        view.findViewById<Button>(R.id.read_button).text = "Delete"
        view.findViewById<Button>(R.id.read_button).setOnClickListener {

            val userID = userAdapter.getSelectedUserID()

            // Set Dialog
            val builder = AlertDialog.Builder(this)
                .setTitle("Delete Data")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Delete") { dialog, _ ->
                    Firebase.firestore
                        .collection("users")
                        .document(userID)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Deleted whole Data", Toast.LENGTH_SHORT).show()
                            this.dialog.dismiss()
                        }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
        view.findViewById<TextView>(R.id.cancel_text_view).setOnClickListener {
            dialog.dismiss()
        }
    }
}
