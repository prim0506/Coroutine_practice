package com.example.coroutineactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var htmlStr = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coroutine()
    }

    fun coroutine() {

        //메인스레드, 동기방식(launch)
        CoroutineScope(Dispatchers.Main).launch {
                       //백그라운드 스레드, 비동기 방식(async)
                       //비동기일때 .await()로 기다려서 value(html)를 받을수있다.
            val html = CoroutineScope(Dispatchers.Default).async {
                //network
                getHtml()
            }.await()

            //main thread
            val mTextMain = findViewById<TextView>(R.id.mTextmain)
            mTextMain.text = html

        }
    }

    fun getHtml() :String {
        //1.클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        //2. 요청
        val req = Request.Builder().url("https://www.google.com").build()
        //3. 응답 동기방식(execute)
        client.newCall(req).execute().use {
            response -> return if(response.body != null){
                response.body!!.string()
            }
            else{
                    "body null"
            }
        }
    }
    fun getHtmlStr(){
        //1.클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        //2. 요청
        val req = Request.Builder().url("https://www.google.com").build()
        //3. 응답 비동기방식(enqueue)일땐 콜백함수를 꼭 구현한다.
        client.newCall(req).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {
                    val mTextMain = findViewById<TextView>(R.id.mTextmain)
                    mTextMain.text = response.body!!.string()
                }
            }
        })
    }
}