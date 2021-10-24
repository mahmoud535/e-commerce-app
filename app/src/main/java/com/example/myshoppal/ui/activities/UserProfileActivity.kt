package com.example.myshoppal.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.firestore.FireStoreClass
import com.example.myshoppal.models.User
import com.example.myshoppal.utils.Constants
import com.example.myshoppal.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.et_email
import kotlinx.android.synthetic.main.activity_user_profile.et_first_name
import kotlinx.android.synthetic.main.activity_user_profile.et_last_name
import kotlinx.android.synthetic.main.activity_user_profile.tv_title
import java.io.IOException

class UserProfileActivity : BaseActivity(),View.OnClickListener {

    private lateinit var mUserDetails:User
    private  var mSelectedImageFileUri:Uri?=null
    private  var mUserProfileImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)



        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            //Get the user details from intent as a ParcelableExtra.
            mUserDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.isEnabled=false
        et_email.setText(mUserDetails.email)
        if (mUserDetails.profileCompleted==0){
            tv_title.text=resources.getString(R.string.title_complete_profile)
            et_first_name.isEnabled=false
            et_last_name.isEnabled=false
        }else{
            setUpActionBar()
            tv_title.text=resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image,iv_user_photo)

            if (mUserDetails.mobile !=0L){
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender==Constants.MALE){
                rb_male.isChecked=true
            }else{
                rb_female.isChecked=true
            }
        }

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)

    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onClick(v: View?) {
        //طلب رخصه لتحميل الصوره
        if (v != null){
            when(v.id){
                R.id.iv_user_photo-> {
                    //Hera we will check if the permission is already allowed or we need to request for it .
                    //First of all we will check the READ_External_Storage permission and if it is not allowed
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                      //showErrorSnackBar("You already have the storage permission.",false)

                    Constants.showImageChooser(this)
                    } else {
                          /*Requests permissions to be granted to this application. These permissions
                              must be requested in your manifest, they should not be granted to your app,
                              and they should have protection level*/

                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit->{
                    if (validateUserProfileDetails()) {

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null) {

                            FireStoreClass().uploadImageToCloudStorage(
                                this@UserProfileActivity,
                                mSelectedImageFileUri,
                            Constants.USER_PROFILE_IMAGE)
                        } else {

                            // TODO Step 4: Move this piece of code to the separate function as the profile image is optional. So, if the image is uploaded then we will update the image URL in the firestore.
                            // START
                            /*val userHashMap = HashMap<String, Any>()

                            // Here the field which are not editable needs no update. So, we will update user Mobile Number and Gender for now.

                            // Here we get the text from editText and trim the space
                            val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

                            val gender = if (rb_male.isChecked) {
                                Constants.MALE
                            } else {
                                Constants.FEMALE
                            }

                            if (mobileNumber.isNotEmpty()) {
                                userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                            }

                            userHashMap[Constants.GENDER] = gender

                            // Show the progress dialog.
                            showProgressDialog(resources.getString(R.string.please_wait))

                            // call the registerUser function of FireStore class to make an entry in the database.
                            FirestoreClass().updateUserProfileData(
                                this@UserProfileActivity,
                                userHashMap
                            )*/

                            // END

                            // TODO Step 8: Call the user update details function.
                            // START
                            updateUserProfileDetails()
                            // END
                        }
                    }

                }
            }
        }
    }

    private fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()

        val firstname=et_first_name.text.toString().trim{it<=' '}
        if (firstname != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME]=firstname
        }
        val lastname=et_last_name.text.toString().trim{it<=' '}
        if (lastname != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME]=lastname
        }

        // Here the field which are not editable needs no update. So, we will update user Mobile Number and Gender for now.

        // Here we get the text from editText and trim the space
        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        // TODO Step 7: Now update the profile image field if the image URL is not empty.
        // START
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        // END

        if (mobileNumber.isNotEmpty()&&mobileNumber!=mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty()&& gender!=mUserDetails.gender) {
            userHashMap[Constants.GENDER] =gender
        }

        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        // TODO 11 : Remove the show progress dialog piece of code from here to avoid the jerk hiding and showing it at the same time.
        // START
        // Show the progress dialog.
        /*showProgressDialog(resources.getString(R.string.please_wait))*/
        // END

        // call the registerUser function of FireStore class to make an entry in the database.
        FireStoreClass().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
        resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO Step 4: Remove the message and Call the image selection function here when the user grant the read storage permission.
                // START
                /*showErrorSnackBar("The storage permission is granted.",false)*/

                Constants.showImageChooser(this@UserProfileActivity)
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                        this,
                        resources.getString(R.string.read_storage_permission_denied),
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        //iv_user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                                this@UserProfileActivity,
                                resources.getString(R.string.image_selection_failed),
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }
            else->{
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL=imageURL
        updateUserProfileDetails()
    }
}