package com.r.dosc.di.modules

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.itextpdf.text.Document
import com.r.dosc.domain.util.DocumentEssential
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@InstallIn(SingletonComponent::class)
@Module
object ScanningModule {

    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Singleton
    @Provides
    @Named(value = "temp")
    fun provideOutputTempDirectory(@ApplicationContext appContext: Context): File {
        return appContext.getExternalFilesDir("").let {
            File(it, "temp").apply { mkdirs() }
        }
    }

    @Singleton
    @Provides
    @Named(value = "dosc")
    fun provideOutputMainDirectory(@ApplicationContext appContext: Context): File {
        return appContext.getExternalFilesDir("").let {
            File(it, "dosc").apply { mkdirs() }
        }
    }

    @Singleton
    @Provides
    fun provideCameraExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }


    @Singleton
    @Provides
    fun provideCam(@ApplicationContext appContext: Context): CamX =
        CampImp(appContext)


    @Singleton
    @Provides
    fun provideScanningEssential(
        @ApplicationContext appContext: Context,
        @Named(value = "temp") file: File,
    ): DocumentEssential =
        DocumentEssential(appContext, file)

}

@InstallIn(ViewModelComponent::class)
@Module
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideDocument(): Document = Document()

}

class CampImp(private val appContext: Context) : CamX {

    override suspend fun getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { cont ->
            ProcessCameraProvider.getInstance(appContext).also { cameraPr ->
                cameraPr.addListener({
                    cont.resume(cameraPr.get())
                }, ContextCompat.getMainExecutor(appContext))

            }
        }
}

interface CamX {
    suspend fun getCameraProvider(): ProcessCameraProvider
}

