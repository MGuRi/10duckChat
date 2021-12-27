package com.sinduck.jotbyungsin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sinduck.jotbyungsin.Util.XmppConnectionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        login.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val result = XmppConnectionManager.setConnection(
                    idText.text.toString(),
                    pwText.text.toString()
                )
                runOnUiThread {
                    if (result) {
                        Log.e(TAG, "SUCCESS AUTH")
                        startActivity(Intent(applicationContext, ChatList::class.java))
                    } else {
                        Log.e(TAG, "FAILED")
                    }
                }
            }
        }
    }
}