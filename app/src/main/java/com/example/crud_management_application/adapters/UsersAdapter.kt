package com.example.crud_management_application.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_management_application.R
import com.example.crud_management_application.data_models.UserModel

class UsersAdapter(
    private val userList: ArrayList<UserModel>
): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private var selectedItemPosition: Int = -1
    private var currentUserID: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_users, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]
        holder.userId.text = user.userID
        holder.name.text = "${user.firstName} ${user.middleInitial} ${user.lastName}"
        holder.radioButton.isChecked = holder.adapterPosition == selectedItemPosition

        holder.itemView.setOnClickListener {
            if (selectedItemPosition != holder.adapterPosition) {
                notifyItemChanged(selectedItemPosition)
                selectedItemPosition = holder.adapterPosition
                notifyItemChanged(selectedItemPosition)
                currentUserID = user.userID
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userId: TextView = itemView.findViewById(R.id.user_id)
        val name: TextView = itemView.findViewById(R.id.user_name)
        val radioButton: RadioButton = itemView.findViewById(R.id.read_radio_button)
    }

    fun getSelectedUserID(): String {
        return currentUserID
    }
}