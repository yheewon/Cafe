package kr.heewon.cafe

import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ManageActivity : AppCompatActivity() {
    var name : EditText? = null
    var price : EditText? = null
    var plus : Button? = null
    var update : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)

        name = findViewById(R.id.name) as EditText
        price = findViewById(R.id.price) as EditText
        plus = findViewById(R.id.plus) as Button
        update = findViewById(R.id.update) as Button

        plus!!.setOnClickListener {
            //drink 테이블에 추가

            val task = InsertData()
            task.execute("http://$IP_ADDRESS/insert_drink.php", name!!.text.toString(), price!!.text.toString())
            Toast.makeText(applicationContext, "추가 완료", Toast.LENGTH_SHORT).show()
        }

        update!!.setOnClickListener {
            //가격 수정

            val task = ChangePrice()
            task.execute("http://$IP_ADDRESS/change_price.php",name!!.text.toString(), price!!.text.toString())
            Toast.makeText(applicationContext, "수정 완료", Toast.LENGTH_SHORT).show()
        }

    }

    internal inner class InsertData : AsyncTask<String?, Void?, String>() {
        var progressDialog: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@ManageActivity,
                "Please Wait", null, true, true)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            progressDialog!!.dismiss()
//            mTextViewResult!!.text = result
            Log.d(TAG, "POST response  - $result")
        }

        override fun doInBackground(vararg params: String?): String? {
            val postParameters = "name=${params[1]}&price=${params[2]}"
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
                Log.d(TAG, "InsertData: Error ", e)
                return "error"
            }
        }
    }

    internal inner class ChangePrice : AsyncTask<String?, Void?, String>() {
        var progressDialog: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@ManageActivity,
                "Please Wait", null, true, true)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            progressDialog!!.dismiss()
            Log.d(ManageActivity.TAG, "POST response  - $result")
        }

        override fun doInBackground(vararg params: String?): String? {
            val postParameters = "name=${params[1]}&price=${params[2]}"
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
                Log.d(ManageActivity.TAG,"POST response code - $responseStatusCode")
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
                Log.d(ManageActivity.TAG, "ChangeBeacon: Error ", e)
                return "error"
            }
        }
    }

    companion object {
        private const val IP_ADDRESS = "192.168.62.84"
        private const val TAG = "phptest"
    }
}