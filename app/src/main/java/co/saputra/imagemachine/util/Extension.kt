package co.saputra.imagemachine.util

import android.content.res.Resources
import android.os.SystemClock
import android.view.View
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
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
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