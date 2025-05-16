package org.dallas.smartshelf.di

import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

// Domain layer
val domainModule = module {
//    factory { GetUserDataUseCase(get()) }
//    factory { UpdateProfileUseCase(get()) }
    // Add other use cases
}

// Data layer
val dataModule = module {
//    single { UserRepository(get(), get()) }
//    single { PreferencesRepository(get()) }
    // Add other repositories
}

// Network layer
val networkModule = module {
//    single { NetworkClient() }
//    single { ApiService(get()) }
    // Add other network components
}

// ViewModels
val viewModelModule = module {
//    viewModel { MainViewModel(get(), get()) }
//    viewModel { ProfileViewModel(get(), get()) }
    // Add other ViewModels
}

fun initKoin() {
    startKoin {
        modules(appModules)
    }
}

// Merge all modules
val appModules = listOf(domainModule, dataModule, networkModule, viewModelModule)