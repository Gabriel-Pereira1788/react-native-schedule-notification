import { type HybridObject } from 'react-native-nitro-modules'
import type { ScheduleDateBase, ScheduleFrequency } from '../types'

export interface ScheduledNotifications
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  schedule(
    id: string,
    title: string,
    message: string,
    frequency: ScheduleFrequency,
    scheduleDate: ScheduleDateBase
  ): void
  addListener(callback: (notificationId: string) => void): string
  removeListener(id: string): void
  cancel(id: string): void
  getPendingNotifications(): Promise<string[]>
}
