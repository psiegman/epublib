package nl.siegmann.epublib.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
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
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.epub.EpubReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class Viewer extends JFrame {
	
	private static final long serialVersionUID = 1610691708767665447L;
	
	static final Logger log = Logger.getLogger(Viewer.class);
	private TableOfContentsPane tableOfContents;
	private ButtonBar buttonBar;
	private JSplitPane leftSplitPane;
	private JSplitPane rightSplitPane;
	private SectionWalker sectionWalker;
	private BrowserHistory browserHistory;
	
	public Viewer(Book book) {
		super(book.getTitle());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setJMenuBar(createMenuBar(this));

		JPanel mainPanel = new JPanel(new BorderLayout());
		
		leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setDividerLocation(0);

		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPane.setOneTouchExpandable(true);
		rightSplitPane.setDividerLocation(600);
		
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setTopComponent(leftSplitPane);
		mainSplitPane.setBottomComponent(rightSplitPane);
		mainSplitPane.setOneTouchExpandable(true);
//		toc_html_splitPane.setDividerLocation(100);
		mainSplitPane.setPreferredSize(new Dimension(1000, 750));
		mainSplitPane.setDividerLocation(200);
		mainPanel.add(mainSplitPane, BorderLayout.CENTER);

		mainPanel.add(createTopNavBar(), BorderLayout.NORTH);
		add(mainPanel);
		init(book);
		pack();
		setVisible(true);
	}

	private JToolBar createTopNavBar() {
		JToolBar result = new JToolBar();
		Font historyButtonFont = new Font("SansSerif", Font.BOLD, 24);
		JButton previousButton = ViewerUtil.createButton("1leftarrow", "\u21E6");
//		previousButton.setFont(historyButtonFont);
//		previousButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
			
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Viewer.this.browserHistory.move(-1);
			}
		});
		
		result.add(previousButton);
		
		JButton nextButton = ViewerUtil.createButton("1rightarrow", "\u21E8");
		nextButton.setFont(historyButtonFont);
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Viewer.this.browserHistory.move(1);
			}
		});
		result.add(nextButton);
		return result;
	}
	
	
	private void init(Book book) {
		sectionWalker = book.createSectionWalker();
		
		this.browserHistory = new BrowserHistory(sectionWalker);
		
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
		fileChooser.setFileFilter(new FileNameExtensionFilter("EPub files", "epub"));
				     
		return fileChooser;
	}
	
	private static JMenuBar createMenuBar(final Viewer viewer) {
		final JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(getText("File"));
		menuBar.add(fileMenu);
		JMenuItem openFileMenuItem = new JMenuItem(getText("Open"));
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		openFileMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = createFileChooser();
				int returnVal = fileChooser.showOpenDialog(viewer);
				if(returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
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
		
		JMenuItem reloadMenuItem = new JMenuItem(getText("Reload"));
		reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		reloadMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				viewer.init(viewer.sectionWalker.getBook());
			}
		});
		fileMenu.add(reloadMenuItem);

		JMenuItem exitMenuItem = new JMenuItem(getText("Exit"));
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		exitMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		
		JMenu helpMenu = new JMenu(getText("Help"));
		menuBar.add(helpMenu);
		JMenuItem aboutMenuItem = new JMenuItem(getText("About"));
		aboutMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new AboutDialog(viewer);
			}
		});
		helpMenu.add(aboutMenuItem);

		return menuBar;
	}
	

	private static Book readBook(String[] args) {
		// jquery-fundamentals-book.epub
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/test2_book1.epub"));
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/three_men_in_a_boat_jerome_k_jerome.epub"));
		
//		String bookFile = "/home/paul/test2_book1.epub";
//		bookFile = "/home/paul/project/private/library/epub/this_dynamic_earth-AAH813.epub";
	
		String bookFile = null;
		if (args.length > 0) {
			bookFile = args[0];
		}
		Book book = null;
		if (! StringUtils.isBlank(bookFile)) {
			try {
				book = (new EpubReader()).readEpub(new FileInputStream(bookFile));
			} catch (Exception e) {
				log.error(e);
			}
		}
		if (book == null) {
			try {
				book = (new EpubReader()).readEpub(Viewer.class.getResourceAsStream("/viewer/epublibviewer-help.epub"));
			} catch (IOException e) {
				log.error(e);
			}
		}
		return book;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error(e);
		}

		final Book book = readBook(args);
		
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Viewer(book);
			}
		});
	}
}
