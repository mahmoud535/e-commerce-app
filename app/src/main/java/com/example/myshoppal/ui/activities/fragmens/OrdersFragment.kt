package com.example.myshoppal.ui.activities.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.firestore.FireStoreClass
import com.example.myshoppal.models.Order
import com.example.myshoppal.ui.activities.adapters.MyOrdersListAdapter
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : BaseFragment() {

  //  private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
       // notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }

    fun populateOrdersListInUI(ordersList:ArrayList<Order>){
         hideProgressDialog()

        if (ordersList.size > 0){
            rv_my_order_items.visibility=View.VISIBLE
            tv_no_orders_found.visibility=View.GONE

            rv_my_order_items.layoutManager=LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrdersAdapter=MyOrdersListAdapter(requireActivity(),ordersList)
            rv_my_order_items.adapter=myOrdersAdapter
        }
        else{
            rv_my_order_items.visibility=View.GONE
            tv_no_orders_found.visibility=View.VISIBLE
        }
    }
    private fun getMyOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getMyOrdersList(this@OrdersFragment)
    }

    override fun onResume() {
        super.onResume()

        getMyOrdersList()
    }
}