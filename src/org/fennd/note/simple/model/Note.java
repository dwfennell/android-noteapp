/*
 * Copyright 2012 Dustin Fennell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fennd.note.simple.model;

import java.io.Serializable;

/**
 * @author Dustin Fennell
 * @date October 10, 2012
 * 
 */
public class Note implements Serializable {

	private static final long serialVersionUID = 1L;

	private String noteTitle;
	private String noteBody;
	private String filename;

	public Note(String file) {
		noteTitle = "Untitled Note";
		noteBody = "";
		filename = new String(file);
	}

	public Note(String title, String body, String file) {
		noteTitle = new String(title);
		noteBody = new String(body);
		filename = new String(file);
	}

	public String getNoteTitle() {
		return new String(noteTitle);
	}

	public String getNoteBody() {
		return new String(noteBody);
	}

	public String getFilename() {
		return new String(filename);
	}
	
	public void setNoteTitle(String title) {
		noteTitle = new String(title);
	}

	public void setNoteBody(String body) {
		noteBody = new String(body);
	}

	public void setFilename(String file) {
		filename = new String(file);
	}

}
