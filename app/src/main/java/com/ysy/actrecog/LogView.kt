package com.ysy.actrecog

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

/** Simple TextView which is used to output log data.
 */
class LogView : TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    /**
     * Formats the log data and prints it out to the LogView.
     * @param msg The actual message to be logged. The actual message to be logged.
     */
    fun println(msg: String) {

        // In case this was originally called from an AsyncTask or some other off-UI thread,
        // make sure the update occurs within the UI thread.
        (context as Activity).runOnUiThread(Thread(Runnable {
            // Display the text we just generated within the LogView.
//            appendToLog(msg)
            text = msg
        }))
    }

    /** Outputs the string as a new line of log data in the LogView.  */
    private fun appendToLog(s: String) {
        append("\n" + s)
    }
}
