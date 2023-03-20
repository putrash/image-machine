package co.saputra.imagemachine.arch.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import co.saputra.imagemachine.base.BaseListAdapter
import co.saputra.imagemachine.base.BaseViewHolder
import co.saputra.imagemachine.data.entity.Image
import co.saputra.imagemachine.databinding.ItemImageBinding
import co.saputra.imagemachine.util.setSafeClickListener
import com.bumptech.glide.RequestManager
import com.google.android.material.imageview.ShapeableImageView

class ImageAdapter(
    layoutInflater: LayoutInflater,
    private val glide: RequestManager,
    private val onItemClick: (Image, ImageView) -> Unit,
) : BaseListAdapter<Image, ItemImageBinding, ImageAdapter.ViewHolder>(
    layoutInflater,
    ItemImageBinding::inflate,
    ImageDiffCallback
) {

    override fun itemViewHolder(viewBinding: ItemImageBinding, viewType: Int): ViewHolder {
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemImageBinding) : BaseViewHolder<Image>(binding.root) {
        override fun onBind(item: Image) {
            binding.root.setSafeClickListener {
                onItemClick(item, binding.ivThumbnail)
            }
            glide.load(item.path).into(binding.ivThumbnail)
        }
    }

    object ImageDiffCallback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.path == newItem.path
        }

    }
}