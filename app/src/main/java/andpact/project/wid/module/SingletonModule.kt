package andpact.project.wid.module

import andpact.project.wid.dataSource.*
import andpact.project.wid.repository.Repository
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // App 전체에서 하나만 생성되는 객체들(Singleton)
object SingletonModule {
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("WiDAuth", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideUserDataSource(): UserDataSource {
        return UserDataSource(repository = provideRepository())
    }

    @Singleton
    @Provides
    fun provideWiDDataSource(): WiDDataSource {
        return WiDDataSource(repository = provideRepository())
    }

    @Singleton
    @Provides
    fun provideRepository(): Repository {
        return Repository(
            auth = provideFirebaseAuth(),
            firestore = provideFirebaseFirestore()
        )
    }
}