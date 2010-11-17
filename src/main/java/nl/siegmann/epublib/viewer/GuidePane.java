package nl.siegmann.epublib.viewer;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;

/**
 * Creates a Panel for navigating a Book via its Guide
 * 
 * @author paul
 *
 */
public class GuidePane extends JPanel implements NavigationEventListener {

	private static final long serialVersionUID = -8988054938907109295L;

	public GuidePane(Navigator navigator) {
		super(new GridLayout(1, 0));
		JTable table = new JTable(
				createTableData(navigator.getBook().getGuide()),
				new String[] {"", ""});
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane);
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
		// TODO Auto-generated method stub
		
	}
	
}
