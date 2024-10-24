import { startClient, startServer } from "@/modules/socket";
import { useState } from "react";
import { StyleSheet, View, Text, TextInput, Pressable, FlatList, Button } from "react-native";

export default function HomeScreen() {
  const [text, setText] = useState("");
  const [notes, setNotes] = useState<string[]>([]);

  const onAddNote = () => {
    setNotes(currentNotes => [text, ...currentNotes]);
    setText("");
  };

  return (
    <View style={styles.container}>
      <View style={{ flexDirection: 'row', gap: 8 }}>
        <Button title="Start Server" onPress={() => startServer()} />
        <Button title="Start Client" onPress={() => startClient()} />
      </View>
      <View style={styles.inputContainer}>
        <TextInput
          style={styles.textInput}
          placeholder="Add note here"
          value={text}
          onChangeText={setText}
        />
        <Pressable style={styles.button} onPress={onAddNote}>
          <Text style={{ color: 'white' }}>Add</Text>
        </Pressable>
      </View>
      <FlatList
        style={styles.noteList}
        data={notes}
        renderItem={({ item }) => <Text>{item}</Text>}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  button: {
    backgroundColor: '#0a7ea4',
    padding: 8,
    borderRadius: 4,
    alignItems: 'center',
    justifyContent: 'center',
    width: 60,
  },
  container: {
    flex: 1,
    alignItems: 'center',
    paddingTop: 16,
    paddingHorizontal: 16
  },
  inputContainer: {
    flexDirection: 'row',
    marginTop: 16,
    gap: 4
  },
  noteList: {
    marginTop: 16,
    width: '100%'
  },
  textInput: {
    borderWidth: 1,
    padding: 8,
    flex: 1,
    borderRadius: 4,
  }
});
