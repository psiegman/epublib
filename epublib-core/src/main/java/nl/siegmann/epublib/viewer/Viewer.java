package nl.siegmann.epublib.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.siegmann.epublib.bookprocessor.BookProcessor;
import nl.siegmann.epublib.browsersupport.NavigationHistory;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubCleaner;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Viewer {
	
	private static final long serialVersionUID = 1610691708767665447L;
	
	static final Logger log = LoggerFactory.getLogger(Viewer.class);
	private final JFrame mainWindow;
	private BrowseBar browseBar;
	private JSplitPane mainSplitPane; 
	private JSplitPane leftSplitPane;
	private JSplitPane rightSplitPane;
	private Navigator navigator = new Navigator();
	NavigationHistory browserHistory;
	private EpubCleaner epubCleaner = new EpubCleaner(Collections.<BookProcessor>emptyList());
	
	public Viewer(InputStream bookStream) {
		mainWindow = createMainWindow();
		Book book;
		try {
			book = (new EpubReader(epubCleaner)).readEpub(bookStream);
			gotoBook(book);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public Viewer(Book book) {
		mainWindow = createMainWindow();
		gotoBook(book);
	}

	private JFrame createMainWindow() {
		JFrame result = new JFrame();
		result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		result.setJMenuBar(createMenuBar());

		JPanel mainPanel = new JPanel(new BorderLayout());
		
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftSplitPane.setTopComponent(new TableOfContentsPane(navigator));
		leftSplitPane.setBottomComponent(new GuidePane(navigator));
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setResizeWeight(0.8);
		
		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPane.setOneTouchExpandable(true);
		rightSplitPane.setContinuousLayout(true);
		rightSplitPane.setResizeWeight(1.0);
		ContentPane htmlPane = new ContentPane(navigator);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(htmlPane, BorderLayout.CENTER);
		this.browseBar = new BrowseBar(navigator, htmlPane);
		contentPanel.add(browseBar, BorderLayout.SOUTH);
		rightSplitPane.setLeftComponent(contentPanel);
		rightSplitPane.setRightComponent(new MetadataPane(navigator));
		
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setLeftComponent(leftSplitPane);
		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		mainSplitPane.setResizeWeight(0.0);
		
		mainPanel.add(mainSplitPane, BorderLayout.CENTER);
		mainPanel.setPreferredSize(new Dimension(1000, 750));
		mainPanel.add(new NavigationBar(navigator), BorderLayout.NORTH);

		result.add(mainPanel);
		result.pack();
		setLayout(Layout.TocContentMeta);
		result.setVisible(true);
		return result;	}
	
	
	private void gotoBook(Book book) {
		mainWindow.setTitle(book.getTitle());
		navigator.gotoBook(book, this);
	}

	private static String getText(String text) {
		return text;
	}
	
	private static JFileChooser createFileChooser(File startDir) {
		if (startDir == null) {
			startDir = new File(System.getProperty("user.home"));
			if (! startDir.exists()) {
				startDir = null;
			}
		}
		JFileChooser fileChooser = new JFileChooser(startDir);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileFilter(new FileNameExtensionFilter("EPub files", "epub"));
				     
		return fileChooser;
	}
	
	private JMenuBar createMenuBar() {
		final JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(getText("File"));
		menuBar.add(fileMenu);
		JMenuItem openFileMenuItem = new JMenuItem(getText("Open"));
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		openFileMenuItem.addActionListener(new ActionListener() {

			private File previousDir;
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = createFileChooser(previousDir);
				int returnVal = fileChooser.showOpenDialog(mainWindow);
				if(returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile == null) {
					return;
				}
				if (! selectedFile.isDirectory()) {
					previousDir = selectedFile.getParentFile();
				}
				try {
					Book book = (new EpubReader(epubCleaner)).readEpub(new FileInputStream(selectedFile));
					gotoBook(book);
				} catch (Exception e1) {
					log.error(e1.getMessage(), e1);
				}
			}
		});
		fileMenu.add(openFileMenuItem);

		JMenuItem saveFileMenuItem = new JMenuItem(getText("Save as ..."));
		saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
		saveFileMenuItem.addActionListener(new ActionListener() {

			private File previousDir;
			
			public void actionPerformed(ActionEvent e) {
				if (navigator.getBook() == null) {
					return;
				}
				JFileChooser fileChooser = createFileChooser(previousDir);
				int returnVal = fileChooser.showOpenDialog(mainWindow);
				if(returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile == null) {
					return;
				}
				if (! selectedFile.isDirectory()) {
					previousDir = selectedFile.getParentFile();
				}
				try {
					(new EpubWriter()).write(navigator.getBook(), new FileOutputStream(selectedFile));
				} catch (Exception e1) {
					log.error(e1.getMessage(), e1);
				}
			}
		});
		fileMenu.add(saveFileMenuItem);
		
		JMenuItem reloadMenuItem = new JMenuItem(getText("Reload"));
		reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		reloadMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gotoBook(navigator.getBook());
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
		
		JMenu viewMenu = new JMenu(getText("View"));
		menuBar.add(viewMenu);
		
		JMenuItem viewTocContentMenuItem = new JMenuItem(getText("TOCContent"), ViewerUtil.createImageIcon("layout-toc-content"));
		viewTocContentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK));
		viewTocContentMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setLayout(Layout.TocContent);
			}
		});
		viewMenu.add(viewTocContentMenuItem);

		JMenuItem viewContentMenuItem = new JMenuItem(getText("Content"), ViewerUtil.createImageIcon("layout-content"));
		viewContentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK));
		viewContentMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setLayout(Layout.Content);
			}
		});
		viewMenu.add(viewContentMenuItem);

		JMenuItem viewTocContentMetaMenuItem = new JMenuItem(getText("TocContentMeta"), ViewerUtil.createImageIcon("layout-toc-content-meta"));
		viewTocContentMetaMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.CTRL_MASK));
		viewTocContentMetaMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setLayout(Layout.TocContentMeta);
			}
		});
		viewMenu.add(viewTocContentMetaMenuItem);
		
		JMenu helpMenu = new JMenu(getText("Help"));
		menuBar.add(helpMenu);
		JMenuItem aboutMenuItem = new JMenuItem(getText("About"));
		aboutMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new AboutDialog(Viewer.this.mainWindow);
			}
		});
		helpMenu.add(aboutMenuItem);

		return menuBar;
	}

	private enum Layout {
		TocContentMeta,
		TocContent,
		Content
	}

	private class LayoutX {
		private boolean tocPaneVisible;
		private boolean contentPaneVisible;
		private boolean metaPaneVisible;
		
	}
	private void setLayout(Layout layout) {
		switch (layout) {
			case Content:
				mainSplitPane.setDividerLocation(0.0d);
				rightSplitPane.setDividerLocation(1.0d);
				break;
			case TocContent:
				mainSplitPane.setDividerLocation(0.2d);
				rightSplitPane.setDividerLocation(1.0d);
				break;
			case TocContentMeta:
				mainSplitPane.setDividerLocation(0.2d);
				rightSplitPane.setDividerLocation(0.6d);
				break;
		}
	}

	private static InputStream getBookInputStream(String[] args) {
		// jquery-fundamentals-book.epub
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/test2_book1.epub"));
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/three_men_in_a_boat_jerome_k_jerome.epub"));
		
//		String bookFile = "/home/paul/test2_book1.epub";
//		bookFile = "/home/paul/project/private/library/epub/this_dynamic_earth-AAH813.epub";
	
		String bookFile = null;
		if (args.length > 0) {
			bookFile = args[0];
		}
		InputStream result = null;
		if (! StringUtils.isBlank(bookFile)) {
			try {
				result = new FileInputStream(bookFile);
			} catch (Exception e) {
				log.error("Unable to open " + bookFile, e);
			}
		}
		if (result == null) {
			result = Viewer.class.getResourceAsStream("/viewer/epublibviewer-help.epub");
		}
		return result;
	}
	

    public static void main(String[] args) throws FileNotFoundException, IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error("Unable to set native look and feel", e);
		}

		final InputStream bookStream = getBookInputStream(args);
//		final Book book = readBook(args);
		
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Viewer(bookStream);
			}
		});
	}
}
