package nl.siegmann.epublib.viewer;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;

public class BrowseBar extends JPanel {
	
	private ButtonBar buttonBar;
	
	public BrowseBar(Navigator navigator, ContentPane chapterPane) {
		super(new BorderLayout());
		add(new ButtonBar(navigator, chapterPane), BorderLayout.CENTER);
		add(createSpineSlider(navigator), BorderLayout.NORTH);
	}

	private static class SpineSlider extends JSlider implements NavigationEventListener {

		private final Navigator navigator;

		public SpineSlider(Navigator navigator) {
			this.navigator = navigator;
			super.setMinimum(0);
			super.setMaximum(navigator.getBook().getSpine().size() - 1);
			super.setValue(0);
			navigator.addNavigationEventListener(this);
//			setPaintTicks(true);
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
			setValue(navigationEvent.getCurrentSpinePos());
		}
	}
	
	
	private Component createSpineSlider(Navigator navigator) {
		return new SpineSlider(navigator);
	}
}
