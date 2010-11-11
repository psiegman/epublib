package nl.siegmann.epublib.viewer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nl.siegmann.epublib.domain.SectionWalker;

/**
 * Creates a panel with the first,previous,next and last buttons.
 * 
 * @return
 */
class ButtonBar extends JPanel {
	private static final long serialVersionUID = 6431437924245035812L;

	private JButton startButton = new JButton("|<");
	private JButton previousChapterButton = new JButton("<<");
	private JButton previousPageButton = new JButton("<");
	private JButton nextPageButton = new JButton(">");
	private JButton nextChapterButton = new JButton(">>");
	private JButton endButton = new JButton(">|");
	private ChapterPane chapterPane;
	private final ValueHolder<SectionWalker> sectionWalkerHolder = new ValueHolder<SectionWalker>();
	
	public ButtonBar(SectionWalker sectionWalker, ChapterPane chapterPane) {
		super(new GridLayout(0, 6));
		this.chapterPane = chapterPane;
		super.add(startButton);
		super.add(previousChapterButton);
		super.add(previousPageButton);
		super.add(nextPageButton);
		super.add(nextChapterButton);
		super.add(endButton);
		setSectionWalker(sectionWalker);
	}
	
	public void setSectionWalker(SectionWalker sectionWalker) {
		sectionWalkerHolder.setValue(sectionWalker);
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				sectionWalkerHolder.getValue().gotoFirst();
			}
		});
		previousChapterButton.addActionListener(new ActionListener() {
						
			@Override
			public void actionPerformed(ActionEvent e) {
				sectionWalkerHolder.getValue().gotoPrevious();
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
				sectionWalkerHolder.getValue().gotoNext();
			}
		});

		endButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sectionWalkerHolder.getValue().gotoLast();
			}
		});
	}
}