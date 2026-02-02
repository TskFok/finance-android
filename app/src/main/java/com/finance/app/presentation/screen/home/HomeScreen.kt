package com.finance.app.presentation.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.HorizontalDivider
import android.graphics.Paint
import android.graphics.Typeface
import com.finance.app.data.model.AIModel
import com.finance.app.data.model.AIChatHistory
import com.finance.app.data.model.AIAnalysisHistory
import com.finance.app.data.model.CategoryStat
import com.finance.app.data.model.PageResponse
import com.finance.app.data.model.User
import com.finance.app.presentation.viewmodel.AIViewModel
import com.finance.app.presentation.viewmodel.AuthViewModel
import com.finance.app.presentation.viewmodel.ExpenseViewModel
import com.finance.app.presentation.viewmodel.IncomeViewModel
import com.finance.app.util.DateUtils
import com.finance.app.util.Resource
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.ui.viewinterop.AndroidView
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.data.BarData
import com.himanshoe.charty.bar.config.BarChartConfig
import com.himanshoe.charty.pie.PieChart
import com.himanshoe.charty.pie.data.PieData
import com.himanshoe.charty.pie.config.PieChartConfig
import com.himanshoe.charty.color.ChartyColor
import com.himanshoe.charty.color.ChartyColors
import android.graphics.Color as AndroidColor

