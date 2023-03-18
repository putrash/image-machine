package co.saputra.imagemachine

import android.app.Application
import co.saputra.imagemachine.di.databaseModule
import co.saputra.imagemachine.di.localDataSourceModule
import co.saputra.imagemachine.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            androidFileProperties()
            modules(viewModelModule, databaseModule, localDataSourceModule)
        }
    }
}