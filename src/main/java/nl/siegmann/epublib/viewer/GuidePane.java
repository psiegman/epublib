package nl.siegmann.epublib.viewer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;

/**
 * Creates a Panel for navigating a Book via its Guide
 * 
 * @author paul
 *
 */
public class GuidePane extends JScrollPane implements NavigationEventListener {

	private static final long serialVersionUID = -8988054938907109295L;
	private Navigator navigator;
	
	public GuidePane(Navigator navigator) {
		this.navigator = navigator;
		navigator.addNavigationEventListener(this);
		initBook(navigator.getBook());
	}

	private void initBook(Book book) {
		if (book == null) {
			return;
		}
		getViewport().removeAll();
		JTable table = new JTable(
				createTableData(navigator.getBook().getGuide()),
				new String[] {"", ""});
		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		getViewport().add(table);
	}

	private Object[][] createTableData(Guide guide) {
		List<String[]> result = new ArrayList<String[]>();
		for (GuideReference guideReference: guide.getReferences()) {
			result.add(new String[] {guideReference.getType(), guideReference.getTitle()});
		}
		return result.toArray(new Object[result.size()][2]);
	}

	@Override
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isBookChanged()) {
			initBook(navigationEvent.getCurrentBook());
		}
	}
}
