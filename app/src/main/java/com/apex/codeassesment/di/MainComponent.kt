package com.apex.codeassesment.di

import android.content.Context
import com.apex.codeassesment.ui.main.JetpackComposeActivity
import com.apex.codeassesment.ui.main.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class, NetworkModule::class, RepositoryModule::class])
interface MainComponent {

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance applicationContext: Context): MainComponent
  }

  interface Injector {
    val mainComponent: MainComponent
  }

  fun inject(mainActivity: MainActivity)
  fun inject(jetpackComposeActivity: JetpackComposeActivity)
}