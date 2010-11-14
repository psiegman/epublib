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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.epub.EpubReader;

import org.apache.log4j.Logger;


public class Viewer extends JPanel {
	
	private static final long serialVersionUID = 1610691708767665447L;
	
	static final Logger log = Logger.getLogger(Viewer.class);
	private TableOfContentsPane tableOfContents;
	private ButtonBar buttonBar;
	private JSplitPane leftSplitPane;
	private JSplitPane rightSplitPane;
	
	public Viewer(Book book) {
		super(new GridLayout(1, 0));
		leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		leftSplitPane.setDividerLocation(100);
		leftSplitPane.setOneTouchExpandable(true);

		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPane.setOneTouchExpandable(true);

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setTopComponent(leftSplitPane);
		mainSplitPane.setBottomComponent(rightSplitPane);
		mainSplitPane.setOneTouchExpandable(true);
//		toc_html_splitPane.setDividerLocation(100);
		mainSplitPane.setPreferredSize(new Dimension(1000, 800));
		
		// Add the split pane to this panel.
		add(mainSplitPane);
		init(book);
	}

	private void init(Book book) {
		SectionWalker sectionWalker = book.createSectionWalker();
		leftSplitPane.setTopComponent(new GuidePane(sectionWalker));
		this.tableOfContents = new TableOfContentsPane(sectionWalker);
		leftSplitPane.setBottomComponent(tableOfContents);

		ContentPane htmlPane = new ContentPane(sectionWalker);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(htmlPane, BorderLayout.CENTER);
		this.buttonBar = new ButtonBar(sectionWalker, htmlPane);
		contentPanel.add(buttonBar, BorderLayout.SOUTH);
		rightSplitPane.setTopComponent(contentPanel);
		rightSplitPane.setBottomComponent(new MetadataPane(sectionWalker));
		htmlPane.displayPage(book.getCoverPage());
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
	
	private static JFileChooser createFileChooser() {
		File userHome = new File(System.getProperty("user.home"));
		if (! userHome.exists()) {
			userHome = null;
		}
		JFileChooser fileChooser = new JFileChooser(userHome);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || 
				file.getName().endsWith(".epub");
			}
			
			@Override
			public String getDescription() {
				return "EPub files";
			}
			
		});
		return fileChooser;
	}
	
	private static JMenuBar createMenuBar(final Viewer viewer) {
		//Where the GUI is created:
		final JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(getText("File"));
		menuBar.add(fileMenu);
		JMenuItem openFileMenuItem = new JMenuItem(getText("Open file"));
		openFileMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = createFileChooser();
				fileChooser.showOpenDialog(menuBar);
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile == null) {
					return;
				}
				try {
					Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));
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
