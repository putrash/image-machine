package co.saputra.imagemachine.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity()
@Parcelize
data class Image(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "image_id")
    var id: Long = 0,

    @ColumnInfo(name = "machine_id")
    var machineId: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "path")
    var path: String = "",
) : Parcelable
