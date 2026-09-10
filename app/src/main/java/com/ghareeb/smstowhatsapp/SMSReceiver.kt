package com.ghareeb.smstowhatsapp
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import java.util.concurrent.Executors
class SMSReceiver:BroadcastReceiver(){ override fun onReceive(c:Context,i:Intent){ if(i.action!=Telephony.Sms.Intents.SMS_RECEIVED_ACTION)return; val p=goAsync(); Executors.newSingleThreadExecutor().execute { try { val prefs=RelayConfig.prefs(c); if(!prefs.getBoolean(RelayConfig.ENABLED,false))return@execute; val allowed=prefs.getString(RelayConfig.SENDERS,"")!!.split(',').map{it.trim().lowercase()}; Telephony.Sms.Intents.getMessagesFromIntent(i).forEach{sms-> val sender=sms.displayOriginatingAddress?:return@forEach; val body=sms.messageBody?:""; if(allowed.any{it.isNotEmpty()&&sender.lowercase().contains(it)}&&!RelayConfig.isOtp(body)) RelayWorker.enqueue(c,sender,body,sms.timestampMillis) } } finally {p.finish()} } } }
