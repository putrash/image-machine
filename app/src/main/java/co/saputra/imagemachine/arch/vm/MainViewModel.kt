package co.saputra.imagemachine.arch.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import co.saputra.imagemachine.base.BaseViewModel
import co.saputra.imagemachine.data.entity.Image
import co.saputra.imagemachine.data.entity.Machine
import co.saputra.imagemachine.data.entity.MachineWithImages
import co.saputra.imagemachine.data.source.ImageLocalSource
import co.saputra.imagemachine.data.source.MachineLocalSource
import co.saputra.imagemachine.util.parseDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val machineLocalSource: MachineLocalSource,
    private val imageLocalSource: ImageLocalSource
) : BaseViewModel() {

    private val sortType = MutableLiveData<String>()

    private fun generateCode(): String {
        return System.currentTimeMillis().toString()
    }

    fun setSortedType(type: String) {
        sortType.value = type
    }

    fun getMachine(id: Long): LiveData<MachineWithImages> {
        return machineLocalSource.getMachine(id)
    }

    fun getMachine(code: String): LiveData<MachineWithImages?> {
        return machineLocalSource.getMachineByCode(code)
    }

    fun getAllMachines(): LiveData<List<MachineWithImages>> = sortType.switchMap { sort ->
        machineLocalSource.getAllMachines(sort)
    }

    fun insertMachine(name: String, type: String, code: String = "", maintenanceDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                machineLocalSource.insert(
                    Machine(
                        name = name,
                        type = type,
                        code = code.ifEmpty { generateCode() },
                        maintenanceDate = maintenanceDate.parseDate().time
                    )
                )
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                showError(throwable.message.toString())
            }
        }
    }

    fun updateMachine(id: Long, name: String, type: String, code: String, maintenanceDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                machineLocalSource.update(
                    Machine(
                        id = id,
                        name = name,
                        type = type,
                        code = code.ifEmpty { generateCode() },
                        maintenanceDate = maintenanceDate.parseDate().time
                    )
                )
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                showError(throwable.message.toString())
            }
        }
    }

    fun deleteMachine(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                machineLocalSource.delete(id)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                showError(throwable.message.toString())
            }
        }
    }

    fun insertImage(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                imageLocalSource.insert(image)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                showError(throwable.message.toString())
            }
        }
    }

    fun setLoading(active: Boolean) {
        if (active) showLoading()
        else hideLoading()
    }
}