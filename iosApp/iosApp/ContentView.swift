import SwiftUI
import Shared
import GoogleMobileAds

struct ContentView: View {
    @State private var deepLinkRoute: String? = nil

    var body: some View {
        ComposeView(deepLinkRoute: deepLinkRoute)
            .ignoresSafeArea(.all)
            .onReceive(NotificationCenter.default.publisher(for: .deepLinkNotification)) { notification in
                if let route = notification.userInfo?["route"] as? String {
                    deepLinkRoute = route
                    // Reset after a short delay so re-tapping same notification type works
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                        deepLinkRoute = nil
                    }
                }
            }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    var deepLinkRoute: String?

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            bannerAdViewFactory: { BannerAdHelper.createBannerView() },
            deepLinkRoute: deepLinkRoute
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

enum BannerAdHelper {
    // AdMob banner ad unit ID — uses test ID in debug, real ID in release
    #if DEBUG
    private static let adUnitID = "ca-app-pub-3940256099942544/2934735716"
    #else
    private static let adUnitID = "ca-app-pub-4962910048695842/3520646134"
    #endif

    static func createBannerView() -> UIView {
        let bannerView = BannerView()
        bannerView.adUnitID = adUnitID
        bannerView.translatesAutoresizingMaskIntoConstraints = false

        // Find the root view controller to set as rootViewController
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootVC = windowScene.windows.first?.rootViewController {
            bannerView.rootViewController = rootVC
        }

        bannerView.load(Request())
        return bannerView
    }
}
