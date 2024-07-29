package com.example.grid

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import com.bumptech.glide.Glide
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var searchView: SearchView
    private lateinit var suggestionsListView: ListView
    private lateinit var adapter: PlayerCursorAdapter
    private  lateinit var newQueries: List<String>

    private lateinit var imageButton1: ImageButton
    private lateinit var imageButton2: ImageButton
    private lateinit var imageButton3: ImageButton
    private lateinit var imageButton4: ImageButton
    private lateinit var imageButton5: ImageButton
    private lateinit var imageButton6: ImageButton
    private lateinit var imageButton7: ImageButton
    private lateinit var imageButton8: ImageButton
    private lateinit var imageButton9: ImageButton
    private lateinit var replayButton: ImageButton

    private lateinit var label1: TextView
    private lateinit var label2: TextView
    private lateinit var label3: TextView
    private lateinit var label4: TextView
    private lateinit var label5: TextView
    private lateinit var label6: TextView
    private lateinit var label7: TextView
    private lateinit var label8: TextView
    private lateinit var label9: TextView

    private lateinit var conditionTextView1: TextView
    private lateinit var conditionTextView2: TextView
    private lateinit var conditionTextView3: TextView
    private lateinit var conditionTextViewA: TextView
    private lateinit var conditionTextViewB: TextView
    private lateinit var conditionTextViewC: TextView

    private lateinit var conditionImageBUttonA: ImageButton





    private val foundPlayers = MutableList<String?>(9) { null }

    val symbols = listOf(">", ">=", "<")
    var positions = listOf("%PG%", "%SG%", "%SF%", "%PF%", "%C%").map { "POS LIKE '$it'" }
    var ethnicities = listOf(
        "American", "Greek", "Croatian", "Montenegrin", "Albanian",
        "Lithuanian", "Russian", "Slovenian", "Georgian", "Bulgarian",
        "Canadian", "British", "Bosnian", "Dutch", "Serbia-Montenegro",
        "Australian", "French", "Senegalese", "Cypriot", "Swedish",
        "Latvian", "Serbian", "Ukrainian", "Italian", "Dominican",
        "Spanish", "Finnish", "Turkish", "Polish", "Nigerian",
        "New Zealand", "Cameroonian", "Ethiopian", "Uruguayan",
        "Macedonian", "Costa Rican", "Guinean", "Azerbaijani",
        "Hungarian", "Jamaican", "Belizean", "Guianan", "Irish",
        "Puerto Rican", "Israeli", "Ivoirian", "Central African",
        "Belorussian", "Malian", "Colombian", "Panamanian", "Argentine",
        "Cuban", "Danish", "Kazakhstani", "Georgian", "Romanian",
        "Bahamas", "Ghanaian", "Angolan", "Sudanese", "Icelandic"
    ).map { "ETHNICITY LIKE '%$it%'" }

    // List of team names
    var teams = listOf(
        "OFI Iraklio",
        "Ionikos Lamias",
        "AEK Athens",
        "Panathinaikos Athens",
        "Rethymno Cretan Kings",
        "Peristeri",
        "Panellinios BC",
        "Trikala",
        "Olympiacos Piraeus",
        "AEP Olimpiada Patron",
        "KAO Dramas",
        "Maroussi",
        "Kavalas",
        "Panionios",
        "Ikaros Esperos",
        "Trikalla",
        "Ermis Agias",
        "Nea Kiffisia",
        "Ilisiakos",
        "Kolossos H Hotels",
        "Sefa Arkadikos",
        "Koroivos",
        "Doxa Lefkadas",
        "PAOK",
        "Apollon Patras",
        "Lavrio",
        "Holargos",
        "Aris BC",
        "Kymis",
        "Promitheas Patras",
        "AS Karditsas",
        "Charilaos TM",
        "AO Ionikos Nikaias",
        "Iraklis",
        "Ifaistos limnou",
        "Panelefsiniakos",
        "Olympia Larissas",
        "AEL Larissa",
        "Egaleo AO",
        "Makedonikos",
        "Aons Milonas Athens",
        "Dafni",
        "Near-East",
        "Ment"
    ).map { "TEAM_NAME LIKE '$it'" }


    fun generateConditions(): List<String> {
        val symbols = listOf(">", ">=", "<")
        val stats = listOf("PTS", "REB", "AST", "BLK", "STL", "FG", "ThreePT", "FT")

        // Generate random values for statistics
        val randomValues = mapOf(
            "PTS" to Random.nextInt(1, 15),
            "REB" to Random.nextInt(1, 6),
            "AST" to Random.nextInt(1, 4),
            "BLK" to Random.nextInt(1, 2),
            "STL" to Random.nextInt(1, 2),
            "FG" to Random.nextInt(35, 100),
            "ThreePT" to Random.nextInt(35, 100),
            "FT" to Random.nextInt(35, 100)
        )

        // Generate a large number of conditions to ensure uniqueness
        var conditions = List(100) {
            val randomStat = stats.random()
            val randomSymbol = symbols.random()
            val value = randomValues[randomStat] ?: 0
            "$randomStat $randomSymbol $value"
        }.distinct()

        val takeEthnicities = ethnicities.shuffled().take(5)
        val takeTeams = teams.shuffled().take(5)

        conditions += positions
        conditions += takeEthnicities
        conditions += takeTeams
        // Take 3 unique conditions
        val uniqueConditions = conditions.shuffled().take(3)
        val condition1 = uniqueConditions.getOrElse(0) { "Unknown Condition 1" }
        val condition2 = uniqueConditions.getOrElse(1) { "Unknown Condition 2" }
        val condition3 = uniqueConditions.getOrElse(2) { "Unknown Condition 3" }

        // Extract used stats from uniqueConditions
        val usedStats = uniqueConditions.map { it.split(" ")[0] }.toSet()

        // Filter out conditions that contain stats in usedStats
        val filteredConditions = conditions.filter {
            val stat = it.split(" ")[0]
            stat !in usedStats
        }

        // Take 3 additional conditions that don't contain the used stats
        val additionalConditionsTaken = filteredConditions.take(3)

        val conditionA = additionalConditionsTaken.getOrElse(0) { "Unknown Condition A" }
        val conditionB = additionalConditionsTaken.getOrElse(1) { "Unknown Condition B" }
        val conditionC = additionalConditionsTaken.getOrElse(2) { "Unknown Condition C" }

        // Return the final list of conditions
        return listOf(condition1, condition2, condition3, conditionA, conditionB, conditionC)
    }

    val firstGen = generateConditions()
    val initialConditions = firstGen

    val queries: List<String> = listOf(
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[0]} AND ${initialConditions[3]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[1]} AND ${initialConditions[3]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[2]} AND ${initialConditions[3]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[0]} AND ${initialConditions[4]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[1]} AND ${initialConditions[4]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[2]} AND ${initialConditions[4]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[0]} AND ${initialConditions[5]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[1]} AND ${initialConditions[5]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${initialConditions[2]} AND ${initialConditions[5]}"
    )

    fun checkRecords(queries: List<String>, dbHelper: MyDatabaseHelper): Boolean {
        for (i in 0..8) {
            val query = queries[i].replace("NAME = ? AND ", "")
            Log.d("checkRecords", "Executing query: $query")
            val recordExists = dbHelper.isRecordExists(query)
            Log.d("checkRecords", "Query result: $recordExists")
            if (!recordExists) {
                return false
            }
        }
        return true
    }

    fun generateValidConditions(dbHelper: MyDatabaseHelper): List<String> {
        var isValid = false
        var finalConditions: List<String> = listOf()
        var loopCounter = 0
        while (!isValid) {
            // Generate a new set of conditions
            loopCounter++ // Increment the counter at the start of each loop
            Log.d("generateValidConditions", "Loop count: $loopCounter")
            val newConditions = generateConditions()
            Log.d("generateValidConditions", "Generated conditions: $newConditions")
            // Construct the list of queries
            newQueries = listOf(
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[0]} AND ${newConditions[3]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[1]} AND ${newConditions[3]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[2]} AND ${newConditions[3]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[0]} AND ${newConditions[4]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[1]} AND ${newConditions[4]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[2]} AND ${newConditions[4]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[0]} AND ${newConditions[5]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[1]} AND ${newConditions[5]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  ${newConditions[2]} AND ${newConditions[5]}"
            )
            Log.d("generateValidConditions", "Generated queries: $newQueries")
            // Check if the generated conditions are valid
            isValid = checkRecords(newQueries, dbHelper)
            Log.d("generateValidConditions", "Are conditions valid? $isValid")
            if (isValid) {
                finalConditions = newConditions
            }
        }

        return finalConditions
    }


    private val checkingRecords: Boolean by lazy {
        checkRecords(queries, MyDatabaseHelper(this))
    }

    private val finalConditions: List<String> by lazy {
        generateValidConditions(MyDatabaseHelper(this))
    }


    fun description(stat: String, symbol: String, value: Int): String = when (symbol) {
        ">" -> "Over $value $stat"
        "<" -> "Under $value $stat"
        ">=" -> "$value+ $stat"
        else -> ""
    }

