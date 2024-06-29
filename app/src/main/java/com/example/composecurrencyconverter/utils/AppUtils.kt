package com.example.composecurrencyconverter.utils

import android.content.Context
import android.widget.Toast

object AppUtils {

    object ToastMsg {
        fun showMsg(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}