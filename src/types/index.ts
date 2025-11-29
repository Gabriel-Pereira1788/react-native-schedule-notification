export interface NotificationProps {
  id: string
  title: string
  message: string
  frequency?: ScheduleFrequency
}

export interface ScheduleDateBase {
  hour: number
  minute: number
  dayOfWeek?: number
  dayOfMonth?: number
}

export interface ScheduleDateConstraint {
  hour: number
  minute: number
}

export interface DailyScheduleDate extends ScheduleDateConstraint {
  dayOfWeek?: number
  dayOfMonth?: number
}

export interface WeeklyScheduleDate extends ScheduleDateConstraint {
  dayOfWeek: number
  dayOfMonth?: number
}

export interface MonthlyScheduleDate extends ScheduleDateConstraint {
  dayOfWeek?: number
  dayOfMonth: number
}

export type ScheduleDate =
  | DailyScheduleDate
  | WeeklyScheduleDate
  | MonthlyScheduleDate

export type ScheduleFrequency = 'daily' | 'weekly' | 'monthly'

export type ScheduleDateForFrequency<T extends ScheduleFrequency> =
  T extends 'daily'
    ? DailyScheduleDate
    : T extends 'weekly'
      ? WeeklyScheduleDate
      : T extends 'monthly'
        ? MonthlyScheduleDate
        : never
