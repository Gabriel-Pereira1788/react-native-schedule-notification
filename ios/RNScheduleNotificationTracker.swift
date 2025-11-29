class RNScheduleNotificationTracker: NSObject, UNUserNotificationCenterDelegate {
    private var owner:HybridScheduledNotifications
    private weak var originalDelegate: UNUserNotificationCenterDelegate?
    private static var isConfigured = false
    
    init(owner: HybridScheduledNotifications) {
        self.owner = owner
    }
    
    func observer() {
        guard !Self.isConfigured else { return }
        Self.isConfigured = true
        
        let center = UNUserNotificationCenter.current()
        
        if let delegate = center.delegate {
            originalDelegate = delegate
        }
        
        center.delegate = self
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        if(response.notification.request.content.userInfo[Constants.NOTIFICATION_SCHEDULE_IDENTIFIER] != nil) {
            
            let id = response.notification.request.content.userInfo["id"] as! String
            self.owner.handleNotificationPress(id: id)    
        }
        
        if let original = originalDelegate,
           original.responds(to: #selector(userNotificationCenter(_:didReceive:withCompletionHandler:))) {
            original.userNotificationCenter?(center, didReceive: response, withCompletionHandler: completionHandler)
        } else {
            completionHandler()
        }
    }
}
