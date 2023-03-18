package co.saputra.imagemachine.arch.vm

import androidx.lifecycle.LiveData
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
import java.util.*

class MainViewModel(
    private val machineLocalSource: MachineLocalSource,
    private val imageLocalSource: ImageLocalSource
) : BaseViewModel() {

    private fun generateCode() : String {
        return System.currentTimeMillis().toString()
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

    fun getMachine(id: Long) : LiveData<MachineWithImages> {
        return machineLocalSource.getMachine(id)
    }

    fun getAllMachines(): LiveData<List<MachineWithImages>> {
        return machineLocalSource.getAllMachines()
    }

    fun insertImage(image: Image) {
        imageLocalSource.insert(image)
    }
}