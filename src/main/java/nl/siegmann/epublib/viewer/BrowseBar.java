package nl.siegmann.epublib.viewer;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;

public class BrowseBar extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5745389338067538254L;
	
	private ButtonBar buttonBar;
	
	public BrowseBar(Navigator navigator, ContentPane chapterPane) {
		super(new BorderLayout());
		add(new ButtonBar(navigator, chapterPane), BorderLayout.CENTER);
		add(new SpineSlider(navigator), BorderLayout.NORTH);
	}

	private static class SpineSlider extends JSlider implements NavigationEventListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8436441824668551056L;
		private Navigator navigator;

		public SpineSlider(Navigator navigator) {
			this.navigator = navigator;
			navigator.addNavigationEventListener(this);
			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					JSlider slider = (JSlider) evt.getSource();
//			        if (!slider.getValueIsAdjusting()) {
					int value = slider.getValue();
					SpineSlider.this.navigator.gotoSection(value, SpineSlider.this);
//			        }
				}
			});
		}

		@Override
		public void navigationPerformed(NavigationEvent navigationEvent) {
			if (this == navigationEvent.getSource()) {
				return;
			}
			if (navigationEvent.isBookChanged()) {
				initNavigation(navigationEvent.getCurrentBook());
			} else if (navigationEvent.isResourceChanged()) {
				setValue(navigationEvent.getCurrentSpinePos());
			}
		}

		public void initNavigation(Book book) {
			if (book == null) {
				return;
			}
			super.setMinimum(0);
			super.setMaximum(book.getSpine().size() - 1);
			super.setValue(0);
//			setPaintTicks(true);
		}
	}
}
