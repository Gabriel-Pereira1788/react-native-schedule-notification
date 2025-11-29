import React, { useEffect } from 'react'
import { Text, View, StyleSheet, Button } from 'react-native'
import rnScheduleNotification from 'react-native-schedule-notification'

function App(): React.JSX.Element {
  function handleScheduleNotification() {
    rnScheduleNotification.schedule(
      'daily',
      {
        id: '1',
        message: 'Teste',
        title: 'Teste',
      },
      {
        hour: 9,
        minute: 59,
      },
    )
  }

  useEffect(() => {
    const unsubscribe = rnScheduleNotification.addListener(id => {
      console.log('NOTIFICATION-CLICKED', id)
    })

    return () => unsubscribe()
  }, [])
  return (
    <View style={styles.container}>
      <Text style={styles.text}>Teste</Text>
      <Button title="Press" onPress={handleScheduleNotification} />
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
