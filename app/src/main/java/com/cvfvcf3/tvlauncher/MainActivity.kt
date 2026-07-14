package com.cvfvcf3.tvlauncher

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var clockText: TextView
    private lateinit var dateText: TextView
    private val handler = Handler(Looper.getMainLooper())

    data class AppTile(val label: String, val pkg: String, val color: String)

    private val apps = listOf(
        AppTile("JustPlayer", "com.cvfvcf3.justplayer", "#6A1B9A"),
        AppTile("Live TV", "com.cvfvcf3.livetv", "#C0392B"),
        AppTile("YouTube", "com.google.android.youtube.tv", "#D32F2F"),
        AppTile("Netflix", "com.netflix.ninja", "#8E24AA"),
        AppTile("Browser", "com.android.browser", "#37474F")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockText = findViewById(R.id.clockText)
        dateText = findViewById(R.id.dateText)

        setupSidebar()
        setupAppDock()
        startClock()
    }

    private fun setupSidebar() {
        findViewById<TextView>(R.id.menuHome).setOnClickListener { }
        findViewById<TextView>(R.id.menuApps).setOnClickListener {
            Toast.makeText(this, "Apps grid — coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.menuSearch).setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_WEB_SEARCH))
            } catch (e: Exception) {
                Toast.makeText(this, "No search app found", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<TextView>(R.id.menuInput).setOnClickListener {
            Toast.makeText(this, "Use your remote's Source/Input button", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.menuSettings).setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Could not open settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAppDock() {
        val dock = findViewById<LinearLayout>(R.id.appDock)
        val dp = resources.displayMetrics.density

        for (app in apps) {
            val tile = TextView(this).apply {
                text = app.label
                setTextColor(Color.WHITE)
                textSize = 13f
                gravity = Gravity.CENTER
                setPadding(8, 8, 8, 8)
                isFocusable = true
                isClickable = true

                val bg = GradientDrawable()
                bg.cornerRadius = 14 * dp
                bg.setColor(Color.parseColor(app.color))
                background = bg

                layoutParams = LinearLayout.LayoutParams((78 * dp).toInt(), (78 * dp).toInt()).apply {
                    marginStart = (10 * dp).toInt()
                    marginEnd = (10 * dp).toInt()
                }

                setOnClickListener {
                    val launchIntent = packageManager.getLaunchIntentForPackage(app.pkg)
                    if (launchIntent != null) {
                        startActivity(launchIntent)
                    } else {
                        Toast.makeText(this@MainActivity, "${app.label} is not installed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dock.addView(tile)
        }
    }

    private fun startClock() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())

        val runnable = object : Runnable {
            override fun run() {
                val now = Date()
                clockText.text = timeFormat.format(now)
                dateText.text = dateFormat.format(now)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
