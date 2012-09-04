
package nl.siegmann.epublib.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import nl.siegmann.epublib.viewer.ContentPane;

public class DesktopUtil {
    
    /**
     * Open a URL in the default web browser.
     * 
     * @param a URL to open in a web browser.
     * @return true if a browser has been launched.
     */
    public static boolean launchBrowser(URL url) throws BrowserLaunchException {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(url.toURI());
                return true;
            } catch (Exception ex) {
                throw new BrowserLaunchException("Browser could not be launched for "+url, ex);
            }
        }
        return false;
    }
    
    public static class BrowserLaunchException extends Exception {

        private BrowserLaunchException(String message, Throwable cause) {
            super(message, cause);
        }
        
    }
}
