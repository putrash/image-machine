package co.saputra.imagemachine.arch.ui.main.image

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import co.saputra.imagemachine.Constants.INTENT_MACHINE
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.adapter.ImageAdapter
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.data.entity.Image
import co.saputra.imagemachine.databinding.FragmentImageListBinding
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImageListFragment : BaseFragment<FragmentImageListBinding, MainViewModel>(
    FragmentImageListBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()
    private val adapter by lazy { ImageAdapter(layoutInflater, Glide.with(requireContext()), ::onItemClick) }
    private var id: Long = 0L

    override fun initView(view: View, savedInstaceState: Bundle?) {
        id = getMachineId()
        binding.apply {
            toolbar.title = "Machine Images"
            toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            rvImages.adapter = adapter
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.getMachine(id).observe(viewLifecycleOwner) {
            adapter.submitList(it.images)
        }
    }

    private fun onItemClick(image: Image, view: ImageView) {
        val bundle = bundleOf("uri_image" to image.path)
        val extras = FragmentNavigatorExtras(
            view to "enlargeImage")
        findNavController()
            .navigate(R.id.action_imageListFragment_to_imageDetailFragment,
                bundle, null, extras)
    }

    private fun getMachineId(): Long {
        return arguments?.getLong(INTENT_MACHINE) ?: 0L
    }

}