package com.example.myshoppal.ui.activities

import android.os.Bundle
import android.widget.Toast
import com.example.myshoppal.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_forgot_password_activity)
        val actionBar=supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }


        btn_submit.setOnClickListener {
            val email:String =et_email.text.toString().trim{it <= ' '}
            if (email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task->
                            hideProgressDialog()

                            if (task.isSuccessful){
                                //Show the toast message and finish the forgot password activity to go back to the loginActivity
                                Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        resources.getString(R.string.email_sent_success),
                                        Toast.LENGTH_LONG
                                ).show()

                                finish()
                            }else{
                                showErrorSnackBar(task.exception!!.message.toString(),true)
                            }
                        }
            }
        }
    }
}