package co.saputra.imagemachine.arch.ui.main.scanner

import android.os.Bundle
import android.view.View
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.databinding.FragmentCodeScanBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class CodeScanFragment : BaseFragment<FragmentCodeScanBinding, MainViewModel>(
    FragmentCodeScanBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()

    override fun initView(view: View, savedInstaceState: Bundle?) {

    }
}