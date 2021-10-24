package com.example.myshoppal.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.firestore.FireStoreClass
import com.example.myshoppal.models.CartItem
import com.example.myshoppal.models.Product
import com.example.myshoppal.utils.Constants
import com.example.myshoppal.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity(),View.OnClickListener {

    private var mProductId:String=""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId=intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
       // var productOwnerId:String=""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            mProductOwnerId=intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        if (FireStoreClass().getCurrentUserID() == mProductOwnerId){
            btn_add_to_cart.visibility=View.GONE
            btn_go_to_cart.visibility=View.GONE
        }else{
            btn_add_to_cart.visibility=View.VISIBLE
        }
        getProductDetails()

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    private fun getProductDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getProductDetails(this,mProductId)

    }
    fun productExistsInCart(){
        hideProgressDialog()
        btn_add_to_cart.visibility=View.GONE
        btn_go_to_cart.visibility=View.VISIBLE
    }

    fun productDetailsSuccess(product: Product){
        mProductDetails=product
       // hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
                product.image,
                iv_product_detail_image
        )
        tv_product_details_title.text=product.title
        tv_product_details_price.text="$${product.price}"
        tv_product_details_description.text=product.description
        tv_product_details_available_quantity.text=product.stock_quantity

        if(product.stock_quantity.toInt() == 0){

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            btn_add_to_cart.visibility = View.GONE

            tv_product_details_available_quantity.text =
                    resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_available_quantity.setTextColor(
                    ContextCompat.getColor(
                            this@ProductDetailsActivity,
                            R.color.colorSnackBarError
                    )
            )
        }else{

            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (FireStoreClass().getCurrentUserID() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FireStoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar=supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun addToCart(){
        val cartItem=CartItem(
                FireStoreClass().getCurrentUserID(),
                mProductOwnerId,
                mProductId,
                mProductDetails.title,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addCartItems(this,cartItem)
    }
    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(
                this@ProductDetailsActivity,
                resources.getString(R.string.success_message_item_added_to_cart),
                Toast.LENGTH_SHORT
        ).show()

        btn_add_to_cart.visibility=View.GONE
        btn_go_to_cart.visibility=View.VISIBLE

    }

    override fun onClick(v: View?) {
            if (v!=null){
               when(v.id){
                   R.id.btn_add_to_cart->{
                       addToCart()
                   }
                   //
                   R.id.btn_go_to_cart->{
                       startActivity(Intent(this@ProductDetailsActivity,CartListActivity::class.java))
                   }
               }
            }
    }
}