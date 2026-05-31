package com.example.expensetracker.feature.stats

import android.app.Activity
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.feature.home.TransactionList
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.widget.ExpenseTextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun StatsScreen(navController: NavController, viewModel: StatsViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val activity = context as? Activity
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }

    val isTabletOrLandscape = windowSizeClass?.widthSizeClass != WindowWidthSizeClass.Compact

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(id = R.string.back_accessibility),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.navigateUp() },
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                )
                ExpenseTextView(
                    text = stringResource(id = R.string.statistics),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
                Image(
                    painter = painterResource(id = R.drawable.dots_menu),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        }
    ) { paddingValues ->
        val dataState = viewModel.entries.collectAsState(initial = emptyList())
        val topExpenseState = viewModel.topEntries.collectAsState(initial = emptyList())

        val entries = viewModel.getEntriesForChart(dataState.value)

        val displayableTopExpenses = remember(topExpenseState.value) {
            topExpenseState.value.map { summary ->
                ExpenseEntity(
                    id = null,
                    userId = "",
                    title = summary.type,
                    amount = summary.total_amount,
                    date = summary.date,
                    type = summary.type
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isTabletOrLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LineChart(entries = entries, modifier = Modifier.fillMaxWidth().height(320.dp))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        TransactionList(
                            modifier = Modifier.fillMaxSize(),
                            list = displayableTopExpenses,
                            title = stringResource(id = R.string.top_spending),
                            onSeeAllClicked = {}
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LineChart(entries = entries, modifier = Modifier.fillMaxWidth().height(250.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    TransactionList(
                        modifier = Modifier.weight(1f),
                        list = displayableTopExpenses,
                        title = stringResource(id = R.string.top_spending),
                        onSeeAllClicked = {}
                    )
                }
            }
        }
    }
}

@Composable
fun LineChart(entries: List<Entry>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val chartLabel = stringResource(id = R.string.title_expenses)

    AndroidView(
        factory = {
            val view = LayoutInflater.from(context).inflate(R.layout.stats_line_chart, null)
            view
        },
        modifier = modifier
    ) { view ->
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        val dataSet = LineDataSet(entries, chartLabel).apply {
            color = android.graphics.Color.parseColor("#FF2F7E79")
            lineWidth = 3f
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawFilled(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 12f
            valueTextColor = android.graphics.Color.parseColor("#FF2F7E79")

            val drawable = ContextCompat.getDrawable(context, R.drawable.char_gradient)
            drawable?.let {
                fillDrawable = it
            }
        }

        lineChart.xAxis.valueFormatter =
            object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return Utils.formatDateForChart(value.toLong())
                }
            }
        lineChart.data = com.github.mikephil.charting.data.LineData(dataSet)
        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.setDrawAxisLine(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.invalidate()
    }
}