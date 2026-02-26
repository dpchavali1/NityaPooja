import SwiftUI
import Shared
import GoogleMobileAds

@main
struct iOSApp: App {
    init() {
        KoinHelperKt.doInitKoinIos()
        // Set max ad content rating to G (General audiences — no adult content)
        MobileAds.shared.requestConfiguration.maxAdContentRating = .general
        MobileAds.shared.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
