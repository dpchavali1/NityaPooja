import SwiftUI
import Shared
import GoogleMobileAds
import StoreKit
import UserNotifications

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        KoinHelperKt.doInitKoinIos()
        // Set max ad content rating to G (General audiences — no adult content)
        MobileAds.shared.requestConfiguration.maxAdContentRating = .general
        MobileAds.shared.start()
        requestNotificationPermission()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onAppear {
                    maybeRequestReview()
                }
        }
    }

    private func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if let error = error {
                print("Notification permission error: \(error)")
            }
        }
    }

    private func maybeRequestReview() {
        let defaults = UserDefaults.standard
        let launchCount = defaults.integer(forKey: "app_launch_count") + 1
        defaults.set(launchCount, forKey: "app_launch_count")

        // Fallback for users who never use Japa; engagement-driven prompts fire via KMP
        if launchCount == 10 || (launchCount > 10 && launchCount % 50 == 0) {
            if let scene = UIApplication.shared.connectedScenes
                .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene {
                SKStoreReviewController.requestReview(in: scene)
            }
        }
    }
}

// AppDelegate to handle notification taps and extract deep link route
class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        return true
    }

    // Handle notification tap when app is in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .sound])
    }

    // Handle notification tap — extract nav_route and post to NotificationCenter
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        if let route = userInfo["nav_route"] as? String {
            NotificationCenter.default.post(name: .deepLinkNotification, object: nil, userInfo: ["route": route])
        }
        completionHandler()
    }
}

extension Notification.Name {
    static let deepLinkNotification = Notification.Name("deepLinkNotification")
}
