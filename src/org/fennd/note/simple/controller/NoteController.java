package org.fennd.note.simple.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.fennd.note.simple.model.Note;
import org.fennd.note.simple.view.NoteView;

import android.content.Context;
import android.content.SharedPreferences;

public class NoteController {
	
	// TODO: Should this be a singleton? 
	
	private final static String FILENAMES_FILE = "note_filenames.dat";
	private final static String NOTE_TITLES_FILE = "note_note_names.dat";
	private final static String ORDER_MAP_FILE = "note_order.dat";
	
	private ArrayList<Integer> orderMap;
	
	private ArrayList<String> noteTitles;
	private ArrayList<String> noteFilenames;

	private NoteView noteView;
	private SharedPreferences preferences;

	public NoteController(NoteView noteViewIn) {
		noteView = noteViewIn;
		preferences = noteView.getPreferences(0);
		loadNoteLists();
	}
	
	public void sleepController() {
		try {
			storeObject(noteTitles, NOTE_TITLES_FILE);
			storeObject(noteFilenames, FILENAMES_FILE);
			storeObject(orderMap, ORDER_MAP_FILE);
		} catch (IOException e) {
			// TODO: what? 
		}
	}
	
//	public void wakeController(NoteView noteViewIn) {
//		noteView = noteViewIn;
//		preferences = noteView.getPreferences(0);
//		loadNoteLists();
//	}
	
	public boolean aNoteExists() {
		return !noteTitles.isEmpty();
	}

	public ArrayList<String> getNoteList() {
		return noteTitles;
	}
	
	public ArrayList<String> getOrderedNoteList() {
		// TODO: Check this.
		ArrayList<String> orderedNoteList = new ArrayList<String>();
		for (int i : orderMap) {
			orderedNoteList.add(noteTitles.get(i));
		}
		
		return orderedNoteList;
	}

	public Note loadNote(String filename) throws IOException {
		if ("".equals(filename)) {
			// Indicates a new note should be created.
			return createNewNote();
		}

		return fetchNote(filename);
	}

	public Note loadNote(int selectedIndex) throws IOException {
		// Load note corresponding to a noteList index.
		int realIndex = orderMap.get(selectedIndex);
		String filename = noteFilenames.get(realIndex);
		
		return loadNote(filename);
	}

	public Note createNewNote() {
		return createNewNote(getUntitledName(), "");
	}
	
	public Note createNewNote(String noteTitle, String noteBody) {
		String noteFilename = getNewFilename();
		
		if ("".equals(noteTitle)) {
			noteTitle = getUntitledName();
		}
		
		noteTitles.add(noteTitle);
		noteFilenames.add(noteFilename);
		orderMap.add(noteTitles.size() - 1);
		
		return new Note(noteTitle, noteBody, noteFilename);
	}

	public Note delete(Note note) {
		// TODO: This definitely needs to be tested.
		
		// Find note index in 'noteFilenames' and 'noteTitles'.
		String noteFilename = note.getFilename();
		int realNoteIndex = 0;
		for (String filename : noteFilenames) {
			if (noteFilename.equals(filename)) {
				break;
			}
			realNoteIndex += 1;
		}
		
		// Adjust order list to account for note deletion.
		int orderNum = orderMap.get(realNoteIndex);
		int prevNoteIndex = 0;
		orderMap.remove(realNoteIndex);
		for (int i = 0; i < orderMap.size(); i++) {
			int mapValue = orderMap.get(i);
			
			if (orderNum > mapValue) {
				orderMap.set(i, mapValue - 1);
			}

			// Find previous index, because we are going to display it.
			if (mapValue == orderNum - 1) {
				prevNoteIndex = i;
			}
		}
		
		noteView.deleteFile(noteFilename);
		noteFilenames.remove(realNoteIndex);
		noteTitles.remove(realNoteIndex);
		
		Note toDisplay;
		try {
			toDisplay = fetchNote(noteFilenames.get(prevNoteIndex));
		} catch (IOException e) {
			toDisplay = createNewNote();
		}
		
		return toDisplay;
	}

	public void saveNote(Note note) throws IOException {
		storeObject(note, note.getFilename());
	}

	public void updateNoteTitle(Note note) {
		// Updates note title in noteTitles.
		String noteFilename = note.getFilename();
		int i = 0;
		for (String filename : noteFilenames) {
			if (filename.equals(noteFilename)) {
				noteTitles.set(i, note.getNoteTitle());
			}
			i++;
		}
	}

	public void noteOrderChanged(int previousIndex, int currentIndex) {
		// TODO: check this, and switch to arrayList implementation.
		// Really, we may not even need this.
//		int currentIndexValue = orderMap[currentIndex];
//
//		if (currentIndex == previousIndex) {
//			return;
//		} else if (currentIndex < previousIndex) {
//
//			// . . . X . . . to:
//			// . X . . . . .
//			// is prev = 3, cur = 1
//			// the same: 0, 4, 5, 6
//			// changed: 1, 2 (both +1)
//
//			for (int i = previousIndex; i < currentIndex; i--) {
//				orderMap[i] = orderMap[i - 1];
//			}
//		} else {
//			// previousIndex > currentIndex
//
//			// . X . . . . .
//			// . . . X . . .
//			// prev = 1, cur = 3
//			// the same: 0, 4, 5, 6
//			// changed: 2, 3 (both -1)
//
//			for (int i = previousIndex; i < currentIndex; i++) {
//				orderMap[i] = orderMap[i + 1];
//			}
//		}
//
//		orderMap[currentIndex] = currentIndexValue;
	}

