package org.dallas.smartshelf.di

import cafe.adriel.voyager.navigator.Navigator
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.dallas.smartshelf.manager.CapturedDataManager
import org.dallas.smartshelf.manager.FirebaseAuthManager
import org.dallas.smartshelf.manager.LocaleManager
import org.dallas.smartshelf.manager.SharedPreferencesManager
import org.dallas.smartshelf.manager.SharedPreferencesManagerFactory
import org.dallas.smartshelf.manager.UserManager
import org.dallas.smartshelf.repository.AuthenticationRepository
import org.dallas.smartshelf.repository.ProductRepository
import org.dallas.smartshelf.repository.ProductRepositoryImpl
import org.dallas.smartshelf.util.BarcodeScannerManager
import org.dallas.smartshelf.viewmodel.CapturedDataViewModel
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
    single { AuthenticationRepository(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
}

val networkModule = module {
    single<FirebaseFirestore> { Firebase.firestore }
}

val authModule = module {
    single<FirebaseAuth> { Firebase.auth }
    single { FirebaseAuthManager(get()) }
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