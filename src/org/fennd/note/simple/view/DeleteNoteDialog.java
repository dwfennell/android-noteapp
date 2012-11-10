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

package org.fennd.note.simple.view;

import org.fennd.note.simple.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteNoteDialog extends DialogFragment {

	static DeleteDialogListener mListener;

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface DeleteDialogListener {
		public void onDeleteDialogPositiveClick(DeleteNoteDialog dialog);
	}

	/*
	 * Call this to instantiate a new NewNoteDialog.
	 * 
	 * @param activity The activity hosting the dialog, which must implement the
	 * NoticeDialogListener to receive event callbacks.
	 * 
	 * @returns A new instance of NewNoteDialog.
	 * 
	 * @throws ClassCastException if the host activity does not implement
	 * NoticeDialogListener.
	 */
	public static DeleteNoteDialog newInstance(Activity activity) {
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events with
			// it.
			mListener = (DeleteDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception.
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
		DeleteNoteDialog frag = new DeleteNoteDialog();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setPositiveButton(R.string.delete,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {

						mListener
								.onDeleteDialogPositiveClick(DeleteNoteDialog.this);
					}
				})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								// Closes.

							}
						})
				.setMessage(R.string.confirm_delete);

		return builder.create();
	}
}
