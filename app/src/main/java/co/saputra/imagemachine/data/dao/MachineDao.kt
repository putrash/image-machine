package co.saputra.imagemachine.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import co.saputra.imagemachine.data.entity.Machine
import co.saputra.imagemachine.data.entity.MachineWithImages

@Dao
interface MachineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(machine: Machine)

    @Update
    fun update(machine: Machine)

    @Query("DELETE FROM machine WHERE id = :id")
    fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM machine ORDER BY name ASC")
    fun getAllMachinesByName() : LiveData<List<MachineWithImages>>

    @Transaction
    @Query("SELECT * FROM machine ORDER BY type ASC")
    fun getAllMachinesByType() : LiveData<List<MachineWithImages>>

    @Transaction
    @Query("SELECT * FROM machine WHERE id = :id")
    fun getMachine(id: Long) : LiveData<MachineWithImages>

    @Transaction
    @Query("SELECT * FROM machine WHERE code = :code")
    fun getMachineByCode(code: String) : LiveData<MachineWithImages?>
}