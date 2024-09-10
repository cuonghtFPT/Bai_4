package cuonghtph34430.poly.app_quanly_cv.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "Task")
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "time") var time:  LocalDateTime?
)