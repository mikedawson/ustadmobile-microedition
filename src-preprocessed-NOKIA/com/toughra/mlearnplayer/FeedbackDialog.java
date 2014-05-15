/*
 * Ustad Mobil.  
 * Copyright 2011-2013 Toughra Technologies FZ LLC.
 * www.toughra.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.toughra.mlearnplayer;

import com.sun.lwuit.*;
import com.sun.lwuit.html.DefaultHTMLCallback;
import com.sun.lwuit.html.HTMLCallback;
import com.sun.lwuit.html.HTMLComponent;

/**
 * Standardized FeedbackDialog that will show an HTMLComponent and play a default
 * positive or negative response sound by calling the appropriate methods from
 * MLearnPlayerMidlet.
 * 
 * @author mike
 */
public class FeedbackDialog extends Dialog {
    
    /**HTMLComponent to be shown*/
    private HTMLComponent htmlComp;
    
    /**The host midlet*/
    private MLearnPlayerMidlet hostMidlet;

    private Component callComp;
    
    //by default the fbtime, however can be changed before calling the show method
    public int timeout;
    
    public boolean callSound = false;
    
    /** Used to decide which sound to play in case a specific one is not detected*/
    public boolean answerCorrect = false;
    
    /**
     * Constructor method
     * 
     * @param hostMidlet  the host midlet
     */
    public FeedbackDialog(MLearnPlayerMidlet hostMidlet) {
        this.hostMidlet = hostMidlet;
        timeout = hostMidlet.fbTime;
    }
    
    
    /**
     * Shows the HTMLComponent for the HTML from htmlStr.
     * 
     * @param comp - the calling component
     * @param htmlStr  - the html to show in the dialog
     */
    public void showFeedback(Component comp, String htmlStr) {
        // we have to do this as an html call back to wait for everything to load
        HTMLCallback callback = new ChangeListener(this);
        this.callComp = comp;
        htmlComp = hostMidlet.makeHTMLComponent(htmlStr, callback);
        addComponent(htmlComp);
        setTimeout(timeout);
    }
    
    /**
     * Calls 
     * 
     * @param comp - the calling component
     * @param htmlStr
     * @param isCorrect 
     */
    public void showFeedback(Component comp, String htmlStr, boolean isCorrect) {
        callSound = true;
        answerCorrect = isCorrect;
        showFeedback(comp, htmlStr);
    }
    
    /**
     * Watches the status of the HTMLComponent so that the sound can be played
     * once the HTMLComponent is shown
     * 
     */
    private class ChangeListener extends DefaultHTMLCallback {
        
        FeedbackDialog parent;
        
        private ChangeListener(FeedbackDialog parent) {
            this.parent = parent;
        }

        /**
         * Play the positive or negative response sound depending on if the
         * answer was correct or not
         * 
         * @param htmlC
         * @param status
         * @param url 
         */
        public void pageStatusChanged(HTMLComponent htmlC, int status, String url) {
            if(status == STATUS_COMPLETED) {
                if(parent.callSound) {
                    if(parent.answerCorrect) {
                        parent.hostMidlet.playPositiveSound();
                    }else {
                        parent.hostMidlet.playNegativeSound();
                    }
                }
                
                parent.showDialog();
            }
        }
        
        
    }
    
}
