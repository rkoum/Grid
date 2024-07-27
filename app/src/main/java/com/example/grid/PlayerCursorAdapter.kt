package com.example.grid

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class PlayerCursorAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val nameTextView = view.findViewById<TextView>(android.R.id.text1)
            val name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"))
       // nameTextView.setTypeface(nameTextView.typeface, android.graphics.Typeface.BOLD)
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.semi_transparent_white))
        nameTextView.text = name



    }
}