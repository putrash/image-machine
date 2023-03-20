package co.saputra.imagemachine.data

import androidx.room.Database
import androidx.room.RoomDatabase
import co.saputra.imagemachine.data.dao.ImageDao
import co.saputra.imagemachine.data.dao.MachineDao
import co.saputra.imagemachine.data.entity.Image
import co.saputra.imagemachine.data.entity.Machine

@Database(
    entities = [
        Machine::class,
        Image::class
    ],
    exportSchema = false,
    version = 4
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun machineDao(): MachineDao
}