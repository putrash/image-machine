package co.saputra.imagemachine.arch.ui.main.machine.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import co.saputra.imagemachine.Constants.INTENT_MACHINE
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.data.entity.Image
import co.saputra.imagemachine.databinding.FragmentMachineDetailBinding
import co.saputra.imagemachine.util.formatDate
import co.saputra.imagemachine.util.getFileName
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MachineDetailFragment : BaseFragment<FragmentMachineDetailBinding, MainViewModel>(
    FragmentMachineDetailBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()
    private var id: Long = 0

    private val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK || result.resultCode != Activity.RESULT_CANCELED) {
            val clipData = result.data?.clipData
            val data = result.data?.data
            // If multiple images are selected
            if (clipData != null) {
                val clipItem = clipData.itemCount
                if (clipItem > 10) {
                    showSnackBar("Maximum choose the images is 10!")
                } else {
                    for (i in 0 until clipItem) {
                        val imageUri = clipData.getItemAt(i).uri
                        insertImage(imageUri)
                    }
                }
            // If single item selected
            } else if (data != null) {
                insertImage(data)
            }
        }
    }

    private fun insertImage(uri: Uri) {
        requireContext().contentResolver
            .takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        viewModel.insertImage(
            Image(
                machineId = id,
                name = uri.getFileName(requireContext()),
                path = uri.toString(),
            )
        )
    }

    override fun initView(view: View, savedInstaceState: Bundle?) {
        id = getMachineId()
        binding.apply {
            toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_white)
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.getMachine(id).observe(viewLifecycleOwner) {
            binding.apply {
                tvTitle.text = it.machine.name
                tvMachineType.text = it.machine.type
                tvMachineCode.text = it.machine.code
                tvDate.text = Date(it.machine.maintenanceDate).formatDate()
                if (it.images.isEmpty()) {
                    tvHelper.text = "Add the thumbnail"
                } else {
                    Glide.with(requireContext())
                        .load(Uri.parse(it.images[0].path))
                        .into(ivThumbnail)
                    tvImageMore.text = "+${it.images.size}"
                    tvHelper.text = "See More"
                }
                ivThumbnail.setOnClickListener {
                    openGalleryForImages()
                }
            }
        }
    }

    private fun openGalleryForImages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        galleryResultLauncher.launch(intent)
    }

    private fun getMachineId(): Long {
        return arguments?.getLong(INTENT_MACHINE) ?: 0
    }
}