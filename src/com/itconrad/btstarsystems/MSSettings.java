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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MSSettings {
public static boolean useLog = false;
public static boolean debug = false;
public static boolean calculateStats = true;
public static File lastBTdir = null;
public static boolean showSkulls = false;
public static boolean showBlackMarkets = false;
public static boolean showStarleague = false;
public static boolean showShopItems = false;
public static boolean showPlanetDescription = false;
public static StatFrame myFrame;
public static int frameWidth=-1;
public static int frameHeight=-1;
public static int frameX=0;
public static int frameY=0;
public static boolean factionColorCheck = true;
public static boolean mapWhiteBackground = true;
public static File lastMapFile = null;

public enum TagItem {
    BATTLEFIELD("Battlefield"),
    BLACKMARKET("Black Market"),
    CHEMICALS("Chemicals"),
    ELECTRONICS("Electronics"),
    INNERSPHERE("Inner Sphere"),
    RESEARCH("Research"),
    RICH("Rich"),
    RUINS("Ruins"),
    STARLEAGUE("Star League");

    private String displayName;

    TagItem(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return this.displayName; }
}

public enum OwnerFactions {
    //    AURIGAN_PIRATES("Aurigan Pirates", "pirate_logo.png"),
    NOFACTION("Abandoned", "NoFaction.png", new Color(103,116,117), new Color(83,96,97)),
//    AURIGAN_DIRECTORATE("Aurigan Directorate", "Directorate.png", new Color(29,142,14), new Color(0,122,0)),
        AURIGAN_RESTAURATION("Aurigan Restauration", "Restoration.png", new Color(29,142,14), new Color(0,122,0)),
    //    COMSTAR("ComStar", "planetlogo.png"),
    //    KURITA("Kurita", "planetlogo.png"),
    DAVION("Davion", "Suns.png", new Color(215,166, 40), new Color(145,96, 0)),
    LIAO("Liao", "Liao.png", new Color(170,185,88), new Color(100,115,18)),
    MAGISTRACY_OF_CANOPUS("Magistracy of Canopus", "Magistracy.png", new Color(75,105,122), new Color(55,85,102)),
    MARIK("Marik", "Marik.png", new Color(161, 98, 174), new Color(121, 58, 134)),
    LOCALS("Planetary Government", "Locals.png", new Color(205,206,183), new Color(15,16,25)),
    //    STEINER("Steiner", "planetlogo.png"),
    TAURIAN_CONCORDAT("Taurian Concordat", "Taurian.png", new Color(151,39,38), new Color(121,9,8));

    private String displayName;
    private ImageIcon icon;
    private Color color;
    private Color darkColor;

    OwnerFactions(String displayName, String iconFileName, Color color, Color darkColor) {
        this.displayName = displayName;
        this.color = color;
        this.darkColor = darkColor;
        try {
            icon = new ImageIcon(MSSettings.class.getResource(iconFileName));
        }
        catch (Exception e) { e.toString(); }
    }

    public String getDisplayName() { return this.displayName; }
    public ImageIcon getIcon() { return this.icon; }
    public Color getColor() { return color; }
    public Color getDarkColor() { return darkColor; }
}

public enum Factions {
//    AURIGAN_DIRECTORATE("Aurigan Directorate", "Directorate.png"),
    AURIGAN_RESTAURATION("Aurigan Restauration", "Restoration.png"),
    COMSTAR("ComStar", "ComStar.png"),
    DAVION("Davion", "Suns.png"),
    KURITA("Kurita", "Kurita.png"),
    LIAO("Liao", "Liao.png"),
    AURIGAN_PIRATES("Local Pirates", "Pirates.png"),
    MAGISTRACY_OF_CANOPUS("Magistracy of Canopus", "Magistracy.png"),
    MARIK("Marik", "Marik.png"),
    LOCALS("Planetary Government", "Locals.png"),
    //    NOFACTION("Abandoned", "planetlogo.png"),
    STEINER("Steiner", "Steiner.png"),
    TAURIAN_CONCORDAT("Taurian Concordat", "Taurian.png");

    private String displayName;
    private ImageIcon icon;

    Factions(String displayName, String iconFileName) {
        this.displayName = displayName;
        try {
            icon = new ImageIcon(MSSettings.class.getResource(iconFileName));
        }
        catch (Exception e) { e.toString(); }
    }

    public String getDisplayName() { return this.displayName; }
    public ImageIcon getIcon() { return this.icon; }
}

public enum StarAttributes {
    OWNER("Owner"),
    EMPLOYERS("Employers"),
    TARGETS("Targets"),
    TAG("Properties"),
    DIFFICULTIES("Difficulties"),
    MAXSHOPSPECIALS("Shop Specials");

    private String displayName;

    StarAttributes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return this.displayName; }
}

public enum Difficulties {
    ZERO_HALF(1, "0.5", "skulls0_5.png"),
    ONE(2, "1", "skulls1.png"),
    ONE_HALF(3, "1.5", "skulls1_5.png"),
    TWO(4, "2", "skulls2.png"),
    TWO_HALF(5, "2.5", "skulls2_5.png"),
    THREE(6, "3", "skulls3.png"),
    THREE_HALF(7, "3.5", "skulls3_5.png"),
    FOUR(8, "4", "skulls4.png"),
    FOUR_HALF(9, "4.5", "skulls4_5.png");

