package com.ghareeb.smstowhatsapp
import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
class RelayWorker(c:Context,p:WorkerParameters):CoroutineWorker(c,p){ override suspend fun doWork():Result=withContext(Dispatchers.IO){ val pref=RelayConfig.prefs(applicationContext); val endpoint=pref.getString(RelayConfig.URL,"")?:return@withContext Result.failure(); val token=pref.getString(RelayConfig.TOKEN,"")?:return@withContext Result.failure(); try { val body=JSONObject().put("sender",inputData.getString("sender")).put("body",inputData.getString("body")).put("received_at",inputData.getLong("received_at",0)).put("event_id",inputData.getString("event_id")).toString(); val h=(URL(endpoint).openConnection() as HttpURLConnection).apply{requestMethod="POST";connectTimeout=15000;readTimeout=15000;setRequestProperty("Content-Type","application/json");setRequestProperty("Authorization","Bearer $token");doOutput=true}; h.outputStream.use{it.write(body.toByteArray())}; if(h.responseCode in 200..299)Result.success() else if(h.responseCode>=500)Result.retry() else Result.failure() }catch(_:Exception){Result.retry()} }
companion object{ fun enqueue(c:Context,s:String,b:String,t:Long){val id=MessageDigest.getInstance("SHA-256").digest("$s|$t|$b".toByteArray()).joinToString(""){"%02x".format(it)};val d=Data.Builder().putString("sender",s).putString("body",b).putLong("received_at",t).putString("event_id",id).build();val r=OneTimeWorkRequestBuilder<RelayWorker>().setInputData(d).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build();WorkManager.getInstance(c).enqueueUniqueWork("bank-$id",ExistingWorkPolicy.KEEP,r)}}}
