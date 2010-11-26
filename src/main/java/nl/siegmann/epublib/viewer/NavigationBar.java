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
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.search.SearchResult;
import nl.siegmann.epublib.search.SearchResults;
import nl.siegmann.epublib.search.SearchIndex;
import nl.siegmann.epublib.util.ResourceUtil;

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
	private JTextField searchField;
	private final NavigationHistory navigationHistory;
	private Navigator navigator;
	private SearchIndex searchIndex = new SearchIndex();
	private String previousSearchTerm = null;
	private int searchResultIndex = -1;
	private SearchResults searchResults;
	
	public NavigationBar(Navigator navigator) {
		this.navigationHistory = new NavigationHistory(navigator);
		this.navigator = navigator;
		navigator.addNavigationEventListener(this);
		addHistoryButtons();
		titleField = (JTextField) add(new JTextField());
		addSearchButtons();
		initBook(navigator.getBook());
	}

	private void initBook(Book book) {
		if (book == null) {
			return;
		}
		searchIndex.initBook(book);
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

	private void addSearchButtons() {
		Font historyButtonFont = new Font("SansSerif", Font.BOLD, 24);
		JButton previousButton = ViewerUtil.createButton("", "\u21E6");
//		previousButton.setFont(historyButtonFont);
//		previousButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
			
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchResults searchResults = searchIndex.doSearch(searchField.getText());
				for(SearchResult searchResult: searchResults.getHits()) {
					System.out.println(this.getClass().getName() + " " + searchResult.getPagePos() + " at resource " + searchResult.getResource());
				}
			}
		});
		
		add(previousButton);
		searchField = new JTextField();
		add(searchField);
		
		JButton nextButton = ViewerUtil.createButton("", "\u21E8");
		nextButton.setFont(historyButtonFont);
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String searchTerm = searchField.getText();
				if (searchTerm.equals(previousSearchTerm)) {
					searchResultIndex++;
				} else {
					searchResults = searchIndex.doSearch(searchTerm);
					previousSearchTerm = searchTerm;
					searchResultIndex = 0;
				}
				if (searchResultIndex < 0 || searchResultIndex >= searchResults.size()) {
					searchResultIndex = 0;
				}
				if (! searchResults.isEmpty()) {
					SearchResult searchResult = searchResults.getHits().get(searchResultIndex);
					navigator.gotoResource(searchResult.getResource(), searchResult.getPagePos(), NavigationBar.this);
				}
			}
		});
		add(nextButton);
	}

	@Override
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isBookChanged()) {
			initBook(navigationEvent.getCurrentBook());
		}
		if (navigationEvent.getCurrentResource() != null) {
			String title = ResourceUtil.getTitle(navigationEvent.getCurrentResource());
			titleField.setText(title);
		}
	}
}