package co.saputra.imagemachine.arch.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.FragmentNavigatorExtras
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
            if (clipData != null) { // If multiple images are selected
                val clipItem = clipData.itemCount
                if (clipItem <= 10) {
                    for (i in 0 until clipItem) {
                        val imageUri = clipData.getItemAt(i).uri
                        insertImage(imageUri)
                    }
                } else {
                    showSnackBar("Maximum choose the images is 10!")
                }
            } else if (data != null) { // If single item selected
                insertImage(data)
            }
        }
    }

    override fun initView(view: View, savedInstaceState: Bundle?) {
        id = getMachineId()
        binding.apply {
            toolbar.inflateMenu(R.menu.menu_detail)
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        val bundle = bundleOf(INTENT_MACHINE to id)
                        findNavController().navigate(R.id.action_machineDetailFragment_to_machineFormFragment, bundle)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete -> {
                        viewModel.deleteMachine(id)
                        findNavController().navigate(R.id.action_machineDetailFragment_to_mainFragment)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener true
                }
            }
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
                handleThumbnail(it.images)
            }
        }
    }

    private fun handleThumbnail(images: List<Image>) {
        binding.apply {
            if (images.isEmpty()) {
                tvImageMore.visibility = View.INVISIBLE
                tvHelper.visibility = View.VISIBLE
                tvHelper.text = "Add the thumbnail"
                ivThumbnail.setOnClickListener {
                    openGalleryForImages()
                }
            } else {
                Glide.with(requireContext())
                    .load(Uri.parse(images[0].path))
                    .into(ivThumbnail)
                tvImageMore.text = "+${images.size}"
                tvHelper.text = "See More"

                if (images.size == 1)  {
                    tvImageMore.visibility = View.GONE
                    tvHelper.visibility = View.GONE
                    ivThumbnail.setOnClickListener {
                        val bundle = bundleOf("uri_image" to images[0].path)
                        val extras = FragmentNavigatorExtras(
                            ivThumbnail to "enlargeImage")
                        findNavController()
                            .navigate(R.id.action_machineDetailFragment_to_imageDetailFragment,
                            bundle, null, extras)
                    }
                } else {
                    tvImageMore.visibility = View.VISIBLE
                    tvHelper.visibility = View.VISIBLE
                    ivThumbnail.setOnClickListener {
                        val bundle = bundleOf(INTENT_MACHINE to id)
                        findNavController().navigate(R.id.action_machineDetailFragment_to_imageListFragment, bundle)
                    }
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

    private fun getMachineId(): Long {
        return arguments?.getLong(INTENT_MACHINE) ?: 0
    }
}