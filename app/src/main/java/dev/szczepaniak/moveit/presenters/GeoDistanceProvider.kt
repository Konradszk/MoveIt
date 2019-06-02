package dev.szczepaniak.moveit.presenters

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import dev.szczepaniak.moveit.R
import logd
import loge
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class GeoDistanceProvider(private val mContext: Context, private val app: MoveEventI ) : AsyncTask<String, Void, String>() {

    private val TAG = "GEO_ASYNC"
    private val BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?"
    private val API_KEY = ""

    private lateinit var progressDialog: AlertDialog

    override fun doInBackground(vararg params: String?): String {
        try {
            val url = URL(buildUrl(params[0], params[1], params[2], params[3]))
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            val statusCode = connection.responseCode
            if (statusCode == HttpURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(connection.inputStream))
                val sb = StringBuilder()
                var line = br.readLine()
                while (line != null) {
                    sb.append(line)
                    line = br.readLine()
                }

                val result = sb.toString()
                val json = JSONObject(result)
                val minutes = json.getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("duration")
                    .get("value")

                return minutes.toString()
            }

        } catch (e: MalformedURLException) {
            "error".loge(TAG)
        } catch (e: IOException) {
            "error".loge(TAG)
        } catch (e: JSONException) {
            "error".loge(TAG)
        }
        return ""
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ShowProgressDialog()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        progressDialog.dismiss()
        if(result!=null){
            result.logd(TAG)
            this.app.moveEventAI(result.toString())
        }
    }

    private fun buildUrl(vararg params: String?): String {
        return "${BASE_URL}origins=${params[0]}&destinations=${params[1]}&mode=${params[2]}&language=pl-PL&departure_time=${params[3]}&key=$API_KEY"
    }


    fun ShowProgressDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val dialogView = inflater!!.inflate(R.layout.progress_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        progressDialog = dialogBuilder.create()
        progressDialog.show()
    }
}

interface MoveEventI {
    public fun moveEventAI(value: String)
}
