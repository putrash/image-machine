package co.saputra.imagemachine.arch.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import co.saputra.imagemachine.base.BaseListAdapter
import co.saputra.imagemachine.base.BaseViewHolder
import co.saputra.imagemachine.data.entity.MachineWithImages
import co.saputra.imagemachine.databinding.ItemMachineBinding
import com.bumptech.glide.RequestManager

class MachineAdapter(
    layoutInflater: LayoutInflater,
    private val glide: RequestManager,
    private val onClickListener : (MachineWithImages) -> Unit
) : BaseListAdapter<MachineWithImages, ItemMachineBinding, MachineAdapter.ViewHolder>(
    layoutInflater,
    ItemMachineBinding::inflate,
    MachineDiffCallback
) {

    override fun itemViewHolder(viewBinding: ItemMachineBinding, viewType: Int): ViewHolder {
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMachineBinding) : BaseViewHolder<MachineWithImages>(binding.root) {
        override fun onBind(item: MachineWithImages) {
            binding.apply {
                root.setOnClickListener {
                    onClickListener(item)
                }
                tvName.text = item.machine.name
                tvType.text = item.machine.type

                if (item.images.isNotEmpty()) {
                    glide.load(Uri.parse(item.images[0].path)).into(ivThumbnail)
                }
            }
        }
    }

    object MachineDiffCallback : DiffUtil.ItemCallback<MachineWithImages>() {
        override fun areItemsTheSame(oldItem: MachineWithImages, newItem: MachineWithImages): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MachineWithImages, newItem: MachineWithImages): Boolean {
            return oldItem.machine.id == newItem.machine.id

        }

    }
}