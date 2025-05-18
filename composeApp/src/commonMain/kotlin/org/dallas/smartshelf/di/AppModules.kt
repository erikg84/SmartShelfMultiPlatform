package org.dallas.smartshelf.di

import cafe.adriel.voyager.navigator.Navigator
import org.dallas.smartshelf.manager.CapturedDataManager
import org.dallas.smartshelf.manager.LocaleManager
import org.dallas.smartshelf.manager.SharedPreferencesManager
import org.dallas.smartshelf.manager.SharedPreferencesManagerFactory
import org.dallas.smartshelf.manager.UserManager
import org.dallas.smartshelf.repository.AuthenticationRepository
import org.dallas.smartshelf.repository.ProductRepository
import org.dallas.smartshelf.repository.ProductRepositoryImpl
import org.dallas.smartshelf.util.BarcodeScannerManager
import org.dallas.smartshelf.util.HttpClientFactory
import org.dallas.smartshelf.viewmodel.BarcodeScanningViewModel
import org.dallas.smartshelf.viewmodel.CapturedDataViewModel
import org.dallas.smartshelf.viewmodel.DashboardViewModel
import org.dallas.smartshelf.viewmodel.DeleteAccountViewModel
import org.dallas.smartshelf.viewmodel.ForgotPasswordViewModel
import org.dallas.smartshelf.viewmodel.HomeViewModel
import org.dallas.smartshelf.viewmodel.OnboardingViewModel
import org.dallas.smartshelf.viewmodel.ReceiptScanningViewModel
import org.dallas.smartshelf.viewmodel.SettingsViewModel
import org.dallas.smartshelf.viewmodel.SignupViewModel
import org.dallas.smartshelf.viewmodel.SplashViewModel
import org.dallas.smartshelf.viewmodel.StockViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

}

val managerModule = module {
    single { UserManager(get(), get()) }
    single { CapturedDataManager() }
}

val dataModule = module {

    single<ProductRepository> { ProductRepositoryImpl(get(), get(), get()) }
}

val networkModule = module {
    single { HttpClientFactory.create(get()) }
}

val authModule = module {
    single { AuthenticationRepository(get()) }
}

val viewModelModule = module {
    factoryOf(::CapturedDataViewModel)
}

val utilModule = module {
    single<SharedPreferencesManager> { SharedPreferencesManagerFactory.createSharedPreferencesManager() }
    single { LocaleManager(get()) }
    single { BarcodeScannerManager() }
}

val screenModelModule = module {
    factory { (navigator: Navigator) ->
        CapturedDataViewModel(
            capturedDataManager = get(),
            productRepository = get(),
            authManager = get(),
            navigator = navigator
        )
    }

    factory { (navigator: Navigator) ->
        SignupViewModel(
            navigator = navigator,
            userManager = get(),
            authRepository = get(),
            sharedPreferencesManager = get()
        )
    }

    factory { (navigator: Navigator) ->
        ForgotPasswordViewModel(
            navigator = navigator,
            authRepository = get()
        )
    }

    factory { (navigator: Navigator) ->
        DeleteAccountViewModel(
            navigator = navigator,
            authRepository = get(),
            sharedPreferencesManager = get()
        )
    }

    // New ViewModels we created
    factory { (navigator: Navigator) ->
        BarcodeScanningViewModel(
            navigator = navigator,
            capturedDataManager = get()
        )
    }

    factory { (navigator: Navigator) ->
        DashboardViewModel(
            navigator = navigator
        )
    }

    factory { (navigator: Navigator) ->
        HomeViewModel(
            navigator = navigator
        )
    }

    factory { (navigator: Navigator) ->
        OnboardingViewModel(
            navigator = navigator,
            sharedPreferencesManager = get()
        )
    }

    factory { (navigator: Navigator) ->
        ReceiptScanningViewModel(
            navigator = navigator,
            capturedDataManager = get()
        )
    }

    factory { (navigator: Navigator) ->
        SettingsViewModel(
            navigator = navigator,
            localeManager = get()
        )
    }

    factory { (navigator: Navigator) ->
        SplashViewModel(
            navigator = navigator
        )
    }

    // This one doesn't need the navigator
    factory {
        StockViewModel(
            productRepository = get()
        )
    }
}

fun initKoin() {
    startKoin {
        modules(appModules)
    }
}

val appModules = listOf(
    domainModule,
    dataModule,
    networkModule,
    viewModelModule,
    authModule,
    managerModule,
    screenModelModule,
    utilModule
)