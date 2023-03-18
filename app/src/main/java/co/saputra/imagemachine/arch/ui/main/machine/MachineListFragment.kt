package co.saputra.imagemachine.arch.ui.main.machine

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import co.saputra.imagemachine.Constants.INTENT_MACHINE
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.adapter.MachineAdapter
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.data.entity.MachineWithImages
import co.saputra.imagemachine.databinding.FragmentMachineListBinding
import co.saputra.imagemachine.util.findNavControllerById
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

class MachineListFragment : BaseFragment<FragmentMachineListBinding, MainViewModel>(
    FragmentMachineListBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()
    private val adapter by lazy {
        MachineAdapter(layoutInflater, Glide.with(requireContext()), ::onItemClick)
    }

    override fun initView(view: View, savedInstaceState: Bundle?) {
        binding.apply {
            rvMachines.adapter = adapter
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.getAllMachines().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun onItemClick(item: MachineWithImages) {
        val bundle = bundleOf(INTENT_MACHINE to item.machine.id)
        findNavControllerById(R.id.container)
             .navigate(R.id.action_mainFragment_to_machineDetailFragment, bundle)
    }
}