package co.saputra.imagemachine.data.dao

import androidx.room.*
import co.saputra.imagemachine.data.entity.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: Image)

    @Update
    fun update(image: Image)

    @Query("DELETE FROM image WHERE image_id = :id")
    fun delete(id: Long)
}