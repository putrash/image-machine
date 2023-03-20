package co.saputra.imagemachine.data.source

import androidx.lifecycle.LiveData
import co.saputra.imagemachine.Constants.SORT_NAME
import co.saputra.imagemachine.Constants.SORT_TYPE
import co.saputra.imagemachine.data.dao.MachineDao
import co.saputra.imagemachine.data.entity.Machine
import co.saputra.imagemachine.data.entity.MachineWithImages

class MachineLocalSource(private val machineDao: MachineDao) {
    fun getAllMachines(sort: String = SORT_NAME): LiveData<List<MachineWithImages>> {
       return if (sort == SORT_TYPE) machineDao.getAllMachinesByType()
       else machineDao.getAllMachinesByName()
    }

    fun getMachine(id: Long) = machineDao.getMachine(id)

    fun insert(machine: Machine) = machineDao.insert(machine)

    fun update(machine: Machine) = machineDao.update(machine)

    fun delete(id: Long) = machineDao.delete(id)
}