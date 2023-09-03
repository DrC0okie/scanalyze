package ch.heigvd.scanalyze.Utils

import android.app.AlertDialog
import android.content.Context
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
}