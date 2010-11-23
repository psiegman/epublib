package nl.siegmann.epublib.viewer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.NavigationHistory;
import nl.siegmann.epublib.browsersupport.Navigator;

/**
 * A toolbar that contains the history back and forward buttons and the page title.
 * 
 * @author paul.siegmann
 *
 */
public class NavigationBar extends JToolBar implements NavigationEventListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1166410773448311544L;
	private JTextField titleField;
	private final NavigationHistory navigationHistory;
	
	public NavigationBar(Navigator navigator) {
		this.navigationHistory = new NavigationHistory(navigator);
		navigator.addNavigationEventListener(this);
		addHistoryButtons();
		
		titleField = new JTextField();
		add(titleField);
	}

	private void addHistoryButtons() {
		Font historyButtonFont = new Font("SansSerif", Font.BOLD, 24);
		JButton previousButton = ViewerUtil.createButton("1leftarrow", "\u21E6");
//		previousButton.setFont(historyButtonFont);
//		previousButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
			
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				navigationHistory.move(-1);
			}
		});
		
		add(previousButton);
		
		JButton nextButton = ViewerUtil.createButton("1rightarrow", "\u21E8");
		nextButton.setFont(historyButtonFont);
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				navigationHistory.move(1);
			}
		});
		add(nextButton);
	}

	@Override
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.getCurrentResource() == null) {
			return;
		}
		if (navigationEvent.getCurrentResource() != null) {
			titleField.setText(navigationEvent.getCurrentResource().getTitle());
		}
	}
}