	private void loadNoteLists() {
		try {
			noteTitles = fetchNoteTitles();
			noteFilenames = fetchFilenames();
			orderMap = fetchOrderMap();
		} catch (IOException e) {
			// Loading list names has failed. Try to recover based on note files
			// 'on disk'.
			if(!recoverNameLists()) {
				// Recovery failed. Hopefully it won't ever come to this.
				noteTitles = new ArrayList<String>();
				noteFilenames = new ArrayList<String>();
			}
		}
	}
	
	private void storeObject(Object object, String filename) throws IOException {
		FileOutputStream outputStream = null;
		ObjectOutputStream serializedOutput = null;

		try {
			outputStream = noteView.openFileOutput(filename, Context.MODE_PRIVATE);
			serializedOutput = new ObjectOutputStream(outputStream);
			serializedOutput.writeObject(object);

		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
				if (serializedOutput != null)
					serializedOutput.close();
			} catch (IOException e) {
				// Output streams did not close properly; we may have just leaked some memory.
			}
		}
	}
	
	private Note fetchNote(String noteFilename) throws IOException {
		Note note = null;
		FileInputStream inputStream = null;
		ObjectInputStream serializedInput = null;

		try {
			inputStream = noteView.openFileInput(noteFilename);
			serializedInput = new ObjectInputStream(inputStream);

			// There will only be one note object in this file.
			note = (Note) serializedInput.readObject();

		} catch (ClassNotFoundException e) {
			handleException(e);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (serializedInput != null)
					serializedInput.close();
			} catch (IOException e) {
				// Input streams haven't closed. We may have just leaked some
				// memory.
			}
		}

		return note;
	}
	
	private ArrayList<Integer> fetchOrderMap() throws IOException {
		return fetchSerializedIntArrayList(ORDER_MAP_FILE);
	}
	
	private ArrayList<String> fetchFilenames() throws IOException {
		return fetchSerializedArrayList(FILENAMES_FILE);

	}

	private ArrayList<String> fetchNoteTitles() throws IOException {
		return fetchSerializedArrayList(NOTE_TITLES_FILE);
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Integer> fetchSerializedIntArrayList(String filename) throws IOException {
		// Check for file existence.
		File file = noteView.getBaseContext().getFileStreamPath(filename);
		if (!file.exists()) {
			return new ArrayList<Integer>();
		}

		FileInputStream inputStream = null;
		ObjectInputStream objStream = null;
		ArrayList<Integer> names = null;

		try {
			inputStream = noteView.openFileInput(filename);
			objStream = new ObjectInputStream(inputStream);
			names = (ArrayList<Integer>) objStream.readObject();

		} catch (ClassNotFoundException e) {
			handleException(e);
			
		} catch (FileNotFoundException e) {
			// We have already checked for file existence...
		} finally {
			// Close data access resources.
			try {
				if (inputStream != null)
					inputStream.close();
				if (objStream != null)
					objStream.close();
			} catch (IOException e) {
				// Looks like we have leaked some memory.
			}
		}

		if (names != null) {
			return names;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> fetchSerializedArrayList(String filename)
			throws IOException {
		// Check for file existence.
		File file = noteView.getBaseContext().getFileStreamPath(filename);
		if (!file.exists()) {
			return new ArrayList<String>();
		}

		FileInputStream inputStream = null;
		ObjectInputStream objStream = null;
		ArrayList<String> names = null;

		try {
			inputStream = noteView.openFileInput(filename);
			objStream = new ObjectInputStream(inputStream);
			names = (ArrayList<String>) objStream.readObject();

		} catch (ClassNotFoundException e) {
			handleException(e);
		} finally {
			// Close data access resources.
			try {
				if (inputStream != null)
					inputStream.close();
				if (objStream != null)
					objStream.close();
			} catch (IOException e) {
				// Looks like we have leaked some memory.
			}
		}

		if (names != null) {
			return names;
		} else {
			return new ArrayList<String>();
		}
	}

	private void handleException(Exception e) {
		// TODO: still need to figure out exceptions exactly.
	}

	private boolean recoverNameLists() {
		// TODO: Recover 'filenames' and 'noteTitles' arrays from note files on
		// disk.

		return false;
	}
	
	private String getNewFilename() {
		int fileNum = preferences.getInt("fileNum", 0);
		fileNum++;
		preferences.edit().putInt("fileNum", fileNum).commit();
		return "NoteFile" + fileNum;
	}

	private String getUntitledName() {
		return getUntitledName(true);
	}

	private String getUntitledName(Boolean doIncrement) {
		int count = preferences.getInt("untitledCount", 1);
		if (doIncrement) {
			preferences.edit().putInt("untitledCount", count + 1).commit();
		}

		return "Untitled Note " + Integer.toString(count);
	}
}
