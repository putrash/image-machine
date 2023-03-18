package co.saputra.imagemachine.di

import androidx.room.Room
import co.saputra.imagemachine.Constants.DB_NAME
import co.saputra.imagemachine.data.LocalDatabase
import co.saputra.imagemachine.data.source.ImageLocalSource
import co.saputra.imagemachine.data.source.MachineLocalSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), LocalDatabase::class.java, DB_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<LocalDatabase>().imageDao() }
    single { get<LocalDatabase>().machineDao() }
}

val localDataSourceModule = module {
    single { MachineLocalSource(get()) }
    single { ImageLocalSource(get()) }
}