// Color 扩展函数：将 Compose Color 转换为 Android Color Int
fun Color.toArgb(): Int {
    return AndroidColor.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = remember { AuthViewModel() },
    expenseViewModel: ExpenseViewModel = remember { ExpenseViewModel() },
    incomeViewModel: IncomeViewModel = remember { IncomeViewModel() }
) {
    // 0: 收支(内部Tab: 支出/收入), 1: 统计, 2: AI
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("收支", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ShowChart,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("统计", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.tertiary,
                        selectedTextColor = MaterialTheme.colorScheme.tertiary,
                        indicatorColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Psychology,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("AI", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("我", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> IncomeExpenseScreen(
                    expenseViewModel = expenseViewModel,
                    incomeViewModel = incomeViewModel
                )
                1 -> StatisticsScreen(expenseViewModel)
                2 -> AIScreen()
                3 -> ProfileScreen(authViewModel = authViewModel, onLogout = onLogout)
            }
        }
    }
}

@Composable
fun IncomeExpenseScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = 0)
    val scope = rememberCoroutineScope()

    val defaultStartTime = remember { DateUtils.getCurrentMonthStart() }
    val defaultEndTime = remember { DateUtils.getCurrentMonthEnd() }
    var startTime by remember { mutableStateOf(defaultStartTime) }
    var endTime by remember { mutableStateOf(defaultEndTime) }
    var showDatePicker by remember { mutableStateOf(false) }

    val summary by expenseViewModel.summary.collectAsState()
    val totalExpense = (summary as? Resource.Success)?.data?.totalExpense
    val totalIncome = (summary as? Resource.Success)?.data?.totalIncome

    LaunchedEffect(startTime, endTime) {
        expenseViewModel.getIncomeExpenseSummary(startTime, endTime)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("支出") }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("收入") }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    ExpenseListScreen(
                        viewModel = expenseViewModel,
                        startTime = startTime,
                        endTime = endTime,
                        totalExpense = totalExpense,
                        onOpenDatePicker = { showDatePicker = true }
                    )
                }
                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    IncomeListScreen(
                        viewModel = incomeViewModel,
                        startTime = startTime,
                        endTime = endTime,
                        totalIncome = totalIncome,
                        onOpenDatePicker = { showDatePicker = true }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        var tempStartTime by remember(startTime) { mutableStateOf(startTime) }
        var tempEndTime by remember(endTime) { mutableStateOf(endTime) }
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("选择时间范围") },
            text = {
                Column {
                    Text("开始时间", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = tempStartTime,
                        onValueChange = { tempStartTime = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        placeholder = { Text("yyyy-MM-dd") }
                    )
                    Text("结束时间", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = tempEndTime,
                        onValueChange = { tempEndTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("yyyy-MM-dd") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        TextButton(onClick = {
                            tempStartTime = DateUtils.getCurrentMonthStart()
                            tempEndTime = DateUtils.getCurrentMonthEnd()
                        }) {
                            Text("本月")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            val calendar = java.util.Calendar.getInstance()
                            calendar.add(java.util.Calendar.MONTH, -1)
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                            tempStartTime = DateUtils.formatDate(calendar.time)
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
                            tempEndTime = DateUtils.formatDate(calendar.time)
                        }) {
                            Text("上月")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        startTime = tempStartTime
                        endTime = tempEndTime
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val profileState by authViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.getProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
        when (val state = profileState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val user = state.data
                if (user != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = null
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(28.dp)
                                        ),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    user.username,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    user.email ?: "未设置邮箱",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "注册时间：${DateUtils.formatDateTime(user.createdAt)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            "暂无用户信息",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            state.message ?: "加载失败",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        }

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("退出登录")
        }
    }
}

@Composable
fun ExpenseListScreen(
    viewModel: ExpenseViewModel,
    startTime: String,
    endTime: String,
    totalExpense: Double?,
    onOpenDatePicker: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    var showAddExpense by remember { mutableStateOf(false) }
    
    LaunchedEffect(startTime, endTime) {
        viewModel.getExpenses(startTime = startTime, endTime = endTime)
    }
    
    val expenses by viewModel.expenses.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val loadMoreError by viewModel.loadMoreError.collectAsState()
    val listState = rememberLazyListState()
    
    // 如果显示添加界面，直接显示添加界面
    if (showAddExpense) {
        AddExpenseScreen(viewModel = viewModel, onBack = { showAddExpense = false })
        return
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 顶部标题栏：支出记录 + 总支出金额
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "支出记录",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    if (totalExpense != null) "¥${"%.2f".format(totalExpense)}" else "—",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onOpenDatePicker,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    "筛选时间",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // 时间范围显示
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "$startTime 至 $endTime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        when (val expensesState = expenses) {
            is com.finance.app.util.Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is com.finance.app.util.Resource.Success -> {
                val expenseList = expensesState.data?.list ?: emptyList()

                // 从添加界面返回时滚动到顶部，便于看到新增数据
                LaunchedEffect(showAddExpense) {
                    if (!showAddExpense && expenseList.isNotEmpty()) {
                        kotlinx.coroutines.delay(100)
                        listState.animateScrollToItem(0)
                    }
                }

                LaunchedEffect(listState, hasMore, isLoadingMore) {
                    snapshotFlow {
                        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                        val total = listState.layoutInfo.totalItemsCount
                        lastVisible to total
                    }
                        .distinctUntilChanged()
                        .map { (lastVisible, total) -> total > 0 && lastVisible >= total - 1 }
                        .distinctUntilChanged()
                        .collect { reachedEnd ->
                            if (reachedEnd && hasMore && !isLoadingMore) {
                                viewModel.loadNextPage()
                            }
                        }
                }

                if (expenseList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                "暂无支出记录",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "点击右下角按钮添加第一条支出记录",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = expenseList,
                            key = { it.id }
                        ) { expense ->
                            val scale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .scale(scale)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        // 类别图标
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            contentAlignment = androidx.compose.ui.Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Category,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                expense.category,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                "¥${String.format("%.2f", expense.amount)}",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.AccessTime,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    DateUtils.formatDateTime(expense.expenseTime),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (!expense.description.isNullOrBlank()) {
                                                Text(
                                                    expense.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                    
                                    IconButton(
                                        onClick = { showDeleteDialog = expense.id },
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            "删除",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 底部加载更多 footer
                        item(key = "expense_footer") {
                            when {
                                isLoadingMore -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                                loadMoreError != null -> {
                                    TextButton(
                                        onClick = { viewModel.loadNextPage() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(loadMoreError ?: "加载失败，点此重试")
                                    }
                                }
                                !hasMore -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Text(
                                            "没有更多了",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is com.finance.app.util.Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(expensesState.message ?: "加载失败", color = MaterialTheme.colorScheme.error)
                }
            }
            null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        }
        
    // 悬浮添加按钮 - 现代化设计
    FloatingActionButton(
        onClick = { showAddExpense = true },
        modifier = Modifier
            .align(androidx.compose.ui.Alignment.BottomEnd)
            .padding(20.dp)
            .size(64.dp),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "添加支出",
            modifier = Modifier.size(28.dp)
        )
    }
    }
    
    // 删除确认对话框
    showDeleteDialog?.let { expenseId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条支出记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExpense(expenseId)
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    val categoryRepository = com.finance.app.di.AppContainer.getCategoryRepository()
    val categoriesState = remember { mutableStateOf<List<com.finance.app.data.model.ExpenseCategory>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        categoryRepository.getCategories().collect { resource ->
            when (resource) {
                is com.finance.app.util.Resource.Success -> {
                    resource.data?.let { categories ->
                        categoriesState.value = categories
                    }
                }
                else -> {}
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // 顶部标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                @Suppress("DEPRECATION")
                Icon(Icons.Default.ArrowBack, "返回", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "添加支出",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        
        // 金额输入框
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("金额", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("0.00") },
            leadingIcon = {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )
        
        // 类别下拉菜单
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("类别", style = MaterialTheme.typography.bodyMedium) },
                placeholder = { Text("请选择类别") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoriesState.value.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category.name
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        
        // 备注输入框
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("备注（可选）", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("添加备注信息...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            maxLines = 3,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )
        
        // 保存按钮
        Button(
            onClick = {
                if (amount.isNotBlank() && selectedCategory != null) {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    val currentTime = DateUtils.getCurrentDateTime()
                    viewModel.createExpense(amountValue, selectedCategory!!, description, currentTime)
                    amount = ""
                    selectedCategory = null
                    description = ""
                    showSuccessMessage = true
                    // 保存成功后延迟返回
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                        kotlinx.coroutines.delay(1500)
                        onBack()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = amount.isNotBlank() && selectedCategory != null,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "保存",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        
        if (showSuccessMessage) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "保存成功！",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StatisticsScreen(viewModel: ExpenseViewModel) {
    // 默认显示当月数据
    val defaultYearMonth = remember { DateUtils.getCurrentYearMonth() }
    val defaultStartTime = remember { DateUtils.getCurrentMonthStart() }
    val defaultEndTime = remember { DateUtils.getCurrentMonthEnd() }
    
    var rangeType by remember { mutableStateOf("month") }
    var yearMonth by remember { mutableStateOf(defaultYearMonth) }
    var startTime by remember { mutableStateOf(defaultStartTime) }
    var endTime by remember { mutableStateOf(defaultEndTime) }
    var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    val categoryRepository = com.finance.app.di.AppContainer.getCategoryRepository()
    val categoriesState = remember { mutableStateOf<List<com.finance.app.data.model.ExpenseCategory>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        categoryRepository.getCategories().collect { resource ->
            when (resource) {
                is com.finance.app.util.Resource.Success -> {
                    resource.data?.let { categories ->
                        categoriesState.value = categories
                    }
                }
                else -> {}
            }
        }
    }
    
    LaunchedEffect(rangeType, yearMonth, startTime, endTime, selectedCategories) {
        when (rangeType) {
            "month" -> {
                viewModel.getDetailedStatistics(
                    rangeType = "month",
                    yearMonth = yearMonth,
                    categories = if (selectedCategories.isEmpty()) null else selectedCategories.joinToString(",")
                )
            }
            "year" -> {
                val year = yearMonth.substring(0, 4)
                viewModel.getDetailedStatistics(
                    rangeType = "year",
                    year = year,
                    categories = if (selectedCategories.isEmpty()) null else selectedCategories.joinToString(",")
                )
            }
            "custom" -> {
                viewModel.getDetailedStatistics(
                    rangeType = "custom",
                    startTime = startTime,
                    endTime = endTime,
                    categories = if (selectedCategories.isEmpty()) null else selectedCategories.joinToString(",")
                )
            }
        }
    }
    
    val statistics by viewModel.statistics.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("统计", style = MaterialTheme.typography.headlineMedium)
            Row {
                IconButton(onClick = { showFilterDialog = true }) {
                    Icon(Icons.Default.DateRange, "筛选时间")
                }
                IconButton(onClick = { showCategoryDialog = true }) {
                    Icon(Icons.Default.FilterList, "筛选类别")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        // 显示当前筛选条件
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "时间范围: ${when (rangeType) {
                        "month" -> yearMonth
                        "year" -> "${yearMonth.substring(0, 4)}年"
                        else -> "$startTime 至 $endTime"
                    }}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (selectedCategories.isNotEmpty()) {
                    Text(
                        "类别: ${selectedCategories.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        when (val statsState = statistics) {
            is com.finance.app.util.Resource.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is com.finance.app.util.Resource.Success -> {
                val stats = statsState.data
                if (stats != null) {
                    // 总览信息
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("总览", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("总金额: ¥${String.format("%.2f", stats.totalAmount)}", style = MaterialTheme.typography.bodyLarge)
                            Text("总记录数: ${stats.totalCount}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    
                    // 图表展示
                    val categoryStats = stats.categoryStats ?: emptyList()
                    if (categoryStats.isNotEmpty()) {
                        // 柱状图
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("类别统计（柱状图）", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                BarChart(categoryStats = categoryStats)
                            }
                        }
                        
                        // 饼图
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("类别占比（饼图）", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                PieChart(categoryStats = categoryStats)
                            }
                        }
                        
                        // 详细列表
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("详细数据", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                categoryStats.forEach { stat ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(stat.category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                            Text("${stat.count} 笔", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                            Text("¥${String.format("%.2f", stat.total)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                            Text("${String.format("%.1f", stat.percentage)}%", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text("暂无统计数据", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            is com.finance.app.util.Resource.Error -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(statsState.message ?: "加载失败", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
    
    // 时间筛选对话框
    if (showFilterDialog) {
        var tempRangeType by remember { mutableStateOf(rangeType) }
        var tempYearMonth by remember { mutableStateOf(yearMonth) }
        var tempStartTime by remember { mutableStateOf(startTime) }
        var tempEndTime by remember { mutableStateOf(endTime) }
        
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("筛选时间") },
            text = {
                Column {
                    // 时间范围类型选择
                    Text("时间范围类型", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        FilterChip(
                            selected = tempRangeType == "month",
                            onClick = { tempRangeType = "month" },
                            label = { Text("按月") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = tempRangeType == "year",
                            onClick = { tempRangeType = "year" },
                            label = { Text("按年") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = tempRangeType == "custom",
                            onClick = { tempRangeType = "custom" },
                            label = { Text("自定义") }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    when (tempRangeType) {
                        "month" -> {
                            Text("年月", style = MaterialTheme.typography.bodyMedium)
                            OutlinedTextField(
                                value = tempYearMonth,
                                onValueChange = { tempYearMonth = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("yyyy-MM") }
                            )
                            Row {
                                TextButton(onClick = {
                                    tempYearMonth = DateUtils.getCurrentYearMonth()
                                }) {
                                    Text("本月")
                                }
                            }
                        }
                        "year" -> {
                            Text("年份", style = MaterialTheme.typography.bodyMedium)
                            OutlinedTextField(
                                value = tempYearMonth.substring(0, 4),
                                onValueChange = { if (it.length <= 4) tempYearMonth = "$it-01" },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("yyyy") }
                            )
                        }
                        "custom" -> {
                            Text("开始时间", style = MaterialTheme.typography.bodyMedium)
                            OutlinedTextField(
                                value = tempStartTime,
                                onValueChange = { tempStartTime = it },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                placeholder = { Text("yyyy-MM-dd") }
                            )
                            Text("结束时间", style = MaterialTheme.typography.bodyMedium)
                            OutlinedTextField(
                                value = tempEndTime,
                                onValueChange = { tempEndTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("yyyy-MM-dd") }
                            )
                            Row {
                                TextButton(onClick = {
                                    tempStartTime = DateUtils.getCurrentMonthStart()
                                    tempEndTime = DateUtils.getCurrentMonthEnd()
                                }) {
                                    Text("本月")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        rangeType = tempRangeType
                        yearMonth = tempYearMonth
                        startTime = tempStartTime
                        endTime = tempEndTime
                        showFilterDialog = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 类别筛选对话框
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("筛选类别") },
            text = {
                Column(modifier = Modifier.fillMaxHeight(0.6f).verticalScroll(rememberScrollState())) {
                    categoriesState.value.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedCategories.contains(category.name),
                                onCheckedChange = { checked ->
                                    selectedCategories = if (checked) {
                                        selectedCategories + category.name
                                    } else {
                                        selectedCategories - category.name
                                    }
                                }
                            )
                            Text(category.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedCategories = emptySet()
                    showCategoryDialog = false
                }) {
                    Text("清除")
                }
            }
        )
    }
}

@Composable
fun BarChart(categoryStats: List<CategoryStat>) {
    if (categoryStats.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "暂无数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val colorScheme = MaterialTheme.colorScheme

    // 现代化颜色方案
    val colors = listOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary,
        Color(0xFFFF9800),
        Color(0xFFF44336),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4),
        Color(0xFF795548)
    )

    // 准备柱状图数据
    val barChartData = categoryStats.map { stat ->
        val label = if (stat.category.length > 4) stat.category.substring(0, 4) else stat.category
        BarData(
            label = label,
            value = stat.total.toFloat()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(16.dp)
    ) {
        BarChart(
            modifier = Modifier.fillMaxSize(),
            data = { barChartData },
            color = ChartyColor.Solid(colorScheme.primary),
            barConfig = BarChartConfig()
        )
    }
}

@Composable
fun PieChart(categoryStats: List<CategoryStat>) {
    if (categoryStats.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "暂无数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val colorScheme = MaterialTheme.colorScheme

    // 颜色方案
    val colors = listOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary,
        Color(0xFFFF9800),
        Color(0xFFF44336),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4),
        Color(0xFF795548)
    )

    // 准备饼图数据
    val pieChartData = categoryStats.mapIndexed { index, stat ->
        PieData(
            label = stat.category,
            value = stat.total.toFloat(),
            color = colors[index % colors.size]
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 饼图
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                modifier = Modifier.fillMaxSize(),
                data = { pieChartData },
                color = ChartyColor.Solid(colorScheme.primary),
                config = PieChartConfig()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categoryStats.forEachIndexed { index, stat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(
                                        colors[index % colors.size],
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Column {
                                Text(
                                    stat.category,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "${stat.count} 笔",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "¥${String.format("%.2f", stat.total)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${String.format("%.1f", stat.percentage)}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncomeListScreen(
    viewModel: IncomeViewModel,
    startTime: String,
    endTime: String,
    totalIncome: Double?,
    onOpenDatePicker: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    var showAddIncome by remember { mutableStateOf(false) }
    
    LaunchedEffect(startTime, endTime) {
        viewModel.getIncomes(startTime = startTime, endTime = endTime)
    }
    
    val incomes by viewModel.incomes.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val loadMoreError by viewModel.loadMoreError.collectAsState()
    val listState = rememberLazyListState()
    
    // 如果显示添加界面，直接显示添加界面
    if (showAddIncome) {
        AddIncomeScreen(viewModel = viewModel, onBack = { showAddIncome = false })
        return
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 顶部标题栏：收入记录 + 总收入金额
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "收入记录",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    if (totalIncome != null) "¥${"%.2f".format(totalIncome)}" else "—",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(
                onClick = onOpenDatePicker,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    "筛选时间",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // 时间范围显示
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "$startTime 至 $endTime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        when (val incomesState = incomes) {
            is com.finance.app.util.Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is com.finance.app.util.Resource.Success -> {
                val incomeList = incomesState.data?.list ?: emptyList()

                LaunchedEffect(listState, hasMore, isLoadingMore) {
                    snapshotFlow {
                        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                        val total = listState.layoutInfo.totalItemsCount
                        lastVisible to total
                    }
                        .distinctUntilChanged()
                        .map { (lastVisible, total) -> total > 0 && lastVisible >= total - 1 }
                        .distinctUntilChanged()
                        .collect { reachedEnd ->
                            if (reachedEnd && hasMore && !isLoadingMore) {
                                viewModel.loadNextPage()
                            }
                        }
                }

                if (incomeList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                "暂无收入记录",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "点击右下角按钮添加第一条收入记录",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = incomeList,
                            key = { it.id }
                        ) { income ->
                            val scale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .scale(scale)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        // 收入类型图标
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            contentAlignment = androidx.compose.ui.Alignment.Center
                                        ) {
                                            Icon(
                                                @Suppress("DEPRECATION")
                                                Icons.Default.TrendingUp,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                income.type,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                "¥${String.format("%.2f", income.amount)}",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.AccessTime,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    DateUtils.formatDateTime(income.incomeTime),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    
                                    IconButton(
                                        onClick = { showDeleteDialog = income.id },
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            "删除",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 底部加载更多 footer
                        item(key = "income_footer") {
                            when {
                                isLoadingMore -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                                loadMoreError != null -> {
                                    TextButton(
                                        onClick = { viewModel.loadNextPage() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(loadMoreError ?: "加载失败，点此重试")
                                    }
                                }
                                !hasMore -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Text(
                                            "没有更多了",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is com.finance.app.util.Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(incomesState.message ?: "加载失败", color = MaterialTheme.colorScheme.error)
                }
            }
            null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        }
        
        // 悬浮添加按钮 - 现代化设计
        FloatingActionButton(
            onClick = { showAddIncome = true },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(20.dp)
                .size(64.dp),
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "添加收入",
                modifier = Modifier.size(28.dp)
            )
        }
    }
    
    // 删除确认对话框
    showDeleteDialog?.let { incomeId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条收入记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteIncome(incomeId)
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    viewModel: IncomeViewModel,
    onBack: () -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var typeExpanded by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // 收入类型列表
    val incomeTypes = remember {
        listOf("工资", "奖金", "投资收益", "其他收入")
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // 顶部标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                @Suppress("DEPRECATION")
                Icon(Icons.Default.ArrowBack, "返回", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "添加收入",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        
        // 金额输入框
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("金额", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("0.00") },
            leadingIcon = {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.secondary
            )
        )
        
        // 收入类型下拉菜单
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
            OutlinedTextField(
                value = selectedType ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("收入类型", style = MaterialTheme.typography.bodyMedium) },
                placeholder = { Text("请选择收入类型") },
                leadingIcon = {
                    Icon(
                        @Suppress("DEPRECATION")
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.secondary
                )
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                incomeTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            typeExpanded = false
                        }
                    )
                }
            }
        }
        
        // 保存按钮
        Button(
            onClick = {
                if (amount.isNotBlank() && selectedType != null) {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    val currentTime = DateUtils.getCurrentDateTime()
                    viewModel.createIncome(amountValue, selectedType!!, currentTime)
                    amount = ""
                    selectedType = null
                    showSuccessMessage = true
                    // 保存成功后延迟返回
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                        kotlinx.coroutines.delay(1500)
                        onBack()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = amount.isNotBlank() && selectedType != null,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "保存",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        
        if (showSuccessMessage) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "保存成功！",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AddRecordScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel
) {
    var recordType by remember { mutableStateOf<RecordType?>(null) }
    
    when (recordType) {
        null -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("选择记录类型", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { recordType = RecordType.EXPENSE },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("添加支出")
                }
                
                Button(
                    onClick = { recordType = RecordType.INCOME },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("添加收入")
                }
            }
        }
        RecordType.EXPENSE -> {
            AddExpenseScreen(expenseViewModel)
        }
        RecordType.INCOME -> {
            AddIncomeScreen(incomeViewModel)
        }
    }
}

private enum class RecordType {
    EXPENSE, INCOME
}

@Composable
fun AIScreen(
    aiViewModel: AIViewModel = remember { AIViewModel() }
) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = 0)
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        aiViewModel.getAIModels()
    }
    
    val aiModels by aiViewModel.aiModels.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("AI聊天") }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("账单分析") }
            )
        }
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    AIChatScreen(aiViewModel, aiModels)
                }
                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    AIAnalysisScreen(aiViewModel, aiModels)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AIChatScreen(
    viewModel: AIViewModel,
    aiModels: Resource<List<AIModel>>?
) {
    var selectedModelId by remember { mutableStateOf<Int?>(null) }
    var message by remember { mutableStateOf("") }
    var showHistoryDialog by remember { mutableStateOf(false) }
    
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏：模型选择 + 右上角历史按钮
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // AI 模型选择 - 下拉框形式
            when (val modelsState = aiModels) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.weight(1f)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterStart).size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                is Resource.Success -> {
                    val models = modelsState.data ?: emptyList()
                    if (models.isNotEmpty() && selectedModelId == null) {
                        selectedModelId = models.first().id
                    }
                    
                    if (models.isNotEmpty()) {
                        var modelExpanded by remember { mutableStateOf(false) }
                        val selectedModel = models.find { it.id == selectedModelId }

                        // 更紧凑的模型选择：Chip + 下拉菜单
                        Box(modifier = Modifier.weight(1f)) {
                            AssistChip(
                                onClick = { modelExpanded = true },
                                label = {
                                    Text(
                                        text = selectedModel?.name ?: "选择AI模型",
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = modelExpanded,
                                onDismissRequest = { modelExpanded = false }
                            ) {
                                models.forEach { model ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(model.name, style = MaterialTheme.typography.bodyMedium)
                                                Text(
                                                    model.baseUrl,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedModelId = model.id
                                            modelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                is Resource.Error -> {
                    Text(
                        modelsState.message ?: "加载失败",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                null -> {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            // 右上角：历史记录按钮 + 清空对话按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                // 现代化的历史记录按钮
                IconButton(
                    onClick = {
                        showHistoryDialog = true
                        selectedModelId?.let { modelId ->
                            viewModel.getChatHistory(modelId)
                        }
                    },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "查看历史",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                // 清空对话按钮（仅在有待清空的消息时显示）
                if (chatMessages.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "清空对话",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
        
        // 历史记录 Dialog
        if (showHistoryDialog) {
            ChatHistoryDialog(
                chatHistory = chatHistory,
                onDismiss = { showHistoryDialog = false },
                onHistoryItemClick = { historyItem ->
                    // 点击历史记录项时，可以加载到当前对话（可选功能）
                    showHistoryDialog = false
                }
            )
        }
        
        // 聊天消息列表和流式响应区域（可滚动）
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val listState = rememberLazyListState()
            
            // 自动滚动到底部
            LaunchedEffect(chatMessages.size, aiResponse) {
                if (chatMessages.isNotEmpty() || aiResponse.isNotEmpty()) {
                    kotlinx.coroutines.delay(100) // 等待布局完成
                    val targetIndex = if (chatMessages.isNotEmpty()) {
                        chatMessages.size - 1
                    } else {
                        0
                    }
                    if (targetIndex >= 0) {
                        listState.animateScrollToItem(targetIndex)
                    }
                }
            }
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                // 聊天消息列表
                items(
                    items = chatMessages,
                    key = { it.hashCode() }
                ) { (role, content) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalArrangement = if (role == "user") Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (role == "user")
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                content,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // AI 响应显示（流式输出）- 只在分析中且响应不为空时显示
                if (isAnalyzing && aiResponse.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    aiResponse,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // 输入框 - 确保始终可见
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入消息...") },
                enabled = !isAnalyzing && selectedModelId != null,
                singleLine = false,
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (message.isNotBlank() && selectedModelId != null) {
                        viewModel.chatWithAI(selectedModelId!!, message)
                        message = ""
                    }
                },
                enabled = !isAnalyzing && selectedModelId != null && message.isNotBlank()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "发送")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AIAnalysisScreen(
    viewModel: AIViewModel,
    aiModels: Resource<List<AIModel>>?
) {
    var selectedModelId by remember { mutableStateOf<Int?>(null) }
    val defaultStartTime = remember { DateUtils.getCurrentMonthStart() }
    val defaultEndTime = remember { DateUtils.getCurrentMonthEnd() }
    var startTime by remember { mutableStateOf(defaultStartTime) }
    var endTime by remember { mutableStateOf(defaultEndTime) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisHistory by viewModel.analysisHistory.collectAsState()
    
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
        if (selectedModelId == null && aiModels is Resource.Success) {
            aiModels.data?.firstOrNull()?.let {
                selectedModelId = it.id
            }
        }
    }
    
    // 自动滚动到底部（当分析结果更新时）
    LaunchedEffect(aiResponse) {
        if (aiResponse.isNotEmpty()) {
            kotlinx.coroutines.delay(50) // 等待布局更新
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // 顶部栏：模型选择 + 右上角历史按钮
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // AI 模型选择 - 下拉框形式
            when (val modelsState = aiModels) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.weight(1f)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterStart).size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                is Resource.Success -> {
                    val models = modelsState.data ?: emptyList()
                    if (models.isNotEmpty() && selectedModelId == null) {
                        selectedModelId = models.first().id
                    }
                    
                    if (models.isNotEmpty()) {
                        var modelExpanded by remember { mutableStateOf(false) }
                        val selectedModel = models.find { it.id == selectedModelId }

                        // 更紧凑的模型选择：Chip + 下拉菜单
                        Box(modifier = Modifier.weight(1f)) {
                            AssistChip(
                                onClick = { modelExpanded = true },
                                label = {
                                    Text(
                                        text = selectedModel?.name ?: "选择AI模型",
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = modelExpanded,
                                onDismissRequest = { modelExpanded = false }
                            ) {
                                models.forEach { model ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(model.name, style = MaterialTheme.typography.bodyMedium)
                                                Text(
                                                    model.baseUrl,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedModelId = model.id
                                            modelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                is Resource.Error -> {
                    Text(
                        modelsState.message ?: "加载失败",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                null -> {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            // 右上角：历史记录按钮
            IconButton(
                onClick = {
                    showHistoryDialog = true
                    selectedModelId?.let { modelId ->
                        viewModel.getAnalysisHistory(modelId)
                    }
                },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "查看历史",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 时间范围选择
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("时间范围", style = MaterialTheme.typography.titleSmall)
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, "选择时间")
                    }
                }
                Text(
                    "$startTime 至 $endTime",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // 分析按钮
        Button(
            onClick = {
                if (selectedModelId != null) {
                    viewModel.analyzeExpenses(selectedModelId!!, startTime, endTime)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isAnalyzing && selectedModelId != null
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isAnalyzing) "分析中..." else "开始分析")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 分析结果
        if (aiResponse.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "分析结果",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        aiResponse,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    
    // 历史记录 Dialog
    if (showHistoryDialog) {
        AnalysisHistoryDialog(
            analysisHistory = analysisHistory,
            onDismiss = { showHistoryDialog = false },
            onHistoryItemClick = { historyItem ->
                // 点击历史记录项时，可以加载到当前分析结果（可选功能）
                showHistoryDialog = false
            }
        )
    }
    
    // 日期选择对话框
    if (showDatePicker) {
        var tempStartTime by remember { mutableStateOf(startTime) }
        var tempEndTime by remember { mutableStateOf(endTime) }
        
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("选择时间范围") },
            text = {
                Column {
                    Text("开始时间", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = tempStartTime,
                        onValueChange = { tempStartTime = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        placeholder = { Text("yyyy-MM-dd") }
                    )
                    Text("结束时间", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = tempEndTime,
                        onValueChange = { tempEndTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("yyyy-MM-dd") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        TextButton(onClick = {
                            tempStartTime = DateUtils.getCurrentMonthStart()
                            tempEndTime = DateUtils.getCurrentMonthEnd()
                        }) {
                            Text("本月")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            val calendar = java.util.Calendar.getInstance()
                            calendar.add(java.util.Calendar.MONTH, -1)
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                            tempStartTime = DateUtils.formatDate(calendar.time)
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
                            tempEndTime = DateUtils.formatDate(calendar.time)
                        }) {
                            Text("上月")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        startTime = tempStartTime
                        endTime = tempEndTime
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryDialog(
    chatHistory: Resource<PageResponse<AIChatHistory>>?,
    onDismiss: () -> Unit,
    onHistoryItemClick: (AIChatHistory) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "聊天历史",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) {
        when (chatHistory) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            "加载中…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is Resource.Success -> {
                val history: List<AIChatHistory> = chatHistory.data?.list ?: emptyList()
                if (history.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 32.dp)
                    ) {
                        items(
                            items = history,
                            key = { item: AIChatHistory -> item.id }
                        ) { item ->
                            ChatHistoryItem(
                                item = item,
                                onClick = { onHistoryItemClick(item) }
                            )
                            if (item != history.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        CircleShape
                                    ),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                "暂无聊天记录",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "与 AI 对话后会出现在这里",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                        Text(
                            chatHistory.message ?: "加载失败",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatHistoryItem(
    item: AIChatHistory,
    onClick: () -> Unit
) {
    val userText = item.userText.orEmpty()
    val aiText = item.aiText.orEmpty()
    val hasContent = userText.isNotBlank() || aiText.isNotBlank()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = hasContent, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.Top
    ) {
        // 左侧时间线圆点
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(10.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    CircleShape
                )
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (userText.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = userText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 48,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
                if (aiText.isNotBlank()) Spacer(modifier = Modifier.height(8.dp))
            }
            if (aiText.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = aiText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 80,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
            if (!hasContent) {
                Text(
                    "无消息内容",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    DateUtils.formatRelativeTime(item.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisHistoryDialog(
    analysisHistory: Resource<PageResponse<AIAnalysisHistory>>?,
    onDismiss: () -> Unit,
    onHistoryItemClick: (AIAnalysisHistory) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "分析历史",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) {
        when (analysisHistory) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            "加载中…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is Resource.Success -> {
                val history: List<AIAnalysisHistory> = analysisHistory.data?.list ?: emptyList()
                if (history.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 32.dp)
                    ) {
                        items(
                            items = history,
                            key = { item: AIAnalysisHistory -> item.id }
                        ) { item ->
                            AnalysisHistoryItem(
                                item = item,
                                onClick = { onHistoryItemClick(item) }
                            )
                            if (item != history.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        CircleShape
                                    ),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                "暂无分析记录",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "进行账单分析后会出现在这里",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                        Text(
                            analysisHistory.message ?: "加载失败",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
private fun AnalysisHistoryItem(
    item: AIAnalysisHistory,
    onClick: () -> Unit
) {
    val dateRangeText = "${item.startDate ?: "—"} 至 ${item.endDate ?: "—"}"
    val resultText = item.result.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(10.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    CircleShape
                )
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = dateRangeText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (resultText.isNotBlank()) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 96,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            } else {
                Text(
                    "无分析结果",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    DateUtils.formatRelativeTime(item.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

