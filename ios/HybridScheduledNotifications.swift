
import Foundation
import NotificationCenter

class HybridScheduledNotifications: HybridScheduledNotificationsSpec {
    private var listeners : [String: ((String) -> Void)] = [:]
    private var rnScheduleNotificationTracker: RNScheduleNotificationTracker?

    override init() {
        super.init()
        rnScheduleNotificationTracker = RNScheduleNotificationTracker(owner: self)
        rnScheduleNotificationTracker?.observer()
    }

    func schedule(id: String, title: String, message: String, frequency: ScheduleFrequency, scheduleDate: ScheduleDateBase) throws {

        let center = UNUserNotificationCenter.current()
        center.getNotificationSettings { settings in
            switch settings.authorizationStatus {
            case .authorized:
                self.createNotification(id:id,title:title,message:message,frequency: frequency,scheduleDate: scheduleDate)

            case .notDetermined:

                center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
                    if granted {
                        self.createNotification(id:id,title:title,message:message,frequency: frequency,scheduleDate: scheduleDate)
                    } else {
                        print("🚫 Permission denied")
                    }
                }

            case .denied:
                print("⚠️ Notifications denied.")
            default:
                break
            }
        }
    }

    private func createNotification(id:String, title:String, message:String,frequency:ScheduleFrequency,
                                    scheduleDate:ScheduleDateBase) {

        removePendingNotification(id: id)
        let content = UNMutableNotificationContent()
        content.userInfo["id"] = id
        content.title = title
        content.body = message
        content.sound = .default
        content.userInfo[Constants.NOTIFICATION_SCHEDULE_IDENTIFIER] = Constants.NOTIFICATION_SCHEDULE_IDENTIFIER

        let date = DateBuilder.build(frequency: frequency, scheduleDate: scheduleDate)

        let trigger = UNCalendarNotificationTrigger(dateMatching: date, repeats: true)

        let request = UNNotificationRequest(
            identifier: id, content: content, trigger: trigger
        )

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error on schedule notification: \(error.localizedDescription)")
            } else {
                print("⏱️ Notification schedule with success!")
            }
        }
    }

    func handleNotificationPress(id:String) {
        listeners.forEach { $0.value(id)}
    }

    func addListener(callback:@escaping (String) -> Void) -> String {
        let identifier = UUID().uuidString
        listeners[identifier] = callback
        return identifier
    }

    func removeListener(id identifier: String){
        listeners.removeValue(forKey: identifier)
    }

    func removePendingNotification(id:String) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [id])
    }

    func cancel(id:String){
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [id])
    }
}



class DateBuilder {
    static func build(frequency:ScheduleFrequency, scheduleDate:ScheduleDateBase) -> DateComponents{
        switch frequency {
        case .daily:
            var date = DateComponents()
            date.hour = Int(scheduleDate.hour)
            date.minute = Int(scheduleDate.minute)
            return date
        case .weekly:
            var date = DateComponents()
            date.hour = Int(scheduleDate.hour)
            date.minute = Int(scheduleDate.minute)
            date.weekday = Int(scheduleDate.dayOfWeek!)
            return date
        case .monthly:
            var date = DateComponents()
            date.hour = Int(scheduleDate.hour)
            date.minute = Int(scheduleDate.minute)
            date.month = Int(scheduleDate.dayOfMonth!)
            return date
        }
    }
}
