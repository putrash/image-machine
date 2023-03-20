package co.saputra.imagemachine.base

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import co.saputra.imagemachine.Constants
import co.saputra.imagemachine.util.EventObserver
import co.saputra.imagemachine.util.ManagePermissions
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB: ViewBinding, VM: BaseViewModel>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding : VB? = null
    val binding get() = _binding!!

    protected abstract val viewModel: VM
    protected lateinit var managePermissions: ManagePermissions
    abstract fun initView(view: View, savedInstaceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(requireActivity(), list , Constants.REQUEST_CODE)
        initView(view, savedInstanceState)
        observeLiveData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun observeLiveData() {
        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver { message ->
            showSnackBar(message)
        })
    }

    fun showSnackBar(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply { show() }
}