//        val conditionMap: Map<String, String> = (1..15).flatMap { pts ->
//            (1..6).flatMap { reb ->
//                (1..4).flatMap { ast ->
//                    (1..2).flatMap { blk ->
//                        (1..2).flatMap { stl ->
//                            symbols.flatMap { symbol ->
//                                listOf(
//                                    "PTS $symbol $pts" to description("Points", symbol, pts),
//                                    "REB $symbol $reb" to description("Rebounds", symbol, reb),
//                                    "AST $symbol $ast" to description("Assists", symbol, ast),
//                                    "BLK $symbol $blk" to description("Blocks", symbol, blk),
//                                    "STL $symbol $stl" to description("Steal", symbol, stl)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }.toMap()

    private var currentPage = 0
    private val pageSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        dbHelper = MyDatabaseHelper(this)

        println(checkingRecords)
        println(finalConditions)
        searchView = findViewById(R.id.searchView)
        suggestionsListView = findViewById(R.id.suggestionsListView)
        searchView.setBackgroundColor(Color.parseColor("#021526"))

        val searchTextView =
            searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchTextView.setTextColor(Color.WHITE) // Set your desired text color

        imageButton1 = findViewById(R.id.imageButton1)
        imageButton2 = findViewById(R.id.imageButton2)
        imageButton3 = findViewById(R.id.imageButton3)
        imageButton4 = findViewById(R.id.imageButton4)
        imageButton5 = findViewById(R.id.imageButton5)
        imageButton6 = findViewById(R.id.imageButton6)
        imageButton7 = findViewById(R.id.imageButton7)
        imageButton8 = findViewById(R.id.imageButton8)
        imageButton9 = findViewById(R.id.imageButton9)

        label1 = findViewById(R.id.label1)
        label2 = findViewById(R.id.label2)
        label3 = findViewById(R.id.label3)
        label4 = findViewById(R.id.label4)
        label5 = findViewById(R.id.label5)
        label6 = findViewById(R.id.label6)
        label7 = findViewById(R.id.label7)
        label8 = findViewById(R.id.label8)
        label9 = findViewById(R.id.label9)

        replayButton = findViewById(R.id.replayButton)

        // Find TextViews by their ID
        conditionTextView1 = findViewById(R.id.conditionTextView1)
        conditionTextView2 = findViewById(R.id.conditionTextView2)
        conditionTextView3 = findViewById(R.id.conditionTextView3)
        conditionTextViewA = findViewById(R.id.conditionTextViewA)
        conditionTextViewB = findViewById(R.id.conditionTextViewB)
        conditionTextViewC = findViewById(R.id.conditionTextViewC)


        conditionImageBUttonA = findViewById(R.id.conditionImageButtonA)


        // Update TextViews with current conditions
        updateConditionTextViews()
    }

    private fun updateConditionTextViews() {
        Log.d("updateConditionTextViews", "Final conditions: $finalConditions")

        // Log the mapping of conditions to text views
        Log.d("updateConditionTextViews", "Condition 1: ${finalConditions[0] ?: "Unknown condition"}")
        Log.d("updateConditionTextViews", "Condition 2: ${finalConditions[1] ?: "Unknown condition"}")
        Log.d("updateConditionTextViews", "Condition 3: ${finalConditions[2] ?: "Unknown condition"}")
        Log.d("updateConditionTextViews", "Condition A: ${finalConditions[3] ?: "Unknown condition"}")
        Log.d("updateConditionTextViews", "Condition B: ${finalConditions[4] ?: "Unknown condition"}")
        Log.d("updateConditionTextViews", "Condition C: ${finalConditions[5] ?: "Unknown condition"}")

        conditionTextView1.text = finalConditions[0] ?: "Unknown condition"
        conditionTextView2.text = finalConditions[1] ?: "Unknown condition"
        conditionTextView3.text = finalConditions[2] ?: "Unknown condition"
        conditionTextViewA.text = finalConditions[3] ?: "Unknown condition"
        conditionTextViewB.text = finalConditions[4] ?: "Unknown condition"
        conditionTextViewC.text = finalConditions[5] ?: "Unknown condition"


//        Glide.with(this).load(imageUrl)
//            // .placeholder(R.drawable.placeholder_image) // Optional: placeholder image
//            // .error(R.drawable.error_image) // Optional: error image
//            .into(conditionImageBUttonA)


        adapter = PlayerCursorAdapter(this, null)
        suggestionsListView.adapter = adapter

        // Set up query text listener for the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("MainActivity", "onQueryTextSubmit: $query")
                query?.let {
                    currentPage = 0 // Reset to the first page
                    updateSuggestions(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("MainActivity", "onQueryTextChange: $newText")
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
            position: Int, searchView: SearchView, imageButton: ImageButton, index: Int
        ) {
            Log.d("MainActivity", "handleItemClick: position=$position, index=$index")
            val cursor = adapter.cursor
            val query = newQueries[index]
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
        val sql = "SELECT * FROM Player WHERE NAME LIKE ? LIMIT ? OFFSET ?"
        Log.d(
            "MainActivity", "Executing query: $sql with parameters: [%$query%, $pageSize, $offset]"
        )

        try {
            val cursor: Cursor? =
                db.rawQuery(sql, arrayOf("%$query%", pageSize.toString(), offset.toString()))
            Log.d("MainActivity", "Cursor count: ${cursor?.count}")
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
        playerNameInput: String, query: String, imageButton: ImageButton
    ) {
        var buttonIndexMap = mapOf(
            imageButton1 to 0,
            imageButton2 to 1,
            imageButton3 to 2,
            imageButton4 to 3,
            imageButton5 to 4,
            imageButton6 to 5,
            imageButton7 to 6,
            imageButton8 to 7,
            imageButton9 to 8
        )

        val buttonToLabel = mapOf(
            imageButton1 to label1,
            imageButton2 to label2,
            imageButton3 to label3,
            imageButton4 to label4,
            imageButton5 to label5,
            imageButton6 to label6,
            imageButton7 to label7,
            imageButton8 to label8,
            imageButton9 to label9
        )

        Log.d("MainActivity", "Searching for player: $playerNameInput")
        val db = dbHelper.readableDatabase
        Log.d("MainActivity", "Executing query: $query with parameter: [$playerNameInput]")

        try {
            val cursor = db.rawQuery(query, arrayOf(playerNameInput))
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow("NAME")
                val imageUrlIndex = cursor.getColumnIndexOrThrow("PHOTO")
                val playerName = cursor.getString(nameIndex)
                val imageUrl = cursor.getString(imageUrlIndex)

                // Check if player is already in the list
                if (!foundPlayers.contains(playerName)) {
                    // Add playerName to the list
                    val index = buttonIndexMap[imageButton] ?: return

                    // Replace old player with new player at the specified index
                    foundPlayers[index] = playerName

                    // Load image into ImageButton using Glide
                    Glide.with(this).load(imageUrl)
                        // .placeholder(R.drawable.placeholder_image) // Optional: placeholder image
                        // .error(R.drawable.error_image) // Optional: error image
                        .into(imageButton)

                    val label = buttonToLabel[imageButton]
                    label?.text = playerName
                    label?.visibility = View.VISIBLE

                    searchView.setQuery("", false)
                    searchView.clearFocus()

                    if (foundPlayers.all { it != null }) {
                        Toast.makeText(this, "YOU WON", Toast.LENGTH_SHORT).show()
                        imageButton1.isEnabled = false
                        imageButton2.isEnabled = false
                        imageButton3.isEnabled = false
                        imageButton4.isEnabled = false
                        imageButton5.isEnabled = false
                        imageButton6.isEnabled = false
                        imageButton7.isEnabled = false
                        imageButton8.isEnabled = false
                        imageButton9.isEnabled = false
                        replayButton.visibility = ImageButton.VISIBLE
                        replayButton.isEnabled = true

                        replayButton.setOnClickListener {
                            foundPlayers.fill(null)
                            imageButton1.isEnabled = true
                            imageButton2.isEnabled = true
                            imageButton3.isEnabled = true
                            imageButton4.isEnabled = true
                            imageButton5.isEnabled = true
                            imageButton6.isEnabled = true
                            imageButton7.isEnabled = true
                            imageButton8.isEnabled = true
                            imageButton9.isEnabled = true
                            Glide.with(this).clear(imageButton1)
                            Glide.with(this).clear(imageButton2)
                            Glide.with(this).clear(imageButton3)
                            Glide.with(this).clear(imageButton4)
                            Glide.with(this).clear(imageButton5)
                            Glide.with(this).clear(imageButton6)
                            Glide.with(this).clear(imageButton7)
                            Glide.with(this).clear(imageButton8)
                            Glide.with(this).clear(imageButton9)

                            label1.text = "Player Name"
                            label2.text = "Player Name"
                            label3.text = "Player Name"
                            label4.text = "Player Name"
                            label5.text = "Player Name"
                            label6.text = "Player Name"
                            label7.text = "Player Name"
                            label8.text = "Player Name"
                            label9.text = "Player Name"


                            label1.visibility = View.INVISIBLE
                            label2.visibility = View.INVISIBLE
                            label3.visibility = View.INVISIBLE
                            label4.visibility = View.INVISIBLE
                            label5.visibility = View.INVISIBLE
                            label6.visibility = View.INVISIBLE
                            label7.visibility = View.INVISIBLE
                            label8.visibility = View.INVISIBLE
                            label9.visibility = View.INVISIBLE



                            replayButton.visibility = ImageButton.INVISIBLE
                            replayButton.isEnabled = false
                        }
                    }
                } else {
                    searchView.setQuery("", false)
                    searchView.clearFocus()
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
                this, "An error occurred while searching for the player", Toast.LENGTH_SHORT
            ).show()
        } finally {
            db.close() // Ensure database is closed after use
            Log.d("MainActivity", "Database closed")
        }
    }
}

//TODO rng conditions until found fix at least 9
//TODO UI -> search and X icon white

//TODO images or text <-> textView
//TODO fix mapping
//TODO percentages