package co.saputra.imagemachine.arch.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.databinding.FragmentMainBinding
import co.saputra.imagemachine.util.setSafeClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(
    FragmentMainBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()

    override fun initView(view: View, savedInstaceState: Bundle?) {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.containerMenu) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        binding.fabAdd.setSafeClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_machineFormFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.machineListFragment) {
                binding.fabAdd.visibility = View.VISIBLE
            } else {
                binding.fabAdd.visibility = View.GONE
            }
        }
    }


}