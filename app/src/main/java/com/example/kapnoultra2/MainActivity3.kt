package com.example.kapnoultra2
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class MainActivity3 : AppCompatActivity() {

    private lateinit var chart1: LineChart
    private lateinit var chart2: LineChart
    private lateinit var btnToggle: Button

    private var isReceiving = false
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        chart1 = findViewById(R.id.chart1)
        chart2 = findViewById(R.id.chart2)
        btnToggle = findViewById(R.id.btnToggle)

        btnToggle.setOnClickListener {
            if (isReceiving) {
                stopReceiving()
                btnToggle.text = "Verileri Al"
            } else {
                startReceiving()
                btnToggle.text = "Veri Akışını Durdur"
            }
            isReceiving = !isReceiving
        }
    }

    private fun startReceiving() {
        scope.launch {
            try {
                socket = Socket("192.168.0.1", 5000)
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                while (isReceiving) {
                    val line = reader?.readLine() ?: break
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        val val1 = parts[0].toFloatOrNull() ?: 0f
                        val val2 = parts[1].toFloatOrNull() ?: 0f

                        withContext(Dispatchers.Main) {
                            updateChart(chart1, val1)
                            updateChart(chart2, val2)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stopReceiving()
            }
        }
    }

    private fun stopReceiving() {
        try {
            reader?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateChart(chart: LineChart, value: Float) {
        val data = chart.data ?: LineData().also { chart.data = it }
        var set = data.getDataSetByIndex(0) as? LineDataSet

        if (set == null) {
            set = LineDataSet(mutableListOf(), "Veri")
            set.color = Color.parseColor("#FF9800")
            set.lineWidth = 2f
            set.setDrawCircles(false)
            data.addDataSet(set)
        }

        data.addEntry(Entry(set.entryCount.toFloat(), value), 0)
        data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(50f)
        chart.moveViewToX(data.entryCount.toFloat())
    }

    override fun onDestroy() {
        super.onDestroy()
        stopReceiving()
    }
}
