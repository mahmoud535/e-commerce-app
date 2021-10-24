package com.example.myshoppal.ui.activities.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.R
import com.example.myshoppal.models.Address
import com.example.myshoppal.ui.activities.AddEditAddressActivity
import com.example.myshoppal.ui.activities.CheckoutActivity
import com.example.myshoppal.utils.Constants
import kotlinx.android.synthetic.main.item_address_layout.view.*

open class AddressListAdapter(
        private val context: Context,
        private var list: ArrayList<Address>,
        private var selectAddress:Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_address_layout,
                        parent,
                        false
                )
        )
    }

    fun notifyEditItem(activity:Activity,position: Int){
        val intent=Intent(context,AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS,list[position])
        activity.startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text = model.type
            holder.itemView.tv_address_details.text = "${model.address}, ${model.zipCode}"
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber

            if (selectAddress) {
                holder.itemView.setOnClickListener {
                   val intent=Intent(context,CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,model)
                    context.startActivity(intent)
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}