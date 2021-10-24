package com.example.myshoppal.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.firestore.FireStoreClass
import com.example.myshoppal.models.CartItem
import com.example.myshoppal.models.Product
import com.example.myshoppal.ui.activities.adapters.CartItemsListAdapter
import com.example.myshoppal.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*


class CartListActivity : BaseActivity() {

    private lateinit var mProductList:ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent=Intent(this@CartListActivity,AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
            startActivity(intent)
        }
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {

        // Hide progress dialog.
        hideProgressDialog()

        for(product in mProductList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity=product.stock_quantity
                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity=product.stock_quantity
                    }
                }
            }
        }
         mCartListItems = cartList

        if (mCartListItems.size > 0) {

            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems,true)
            rv_cart_items_list.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if ( availableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            tv_sub_total.text = "$$subTotal"
            // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
            tv_shipping_charge.text = "$10.0"

            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 10
                tv_total_amount.text = "$$total"
            } else {
                ll_checkout.visibility = View.GONE
            }

        } else {
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }


    fun successProductsListFromFireStore(productsList:ArrayList<Product>){
        hideProgressDialog()
         mProductList=productsList
        getCartItemsList()
    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAllProductList(this)
    }
    /**
     * A function to get the list of cart items in the activity.
     */
    private fun getCartItemsList() {

        // Show the progress dialog.
       // showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getCartList(this@CartListActivity)
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }

    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     *
     * @param cartList
     */
    override fun onResume() {
        super.onResume()
        // TODO Step 5: Replace the function with getCartItemsList with getProductList as before cart list we require the product list.
      //  getCartItemsList()
        getProductList()
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()

        Toast.makeText(
                this@CartListActivity,
                resources.getString(R.string.msg_item_removed_successfully),
                Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }
    //عمل زر لللرجوع الToolBar
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // TODO Step 4: Create a function to get product list to compare the current stock with the cart items.
    // START
    /**
     * A function to get product list to compare the current stock with the cart items.
     */
}