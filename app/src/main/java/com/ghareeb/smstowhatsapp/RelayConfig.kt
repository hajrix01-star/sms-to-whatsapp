package com.ghareeb.smstowhatsapp
import android.content.Context
object RelayConfig { const val SENDERS="senders"; const val URL="url"; const val TOKEN="token"; const val ENABLED="enabled"; fun prefs(c:Context)=c.getSharedPreferences("bank_relay",Context.MODE_PRIVATE); fun isOtp(body:String):Boolean { val b=body.lowercase(); return listOf("otp","one-time","verification code","رمز التحقق","رمز التاكيد","كود التحقق").any{b.contains(it)} } }