    int number;
    private String displayName;
    private ImageIcon icon;

    Difficulties(long number, String displayName, String iconFileName) {
        this.number = (int)number;
        this.displayName = displayName;
        try {
            icon = new ImageIcon(MSSettings.class.getResource(iconFileName));
        }
        catch (Exception e) { e.toString(); }
    }

    public int getNumber() { return number; }
    public String getDisplayName() { return this.displayName; }
    public ImageIcon getIcon() { return this.icon; }
}

public static String[] maxShopSpecialStrings = new String[]
        { "2", "3", "4", "5", "6", "7", "8", "9", "10" };

public static double mapStretchFactor = 12.0;

public enum MessageLevel {TRACE, DEBUG, INFO, WARNING, ERROR, FATAL}

public static File logFile = new File("log.html");

public static String appFont = "Segoe UI";

public static void addGridBagItem(JPanel p, JComponent c, int x, int y, int width, int height, int align) {
    addGridBagItem(p, c, x, y, width, height, align, GridBagConstraints.NONE, 0.1, 0.1);
}

public static void addGridBagItem(JPanel p, JComponent c, int x, int y, int width, int height, int align, int fill, double weightX, double weightY) {
    GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = x;
    gc.gridy = y;
    gc.gridwidth = width;
    gc.gridheight = height;
    gc.weightx = weightX;
    gc.weighty = weightY;
    gc.insets = new Insets(5, 5, 5, 5);
    gc.anchor = align;
    gc.fill = fill;
    p.add(c, gc);
}

public static class Logger {
    private static StringBuilder output = new StringBuilder();
    private static int inputCounter = 0;
    private static final Path path = Paths.get(logFile.getPath());
//        boolean linesToWrite;

//        public Logger() {
////            output = new StringBuilder("<html><body style=\"background:black\"><table>\n");
//            output = new StringBuilder();
//            inputCounter = 0;
////            linesToWrite = false;
//        }

    public static boolean logDelete() {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) { System.out.println(e.toString()); }
        return true;
    }

    public static void logEntry(String message, MessageLevel level, String methodName) {
        String messageColor = "CCCCCC";
        switch (level) {
            case TRACE:
                messageColor = "#ACC5E0";
                break;
            case DEBUG:
                messageColor = "BB7838";
                break;
            case INFO:
                messageColor = "8CEF7F";
                break;
            case WARNING:
                messageColor = "E0A655";
                break;
            case ERROR:
                messageColor = "E01A00";
                break;
            default:
                messageColor = "CECECE";
                break;
        }
        if (!Files.exists(path) && (inputCounter == 0)) {
            output.append("<html><head><style>\n" +
                                  "body { background:black; color:#DDDDDD }\n" +
                                  "table { border:1px; }\n" +
                                  "table td {\n" +
                                  "    border-top: thin solid; \n" +
                                  "    border-bottom: thin solid;\n" +
                                  "\tpadding:2px 10px 2px 3px\n" +
                                  "}\n" +
                                  "\n" +
                                  "table td:first-child {\n" +
                                  "    border-left: thin solid;\n" +
                                  "}\n" +
                                  "\n" +
                                  "table td:last-child {\n" +
                                  "    border-right: thin solid;\n" +
                                  "\tpadding:2px 3px 2px 3px\n" +
                                  "}\n" +
                                  "table th {\n" +
                                  "\ttext-align:left;\n" +
                                  "\tpadding:2px 3px 2px 3px\n" +
                                  "}" +
                                  "</style></head><body><table cellpadding=\"0\" cellspacing=\"0\" rules=\"none\">\n" +
                                  "<tr><th>Function</th><th>Level</th><th>Message</th></tr>\n");
        }
        output.append("<tr><td><font color=\"" + messageColor + "\">" + methodName + "</font></td>" +
                              "<td><font color=\"" + messageColor + "\">" + level.toString() + "</font></td>" +
                              "<td><font color=\"" + messageColor + "\">" + message + "</font></td></tr>\n");
//            if(!linesToWrite) { linesToWrite = true; }
        inputCounter++;
        if (inputCounter >= 1) {
            logPurge();
            inputCounter = 0;
        }
    }

    public static void logPurge() {
        try {
            if (Files.exists(path)) {
                Files.write(path, output.toString().getBytes(), StandardOpenOption.APPEND);
            }
            else {
                Files.write(path, output.toString().getBytes(), StandardOpenOption.CREATE);
            }
            output = new StringBuilder();
        } catch (final IOException ioe) {
            System.err.println("Error writing to log file!\n");
        }
    }

    public static void logFinalize() {
        try {
            if (Files.exists(path)) {
                output.append("</table></body></html>");
                Files.write(path, output.toString().getBytes(), StandardOpenOption.APPEND);
            }
            else if (output.length() > 0) {
                output.append("</table></body></html>");
                Files.write(path, output.toString().getBytes(), StandardOpenOption.CREATE);
            }
        } catch (final IOException ioe) {
            System.err.println("Error writing to log file!\n");
        }
//            }
    }
}
}
