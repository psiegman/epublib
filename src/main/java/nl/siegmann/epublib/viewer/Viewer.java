package nl.siegmann.epublib.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.epub.EpubReader;

import org.apache.log4j.Logger;


public class Viewer extends JPanel {
	
	private static final long serialVersionUID = 1610691708767665447L;
	
	static final Logger log = Logger.getLogger(Viewer.class);
	private JScrollPane treeView;
	private JScrollPane htmlView;
	private ButtonBar buttonBar;
	
	public Viewer(Book book) {
		super(new GridLayout(1, 0));
		SectionWalker sectionWalker = book.createSectionWalker();

		// setup the html view
		ChapterPane htmlPane = new ChapterPane(sectionWalker);
		JScrollPane htmlView = new JScrollPane(htmlPane);
		
		// setup the table of contents view
		JTree tree = new TableOfContentsPane(sectionWalker);
		treeView = new JScrollPane(tree);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(htmlView, BorderLayout.CENTER);
		this.buttonBar = new ButtonBar(sectionWalker);
		contentPanel.add(buttonBar, BorderLayout.SOUTH);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(contentPanel);
		splitPane.setOneTouchExpandable(true);
		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(600, 800));

		// Add the split pane to this panel.
		add(splitPane);

		htmlPane.displayPage(book.getCoverPage());
//		sectionWalker.setCurrentResource(book.getCoverPage());
	}

	private void init(Book book) {
		SectionWalker sectionWalker = book.createSectionWalker();
		treeView = new JScrollPane(new TableOfContentsPane(sectionWalker));

		// setup the html view
		ChapterPane htmlPane = new ChapterPane(sectionWalker);
		sectionWalker.addSectionChangeEventListener(htmlPane);
		htmlView = new JScrollPane(htmlPane);
		
		buttonBar.setSectionWalker(sectionWalker);
	}

	/**
	 * Creates a panel with the first,previous,next and last buttons.
	 * 
	 * @return
	 */
	private static class ButtonBar extends JPanel {
		private static final long serialVersionUID = 6431437924245035812L;

		private JButton firstButton = new JButton("|<");
		private JButton previousButton = new JButton("<");
		private JButton nextButton = new JButton(">");
		private JButton lastButton = new JButton(">|");
		private final ValueHolder<SectionWalker> sectionWalkerHolder = new ValueHolder<SectionWalker>();
		
		public ButtonBar(SectionWalker sectionWalker) {
			super(new GridLayout(0, 4));
			super.add(firstButton);
			super.add(previousButton);
			super.add(nextButton);
			super.add(lastButton);
			setSectionWalker(sectionWalker);
		}
		
		public void setSectionWalker(SectionWalker sectionWalker) {
			sectionWalkerHolder.setValue(sectionWalker);
			
			firstButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {

					sectionWalkerHolder.getValue().gotoFirst();
				}
			});
			previousButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					sectionWalkerHolder.getValue().gotoPrevious();
				}
			});
			
			nextButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					sectionWalkerHolder.getValue().gotoNext();
				}
			});

			lastButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					sectionWalkerHolder.getValue().gotoLast();
				}
			});
		}
	}


	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI(Book book) {

		// Create and set up the window.
		JFrame frame = new JFrame(book.getTitle());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Viewer viewer = new Viewer(book);
		// Add content to the window.
		frame.add(viewer);

		frame.setJMenuBar(createMenuBar(viewer));
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private static String getText(String text) {
		return text;
	}
	
	private static JMenuBar createMenuBar(final Viewer viewer) {
		//Where the GUI is created:
		final JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(getText("File"));
		menuBar.add(fileMenu);
		JMenuItem openFileMenuItem = new JMenuItem(getText("Open file"));
		openFileMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				String filename = File.separator+"tmp";
				JFileChooser fc = new JFileChooser(new File(filename));
				// Show open dialog; this method does not return until the dialog is closed
				fc.showOpenDialog(menuBar);
				File selFile = fc.getSelectedFile();
				if (selFile == null) {
					return;
				}
				try {
					Book book = (new EpubReader()).readEpub(new FileInputStream(selFile));
					viewer.init(book);
				} catch (Exception e1) {
					log.error(e1);
				}
			}
		});
		fileMenu.add(openFileMenuItem);
		return menuBar;
	}
	

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// jquery-fundamentals-book.epub
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/test2_book1.epub"));
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/three_men_in_a_boat_jerome_k_jerome.epub"));
	
		String bookFile = "/home/paul/test2_book1.epub";
//		bookFile = "/home/paul/project/private/library/epub/this_dynamic_earth-AAH813.epub";
		
		if (args.length > 0) {
			bookFile = args[0];
		}
		final Book book = (new EpubReader()).readEpub(new FileInputStream(bookFile));
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(book);
			}
		});
	}
}
