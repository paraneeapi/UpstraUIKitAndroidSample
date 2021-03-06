package com.ekoapp.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ekoapp.ekosdk.EkoClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isLoggedInUser()) {
            navigateToFeatureList()
        }
        else {
            initView()
        }

    }

    private fun initView() {
        btnLogin.setOnClickListener {
            if (etUserId.text.isNotEmpty() && etUserName.text.isNotEmpty()) {
                signInUser(etUserId.text.toString(), etUserName.text.toString())
                EkoClient.registerDevice("").displayName("").authToken("")
                EkoClient.registerDevice(etUserId.text.toString(), etUserName.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete {
                            navigateToFeatureList()
                        }
                        .doOnError {
                            Toast.makeText(this, "Could not register user "+ it.message, Toast.LENGTH_LONG).show()
                        }
                        .subscribe()
            } else {
                Toast.makeText(this, "Enter userId and Display Name", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun signInUser(userId: String, userName: String) {
       EkoClient.registerDevice(userId).displayName(userName).build().submit()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .doOnComplete {
                   navigateToFeatureList()
               }
               .doOnError {
                   Toast.makeText(this, "Could not register user "+ it.message, Toast.LENGTH_LONG).show()
               }
               .subscribe()
    }

    private fun navigateToFeatureList() {
        val intent = Intent(this, FeatureListActivity::class.java)
        startActivity(intent)
    }

    private fun isLoggedInUser(): Boolean {
        return EkoClient.getDisplayName().isNotEmpty()
    }
}