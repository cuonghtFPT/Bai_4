package cuonghtph34430.poly.app_quanly_cv.screen

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cuonghtph34430.poly.app_quanly_cv.Model.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskListActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList() {
    val navController = rememberNavController()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addTask") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        },
        content = { innerPadding ->
            NavHost(
                navController, startDestination = "main", modifier = Modifier.padding(innerPadding)
            ) {
                composable("main") { TaskListScreen() }
                composable("addTask") { AddTaskScreen(navController) }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskListScreen() {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    var tasks by remember { mutableStateOf(listOf<Task>()) }

    LaunchedEffect(Unit) {
        val cursor = contentResolver.query(TaskProvider.CONTENT_URI, null, null, null, null)
        tasks = cursor?.use {
            val tasksList = mutableListOf<Task>()
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(TaskDatabase.COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(TaskDatabase.COLUMN_NAME))
                val dateString = it.getString(it.getColumnIndexOrThrow(TaskDatabase.COLUMN_DATETIME))
                val dateTime = LocalDateTime.parse(dateString) // Chuyển đổi String thành LocalDateTime
                tasksList.add(Task(id, name, dateTime))
            }
            tasksList
        } ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Task List",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(task)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .graphicsLayer { shadowElevation = 4.dp.toPx() }
            .background(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        onClick = { /* Handle click event */ }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            task.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            task.time?.let {
                Text(
                    text = it.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
