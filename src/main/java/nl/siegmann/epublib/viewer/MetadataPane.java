package nl.siegmann.epublib.viewer;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.SectionWalker;

import org.apache.commons.lang.StringUtils;

public class MetadataPane extends JPanel {

	private static final long serialVersionUID = -2810193923996466948L;

	public MetadataPane(SectionWalker sectionWalker) {
		super(new GridLayout(1, 0));
		JTable table = new JTable(
				createTableData(sectionWalker.getBook().getMetadata()),
				new String[] {"", ""});
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane);
	}

	private Object[][] createTableData(Metadata metadata) {
		List<String[]> result = new ArrayList<String[]>();
		addStrings(metadata.getIdentifiers(), "Identifier", result);
		addStrings(metadata.getTitles(), "Title", result);
		addStrings(metadata.getAuthors(), "Author", result);
		result.add(new String[] {"Language", metadata.getLanguage()});
		addStrings(metadata.getContributors(), "Contributor", result);
		addStrings(metadata.getDescriptions(), "Description", result);
		addStrings(metadata.getPublishers(), "Publisher", result);
		addStrings(metadata.getDates(), "Date", result);
		addStrings(metadata.getSubjects(), "Subject", result);
		addStrings(metadata.getTypes(), "Type", result);
		addStrings(metadata.getRights(), "Rights", result);
		result.add(new String[] {"Format", metadata.getFormat()});
		return result.toArray(new Object[result.size()][2]);
	}

	private void addStrings(List<? extends Object> values, String label, List<String[]> result) {
		boolean labelWritten = false;
		for (int i = 0; i < values.size(); i++) {
			Object value = values.get(i);
			if (value == null) {
				continue;
			}
			String valueString = String.valueOf(value);
			if (StringUtils.isBlank(valueString))  {
				continue;
			}
			
			String currentLabel = "";
			if (! labelWritten) {
				currentLabel = label;
				labelWritten = true;
			}
			result.add(new String[] {currentLabel, valueString});
		}

	}
	
	private TableModel createTableModel(SectionWalker sectionWalker) {
		return new AbstractTableModel() {
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
		};
	}
}
