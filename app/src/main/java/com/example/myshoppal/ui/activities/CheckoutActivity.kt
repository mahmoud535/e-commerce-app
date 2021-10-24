package com.example.myshoppal.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.firestore.FireStoreClass
import com.example.myshoppal.models.Address
import com.example.myshoppal.models.CartItem
import com.example.myshoppal.models.Order
import com.example.myshoppal.models.Product
import com.example.myshoppal.ui.activities.adapters.CartItemsListAdapter
import com.example.myshoppal.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails:Address?=null
    private lateinit var  mProductList:ArrayList<Product>
    private lateinit var mCartItemList:ArrayList<CartItem>
    private var mSubTotal:Double=0.0
    private var mTotalAmount:Double=0.0
    private lateinit var mOrderDetails:Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mAddressDetails=intent.getParcelableExtra<Address>(Constants.EXTRA_SELECT_ADDRESS)
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()

        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
                .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess() {
         FireStoreClass().updateAllDetails(this,mCartItemList,mOrderDetails)

    }

    fun successProductListFromFireStore(productsList:ArrayList<Product>){
        mProductList=productsList
        getCartItemList()
    }

    private fun getCartItemList(){
        FireStoreClass().getCartList(this@CheckoutActivity)
    }

    private fun placeAnOrder(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mAddressDetails !=null) {
            mOrderDetails = Order(
                FireStoreClass().getCurrentUserID(),
                mCartItemList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemList[0].image,
                mSubTotal.toString(),
                "10.0", // The Shipping Charge is fixed as $10 for now in our case.
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )
            // END

            // TODO Step 10: Call the function to place the order in the cloud firestore.
            // START
            FireStoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)
        }
    }

    fun successCartItemList(cartList:ArrayList<CartItem>){
        hideProgressDialog()
        for(product in mProductList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity=product.stock_quantity
                }
            }
        }
        mCartItemList=cartList

        rv_cart_list_items.layoutManager=LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter=CartItemsListAdapter(this@CheckoutActivity,mCartItemList,false)
         rv_cart_list_items.adapter=cartListAdapter

        for (item in mCartItemList){
            val availableQuantity=item.stock_quantity.toInt()
            if (availableQuantity>0){
                val price=item.price.toDouble()
                val quantity=item.cart_quantity.toInt()
                mSubTotal +=(price * quantity)
            }
        }
        tv_checkout_sub_total.text="$$mSubTotal"
        tv_checkout_shipping_charge.text="$10.0"

        if (mSubTotal>0){
            ll_checkout_place_order.visibility=View.VISIBLE
            mTotalAmount=mSubTotal+10.0
            tv_checkout_total_amount.text="$$mTotalAmount"
        }else{
            ll_checkout_place_order.visibility=View.GONE
        }
    }

    private fun getProductList(){
        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getAllProductList(this@CheckoutActivity)
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
}