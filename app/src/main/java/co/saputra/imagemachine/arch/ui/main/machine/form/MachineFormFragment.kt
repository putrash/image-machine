package co.saputra.imagemachine.arch.ui.main.machine.form

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import co.saputra.imagemachine.Constants
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.databinding.FragmentMachineFormBinding
import co.saputra.imagemachine.util.formatDate
import co.saputra.imagemachine.util.isEmpty
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormatSymbols
import java.util.*

class MachineFormFragment : BaseFragment<FragmentMachineFormBinding, MainViewModel>(
    FragmentMachineFormBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()
    private lateinit var datePicker: DatePickerDialog
    private var id: Long = 0
    private var nameNotEmpty = false
    private var typeNotEmpty = false
    private var dateNotEmpty = false

    private val currentTime = Calendar.getInstance()
    private var yearNow = currentTime.get(Calendar.YEAR)
    private var monthNow = currentTime.get(Calendar.MONTH)
    private var dayNow = currentTime.get(Calendar.DAY_OF_MONTH)

    override fun initView(view: View, savedInstaceState: Bundle?) {
        id = getMachineId()
        initCalendarDialog()
        binding.apply {
            toolbar.title = if (id.isEmpty()) "Add Machine" else "Edit Machine"
            toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            etMachineName.addTextChangedListener {
                nameNotEmpty = it.toString().isNotEmpty()
                handleButtonState()
            }
            etMachineType.addTextChangedListener {
                typeNotEmpty = it.toString().isNotEmpty()
                handleButtonState()
            }
            etMachineDate.addTextChangedListener {
                dateNotEmpty = it.toString().isNotEmpty()
                handleButtonState()
            }
            etMachineDate.setOnClickListener {
                datePicker.show()
            }
            btnSave.setOnClickListener {
                handleButtonAction()
                findNavController().navigate(R.id.action_machineFormFragment_to_mainFragment)
            }
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        if (!id.isEmpty()) {
            viewModel.getMachine(id).observe(viewLifecycleOwner) {
                binding.apply {
                    etMachineName.setText(it.machine.name)
                    etMachineType.setText(it.machine.type)
                    etMachineCode.setText(it.machine.code)
                    etMachineDate.setText(Date(it.machine.maintenanceDate).formatDate())
                    btnSave.text = "Edit Existing Machine"
                }
                currentTime.timeInMillis = it.machine.maintenanceDate
                yearNow = currentTime.get(Calendar.YEAR)
                monthNow = currentTime.get(Calendar.MONTH)
                dayNow = currentTime.get(Calendar.DAY_OF_MONTH)
                datePicker.updateDate(yearNow, monthNow, dayNow)
            }
        }
    }

    private fun initCalendarDialog() {
        datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            binding.etMachineDate.setText(
                String.format("%d %s %d", dayOfMonth, DateFormatSymbols().months[month], year)
            ) }, yearNow, monthNow, dayNow)
    }

    private fun handleButtonAction() {
        binding.apply {
            if (id.isEmpty()) {
                viewModel.insertMachine(
                    etMachineName.text.toString(),
                    etMachineType.text.toString(),
                    etMachineCode.text.toString(),
                    etMachineDate.text.toString()
                )
            } else {
                viewModel.updateMachine(
                    id,
                    etMachineName.text.toString(),
                    etMachineType.text.toString(),
                    etMachineCode.text.toString(),
                    etMachineDate.text.toString()
                )
            }
        }
    }

    private fun handleButtonState() {
        binding.apply {
            if (nameNotEmpty && typeNotEmpty && dateNotEmpty) {
                btnSave.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button)
                btnSave.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                btnSave.isEnabled = true
            } else {
                btnSave.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_disabled)
                btnSave.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                btnSave.isEnabled = false
            }
        }
    }

    private fun getMachineId(): Long {
        return arguments?.getLong(Constants.INTENT_MACHINE) ?: 0L
    }
}