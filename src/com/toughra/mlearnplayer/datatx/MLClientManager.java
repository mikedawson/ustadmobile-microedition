/*
 * Ustad Mobile (Micro Edition App)
 * 
 * Copyright 2011-2014 UstadMobile Inc. All rights reserved.
 * www.ustadmobile.com
 *
 * Ustad Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version with the following additional terms:
 * 
 * All names, links, and logos of Ustad Mobile and Toughra Technologies FZ
 * LLC must be kept as they are in the original distribution.  If any new
 * screens are added you must include the Ustad Mobile logo as it has been
 * used in the original distribution.  You may not create any new
 * functionality whose purpose is to diminish or remove the Ustad Mobile
 * Logo.  You must leave the Ustad Mobile logo as the logo for the
 * application to be used with any launcher (e.g. the mobile app launcher).
 * 
 * If you want a commercial license to remove the above restriction you must
 * contact us and purchase a license without these restrictions.
 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 * Ustad Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.toughra.mlearnplayer.datatx;

import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import java.util.Hashtable;
import java.util.Vector;
import com.toughra.mlearnplayer.EXEStrMgr;

/**
 * MLClientManager uses MLDeviceDiscovery to find Bluetooth devices and then
 * MLServiceDiscoverer to find devices which are ustad mobile servers.
 * 
 * ActionListeners can be registered and when the search is done or terminates
 * with an error an ActionEvent will be fired.  
 * 
 * @author mike
 */
public class MLClientManager {

    
    /** reference to self - should only be one instance of MLClientManager ever*/
    static MLClientManager instance;
    
    /** Thread which contains Runnable deviceDiscoverer - runs in background */
    Thread discoveryThread;
    
    /**
     * Candidate servers that are found and could be used
     * 
     * This is a list in the form Bluetooth FriendlyName -> connection URL
     */
    Hashtable candidates;
    
    /**Those listening for events - we will fire when the search is done*/
    Vector actionListeners;
    
    /** Return status indicates the search was finished OK*/
    public static final int DONE_OK = 0;
    
    /** Return status that there was an error (it happens - try again...) */
    public static final int ERROR = 1;
    
    /** the last return status */
    public int lastStatus = -1;
    
    /**
     * Simple return static instance
     * 
     * @return MLClientManager instance
     */
    public static MLClientManager getInstance() {
        if(instance == null) {
            instance = new MLClientManager();
        }
        return instance;
    }
    
    /**
     * Add action listener
     * @param al ActionListener to notify
     */
    public void addActionListener(ActionListener al) {
        if(actionListeners == null) {
            actionListeners = new Vector();
        }
        
        actionListeners.addElement(al);
    }
    
    /**
     * Remove action listener
     * 
     * @param al  ActionListener to remove
     */
    public void removeActionListener(ActionListener al) {
        if(actionListeners != null) {
            actionListeners.removeElement(al);
        }
    }
    
    /**
     * fireActionEvent - inform listeners that the search is completed
     */
    protected void fireActionEvent() {
        ActionEvent evt = new ActionEvent(this);
        discoveryThread = null;

        int numAl = actionListeners.size();
        for(int i = 0; i < numAl; i++) {
            ((ActionListener)actionListeners.elementAt(i)).actionPerformed(evt);
        }
        
    }
    
    /**
     * Gets the list of potential servers.  Should be called after the search is 
     * done (e.g. getInstance(), addActionListener, then in actionEvent call
     * this method)
     * 
     * @return Hashtable in the form of Bluetooth Friendly Name -> Bluetooth Address
     */
    public Hashtable getCandidates() {
        return candidates;
    }
    
    
    /**
     * Returns the last status of the discovery process
     * 
     * @return last status from the last discovery run
     */
    public int getLastStatus() {
        return lastStatus;
    }
    
}
