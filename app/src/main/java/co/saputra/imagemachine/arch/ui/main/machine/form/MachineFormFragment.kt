package co.saputra.imagemachine.arch.ui.main.machine.form

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.databinding.FragmentMachineFormBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormatSymbols
import java.util.*

class MachineFormFragment : BaseFragment<FragmentMachineFormBinding, MainViewModel>(
    FragmentMachineFormBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()
    private lateinit var datePicker: DatePickerDialog
    private var nameNotEmpty = false
    private var typeNotEmpty = false
    private var dateNotEmpty = false

    override fun initView(view: View, savedInstaceState: Bundle?) {
        initCalendarDialog()
        binding.apply {
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
                viewModel.insertMachine(
                    etMachineName.text.toString(),
                    etMachineType.text.toString(),
                    etMachineCode.text.toString(),
                    etMachineDate.text.toString()
                )
                findNavController().navigate(R.id.action_machineFormFragment_to_mainFragment)
            }
        }
    }

    private fun initCalendarDialog() {
        val currentTime = Calendar.getInstance()
        val yearNow = currentTime.get(Calendar.YEAR)
        val monthNow = currentTime.get(Calendar.MONTH)
        val dayNow = currentTime.get(Calendar.DAY_OF_MONTH)

        datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            binding.etMachineDate.setText(
                String.format("%d %s %d", dayOfMonth, DateFormatSymbols().months[month], year)
            ) }, yearNow, monthNow, dayNow)
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
}