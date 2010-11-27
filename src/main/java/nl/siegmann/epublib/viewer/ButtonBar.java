package nl.siegmann.epublib.viewer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nl.siegmann.epublib.browsersupport.Navigator;

/**
 * Creates a panel with the first,previous,next and last buttons.
 * 
 * @return
 */
class ButtonBar extends JPanel {
	private static final long serialVersionUID = 6431437924245035812L;

	private JButton startButton = ViewerUtil.createButton("Xstart", "\u007c\u25c0");
	private JButton previousChapterButton = ViewerUtil.createButton("Xprevious_chapter", "\u25c0\u25c0");
	private JButton previousPageButton = ViewerUtil.createButton("Xprevious_page", "\u25c0");
	private JButton nextPageButton = ViewerUtil.createButton("Xnext_page", "\u25B6");
	private JButton nextChapterButton = ViewerUtil.createButton("Xnext_chapter", "\u25B6\u25B6");
	private JButton endButton = ViewerUtil.createButton("Xend", "\u25B6\u007C");
	private ContentPane chapterPane;
	private final ValueHolder<Navigator> navigatorHolder = new ValueHolder<Navigator>();
	
	public ButtonBar(Navigator navigator, ContentPane chapterPane) {
		super(new GridLayout(0, 4));
		this.chapterPane = chapterPane;
		
		JPanel bigPrevious = new JPanel(new GridLayout(0, 2));
		bigPrevious.add(startButton);
		bigPrevious.add(previousChapterButton);
		add(bigPrevious);
		
		add(previousPageButton);
		add(nextPageButton);
		
		JPanel bigNext = new JPanel(new GridLayout(0, 2));
		bigNext.add(nextChapterButton);
		bigNext.add(endButton);
		add(bigNext);
		
		setSectionWalker(navigator);
	}
	
	public void setSectionWalker(Navigator navigator) {
		navigatorHolder.setValue(navigator);
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				navigatorHolder.getValue().gotoFirst(ButtonBar.this);
			}
		});
		previousChapterButton.addActionListener(new ActionListener() {
						
			@Override
			public void actionPerformed(ActionEvent e) {
				navigatorHolder.getValue().gotoPrevious(ButtonBar.this);
			}
		});
		previousPageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chapterPane.gotoPreviousPage();
			}
		});

		nextPageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chapterPane.gotoNextPage();
			}
		});
		nextChapterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				navigatorHolder.getValue().gotoNext(ButtonBar.this);
			}
		});

		endButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				navigatorHolder.getValue().gotoLast(ButtonBar.this);
			}
		});
	}
}