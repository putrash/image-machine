package co.saputra.imagemachine.arch.ui.main.image.detail

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import co.saputra.imagemachine.R
import co.saputra.imagemachine.arch.vm.MainViewModel
import co.saputra.imagemachine.base.BaseFragment
import co.saputra.imagemachine.databinding.FragmentImageDetailBinding
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImageDetailFragment : BaseFragment<FragmentImageDetailBinding, MainViewModel>(
    FragmentImageDetailBinding::inflate
) {
    override val viewModel: MainViewModel by viewModel()
    private lateinit var uriImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun initView(view: View, savedInstaceState: Bundle?) {
        uriImage = getImage()
        binding.apply {
            toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            Glide.with(requireContext())
                .load(Uri.parse(uriImage))
                .into(ivThumbnail)
        }
    }

    private fun getImage(): String {
        return arguments?.getString("uri_image") ?: ""
    }
}