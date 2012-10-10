/**
 * 
 */
package org.fennd.note.simple;

/**
 * @author Owner
 * 
 */
public class Note {

	private String noteTitle;
	private String noteBody;
	private String fileName;

	public Note() {
		noteTitle = "Untitled Note";
		noteBody = "";

		fileName = getFileName();
	}

	public Note(String title, String body) {
		noteTitle = new String(title);
		noteBody = new String(body);

		fileName = getFileName();
	}

	public String getNoteTitle() {
		return new String(noteTitle);
	}

	public String getNoteBody() {
		return new String(noteBody);
	}

	public void setNoteTitle(String title) {
		noteTitle = new String(title);
	}

	public void setNoteBody(String body) {
		noteBody = new String(body);
	}

	private String getFileName() {
		// TODO

		return "";
	}

}
