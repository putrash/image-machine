package co.saputra.imagemachine.data.source

import co.saputra.imagemachine.data.dao.ImageDao
import co.saputra.imagemachine.data.entity.Image

class ImageLocalSource(private val imageDao: ImageDao) {
    fun insert(image: Image) = imageDao.insert(image)

    fun update(image: Image) = imageDao.update(image)

    fun delete(id: Long) = imageDao.delete(id)
}