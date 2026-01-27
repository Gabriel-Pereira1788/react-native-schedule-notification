import { NitroModules } from 'react-native-nitro-modules'
import type { ScheduledNotifications as ScheduledNotificationsSpec } from './specs/scheduled-notifications.nitro'
import type {
  NotificationProps,
  DailyScheduleDate,
  WeeklyScheduleDate,
  MonthlyScheduleDate,
} from './types'

export const ScheduledNotifications =
  NitroModules.createHybridObject<ScheduledNotificationsSpec>(
    'ScheduledNotifications'
  )

export function schedule(
  frequency: 'daily',
  notification: NotificationProps,
  scheduleDate: DailyScheduleDate
): void

export function schedule(
  frequency: 'weekly',
  notification: NotificationProps,
  scheduleDate: WeeklyScheduleDate
): void

export function schedule(
  frequency: 'monthly',
  notification: NotificationProps,
  scheduleDate: MonthlyScheduleDate
): void

export function schedule(
  frequency: 'daily' | 'weekly' | 'monthly',
  notification: NotificationProps,
  scheduleDate: DailyScheduleDate | WeeklyScheduleDate | MonthlyScheduleDate
): void {
  const { dayOfMonth = 1, dayOfWeek = 1, hour, minute } = scheduleDate as any

  ScheduledNotifications.schedule(
    notification.id,
    notification.title,
    notification.message,
    frequency,
    {
      hour,
      minute,
      dayOfMonth,
      dayOfWeek,
    }
  )
}

export function cancel(id: string) {
  ScheduledNotifications.cancel(id)
}

export function addListener(callback: (id: string) => void) {
  const id = ScheduledNotifications.addListener(callback)
  return () => ScheduledNotifications.removeListener(id)
}

export async function getPendingNotifications(): Promise<string[]> {
  return await ScheduledNotifications.getPendingNotifications()
}

export default {
  schedule,
  addListener,
  cancel,
  getPendingNotifications,
}
