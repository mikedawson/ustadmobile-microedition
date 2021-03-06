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
package com.toughra.mlearnplayer;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.toughra.mlearnplayer.datatx.MLCloudConnector;
import com.toughra.mlearnplayer.datatx.ContentDownloader;
import java.util.Vector;

/**
 *
 * @author varuna
 */
public class ContentDownloadByIdForm extends Form implements ActionListener{

    /** Button Labels for this form */
    public static final String[] buttonLabels = new String[] 
    {"Download", "Go Back"};
    
    /** Initialising texfield for course id input**/
    public TextField idTextField;
    
    /** Constant to download a course by specified Course ID **/
    public static final int DL = 0;
    
    /** Constant to go back to Menu **/
    public static final int GOBACK = 1;
    
    /** Such that we can go back to the content form**/
    private Form contentDownForm;
    
    CourseLinkThread courseLinkThread = null;

    
    /** Constructor**/
    public ContentDownloadByIdForm(Form contentDownForm){
        setTitle("Download by ID");
        this.contentDownForm = contentDownForm;
        
        
        /** Initialising the form as a box layout**/
        BoxLayout bLayout = new BoxLayout(BoxLayout.Y_AXIS);
        setLayout(bLayout);
        
        /** Display and render the form elements.**/
        Label courseIDLabel = new Label("id");
        addComponent(courseIDLabel);
        
        idTextField = new TextField();
        addComponent(idTextField);
        
        MLearnUtils.addButtonsToForm(this, buttonLabels, null, "/icons/download_id/icon-");
        
    }
    
    /** Handle events from button
     * 
     * @param evt ActionEvent from button
     */
    public void actionPerformed(ActionEvent evt){
        Command cmd = evt.getCommand();
        if (cmd !=null){
            switch(cmd.getId()){
                case DL:
                    //Check if Course id is filled and initiate download.
                    String courseID = idTextField.getText();
                    //Talk to the server in a seperate thread.
                    System.out.println("Course id is: " + courseID);                
                    courseLinkThread = new CourseLinkThread(this, courseID);
                    courseLinkThread.start();
                                   
                    break;
                case GOBACK:
                    //show the course list
                    contentDownForm.show();
            }
                    
        }
    }
    
}

class CourseLinkThread extends Thread {
    
    ContentDownloadByIdForm contentDownloadByIdForm;
    String courseID;
    
    public CourseLinkThread(ContentDownloadByIdForm contentDownloadByIdForm, String courseID) {
        this.contentDownloadByIdForm = contentDownloadByIdForm;
        this.courseID = courseID;
    }
     /**
    *   Custom split method for J2ME
    * 
    * */
    public static String[] Split(String splitStr, String delimiter) {
        StringBuffer token = new StringBuffer();
        Vector tokens = new Vector();
        // split
        char[] chars = splitStr.toCharArray();
        for (int i=0; i < chars.length; i++) {
            if (delimiter.indexOf(chars[i]) != -1) {
                // we bumbed into a delimiter
                if (token.length() > 0) {
                    tokens.addElement(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(chars[i]);
            }
        }
        // don't forget the "tail"...
        if (token.length() > 0) {
            tokens.addElement(token.toString());
        }
        // convert the vector into an array
        String[] splitArray = new String[tokens.size()];
        for (int i=0; i < splitArray.length; i++) {
            splitArray[i] = (String)tokens.elementAt(i);
        }
        return splitArray;
 }
    
    public void run() {
        
        MLCloudConnector contentDownloaderCon  = ContentDownloader.getInstance().getConnector();
        //ContentDownloader contentDownloaderCon = ContentDownloader.getInstance().getConnector();
        
         //cloudCon = MLCloudConnector.getInstance();
        
        String courseFolderLink = contentDownloaderCon.getCourseLinkByID(courseID);
        
        if(!courseFolderLink.equals("FAIL")) {
            System.out.println("Success, return 200 and has value: " + courseFolderLink);
        }
        String courseToDownload = Split(courseFolderLink, "/")[3];
        System.out.println("Initiating download of course..");
        String courseURL = "svr2.ustadmobile.com:8010"+courseFolderLink;
        courseURL = courseURL.trim();
        
        String destFolder = "file://localhost/root1/ustadmobileContent/"
                +courseToDownload;
        
        //Ask the user if he wants to download the course with course name as mentioned. If confirmed..
        boolean userChoice = Dialog.show("Download?", 
                            "Download course: " + courseToDownload,
                            "OK", "Cancel");
        if(userChoice) {
            ContentDownloadThread courseDownloadThread = ContentDownloader.getInstance()
                .makeDownloadThread(courseURL, destFolder);
            courseDownloadThread.start();   //starts the download thread which is created above.
        }
    }
    
}
    
    

