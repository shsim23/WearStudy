package com.example.wear_study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent.DispatcherState
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val DUAL_VIEWER_WATCH_CAPABILITY_NAME = "dual_viewer_watch"
private const val DUAL_VIEWER_PATH = "/dual_viewer"

class MainActivity : AppCompatActivity(), OnClickListener {
    private var num = 0
    private lateinit var txtNum: TextView
    private val mClient by lazy { Wearable.getMessageClient(this)}
    private val cClient by lazy { Wearable.getCapabilityClient(this)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bPlus = findViewById<Button>(R.id.bUp)
        val bMinus = findViewById<Button>(R.id.bDown)
        txtNum = findViewById<TextView>(R.id.num)

        bPlus.setOnClickListener(this)
        bMinus.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bUp -> num++
            R.id.bDown -> num--
        }
        txtNum.text = "number : ${num.toString()}"
        sendMsg()
    }

    private fun sendMsg(){
        CoroutineScope(Dispatchers.IO).launch {
            val nodes = Tasks.await(
                cClient.getCapability(
                    DUAL_VIEWER_WATCH_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE
                )
            ).nodes
            var nodeId = nodes.first().id
            mClient
                .sendMessage(nodeId, DUAL_VIEWER_PATH, byteArrayOf(num.toByte()))
        }
    }
}

