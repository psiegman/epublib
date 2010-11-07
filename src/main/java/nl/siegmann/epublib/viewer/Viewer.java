package nl.siegmann.epublib.viewer;

//Import the swing and AWT classes needed
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeListener;
import nl.siegmann.epublib.epub.EpubReader;

import org.apache.commons.io.IOUtils;


public class Viewer extends JPanel  {
	// show images in htmlviewer
	//	http://www.javaworld.com/javaworld/javatips/jw-javatip109.html
	/**
	 * 
	 */
	private static final long serialVersionUID = 1610691708767665447L;
	
	private JScrollPane htmlView;
	private JEditorPane htmlPane;
	private JTree tree;
	private URL helpURL;
	private SectionWalker sectionWalker;
	
	// Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = false;

	public Viewer(Book book) {
		super(new GridLayout(1, 0));
		this.sectionWalker = book.createSectionWalker();
		this.sectionWalker.addSectionChangeEventListener(new SectionChangeListener() {
			
			@Override
			public void sectionChanged(SectionWalker sectionWalker, int oldPosition,
					int newPosition) {
				if (oldPosition == newPosition) {
					return;
				}
				displayURL(sectionWalker.getBook().getSpine().getResource(newPosition));
			}
		});
		
		tree = TableOfContentsTreeFactory.createTableOfContentsTree(sectionWalker);

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		// Create the HTML viewing pane.
		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		htmlPane.setContentType("text/html");
		initHelp(book);
		htmlView = new JScrollPane(htmlPane);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(htmlView, BorderLayout.CENTER);
		contentPanel.add(createButtonBar(sectionWalker), BorderLayout.SOUTH);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(contentPanel);
		splitPane.setOneTouchExpandable(true);
		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(500, 300));

		// Add the split pane to this panel.
		add(splitPane);
	}


	/**
	 * Creates a panel with the first,previous,next and last buttons.
	 * 
	 * @return
	 */
	private static JPanel createButtonBar(final SectionWalker sectionWalker) {
		JPanel result = new JPanel(new GridLayout(0, 4));
		
		JButton firstButton = new JButton("|<");
		firstButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sectionWalker.gotoFirst();
			}
		});
		result.add(firstButton);
		
		
		JButton previousButton = new JButton("<");
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sectionWalker.gotoPrevious();
			}
		});
		result.add(previousButton);
		
		JButton nextButton = new JButton(">");
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sectionWalker.gotoNext();
			}
		});
		result.add(nextButton);
		
		JButton lastButton = new JButton(">|");
		lastButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sectionWalker.gotoLast();
			}
		});
		result.add(lastButton);

		return result;
	}


	private void initHelp(Book book) {
//		helpURL = getClass().getResource(s);
		if (helpURL == null) {
			System.err.println("Couldn't open help file: " + "hi");
		}

		displayURL(book.getCoverPage());
	}

	
	private void displayURL(Resource resource) {
		try {
		System.out.println("displaying contents of " + resource.getHref());
		} catch(Exception e) {
			e.printStackTrace();
		}
		String pageContent;
		try {
			Reader reader = new InputStreamReader(resource.getInputStream(), resource.getInputEncoding());
			pageContent = IOUtils.toString(reader);
			htmlPane.setText(pageContent);
			htmlPane.setCaretPosition(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI(Book book) {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}

		// Create and set up the window.
		JFrame frame = new JFrame(book.getTitle());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new Viewer(book));

		frame.setJMenuBar(createMenuBar());
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private static String getText(String text) {
		return text;
	}
	
	private static JMenuBar createMenuBar() {
		//Where the GUI is created:
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(getText("File"));
		menuBar.add(fileMenu);
		JMenuItem openFileMenuItem = new JMenuItem(getText("Open file"));
		openFileMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

		        JFileChooser fc = new JFileChooser();
		        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		        fc.setVisible(true);
//				        //Create the open button.  We use the image from the JLF
//				        //Graphics Repository (but we extracted it from the jar).
//				        JButton openButton = new JButton("Open");
//				        openButton.addActionListener(new ActionListener() {
		    //Handle open button action.
//		    if (e.getSource() == openButton) {
//		        int returnVal = fc.showOpenDialog(FileChooserDemo.this);
//
//		        if (returnVal == JFileChooser.APPROVE_OPTION) {
//		            File file = fc.getSelectedFile();
//		            //This is where a real application would open the file.
//		            log.append("Opening: " + file.getName() + "." + newline);
//		        } else {
//		            log.append("Open command cancelled by user." + newline);
//		        }
		        //Create the open button.  We use the image from the JLF
		        //Graphics Repository (but we extracted it from the jar).
		        JButton openButton = new JButton("Open a File...");
//		        openButton.addActionListener(this);

		        //Create the save button.  We use the image from the JLF
		        //Graphics Repository (but we extracted it from the jar).
//		        saveButton = new JButton("Save a File...",
//		                                 createImageIcon("images/Save16.gif"));
//		        saveButton.addActionListener(this);

		        //For layout purposes, put the buttons in a separate panel
		        JPanel buttonPanel = new JPanel(); //use FlowLayout
		        buttonPanel.add(openButton);
//		        buttonPanel.add(saveButton);

		        //Add the buttons and the log to this panel.
		        fc.add(buttonPanel, BorderLayout.PAGE_START);
//		        add(logScrollPane, BorderLayout.CENTER);

			
			}
		});
		fileMenu.add(openFileMenuItem);
		return menuBar;
	}
	
//	public static void handleOpenFile() {
//        //Create a file chooser
//        JFileChooser fc = new JFileChooser();
//
//        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        //Create the open button.  We use the image from the JLF
//        //Graphics Repository (but we extracted it from the jar).
//        JButton openButton = new JButton("Open");
//        openButton.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//	            int returnVal = fc.showOpenDialog();
//
//	            if (returnVal == JFileChooser.APPROVE_OPTION) {
//	                File file = fc.getSelectedFile();
//	                //This is where a real application would open the file.
//	                log.append("Opening: " + file.getName() + "." + newline);
//	            } else {
//	                log.append("Open command cancelled by user." + newline);
//	            }
//			}
//		};
//
//	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// jquery-fundamentals-book.epub
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/test2_book1.epub"));
		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/three_men_in_a_boat_jerome_k_jerome.epub"));
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(book);
			}
		});
	}
}
