package com.example.myshoppal.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myshoppal.R
import com.example.myshoppal.firestore.FireStoreClass
import com.example.myshoppal.models.User
import com.example.myshoppal.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() ,View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //لازاله الشريط اللزي يوجد اعلي واجهه الSplashActivity
        //لجعل الواجهه ملئ الشاشه
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        // START
        // Click event assigned to Forgot Password text.
        tv_forgot_password.setOnClickListener(this)
        // Click event assigned to Login button.
        btn_login.setOnClickListener(this)
        // Click event assigned to Register text.
        tv_register.setOnClickListener(this)
        // END
    }

    //لملء البيانات والانتقال من واجهه الLoginActivity الي واجهه الMainActivity
    fun userLoggedInSuccess(user:User){
        //Hide the progress dialog
        hideProgressDialog()



        if (user.profileCompleted==0){
            //if the user profile is incomplete the launch the UserProfileActivity.
            val intent =Intent(this@LoginActivity, UserProfileActivity::class.java)
           intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            //Redirect the user to Main Screen after log in.
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()

    }

    //In Login screen the clickable components are Login Button,ForgotPassword text Register Text.
    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.tv_forgot_password->{
                    val intent=Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login->{
                    logInRegisteredUser()
                }


                R.id.tv_register->{
                    //Launch the register screen when the user click on the text.
                    val intent=Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun validateLoginDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_email.text.toString().trim{it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim{it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            else->{
              //  showErrorSnackBar("Your details are valid",true)
                 true
            }

        }
    }

    private fun logInRegisteredUser(){
        if (validateLoginDetails()){

            //Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            //Get the text from editText and trim the space.
            val email =et_email.text.toString().trim{it <= ' '}
            val password =et_password.text.toString().trim{it <= ' '}

            //Login Using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){

                            FireStoreClass().getUserDetails(this@LoginActivity)
                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)

                        }
                    }
        }
    }
}