package nl.siegmann.epublib.viewer;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeEvent;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeListener;

/**
 * Creates a Panel for navigating a Book via its Guide
 * 
 * @author paul
 *
 */
public class GuidePane extends JPanel implements SectionChangeListener {

	private static final long serialVersionUID = -8988054938907109295L;

	public GuidePane(SectionWalker sectionWalker) {
		super(new GridLayout(1, 0));
		JTable table = new JTable(
				createTableData(sectionWalker.getBook().getGuide()),
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
	public void sectionChanged(SectionChangeEvent sectionChangeEvent) {
		// TODO Auto-generated method stub
		
	}
	
}
