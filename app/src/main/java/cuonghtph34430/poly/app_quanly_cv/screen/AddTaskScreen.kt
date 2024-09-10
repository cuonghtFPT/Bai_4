package cuonghtph34430.poly.app_quanly_cv.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskScreen(navController: NavController) {
    val context = LocalContext.current
    val taskName = remember { mutableStateOf("") }
    val taskDateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Task") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = taskName.value,
                    onValueChange = { taskName.value = it },
                    label = { Text("Task Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                DateTimePickerButton(
                    dateTime = taskDateTime.value,
                    onDateTimeChange = { dateTime ->
                        taskDateTime.value = dateTime
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val values = ContentValues().apply {
                            put(TaskDatabase.COLUMN_NAME, taskName.value)
                            put(TaskDatabase.COLUMN_DATETIME, taskDateTime.value.format(dateTimeFormatter))
                        }
                        context.contentResolver.insert(TaskProvider.CONTENT_URI, values)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Task", color = Color.White)
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimePickerButton(dateTime: LocalDateTime, onDateTimeChange: (LocalDateTime) -> Unit) {
    val context = LocalContext.current

    Column {
        Button(
            onClick = {
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val timePickerDialog = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val newDateTime = LocalDateTime.of(
                                    year,
                                    month + 1,
                                    dayOfMonth,
                                    hourOfDay,
                                    minute
                                )
                                onDateTimeChange(newDateTime)
                            },
                            dateTime.hour,
                            dateTime.minute,
                            true
                        )
                        timePickerDialog.show()
                    },
                    dateTime.year,
                    dateTime.monthValue - 1,
                    dateTime.dayOfMonth
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick Date", color = Color.White)
        }
    }
}
