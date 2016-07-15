package threads;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import window.FidusDocxConverterWindow;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains the main function and the program is started from
 *       here. The program needs a main thread to run the GUI. That thread is
 *       created here.
 */
public class MainThread {
	/**
	 * Schedules a job for the event dispatch thread: creating and showing this
	 * application's GUI.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				FidusDocxConverterWindow.createAndShowGUI();
			}
		});

	}
}
