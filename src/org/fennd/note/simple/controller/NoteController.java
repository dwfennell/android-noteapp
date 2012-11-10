package org.fennd.note.simple.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.fennd.note.simple.model.Note;
import org.fennd.note.simple.view.NoteView;

public class NoteController {
	
	private final static String FILENAMES_FILE = "note_filenames.dat";
	private final static String NOTENAMES_FILE = "note_note_names.dat";
	
	private int[] orderMap;
	private String[] noteList;
	
	private int noteIndex;
	
	private NoteView noteView;
	
	public NoteController(NoteView noteViewIn) {
		noteView = noteViewIn;
		
		
	}
	
	public NoteController(Note activeNote, String[] noteList) {
		
		
		
	}
	
	public boolean aNoteExists() {
		// TODO
		
		
		return false;
	}
	
	public ArrayList<String> getNoteList() {
		
		
		return null;
	}
	
	public String loadNoteList() {
		return null;
	}
	
	public Note loadNote(String noteName) {
		return null;
	}
	
	public Note loadNote(int selectedIndex) {
		// Load note corresponding to a noteList index.
		
		return null;
	}
	
	
	public Note getSelectedNote() {

		return null;
	}
	
	public Note delete(Note note) {
		
		
		return null;
	}
	
//	public Note createNote(String noteTitle, String noteBody, String filename) {
//		
//		
//		return null;
//	}
	
	public void saveNote(Note note) throws IOException {
		// Called when saving current note.
		
		
	}
	
	public void updateNoteName(Note note) {
		// Updates note name in noteNameList??
	}
	
	public void saveObject(Object o) throws IOException {
		
	}
	
	// Moved from NoteView:
	
	public Note fetchNote(String noteFilename) throws IOException {
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
				// We may have just leaked some memory.
			}
		}

		return note;
	}
	
	public ArrayList<String> fetchFilenames() throws IOException {
		return fetchSerializedArrayList(FILENAMES_FILE);
		
	}
	
	public ArrayList<String> fetchNoteNames() throws IOException {
		return fetchSerializedArrayList(NOTENAMES_FILE);
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
	
	public boolean recoverNameLists() {
		// TODO: Recover 'filenames' and 'noteNames' arrays from note files on
		// disk.

		return false;
	}
	
	// End moved from NoteView
	
	public void noteOrderChanged(int previousIndex, int currentIndex) {
		// TODO: check this.
		int currentIndexValue = orderMap[currentIndex];
		
		if (currentIndex == previousIndex) {
			return;
		} else if (currentIndex < previousIndex) {
			
			// . . . X . . . to:
			// . X . . . . .
			// is prev = 3, cur = 1
			// the same: 0, 4, 5, 6
			// changed: 1, 2 (both +1)
			
			for (int i = previousIndex; i < currentIndex; i--) {
				orderMap[i] = orderMap[i - 1];
			}
		} else {
			// previousIndex > currentIndex
			
			// . X . . . . .
			// . . . X . . .
			// prev = 1, cur = 3
			// the same: 0, 4, 5, 6
			// changed: 2, 3 (both -1)
			
			for (int i = previousIndex; i < currentIndex; i++) {
				orderMap[i] = orderMap[i + 1];
			}
		}

		orderMap[currentIndex] = currentIndexValue;
	}

}
