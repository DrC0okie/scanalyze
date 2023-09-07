package ch.heigvd.scanalyze.Utils

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast

object Utils {

    private fun showToast(msg: String, context: Context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun showErrorDialog(e: Exception, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Oops...")
        builder.setMessage(e.message ?: "An unknown error occurred.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    fun showErrorDialog(msg: String, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Oops...")
        builder.setMessage(msg)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss()}
        builder.show()
    }


    /**
     * @author Manohar
     * @see https://stackoverflow.com/a/53532456
     */
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }
}