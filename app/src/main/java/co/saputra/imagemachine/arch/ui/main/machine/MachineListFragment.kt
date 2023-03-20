package co.saputra.imagemachine.arch.ui.main.machine

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import co.saputra.imagemachine.Constants.INTENT_MACHINE
import co.saputra.imagemachine.Constants.SORT_NAME
import co.saputra.imagemachine.Constants.SORT_TYPE
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.adapter.MachineAdapter
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.data.entity.MachineWithImages
import co.saputra.imagemachine.databinding.FragmentMachineListBinding
import co.saputra.imagemachine.util.capitalize
import co.saputra.imagemachine.util.findNavControllerById
import co.saputra.imagemachine.util.setSafeClickListener
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

class MachineListFragment : BaseFragment<FragmentMachineListBinding, MainViewModel>(
    FragmentMachineListBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()
    private val adapter by lazy {
        MachineAdapter(layoutInflater, Glide.with(requireContext()), ::onItemClick)
    }
    private var checkedSortItem = 0

    override fun initView(view: View, savedInstaceState: Bundle?) {
        binding.apply {
            tvSort.setSafeClickListener {
                showDialogFilter()
            }
            rvMachines.adapter = adapter
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.setSortedType(SORT_NAME)
        viewModel.getAllMachines().observe(viewLifecycleOwner) { data ->
            adapter.submitList(data)
        }
    }

    private fun showDialogFilter() {
        val listSort = arrayOf(SORT_NAME, SORT_TYPE)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Sort by")
            .setSingleChoiceItems(listSort.map { it.capitalize() }.toTypedArray(), checkedSortItem) { dialog, index ->
                checkedSortItem = index
                viewModel.setSortedType(listSort[index])
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun onItemClick(item: MachineWithImages) {
        val bundle = bundleOf(INTENT_MACHINE to item.machine.id)
        findNavControllerById(R.id.container)
             .navigate(R.id.action_mainFragment_to_machineDetailFragment, bundle)
    }
}