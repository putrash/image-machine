package co.saputra.imagemachine.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MachineWithImages(
    @Embedded
    val machine: Machine,
    @Relation(
        parentColumn = "id",
        entityColumn = "machine_id"
    )
    val images: List<Image>
)
