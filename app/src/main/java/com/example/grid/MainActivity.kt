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
import android.widget.ImageView
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
import android.media.MediaPlayer;
import android.widget.Button
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit
import nl.dionsegijn.konfetti.xml.KonfettiView


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var searchView: SearchView
    private lateinit var suggestionsListView: ListView
    private lateinit var adapter: PlayerCursorAdapter
    private lateinit var newQueries: List<String>
    private var currentPage = 0
    private val pageSize = 10
    var offset = currentPage * pageSize
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var viewKonfetti: KonfettiView

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

    private lateinit var conditionImageButtonA: ImageButton
    private lateinit var conditionImageButtonB: ImageButton
    private lateinit var conditionImageButtonC: ImageButton
    private lateinit var conditionImageButton1: ImageButton
    private lateinit var conditionImageButton2: ImageButton
    private lateinit var conditionImageButton3: ImageButton


    private val foundPlayers = MutableList<String?>(9) { null }

    val symbols = listOf("<", ">=")
    var positions = listOf("%PG%", "%SG%", "%SF%", "%PF%", "%C%").map { "POS LIKE '$it'" }
    var ethnicities = listOf(
        "American",
        "Greek",
        "Croatian",
        //     "Montenegrin",
        //      "Albanian",
        "Lithuanian",
        "Russian",
        //       "Slovenian",
        //       "Georgian",
        //       "Bulgarian",
        "Canadian",
        //       "British",
        "Bosnian",
        "Dutch",
        "Serbia-Montenegro",
        "Australian",
        "French",
        "Senegalese",
        "Cypriot",
        "Swedish",
        "Latvian",
        "Serbian",
        "Ukrainian",
        "Italian",
        "Dominican",
        "Spanish",
        "Finnish",
        "Turkish",
        "Polish",
        "Nigerian",
        "New Zealand",
        "Cameroonian",
        "Ethiopian",
        "Uruguayan",
        "Macedonian",
        "Costa Rican",
        "Guinean",
        "Azerbaijani",
        "Hungarian",
        "Jamaican",
        "Belizean",
        "Guianan",
        "Irish",
        "Puerto Rican",
        "Israeli",
        "Ivoirian",
        "Central African",
        "Belorussian",
        "Malian",
        "Colombian",
        "Panamanian",
        "Argentine",
        "Cuban",
        "Danish",
        "Kazakhstani",
        "Georgian",
        "Romanian",
        "Bahamas",
        "Ghanaian",
        "Angolan",
        "Sudanese",
        "Icelandic"
    ).map { "ETHNICITY LIKE '%$it%'" }

    // List of team names
    val teamList = listOf(
        "AEK Athens",
        "Panathinaikos",
        "Rethymno",
        "Peristeri",
        "Olympiacos",
        "Maroussi",
        "Panionios",
        "Nea Kifisia",
        "Kolossos Rodou",
        "Koroivos",
        "PAOK",
        "Apollon Patras",
        "Lavrio",
        "Aris",
        "Promitheas Patras",
        "Iraklis"
        //"Ifaistos Limnou",
        //"Panelefsiniakos",
        //"Olympia Larissas",
        //"AEL Larissa",
        //"Egaleo",
        //"Makedonikos",
        //"Milonas",
        //"Dafni",
        //"Near-East",
        //"Ment",
        //"Karditsa",
        //"Charilaos TM",
        //"Ionikos Nikaias",
        //"Kymi",
        //"Holargos",
        //"Doxa Lefkadas",
        //"Arkadikos",
        //"Ilisiakos",
        //"Ikaros Esperos",
        //"Trikala",
        //"Ermis Agias",
        //"OFI Iraklio",
        //"Ionikos Lamias",
        //"Panellinios",
        //"Trikala",
        //"Olimpiada Patron",
        //"KAO Dramas",
        //"Kavala",
    )

    var teams = teamList.map { "TEAM_NAME LIKE '$it'" }


    fun generateConditions(): List<String> {
        val symbols = listOf(">=", "<")
        val stats = listOf("PTS", "REB", "AST", "BLK", "STL", "FG", "ThreePT", "FT")

        // Generate random values for statistics
        val randomValues = mapOf(
            "PTS" to Random.nextInt(4, 20),
            "REB" to Random.nextInt(4, 8),
            "AST" to Random.nextInt(3, 5),
            "BLK" to Random.nextInt(1, 2),
            "STL" to Random.nextInt(1, 2),
            "FG" to Random.nextInt(35, 60),
            "ThreePT" to Random.nextInt(25, 55),
            "FT" to Random.nextInt(35, 95)
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
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[0]} AND ${initialConditions[3]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[1]} AND ${initialConditions[3]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[2]} AND ${initialConditions[3]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[0]} AND ${initialConditions[4]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[1]} AND ${initialConditions[4]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[2]} AND ${initialConditions[4]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[0]} AND ${initialConditions[5]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[1]} AND ${initialConditions[5]}",
        "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND   ${initialConditions[2]} AND ${initialConditions[5]}"
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
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[0]} AND ${newConditions[3]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[1]} AND ${newConditions[3]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[2]} AND ${newConditions[3]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[0]} AND ${newConditions[4]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[1]} AND ${newConditions[4]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[2]} AND ${newConditions[4]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[0]} AND ${newConditions[5]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[1]} AND ${newConditions[5]}",
                "SELECT * FROM Player JOIN Stats ON Player._id=Stats.PLAYER_ID JOIN Squads ON Squads.PLAYER_ID=Stats.PLAYER_ID JOIN Team ON Team.TM_ID=Squads.TEAM_ID WHERE NAME = ? AND  Stats.GAMES_PLAYED >=5 AND  ${newConditions[2]} AND ${newConditions[5]}"
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


    fun mapCondition(condition: String): String? {
        val regex = "(\\w+)\\s*(>=|<|=|LIKE)\\s*'?(%?.+?%?)'?".toRegex()
        val matchResult = regex.matchEntire(condition) ?: return null

        val (stat, operator, value) = matchResult.destructured

        val positionMap = mapOf(
            "PG" to "Point Guard",
            "SG" to "Shooting Guard",
            "SF" to "Small Forward",
            "PF" to "Power Forward",
            "C" to "Center"
        )

        return when (stat) {
            "PTS" -> when (operator) {
                ">=" -> "$value+ Points (Season)"
                "<" -> "Under $value Points (Season)"
                else -> null
            }

            "AST" -> when (operator) {
                ">=" -> "$value+ Assists (Season)"
                "<" -> "Under $value Assists (Season)"
                else -> null
            }

            "FG" -> when (operator) {
                ">=" -> "$value%+ FG (Season)"
                "<" -> "Under $value% FG (Season)"
                else -> null
            }

            "REB" -> when (operator) {
                ">=" -> "$value+ Rebounds (Season)"
                "<" -> "Under $value Rebounds (Season)"
                else -> null
            }

            "BLK" -> when (operator) {
                ">=" -> "$value+ Blocks (Season)"
                "<" -> "Under $value Blocks (Season)"
                else -> null
            }

            "STL" -> when (operator) {
                ">=" -> "$value+ Steal (Season)"
                "<" -> "Under $value Steal (Season)"
                else -> null
            }

            "FT" -> when (operator) {
                ">=" -> "$value%+ FT (Season)"
                "<" -> "Under $value% FT (Season)"
                else -> null
            }

            "ThreePT" -> when (operator) {
                ">=" -> "$value%+ 3PT (Season)"
                "<" -> "Under $value% 3PT (Season)"
                else -> null
            }

            "ETHNICITY" -> when (operator) {
                "LIKE" -> value.replace("%", "")
                else -> null
            }

            "TEAM_NAME" -> when (operator) {
                "LIKE" -> "$value"
                else -> null
            }

            "POS" -> when (operator) {
                "LIKE" -> positionMap[value.replace("%", "")] ?: "Unknown Position"
                else -> null
            }

            else -> null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        dbHelper = MyDatabaseHelper(this)
        println(dbHelper)
        println(checkingRecords)
        println(finalConditions)
        searchView = findViewById(R.id.searchView)
        suggestionsListView = findViewById(R.id.suggestionsListView)
        searchView.setBackgroundColor(Color.parseColor("#021526"))
        viewKonfetti = findViewById(R.id.konfettiView)

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


        conditionImageButtonA = findViewById(R.id.conditionImageButtonA)
        conditionImageButtonB = findViewById(R.id.conditionImageButtonB)
        conditionImageButtonC = findViewById(R.id.conditionImageButtonC)
        conditionImageButton1 = findViewById(R.id.conditionImageButton1)
        conditionImageButton2 = findViewById(R.id.conditionImageButton2)
        conditionImageButton3 = findViewById(R.id.conditionImageButton3)

        // Update TextViews with current conditions
        updateConditionTextViews()
    }


    private fun updateConditionTextViews() {
        Log.d("updateConditionTextViews", "Final conditions: $finalConditions")

        // Log the mapping of conditions to text views
        Log.d(
            "updateConditionTextViews",
            "Condition 1: ${mapCondition(finalConditions[0]) ?: "Unknown condition"}"
        )
        Log.d(
            "updateConditionTextViews",
            "Condition 2: ${mapCondition(finalConditions[1]) ?: "Unknown condition"}"
        )
        Log.d(
            "updateConditionTextViews",
            "Condition 3: ${mapCondition(finalConditions[2]) ?: "Unknown condition"}"
        )
        Log.d(
            "updateConditionTextViews",
            "Condition A: ${mapCondition(finalConditions[3]) ?: "Unknown condition"}"
        )
        Log.d(
            "updateConditionTextViews",
            "Condition B: ${mapCondition(finalConditions[4]) ?: "Unknown condition"}"
        )
        Log.d(
            "updateConditionTextViews",
            "Condition C: ${mapCondition(finalConditions[5]) ?: "Unknown condition"}"
        )

        conditionTextView1.text = mapCondition(finalConditions[0]) ?: "Unknown condition"
        conditionTextView2.text = mapCondition(finalConditions[1]) ?: "Unknown condition"
        conditionTextView3.text = mapCondition(finalConditions[2]) ?: "Unknown condition"
        conditionTextViewA.text = mapCondition(finalConditions[3]) ?: "Unknown condition"
        conditionTextViewB.text = mapCondition(finalConditions[4]) ?: "Unknown condition"
        conditionTextViewC.text = mapCondition(finalConditions[5]) ?: "Unknown condition"

        val imageButtonMap = mapOf(
            finalConditions[0] to conditionImageButton1,
            finalConditions[1] to conditionImageButton2,
            finalConditions[2] to conditionImageButton3,
            finalConditions[3] to conditionImageButtonA,
            finalConditions[4] to conditionImageButtonB,
            finalConditions[5] to conditionImageButtonC
        )

        val imageTextMap = mapOf(
            finalConditions[0] to conditionTextView1,
            finalConditions[1] to conditionTextView2,
            finalConditions[2] to conditionTextView3,
            finalConditions[3] to conditionTextViewA,
            finalConditions[4] to conditionTextViewB,
            finalConditions[5] to conditionTextViewC
        )

        var db = dbHelper.readableDatabase
        var teamLogo: String? = null

        for (i in 0..5) {
            val condition = mapCondition(finalConditions[i])?.trim()
            Log.d("Info", "Mapped condition for finalConditions[$i]: $condition")

            if (condition in teamList) {
                Log.d("Info", "Condition met: ${finalConditions[i]}")


                val sql = "SELECT * FROM Team WHERE TEAM_NAME = ? LIMIT ? OFFSET ?"
                val cursor: Cursor? = db.rawQuery(
                    sql, arrayOf(condition, pageSize.toString(), offset.toString())
                )

                cursor?.use {
                    if (it.moveToNext()) {
                        teamLogo = it.getString(it.getColumnIndexOrThrow("LOGO"))
                        // You can process other data from the cursor if needed
                    }
                }

                // Load image using Glide
                teamLogo?.let { logoUrl ->
                    Glide.with(this)
                        .load(teamLogo)
                        // Add placeholder and error handling if needed
                        // .placeholder(R.drawable.placeholder_image)
                        // .error(R.drawable.error_image)
                        .into(imageButtonMap[finalConditions[i]] as ImageView)
                    Log.d("Info", "Loading image into ImageView")
                }

                imageTextMap[finalConditions[i]]?.visibility = View.INVISIBLE
                // Exit the loop once the condition is met
            } else {
                Log.d("Info", "IMAGE NOT LOADED - Condition: $condition")
            }
        }


// Log the list to ensure it contains the expected value
        Log.d("Info", "Teams list: $teamList")

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
        var db = dbHelper.readableDatabase
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

    private fun playSoundBasedOnPlayerName(playerName: String) {
        // Release any existing MediaPlayer
        mediaPlayer?.release()

        // Initialize MediaPlayer with the correct sound file based on playerName
        mediaPlayer = when (playerName) {
            "Georgios Printezis" -> MediaPlayer.create(this, R.raw.georgiosprintezis)
            "Kostas Sloukas" -> MediaPlayer.create(this, R.raw.kostassloukas)
            "Thanasis Antetokounmpo" -> MediaPlayer.create(this, R.raw.thanasisantetokounmpo)
            "Dimitrios Agravanis" -> MediaPlayer.create(this, R.raw.dimitriosagravanis)
            "Vangelis Mantzaris" -> MediaPlayer.create(this, R.raw.vangelismantzaris)
            "Vassilis Spanoulis" -> MediaPlayer.create(this, R.raw.vassilisspanoulis)
            "Tyler Dorsey" -> MediaPlayer.create(this, R.raw.tylerdorsey)
            "Kevin Punter" -> MediaPlayer.create(this, R.raw.kevinpunter)
            "Nick Calathes" -> MediaPlayer.create(this, R.raw.nickcalathes)
            "Georgios Papagiannis" -> MediaPlayer.create(this, R.raw.georgiospapagiannis)

            else -> null
        }

        // Play the sound if MediaPlayer is initialized
        mediaPlayer?.start()
    }

    private fun triggerConfetti() {
        viewKonfetti.start(
            Party(
                speed = 5f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0x03ff6c),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                position = Position.Relative(0.623, 0.48)
            )
        )
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


                    playSoundBasedOnPlayerName(playerName)

                    val label = buttonToLabel[imageButton]
                    label?.text = playerName
                    label?.visibility = View.VISIBLE

                    searchView.setQuery("", false)
                    searchView.clearFocus()

                    if (foundPlayers.all { it != null }) {
                        Toast.makeText(this, "YOU WON", Toast.LENGTH_SHORT).show()
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(this, R.raw.confetti)
                        mediaPlayer?.start()
                        triggerConfetti()
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
//TODO search and X icon white
//TODO percentages
//TODO logos 404 fix db
