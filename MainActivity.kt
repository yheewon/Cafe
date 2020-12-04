package kr.heewon.cafe

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {
    private var mArrayList: ArrayList<ReservationData>? = null//뷰에 출력하기 위한 리스트
    private var mAdapter: UsersAdapter? = null
    private var mRecyclerView: RecyclerView? = null// 리사이클러뷰
    private var mJsonString: String? = null

    var text : TextView? = null//리사이클러뷰 클릭한 아이템 출력할 텍스트뷰
    var commit : Button? = null //비콘 = 2 변경
    var manage : Button? = null

    var stop = 1 // 조회 무한루프 조건


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById<View>(R.id.listView_main_list) as RecyclerView
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(this))
        mArrayList = ArrayList<ReservationData>()
        mAdapter = UsersAdapter(this, mArrayList)
        mRecyclerView!!.setAdapter(mAdapter)
        text = findViewById(R.id.text) as TextView
        commit = findViewById(R.id.commit) as Button
        manage = findViewById(R.id.manage) as Button


        ////////////////////시작화면 이동
        val welcome_intent = Intent(this, WelcomeActivity::class.java)
        startActivityForResult(welcome_intent, 0)

        //////////////////////mysql data select
        object : Thread() {
            //데이터 조회 스레드
            override fun run() {//스레드 생성
                while (stop == 1) {
                    runOnUiThread() {//UI스레드 사용
                        mArrayList!!.clear()
                        mAdapter!!.notifyDataSetChanged()
                        val task = GetData()
                        task.execute("http://$IP_ADDRESS/getjson.php", "")
                    }
                    SystemClock.sleep(5000)
                }
            }
        }.start()

        /////////////////////////item click
        object : Thread() {
            override fun run() {
                runOnUiThread {
                    mAdapter!!.setItemClickListener(object : UsersAdapter.OnItemClickListener {
                        override fun onClick(v: View, position: Int) {
                            val item = mArrayList!![position]
                            val divide_drink = item.res_drink!!.split("/")
                            var drink = ""
                            for (i in 0..divide_drink.size-1){
                                drink += divide_drink[i] + "\n"
                            }
                            text!!.text =
                                "\n 010 - **** - " + item.res_pnum + "\n" + item.res_room +"  "+ item.res_time +"\n"+ drink
                            mRecyclerView!!.adapter = mAdapter
                            mAdapter!!.notifyDataSetChanged()

                            commit!!.setOnClickListener {
                                var sql_beacon = 2
                                val task = ChangeBeacon()
                                task.execute("http://$IP_ADDRESS/commit_beacon.php",item.res_id, sql_beacon.toString())
                                Toast.makeText(applicationContext, "음료 제작 완료", Toast.LENGTH_SHORT).show()
                            }

                        }
                    })

                }
                //SystemClock.sleep(2000)
            }
        }.start()

        /////////////////////////manager click
        object : Thread() {
            override fun run() {
                runOnUiThread {
                    manage!!.setOnClickListener {
                        Toast.makeText(applicationContext, "click", Toast.LENGTH_SHORT).show()
                        var manage_intent = Intent(applicationContext,ManageActivity::class.java)
                        startActivity(manage_intent)


                    }

                }
                //SystemClock.sleep(2000)
            }
        }.start()

    }

    internal inner class ChangeBeacon : AsyncTask<String?, Void?, String>() {
        var progressDialog: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@MainActivity,
                "Please Wait", null, true, true)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            progressDialog!!.dismiss()
            Log.d(TAG, "POST response  - $result")
        }

        override fun doInBackground(vararg params: String?): String? {
            val postParameters = "id=${params[1]}&beacon=${params[2]}"
            return try {
                val url = URL(params[0]) //php파일을 실행시킬수 있는 주소와 전송할 데이터 준비
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.readTimeout = 5000//5초안에 응답이 오지않으면 예외 발생
                httpURLConnection.connectTimeout = 5000 //5초안에 연결이 안되면 예외 발생
                httpURLConnection.requestMethod = "POST" //요청방식 POST
                httpURLConnection.connect()
                val outputStream = httpURLConnection.outputStream
                outputStream.write(postParameters.toByteArray(charset("UTF-8")))//전송할 데이터가 저장된 변수를 이곳에 입력
                outputStream.flush()
                outputStream.close()

                // 응답 읽기
                val responseStatusCode = httpURLConnection.responseCode
                Log.d(TAG, "POST response code - $responseStatusCode")
                val inputStream: InputStream
                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {//정상적인 응답
                    httpURLConnection.inputStream
                } else {//에러 발생
                    httpURLConnection.errorStream
                }
                //StringBuilder를 사용하여 수신되는 데이터 저장
                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)
                val sb = StringBuilder()
                var line : String? = null
                while (bufferedReader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                bufferedReader.close()
                sb.toString()//저장된 데이터 스트링으로 변환하여 리턴
            } catch (e: Exception) {
                Log.d(TAG, "ChangeBeacon: Error ", e)
                return "error"
            }
        }
    }

    private inner class GetData : AsyncTask<String?, Void?, String?>() {
        var errorString: String? = null

        override fun onPostExecute(result: String?) { //에러메세지 출력
            super.onPostExecute(result)
            //Log.d(TAG, "response - $result")
            if (result == null) {
                Toast.makeText(applicationContext, "오류!!!!!!!!!!", Toast.LENGTH_SHORT).show()
            } else {
                mJsonString = result
                showResult()//JSON파싱 함수
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val serverURL = params[0]
            val postParameters = params[1]
            return try {
                val url = URL(serverURL)
                val httpURLConnection =
                    url.openConnection() as HttpURLConnection
                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.doInput = true
                httpURLConnection.connect()
                val outputStream = httpURLConnection.outputStream
                if (postParameters != null) {
                    outputStream.write(postParameters.toByteArray(charset("UTF-8")))
                }
                outputStream.flush()
                outputStream.close()
                val responseStatusCode = httpURLConnection.responseCode
                Log.d(
                    TAG,
                    "response code - $responseStatusCode"
                )
                val inputStream: InputStream
                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    httpURLConnection.inputStream
                } else {
                    httpURLConnection.errorStream
                }
                val inputStreamReader =
                    InputStreamReader(inputStream, "UTF-8")
                val bufferedReader =
                    BufferedReader(inputStreamReader)
                val sb = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                bufferedReader.close()
                sb.toString().trim { it <= ' ' }
            } catch (e: Exception) {
                Log.d(TAG, "GetData : Error ", e)
                errorString = e.toString()
                null
            }
        }
    }

    private fun showResult() { //에러메시지나 결과 출력
        val TAG_JSON = "heewon"
        val TAG_ID = "id"
        val TAG_PNUM = "pnum"
        val TAG_ROOM = "room"
        val TAG_TIME = "time"
        val TAG_DRINK = "drink"

        try {
            val jsonObject = JSONObject(mJsonString)//중괄호 JSONObject
            val jsonArray = jsonObject.getJSONArray(TAG_JSON) //jsonobject에서 TAG_JSON키를 갖는 JSONArray를 가져옴

            for (i in 0 until jsonArray.length()) {//JSONObject 하나씩 가져오기
                val item = jsonArray.getJSONObject(i)
                var id = item.getString(TAG_ID)
                var pnum = item.getString(TAG_PNUM)
                var room = item.getString(TAG_ROOM)
                val time = item.getString(TAG_TIME)
                val drink = item.getString(TAG_DRINK)

                var d_pnum = pnum.split("-")
                room += "번 방"


                val reservationData = ReservationData() //arraylist에 추가
                reservationData.res_id = id
                reservationData.res_pnum = d_pnum[2]
                reservationData.res_room = room
                reservationData.res_time = time
                reservationData.res_drink = drink


                mArrayList!!.add(reservationData)
                mAdapter?.notifyDataSetChanged()//리스트에 데이터 변경
            }
        } catch (e: JSONException) {
            Log.d(TAG, "showResult : ", e)
        }
    }

    companion object {
        private const val IP_ADDRESS = "192.168.62.84"
        private const val TAG = "phptest"
    }
}