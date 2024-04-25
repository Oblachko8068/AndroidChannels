package com.example.di.di.hiltModules

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val GOOGLE_TOKEN = "272775250621-523c05jdaabblc837u7qh92tmvd220vu.apps.googleusercontent.com"

@Module
@InstallIn(SingletonComponent::class)
class AuthorizationModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = Firebase.auth
        FirebaseAuth.getInstance().firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
        auth.setLanguageCode("ru")
        return auth
    }

    @Provides
    @Singleton
    fun provideGoogleSignIn(@ApplicationContext appContext: Context): GoogleSignInClient {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_TOKEN)
                .requestEmail()
                .build()
        return GoogleSignIn.getClient(appContext, googleSignInOptions)
    }
}