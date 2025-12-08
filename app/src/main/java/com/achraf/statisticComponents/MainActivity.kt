package com.achraf.statisticComponents

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.achraf.barchart.BarChart
import com.achraf.barchart.BarChartInput
import com.achraf.barchart.BarChartStyle
import com.achraf.linechart.DataPoint
import com.achraf.linechart.LineChartData
import com.achraf.linechart.MultiLineChart
import com.achraf.piechat.PieChart
import com.achraf.piechat.PieChartInput

import com.achraf.statisticComponents.ui.theme.StatisticsComponentsTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StatisticsComponentsTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 40.dp), // Added padding to top and bottom
                    verticalArrangement = Arrangement.spacedBy(50.dp), // Increased spacing between components
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PieChartExample()

                    CryptoPortfolioChart()

                    BarChartExample()
                }
            }
        }
    }
}

@Composable
fun BarChartExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        // Example 1: 2D Bar Chart
        BarChart(
            inputList = listOf(
                BarChartInput(
                    value = 60000,
                    description = "January",
                    color = Color(0xFFFF6B6B),
                    textColor = Color(0xFFFF6B6B)
                ),
                BarChartInput(
                    value = 4000,
                    description = "February",
                    color = Color(0xFFFF6B6B),
                    textColor = Color(0xFFFF6B6B),
                ),
                BarChartInput(
                    value = 0,
                    description = "March",
                    color =Color(0xFFFF6B6B),
                    textColor =Color(0xFFFF6B6B),
                ),BarChartInput(
                    value = 0,
                    description = "March",
                    color =Color(0xFFFF6B6B),
                    textColor =Color(0xFFFF6B6B),
                ),BarChartInput(
                    value = 0,
                    description = "March",
                    color =Color(0xFFFF6B6B),
                    textColor =Color(0xFFFF6B6B),
                ),
                BarChartInput(
                    value = 0,
                    description = "April",
                    color = Color(0xFFFF6B6B),
                    textColor = Color(0xFFFF6B6B),
                )
            ),
            style = BarChartStyle.FLAT_2D,
            barWidth = 50.dp,
            maxBarHeight = 300.dp,
            textSize = 14.dp,
            showDescription = true,
            showYAxisValues = true,
            yAxisSteps = 5,
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Example 2: 3D Bar Chart
        BarChart(
            inputList = listOf(
                BarChartInput(
                    value = 25,
                    description = "Q1",
                    color = Color(0xFFFF6B6B),
                    textColor = Color.Black
                ),
                BarChartInput(
                    value = 40,
                    description = "Q2",
                    color = Color(0xFF4ECDC4),
                    textColor = Color.Black
                ),
                BarChartInput(
                    value = 35,
                    description = "Q3",
                    color = Color(0xFFFFE66D),
                    textColor = Color.Black
                ),
                BarChartInput(
                    value = 50,
                    description = "Q4",
                    color = Color(0xFF95E1D3),
                    textColor = Color.Black
                )
            ),
            style = BarChartStyle.PERSPECTIVE_3D,
            barWidth = 40.dp,
            maxBarHeight = 150.dp,
            textSize = 12.dp,
            showDescription = true,
            showYAxisValues = true,
            yAxisSteps = 5,
            spacing = 12.dp
        )
    }
}

@Composable
fun PieChartExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(20.dp), // Added more padding
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Preferred Programming Languages",
            fontSize = 28.sp, // Slightly smaller for better proportion
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        // Center the PieChart with Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp), // Fixed height for centering
            contentAlignment = Alignment.Center // This centers the PieChart
        ) {
            PieChart(
                input = listOf(
                    PieChartInput(
                        color = Color.Black,
                        value = 29,
                        description = "Python"
                    ),
                    PieChartInput(
                        color = Color.Red,
                        value = 21,
                        description = "Swift"
                    ),
                    PieChartInput(
                        color = Color.Yellow,
                        value = 32,
                        description = "JavaScript"
                    ),
                    PieChartInput(
                        color = Color.Gray,
                        value = 18,
                        description = "Java"
                    ),
                    PieChartInput(
                        color = Color.Green,
                        value = 12,
                        description = "Ruby"
                    ),
                    PieChartInput(
                        color = Color.Blue,
                        value = 38,
                        description = "Kotlin"
                    ),
                ),
                centerText = "150 persons were asked",
                textColor = Color.White,
                centerTextColor = Color.White,
                innerRadius = 300f,
                modifier = Modifier.size(350.dp) // Adjust size as needed
            )
        }

        Spacer(modifier = Modifier.height(20.dp)) // Bottom spacing
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CryptoPortfolioChart() {
    var selectedDataPoint by remember { mutableStateOf<DataPoint?>(null) }
    var selectedLineIndex by remember { mutableIntStateOf(0) }

    val cryptoData = remember {
        listOf(
            LineChartData(
                dataPoints = (1..50).map { hours ->
                    val dateTime = ZonedDateTime.now().plusHours(hours.toLong())
                    DataPoint(
                        x = hours.toFloat(),
                        y = 45000f + Random.nextFloat() * 5000f,
                        xLabel = DateTimeFormatter.ofPattern("ha").format(dateTime)
                    )
                },
                color = Color(0xFFF7931A), // Bitcoin orange
                label = "BTC"
            ),
            LineChartData(
                dataPoints = (1..50).map { hours ->
                    val dateTime = ZonedDateTime.now().plusHours(hours.toLong())
                    DataPoint(
                        x = hours.toFloat(),
                        y = 2500f + Random.nextFloat() * 500f,
                        xLabel = DateTimeFormatter.ofPattern("ha").format(dateTime)
                    )
                },
                color = Color(0xFF627EEA), // Ethereum blue
                label = "ETH"
            ),
            LineChartData(
                dataPoints = (1..50).map { hours ->
                    val dateTime = ZonedDateTime.now().plusHours(hours.toLong())
                    DataPoint(
                        x = hours.toFloat(),
                        y = 50f + Random.nextFloat() * 30f,
                        xLabel = DateTimeFormatter.ofPattern("ha").format(dateTime)
                    )
                },
                color = Color(0xFF26A17B), // Tether green
                label = "ADA"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(20.dp) // Increased padding
    ) {
        Text(
            text = "Crypto Portfolio",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Legend
        Row(
            modifier = Modifier.padding(vertical = 12.dp), // Increased vertical padding
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            cryptoData.forEach { crypto ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(crypto.color)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = crypto.label,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        selectedDataPoint?.let {
            Text(
                text = "${cryptoData[selectedLineIndex].label}: $${it.y.toInt()}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = cryptoData[selectedLineIndex].color,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp)) // Increased spacing

        MultiLineChart(
            lineData = cryptoData,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFF2A2A2A)),
            unit = "$",
            selectedDataPoint = selectedDataPoint,
            selectedLineIndex = selectedLineIndex,
            onSelectedDataPoint = { point, lineIndex ->
                selectedDataPoint = point
                selectedLineIndex = lineIndex
            },
            visibleDataPointsCount = 15,
            enableHorizontalScroll = true
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}