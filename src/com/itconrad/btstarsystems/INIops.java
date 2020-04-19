/** BATTLETECH Star Systems
 Copyright (C) 2020, Markus Conrad

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itconrad.btstarsystems;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

public class INIops {
/**
 * Read the settings from a local directory, if it exists
 */
public static void readINI() {
    File iniFile = new File("btstats.ini");
    MSSettings.iniSystemLines = new ArrayList<>();
    if (iniFile.exists()) {
//        ArrayList<String> lines = new ArrayList<>();
        String line;
        BufferedReader reader = null;
        boolean eof = false;

        try {
            reader = new BufferedReader(new FileReader(iniFile));

            while (!eof) {
                line = reader.readLine();
                if (line == null) { eof = true; }
                else {
                    if(MSSettings.useLog) { MSSettings.Logger.logEntry("reading ini line: " + line, MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                    /** reading settings: */
                    if (line.startsWith("useLog=")) {
                        if(line.substring(7).equalsIgnoreCase("true")) {  MSSettings.useLog = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting useLog to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
//                        else if (line.startsWith("debug=")) {
//                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("found debug line: " + line.substring(6), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
//                            if(line.substring(6).equalsIgnoreCase("true")) {  MSSettings.debug = true;
//                                if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting debug to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
//                            }
//                        }
                    else if (line.startsWith("directDirectorySelection=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found directDirectorySelection line: " + line.substring(25), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(25).equalsIgnoreCase("true")) {  MSSettings.directDirectorySelection = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting directDirectorySelection to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("lastDir=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found lastDir line: " + line.substring(8), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        String s = line.substring(8);
                        File nf = null;
                        if(MSSettings.directDirectorySelection) { nf = new File(line.substring(8)); }
                        else { nf = new File(line.substring(8) + "\\BattleTech_Data\\StreamingAssets\\data\\starsystem"); }
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("lastDir exists?: " + nf.exists(), MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        if(nf.exists()) {
                            MSSettings.lastBTdir = new File(s);
                        }
                    }
                    else if (line.startsWith("calculateStats=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found calcStats line: " + line.substring(15), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(15).equalsIgnoreCase("true")) {  MSSettings.calculateStats = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting calcStats to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("showSkulls=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found showSkulls line: " + line.substring(11), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(11).equalsIgnoreCase("true")) {  MSSettings.showSkulls = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting showSkulls to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("showBlackMarkets=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found showBlackMarkets line: " + line.substring(17), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(17).equalsIgnoreCase("true")) {  MSSettings.showBlackMarkets = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting showBlackMarkets to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("showStarleague=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found showStarleague line: " + line.substring(15), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(15).equalsIgnoreCase("true")) {  MSSettings.showStarleague = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting showStarleague to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("showPlanetShopItems=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found showPlanetShopItems line: " + line.substring(20), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(20).equalsIgnoreCase("true")) {  MSSettings.showShopItems = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting showPlanetShopItems to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("showPlanetDescription=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found showPlanetDescription line: " + line.substring(22), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(22).equalsIgnoreCase("true")) {  MSSettings.showSystemDescription = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting showPlanetDescription to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("showIntermediaryRoutes=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found showIntermediaryRoutes line: " + line.substring(23), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        if(line.substring(23).equalsIgnoreCase("true")) {  MSSettings.showIntermediaryRoutes = true;
                            if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting showIntermediaryRoutes to true", MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                        }
                    }
                    else if (line.startsWith("mapZoomFactor=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found mapZoomFactor line: " + line.substring(14), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        Double readDouble = null;
                        try {
                            readDouble = Double.valueOf(line.substring(14));
                        }
                        catch (NumberFormatException e) { System.err.println(e.toString()); }
                        if(readDouble == null || (readDouble < 1.0)) { MSSettings.mapStretchFactor = 8.0; }
                        else { MSSettings.mapStretchFactor = readDouble; }

                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting mapZoomFactor to " + MSSettings.mapStretchFactor, MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                    }
                    else if (line.startsWith("frameWidth=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found frameWidth line: " + line.substring(11), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        Integer readInteger = null;
                        try {
                            readInteger = Integer.valueOf(line.substring(11));
                        }
                        catch (NumberFormatException e) { System.err.println(e.toString()); }
                        if(readInteger != null && (readInteger > 1)) { MSSettings.frameWidth = readInteger; }

                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting frameWidth to " + MSSettings.frameWidth, MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                    }
                    else if (line.startsWith("frameHeight=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found frameHeight line: " + line.substring(12), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        Integer readInteger = null;
                        try {
                            readInteger = Integer.valueOf(line.substring(12));
                        }
                        catch (NumberFormatException e) { System.err.println(e.toString()); }
                        if(readInteger != null && (readInteger > 1)) { MSSettings.frameHeight = readInteger; }

                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting frameHeight to " + MSSettings.frameHeight, MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                    }
                    else if (line.startsWith("frameX=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found frameX line: " + line.substring(7), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        Integer readInteger = null;
                        try {
                            readInteger = Integer.valueOf(line.substring(7));
                        }
                        catch (NumberFormatException e) { System.err.println(e.toString()); }
                        if(readInteger != null) { MSSettings.frameX = readInteger; }

                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting frameX to " + MSSettings.frameX, MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                    }
                    else if (line.startsWith("frameY=")) {
                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("found frameY line: " + line.substring(7), MSSettings.MessageLevel.DEBUG, "MSSettings.readINI"); }
                        Integer readInteger = null;
                        try {
                            readInteger = Integer.valueOf(line.substring(7));
                        }
                        catch (NumberFormatException e) { System.err.println(e.toString()); }
                        if(readInteger != null) { MSSettings.frameY = readInteger; }

                        if(MSSettings.useLog) { MSSettings.Logger.logEntry("setting frameY to " + MSSettings.frameY, MSSettings.MessageLevel.INFO, "MSSettings.readINI"); }
                    }
                    else if (line.startsWith("starsystem=")) {
                        MSSettings.iniSystemLines.add(line.substring(11));
                    }
                }
            }
        }
        catch (Exception e) {
//                System.err.println("Error when reading file: " + e.toString());
            JOptionPane.showMessageDialog(MSSettings.myFrame, "Error when reading the INI file: " + e.toString(), "INI File Error", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(reader != null) {
//                System.out.println("EOF REACHED " + (char)34 + " " + (char)148 + " " + (char)8221);

                try {
                    reader.close();
                } catch (IOException ie) {
                    System.out.println(ie.toString());
                }
            }
        }
    }
}

public static void writeIni() {
    File iniFile = new File("btstats.ini");
    try {
        StringBuilder sb = new StringBuilder("[BATTLETECH settings]\nuseLog=");
        String s = MSSettings.useLog ? "true" : "false";
//            sb.append(s + "\ndebug=");
//            s = MSSettings.debug == true ? "true" : "false";
        sb.append(s + "\ncalculateStats=");
        s = MSSettings.calculateStats ? "true" : "false";
        sb.append(s + "\nmapZoomFactor=" + MSSettings.mapStretchFactor + "\nshowSkulls=");
        s = MSSettings.showSkulls ? "true" : "false";
        sb.append(s + "\nshowBlackMarkets=");
        s = MSSettings.showBlackMarkets ? "true" : "false";
        sb.append(s + "\nshowStarleague=");
        s = MSSettings.showStarleague ? "true" : "false";
        sb.append(s + "\nshowPlanetShopItems=");
        s = MSSettings.showShopItems ? "true" : "false";
        sb.append(s + "\nshowPlanetDescription=");
        s = MSSettings.showSystemDescription ? "true" : "false";
        sb.append(s + "\nshowIntermediaryRoutes=");
        s = MSSettings.showIntermediaryRoutes ? "true" : "false";
        sb.append(s + "\nframeWidth=" + MSSettings.myFrame.getBounds().width + "\nframeHeight=" + MSSettings.myFrame.getBounds().height);
        sb.append("\nframeX=" + MSSettings.myFrame.getBounds().x + "\nframeY=" + MSSettings.myFrame.getBounds().y);
        sb.append("\n; Use directDirectorySelection=true to directly select the folder where the star system JSON files are located.\n" +
                  "; WARNING! This disables certain safety checks, so make sure you have entered/selected the correct folder!\ndirectDirectorySelection=");
        s = MSSettings.directDirectorySelection ? "true" : "false";
        sb.append(s + "\n; lastDir is the last used BATTLETECH main directory (e.g. C:\\Program Files\\BATTLETECH)\nlastDir=");
        if(MSSettings.lastBTdir != null) { sb.append(MSSettings.lastBTdir.getAbsolutePath()); }
        if((MSSettings.starSystems.get(0) != null) && (MSSettings.starSystems.get(0).closeIntermediarySystems.size() > 0)) {
            sb.append("\n\n[Last Loaded StarSystems]\n");
            for(StarSystem ss : MSSettings.starSystems) {
                sb.append("starsystem=" + ss.name + "," + ss.positionX + "," + ss.positionY);
                for(StarSystem n : ss.closeIntermediarySystems) {
                    sb.append("," + n.name);
                }
                sb.append("\n");
            }
        }

        Files.write(iniFile.toPath(), Collections.singleton(sb.toString()), StandardCharsets.UTF_8);
    }
    catch (Exception e) { System.out.println(e.toString()); }
}
}
