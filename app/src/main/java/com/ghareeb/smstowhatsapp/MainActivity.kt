package com.ghareeb.smstowhatsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
  private lateinit var senders: EditText; private lateinit var url: EditText; private lateinit var token: EditText; private lateinit var status: TextView
  override fun onCreate(state: Bundle?) { super.onCreate(state); setContentView(R.layout.activity_main)
    senders=findViewById(R.id.senderFilter); url=findViewById(R.id.apiUrl); token=findViewById(R.id.apiToken); status=findViewById(R.id.statusText)
    val p=RelayConfig.prefs(this); senders.setText(p.getString(RelayConfig.SENDERS,"")); url.setText(p.getString(RelayConfig.URL,"")); token.setText(p.getString(RelayConfig.TOKEN,"")); update()
    findViewById<Button>(R.id.startButton).setOnClickListener { if(save()) request() }
    findViewById<Button>(R.id.stopButton).setOnClickListener { p.edit().putBoolean(RelayConfig.ENABLED,false).apply(); update() }
    findViewById<Button>(R.id.testButton).setOnClickListener { if(save()) { RelayWorker.enqueue(this,"BANK_RELAY_TEST","اختبار اتصال Bank Relay",System.currentTimeMillis()); Toast.makeText(this,"أُرسل اختبار للطابور",Toast.LENGTH_SHORT).show() } }
  }
  private fun save(): Boolean { val s=senders.text.toString().trim(); val u=url.text.toString().trim(); val t=token.text.toString().trim(); if(s.isEmpty()||!u.startsWith("https://")||t.isEmpty()){Toast.makeText(this,"أدخل المرسلين ورابط HTTPS والمفتاح",Toast.LENGTH_LONG).show();return false}; RelayConfig.prefs(this).edit().putString(RelayConfig.SENDERS,s).putString(RelayConfig.URL,u).putString(RelayConfig.TOKEN,t).apply();return true }
  private fun request(){ val a=mutableListOf(Manifest.permission.RECEIVE_SMS);if(Build.VERSION.SDK_INT>=33)a.add(Manifest.permission.POST_NOTIFICATIONS);val n=a.filter{ContextCompat.checkSelfPermission(this,it)!=PackageManager.PERMISSION_GRANTED};if(n.isEmpty())activate() else ActivityCompat.requestPermissions(this,n.toTypedArray(),1) }
  override fun onRequestPermissionsResult(c:Int,p:Array<out String>,r:IntArray){super.onRequestPermissionsResult(c,p,r);if(c==1&&r.all{it==PackageManager.PERMISSION_GRANTED})activate()}
  private fun activate(){RelayConfig.prefs(this).edit().putBoolean(RelayConfig.ENABLED,true).apply();update()}
  private fun update(){status.text=if(RelayConfig.prefs(this).getBoolean(RelayConfig.ENABLED,false))"الحالة: التحويل مفعّل" else "الحالة: غير مفعّل"}
}
