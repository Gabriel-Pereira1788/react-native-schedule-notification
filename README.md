# React Native Scheduled Notifications

A powerful and efficient React Native library for scheduling recurring local notifications on iOS and Android, built with [Nitro Modules](https://github.com/mrousavy/react-native-nitro-modules) for maximum performance.

[![Version](https://img.shields.io/npm/v/react-native-schedule-notification.svg)](https://www.npmjs.com/package/react-native-schedule-notification)
[![Downloads](https://img.shields.io/npm/dm/react-native-schedule-notification.svg)](https://www.npmjs.com/package/react-native-schedule-notification)
[![License](https://img.shields.io/npm/l/react-native-schedule-notification.svg)](https://www.npmjs.com/package/react-native-schedule-notification/LICENSE)

## 📋 Table of Contents

- [Features](#-features)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Setup](#-setup)
  - [iOS](#ios)
  - [Android](#android)
- [Usage](#-usage)
  - [Import](#import)
  - [Schedule Notifications](#schedule-notifications)
  - [Cancel Notifications](#cancel-notifications)
  - [Listen to Events](#listen-to-events)
- [API Reference](#-api-reference)
- [Examples](#-examples)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

- 📅 **Flexible Scheduling**: Support for daily, weekly, and monthly notifications
- ⚡ **High Performance**: Built with Nitro Modules for optimized native communication
- 🔔 **Recurring Notifications**: Set up notifications that repeat automatically
- 📱 **Cross-Platform**: Full support for both iOS and Android
- 🎯 **Click Events**: Capture when users interact with notifications
- 🚫 **Easy Cancellation**: Cancel scheduled notifications at any time
- 🛠️ **TypeScript**: Fully typed for better development experience
- 🔐 **Permission Handling**: Automatic permission management with fallbacks

## 📱 Requirements

- React Native v0.76.0 or higher
- Node.js v18.0.0 or higher
- iOS 13.0 or higher
- Android 6.0 (API Level 23) or higher

> [!IMPORTANT]  
> To support `Nitro Views` you need to install React Native version v0.78.0 or higher.

## 🚀 Installation

```bash
npm install react-native-schedule-notification react-native-nitro-modules
```

or using Yarn:

```bash
yarn add react-native-schedule-notification react-native-nitro-modules
```

### Pod Installation (iOS)

```bash
cd ios && pod install
```

## ⚙️ Setup

### iOS

#### Notification Permissions

The library automatically requests permissions when you schedule the first notification. However, you can customize the permission prompt by adding to your `Info.plist`:

```xml
<key>NSUserNotificationAlertStyle</key>
<string>alert</string>
```

#### Project Capabilities

1. Open your project in Xcode
2. Select your target
3. Go to "Signing & Capabilities"
4. Add "Push Notifications" capability (if not already present)

### Android

#### Permissions

The following permissions are automatically added by the library:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
```

#### AndroidManifest.xml Configuration

The library automatically registers the necessary receivers and activities. No additional configuration is required.

#### Android 13+ (API Level 33)

For Android 13 and above, the `POST_NOTIFICATIONS` permission needs to be requested at runtime. You can use libraries like `react-native-permissions` to handle this:

```javascript
import { request, PERMISSIONS, RESULTS } from 'react-native-permissions';

const requestNotificationPermission = async () => {
  const result = await request(PERMISSIONS.ANDROID.POST_NOTIFICATIONS);
  return result === RESULTS.GRANTED;
};
```

## 💻 Usage

### Import

```javascript
import ScheduledNotifications from 'react-native-schedule-notification';
```

or with named imports:

```javascript
import { schedule, cancel, addListener } from 'react-native-schedule-notification';
```

### Schedule Notifications

#### Daily Notification

```javascript
// Schedule a daily notification at 9:00 AM
ScheduledNotifications.schedule(
  'daily',
  {
    id: 'daily-reminder',
    title: 'Daily Reminder',
    message: "Don't forget to check your tasks!",
  },
  {
    hour: 9,
    minute: 0,
  }
);
```

#### Weekly Notification

```javascript
// Schedule a notification every Monday at 10:30 AM
ScheduledNotifications.schedule(
  'weekly',
  {
    id: 'weekly-meeting',
    title: 'Weekly Meeting',
    message: 'Your weekly meeting starts in 30 minutes',
  },
  {
    hour: 10,
    minute: 30,
    dayOfWeek: 2, // 1 = Sunday, 2 = Monday, ... 7 = Saturday
  }
);
```

#### Monthly Notification

```javascript
// Schedule a notification on the 15th of every month at 2:00 PM
ScheduledNotifications.schedule(
  'monthly',
  {
    id: 'monthly-report',
    title: 'Monthly Report',
    message: 'Time to submit your monthly report',
  },
  {
    hour: 14,
    minute: 0,
    dayOfMonth: 15,
  }
);
```

### Cancel Notifications

```javascript
// Cancel a specific notification
ScheduledNotifications.cancel('daily-reminder');
```

### Listen to Events

```javascript
import { useEffect } from 'react';

function App() {
  useEffect(() => {
    // Add listener for notification clicks
    const unsubscribe = ScheduledNotifications.addListener((notificationId) => {
      console.log('Notification clicked:', notificationId);
      
      // Handle actions based on notification ID
      switch(notificationId) {
        case 'daily-reminder':
          // Navigate to tasks screen
          break;
        case 'weekly-meeting':
          // Open calendar
          break;
      }
    });

    // Cleanup - remove listener
    return () => unsubscribe();
  }, []);
}
```

## 📖 API Reference

### Types

#### NotificationProps

```typescript
interface NotificationProps {
  id: string;           // Unique notification identifier
  title: string;        // Notification title
  message: string;      // Notification body/message
}
```

#### DailyScheduleDate

```typescript
interface DailyScheduleDate {
  hour: number;         // Hour (0-23)
  minute: number;       // Minute (0-59)
}
```

#### WeeklyScheduleDate

```typescript
interface WeeklyScheduleDate {
  hour: number;         // Hour (0-23)
  minute: number;       // Minute (0-59)
  dayOfWeek: number;    // Day of week (1=Sun, 2=Mon, ..., 7=Sat)
}
```

#### MonthlyScheduleDate

```typescript
interface MonthlyScheduleDate {
  hour: number;         // Hour (0-23)
  minute: number;       // Minute (0-59)
  dayOfMonth: number;   // Day of month (1-31)
}
```

### Methods

#### `schedule(frequency, notification, scheduleDate)`

Schedules a recurring notification.

**Parameters:**
- `frequency`: `'daily' | 'weekly' | 'monthly'` - Notification frequency
- `notification`: `NotificationProps` - Notification data
- `scheduleDate`: `DailyScheduleDate | WeeklyScheduleDate | MonthlyScheduleDate` - Schedule configuration

**Returns:** `void`

#### `cancel(id)`

Cancels a scheduled notification.

**Parameters:**
- `id`: `string` - ID of the notification to cancel

**Returns:** `void`

#### `addListener(callback)`

Adds a listener for notification click events.

**Parameters:**
- `callback`: `(id: string) => void` - Function called when a notification is clicked

**Returns:** `() => void` - Function to remove the listener

## 🎯 Examples

### Complete Example with Custom Hook

```javascript
// hooks/useNotifications.js
import { useEffect, useCallback } from 'react';
import ScheduledNotifications from 'react-native-schedule-notification';

export function useNotifications() {
  useEffect(() => {
    const unsubscribe = ScheduledNotifications.addListener((id) => {
      console.log('Notification interacted:', id);
    });

    return unsubscribe;
  }, []);

  const scheduleWaterReminder = useCallback(() => {
    // Water reminder every 2 hours during the day
    const hours = [9, 11, 13, 15, 17, 19];
    
    hours.forEach((hour, index) => {
      ScheduledNotifications.schedule(
        'daily',
        {
          id: `water-reminder-${index}`,
          title: '💧 Time to Drink Water',
          message: 'Stay hydrated! Drink a glass of water.',
        },
        {
          hour,
          minute: 0,
        }
      );
    });
  }, []);

  const scheduleMedicationReminder = useCallback((medication, time) => {
    ScheduledNotifications.schedule(
      'daily',
      {
        id: `medication-${medication.id}`,
        title: '💊 Medication Reminder',
        message: `Time to take ${medication.name}`,
      },
      {
        hour: time.hour,
        minute: time.minute,
      }
    );
  }, []);

  const cancelAllReminders = useCallback(() => {
    // Cancel all water reminders
    for (let i = 0; i < 6; i++) {
      ScheduledNotifications.cancel(`water-reminder-${i}`);
    }
  }, []);

  return {
    scheduleWaterReminder,
    scheduleMedicationReminder,
    cancelAllReminders,
  };
}
```

### Settings Screen Example

```javascript
import React, { useState } from 'react';
import {
  View,
  Text,
  Switch,
  Button,
  StyleSheet,
  ScrollView,
} from 'react-native';
import ScheduledNotifications from 'react-native-schedule-notification';

function NotificationSettings() {
  const [dailyEnabled, setDailyEnabled] = useState(false);
  const [weeklyEnabled, setWeeklyEnabled] = useState(false);

  const toggleDailyNotification = (enabled) => {
    setDailyEnabled(enabled);
    
    if (enabled) {
      ScheduledNotifications.schedule(
        'daily',
        {
          id: 'daily-summary',
          title: '📊 Daily Summary',
          message: 'Check your daily activity summary',
        },
        {
          hour: 20,
          minute: 0,
        }
      );
    } else {
      ScheduledNotifications.cancel('daily-summary');
    }
  };

  const toggleWeeklyNotification = (enabled) => {
    setWeeklyEnabled(enabled);
    
    if (enabled) {
      ScheduledNotifications.schedule(
        'weekly',
        {
          id: 'weekly-review',
          title: '📅 Weekly Review',
          message: 'Time to review your weekly goals',
        },
        {
          hour: 18,
          minute: 0,
          dayOfWeek: 1, // Sunday
        }
      );
    } else {
      ScheduledNotifications.cancel('weekly-review');
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Notification Settings</Text>
      
      <View style={styles.settingRow}>
        <Text style={styles.settingText}>Daily Summary</Text>
        <Switch
          value={dailyEnabled}
          onValueChange={toggleDailyNotification}
        />
      </View>
      
      <View style={styles.settingRow}>
        <Text style={styles.settingText}>Weekly Review</Text>
        <Switch
          value={weeklyEnabled}
          onValueChange={toggleWeeklyNotification}
        />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  settingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  settingText: {
    fontSize: 16,
  },
});

export default NotificationSettings;
```

## 🐛 Troubleshooting

### iOS Issues

#### Notifications not showing
- Ensure you've added the Push Notifications capability in Xcode
- Check that notification permissions are granted in device settings
- Verify that the app is in background or closed (notifications don't show when app is in foreground by default)

#### Build errors
- Run `cd ios && pod install` after installation
- Clean build folder in Xcode (Shift+Cmd+K)
- Delete `ios/Pods` and `ios/Podfile.lock`, then run `pod install` again

### Android Issues

#### Notifications not working on Android 13+
- Ensure you're requesting `POST_NOTIFICATIONS` permission at runtime
- Check that the permission is granted in device settings

#### Exact alarm permission issues
- On Android 12+, users need to grant exact alarm permission
- Guide users to Settings > Apps > Your App > Alarms & reminders

#### Build errors
- Clean gradle cache: `cd android && ./gradlew clean`
- Ensure `compileSdkVersion` is 33 or higher for Android 13 support

### General Issues

#### Notification click events not firing
- Ensure you've set up the listener before the notification is clicked
- Check that the app is properly configured to handle background events
- On iOS, ensure the app delegate is properly set up

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Credits

- Built with [Nitro Modules](https://github.com/mrousavy/react-native-nitro-modules) by Marc Rousavy
- Bootstrapped with [create-nitro-module](https://github.com/patrickkabwe/create-nitro-module)

## 📧 Support

For bugs and feature requests, please [open an issue](https://github.com/Gabriel-Pereira1788/react-native-schedule-notification/issues).

For questions and support, please use the [Discussions](https://github.com/Gabriel-Pereira1788/react-native-schedule-notification/discussions) section.

---

Made with ❤️ by [Gabriel Pereira](https://github.com/Gabriel-Pereira1788)
