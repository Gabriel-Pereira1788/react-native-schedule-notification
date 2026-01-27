import React, { useEffect } from 'react'
import { Text, View, StyleSheet, Button } from 'react-native'
import rnScheduleNotification from 'react-native-schedule-notification'

function App(): React.JSX.Element {
  function handleScheduleNotification() {
    console.log('SCHEDULE')
    rnScheduleNotification.schedule(
      'daily',
      {
        id: '1',
        message: 'Teste',
        title: 'Teste',
      },
      {
        hour: 9,
        minute: 1,
      },
    )
    rnScheduleNotification.schedule(
      'daily',
      {
        id: '2',
        message: 'Teste',
        title: 'Teste',
      },
      {
        hour: 9,
        minute: 1,
      },
    )

    rnScheduleNotification.schedule(
      'daily',
      {
        id: '3',
        message: 'Teste',
        title: 'Teste',
      },
      {
        hour: 9,
        minute: 1,
      },
    )
  }

  useEffect(() => {
    const unsubscribe = rnScheduleNotification.addListener(id => {
      console.log('NOTIFICATION-CLICKED', id)
    })

    return () => unsubscribe()
  }, [])

  function getPending() {
    rnScheduleNotification.getPendingNotifications().then(notifications => {
      console.log('PENDING_NOTIFICATIONS', notifications)
    })
  }

  function clearAll() {
    rnScheduleNotification.getPendingNotifications().then(notifications => {
      notifications.forEach(identifier => {
        rnScheduleNotification.cancel(identifier)
      })
    })
  }
  return (
    <View style={styles.container}>
      <Text style={styles.text}>Teste</Text>
      <Button title="Press" onPress={handleScheduleNotification} />
      <Button title="Get pending" onPress={getPending} />
      <Button title="Clear all" onPress={clearAll} />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 40,
    color: 'green',
  },
})

export default App
