package co.saputra.imagemachine.arch.ui.main.scanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import co.saputra.imagemachine.Constants.INTENT_MACHINE
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.databinding.FragmentCodeScanBinding
import co.saputra.imagemachine.util.MLKitBarcodeAnalyzer
import co.saputra.imagemachine.util.ScanningResultListener
import co.saputra.imagemachine.util.findNavControllerById
import co.saputra.imagemachine.util.setSafeClickListener
import com.google.common.util.concurrent.ListenableFuture
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class CodeScanFragment : BaseFragment<FragmentCodeScanBinding, MainViewModel>(
    FragmentCodeScanBinding::inflate
), ScanningResultListener {
    override val viewModel: MainViewModel by viewModel()
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageAnalysis: ImageAnalysis
    private var flashEnabled = false

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    override fun initView(view: View, savedInstaceState: Bundle?) {
        managePermissions.checkPermissions()
        initAction()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (managePermissions.processPermissionsResult(requestCode, grantResults)) {
            initAction()
        } else {
            managePermissions.checkPermissions()
        }
    }

    private fun initAction() {
        binding.apply {
            viewFinder.post {
                cameraExecutor = Executors.newSingleThreadExecutor()
                cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
                cameraProviderFuture.addListener({
                    cameraProvider = cameraProviderFuture.get()
                    bindCameraUseCases(cameraProvider)
                }, ContextCompat.getMainExecutor(requireContext()))

            }
            toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        if (requireActivity().isDestroyed || requireActivity().isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider.unbindAll()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val metrics = DisplayMetrics().also { binding.viewFinder.display?.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val preview = buildPreviewUseCase(screenAspectRatio, binding.viewFinder.display.rotation)

        imageAnalysis =
            buildImageAnalysisUseCase(screenAspectRatio, binding.viewFinder.display.rotation)

        val analyzer = MLKitBarcodeAnalyzer(this)
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        val camera =
            cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        if (camera.cameraInfo.hasFlashUnit()) {
            binding.btnFlash.setSafeClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }
            camera.cameraInfo.torchState.observe(viewLifecycleOwner) { torchState ->
                if (torchState == TorchState.ON) {
                    flashEnabled = true
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
                } else {
                    flashEnabled = false
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
                }
            }
        } else binding.btnFlash.visibility = View.GONE
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun buildPreviewUseCase(aspectRatio: Int, rotation: Int): Preview {
        return Preview.Builder().apply {
            setTargetAspectRatio(aspectRatio)
            setTargetRotation(rotation)
        }.build()
    }

    private fun buildImageAnalysisUseCase(aspectRatio: Int, rotation: Int): ImageAnalysis {
        return ImageAnalysis.Builder().apply {
            setTargetAspectRatio(aspectRatio)
            setTargetRotation(rotation)
        }.build()
    }

    override fun onScanned(result: String) {
        activity?.runOnUiThread {
            imageAnalysis.clearAnalyzer()
            cameraProvider.unbindAll()

            viewModel.setLoading(true)
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.setLoading(false)
                viewModel.getMachine(result).observe(viewLifecycleOwner) { data ->
                    if (data != null) {
                        try {
                            val bundle = bundleOf(INTENT_MACHINE to data.machine.id)
                            findNavControllerById(R.id.container)
                                .navigate(R.id.action_mainFragment_to_machineDetailFragment, bundle)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            bindCameraUseCases(cameraProvider)
                            showSnackBar("Data error")
                        }
                    } else {
                        bindCameraUseCases(cameraProvider)
                        showSnackBar("Machine Data doesn't match")
                    }
                }
            }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}