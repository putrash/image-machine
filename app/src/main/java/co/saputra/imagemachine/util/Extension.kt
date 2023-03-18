package co.saputra.imagemachine.util

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.SystemClock
import android.provider.OpenableColumns
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import co.saputra.imagemachine.Constants
import java.text.SimpleDateFormat
import java.util.*

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Date.formatDate(): String {
    val formatter = SimpleDateFormat(Constants.DATE, Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatTime(): String {
    val formatter = SimpleDateFormat(Constants.TIME, Locale.getDefault())
    return formatter.format(this)
}

fun String.parseDate(): Date {
    val formatter = SimpleDateFormat(Constants.DATE, Locale.getDefault())
    return formatter.parse(this) ?: Date()
}

fun View.setSafeClickListener(throttle: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttle) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

// Find one nav controller by id FragmentContainerView
fun Fragment.findNavControllerById(@IdRes id: Int): NavController {
    var parent = parentFragment
    while (parent != null) {
        if (parent is NavHostFragment && parent.id == id) {
            return parent.navController
        }
        parent = parent.parentFragment
    }
    throw RuntimeException("NavController with specified id not found")
}

fun Uri.getFileName(context: Context): String {
    var fileName = ""
    context.contentResolver.query(this, null, null, null, null)
        ?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName =  cursor.getString(nameIndex)
        }
    return fileName
}