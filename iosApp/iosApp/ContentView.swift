import SwiftUI
import Shared
import GoogleMobileAds

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(bannerAdViewFactory: {
            BannerAdHelper.createBannerView()
        })
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

enum BannerAdHelper {
    // Test banner ad unit ID — replace with production ID before release
    private static let adUnitID = "ca-app-pub-3940256099942544/2934735716"

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
