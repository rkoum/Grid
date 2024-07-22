package com.example.grid

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.ToolbarWidgetWrapper
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var searchView: SearchView
    private lateinit var suggestionsListView: ListView
    private lateinit var adapter: PlayerCursorAdapter
    private lateinit var imageButton1: ImageButton
    private lateinit var imageButton2: ImageButton
    private lateinit var imageButton3: ImageButton
    private lateinit var imageButton4: ImageButton
    private lateinit var imageButton5: ImageButton
    private lateinit var imageButton6: ImageButton
    private lateinit var imageButton7: ImageButton
    private lateinit var imageButton8: ImageButton
    private lateinit var imageButton9: ImageButton

    private lateinit var conditionTextView1: TextView
    private lateinit var conditionTextView2: TextView
    private lateinit var conditionTextView3: TextView
    private lateinit var conditionTextViewA: TextView
    private lateinit var conditionTextViewB: TextView
    private lateinit var conditionTextViewC: TextView

    private val foundPlayers: MutableList<String> = mutableListOf()

    // Define initial conditions
    private var condition1 = "REB = 3"
    private var condition2 = "AST > 2"
    private var condition3 = "REB = 4"
    private var conditionA = "REB = 3"
    private var conditionB = "PTS < 11"
    private var conditionC = "TEAM = \"Olympiacos\""
    private var queries: List<String> = listOf(
        "SELECT * FROM Players WHERE NAME = ? AND ${condition1} AND ${conditionA} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition2} AND ${conditionA} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition3} AND ${conditionA} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition1} AND ${conditionB} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition2} AND ${conditionB} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition3} AND ${conditionB} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition1} AND ${conditionC} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition2} AND ${conditionC} ",
        "SELECT * FROM Players WHERE NAME = ? AND ${condition3} AND ${conditionC} "
    )

    private var currentPage = 0
    private val pageSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        dbHelper = MyDatabaseHelper(this)

        searchView = findViewById(R.id.searchView)
        suggestionsListView = findViewById(R.id.suggestionsListView)
        imageButton1 = findViewById(R.id.imageButton1)
        imageButton2 = findViewById(R.id.imageButton2)
        imageButton3 = findViewById(R.id.imageButton3)
        imageButton4 = findViewById(R.id.imageButton4)
        imageButton5 = findViewById(R.id.imageButton5)
        imageButton6 = findViewById(R.id.imageButton6)
        imageButton7 = findViewById(R.id.imageButton7)
        imageButton8 = findViewById(R.id.imageButton8)
        imageButton9 = findViewById(R.id.imageButton9)

        // Find TextViews by their ID
        conditionTextView1 = findViewById(R.id.conditionTextView1)
        conditionTextView2 = findViewById(R.id.conditionTextView2)
        conditionTextView3 = findViewById(R.id.conditionTextView3)
        conditionTextViewA = findViewById(R.id.conditionTextViewA)
        conditionTextViewB = findViewById(R.id.conditionTextViewB)
        conditionTextViewC = findViewById(R.id.conditionTextViewC)

        // Update TextViews with current conditions
        updateConditionTextViews()
    }

    private fun updateConditionTextViews() {
        conditionTextView1.text = condition1
        conditionTextView2.text = condition2
        conditionTextView3.text = condition3
        conditionTextViewA.text = conditionA
        conditionTextViewB.text = conditionB
        conditionTextViewC.text = conditionC


        adapter = PlayerCursorAdapter(this, null)
        suggestionsListView.adapter = adapter

        // Set up query text listener for the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    currentPage = 0 // Reset to the first page
                    updateSuggestions(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        currentPage = 0 // Reset to the first page
                        updateSuggestions(it)
                    } else {
                        // Clear the suggestions if the query is empty
                        adapter.changeCursor(null)
                    }
                }
                return true
            }
        })

        // Handle ListView item click
        fun handleItemClick(
            position: Int,
            searchView: SearchView,
            imageButton: ImageButton,
            index: Int
        ) {
            val cursor = adapter.cursor
            val query = queries[index]
            cursor?.let {
                if (it.moveToPosition(position)) {
                    val playerName = it.getString(it.getColumnIndexOrThrow("NAME"))
                    // Set the player name in the SearchView
                    searchView.setQuery(playerName, true)
                    // Check if the player is found and update the ImageButton
                    searchPlayer(playerName, query, imageButton)
                } else {
                    Log.e("MainActivity", "Failed to move cursor to position: $position")
                }
            } ?: Log.e("MainActivity", "Cursor is null")
        }

        val imageButtons = listOf(
            Pair(imageButton1, 0),
            Pair(imageButton2, 1),
            Pair(imageButton3, 2),
            Pair(imageButton4, 3),
            Pair(imageButton5, 4),
            Pair(imageButton6, 5),
            Pair(imageButton7, 6),
            Pair(imageButton8, 7),
            Pair(imageButton9, 8)
        )

        for ((button, queryIndex) in imageButtons) {
            button.setOnClickListener {
                suggestionsListView.setOnItemClickListener { _, _, position, _ ->
                    handleItemClick(position, searchView, button, queryIndex)
                }
                openSearchView()
            }
        }
    }

    private fun openSearchView() {
        // Expand the SearchView if it is collapsed
        searchView.isIconified = false

        // Use a Handler to ensure the view has time to be fully rendered
        searchView.postDelayed({
            searchView.requestFocus()

            // Show the soft keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
        }, 100) // Delay for 100 milliseconds
    }

    private fun updateSuggestions(query: String) {
        Log.d("MainActivity", "Updating suggestions with query: $query")
        val db = dbHelper.readableDatabase
        val offset = currentPage * pageSize
        val sql = "SELECT * FROM Players WHERE NAME LIKE ? LIMIT ? OFFSET ?"
        Log.d(
            "MainActivity",
            "Executing query: $sql with parameters: [%$query%, $pageSize, $offset]"
        )

        try {
            val cursor: Cursor? =
                db.rawQuery(sql, arrayOf("%$query%", pageSize.toString(), offset.toString()))
            if (cursor != null) {
                Log.d("MainActivity", "Cursor count: ${cursor.count}")
                adapter.changeCursor(cursor)

                if (cursor.count == 0) {
                    Toast.makeText(this, "No players found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("MainActivity", "Cursor is null after query execution")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception occurred while updating suggestions: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "An error occurred while updating suggestions", Toast.LENGTH_SHORT)
                .show()
        } finally {
            db.close() // Ensure database is closed after use
            Log.d("MainActivity", "Database closed")
        }
    }

    private fun searchPlayer(
        playerNameInput: String,
        query: String,
        imageButton: ImageButton
    ) {
        Log.d("MainActivity", "Searching for player: $playerNameInput")
        val db = dbHelper.readableDatabase
        Log.d("MainActivity", "Executing query: $query with parameter: [$playerNameInput]")

        try {
            val cursor = db.rawQuery(query, arrayOf(playerNameInput))
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow("NAME")
                val imageUrlIndex = cursor.getColumnIndexOrThrow("IMAGE_URL")
                val playerName = cursor.getString(nameIndex)
                val imageUrl = cursor.getString(imageUrlIndex)

                // Check if player is already in the list
                if (!foundPlayers.contains(playerName)) {
                    // Add playerName to the list
                    foundPlayers.add(playerName)

                    // Load image into ImageButton using Glide
                    Glide.with(this)
                        .load(imageUrl)
                        // .placeholder(R.drawable.placeholder_image) // Optional: placeholder image
                        // .error(R.drawable.error_image) // Optional: error image
                        .into(imageButton)
                    searchView.setQuery("", false)
                    searchView.clearFocus()
                } else {
                    Toast.makeText(this, "Player already in Grid!: $playerName", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("MainActivity", "Player already in list: $playerName")
                }

            } else {
                searchView.setQuery("", false)
                searchView.clearFocus()
                Toast.makeText(this, "Player not found", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Player not found")
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception occurred while searching for player: ${e.message}")
            e.printStackTrace()
            Toast.makeText(
                this,
                "An error occurred while searching for the player",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            db.close() // Ensure database is closed after use
            Log.d("MainActivity", "Database closed")
        }
    }
}
//test123