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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BTstats {

ArrayList<StarSystem> starSystemSelection;



public BTstats(JFrame parentFrame) {
    MSSettings.starSystems = null;

    File pathFile = getInputFile(true, parentFrame);
    if(pathFile != null) {
        File starsystemFile = null;
        if(MSSettings.directDirectorySelection) { starsystemFile = new File(pathFile.getPath()); }
        else { starsystemFile = new File(pathFile.getPath() + "\\BattleTech_Data\\StreamingAssets\\data\\starsystem"); }
        MSSettings.starSystems = new ArrayList<>();
        MSSettings.intermediaryRoutesSet = false;

        for (File fileEntry : starsystemFile.listFiles()) {
            if(MSSettings.useLog) { MSSettings.Logger.logEntry("file in dir: " + fileEntry.getName(), MSSettings.MessageLevel.DEBUG, this.getClass().getSimpleName() + "." + new Throwable().getStackTrace()[0].getMethodName()); }
            if(fileEntry.getName().contains("Contested") || fileEntry.getName().contains("Flipped") || fileEntry.getName().contains("Career")) {
                if(MSSettings.useLog) { MSSettings.Logger.logEntry("skipping file: " + fileEntry.getName(), MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats"); }
                continue;
            }

            JSONParser parser = new JSONParser();
            FileReader fr = null;

            try {
                fr = new FileReader(fileEntry);

                JSONObject jo = (JSONObject)parser.parse(fr);
//                if(MSSettings.useLog) { MSSettings.Logger.logEntry(jo.toString(), MSSettings.MessageLevel.DEBUG, this.getClass().getSimpleName() + "." + "BTstats"); }

                try {
                    JSONObject jsonObject = (JSONObject)jo.get("Description");
                    String name = (String) jsonObject.get("Name");
                    String details = (String) jsonObject.get("Details");

                    jsonObject = (JSONObject) jo.get("Position");
                    Object o = jsonObject.get("x");
                    double xPos = Double.valueOf(o.toString());
                    o = jsonObject.get("y");
                    double yPos = Double.valueOf(o.toString());
                    o = jsonObject.get("z");
                    double zPos = Double.valueOf(o.toString());

                    o = jo.get("JumpDistance");
                    long jumpDistance = Long.valueOf(o.toString());
                    o = jo.get("ownerID");
                    String ownerString = (String)o.toString();
                    MSSettings.OwnerFactions ownerFaction = null;
                    if(ownerString.equals("AuriganDirectorate")) {
                        ownerFaction = MSSettings.OwnerFactions.AURIGAN_RESTAURATION;
                    }
//                    else if(ownerString.equals("AuriganRestoration")) {
//                        ownerFaction = MSSettings.OwnerFactions.AURIGAN_RESTAURATION;
//                    }
//                    else if(ownerString.equals("ComStar")) {
//                        ownerFaction = MSSettings.OwnerFactions.COMSTAR;
//                    }
//                    else if(ownerString.equals("Kurita")) {
//                        ownerFaction = MSSettings.OwnerFactions.KURITA;
//                    }
                    else if(ownerString.equals("Davion")) {
                        ownerFaction = MSSettings.OwnerFactions.DAVION;
                    }
                    else if(ownerString.equals("Liao")) {
                        ownerFaction = MSSettings.OwnerFactions.LIAO;
                    }
                    else if(ownerString.equals("Locals")) {
                        ownerFaction = MSSettings.OwnerFactions.LOCALS;
                    }
                    else if(ownerString.equals("MagistracyOfCanopus")) {
                        ownerFaction = MSSettings.OwnerFactions.MAGISTRACY_OF_CANOPUS;
                    }
                    else if(ownerString.equals("Marik")) {
                        ownerFaction = MSSettings.OwnerFactions.MARIK;
                    }
                    else if(ownerString.equals("NoFaction")) {
                        ownerFaction = MSSettings.OwnerFactions.NOFACTION;
                    }
//                    else if(ownerString.equals("Steiner")) {
//                        ownerFaction = MSSettings.OwnerFactions.STEINER;
//                    }
                    else if(ownerString.equals("TaurianConcordat")) {
                        ownerFaction = MSSettings.OwnerFactions.TAURIAN_CONCORDAT;
                    }
                    else {
                        if (MSSettings.useLog) { MSSettings.Logger.logEntry("value not found: " + ownerString, MSSettings.MessageLevel.ERROR, this.getClass().getSimpleName() + "." + "BTstats"); }
                    }
                    o = jo.get("ShopMaxSpecials");
                    long maxShopSpecials = Long.valueOf(o.toString());

                    JSONArray jsonArray = (JSONArray)jo.get("DifficultyList");
                    o = jsonArray.get(1);
                    long difficulty = Long.valueOf(o.toString());

                    ArrayList<MSSettings.TagItem> tags = new ArrayList();
                    jsonObject = (JSONObject) jo.get("Tags");
                    jsonArray = (JSONArray)jsonObject.get("items");
                    Iterator<String> iterator = jsonArray.iterator();
                    String itString;
                    while (iterator.hasNext()) {
                        itString = iterator.next();
//                        System.out.println(itString);
                        if(itString.equals("planet_other_starleague")) {
                            tags.add(MSSettings.TagItem.STARLEAGUE);
                        }
                        else if(itString.equals("planet_other_blackmarket")) {
                            tags.add(MSSettings.TagItem.BLACKMARKET);
                        }
                        else if(itString.equals("planet_industry_electronics")) {
                            tags.add(MSSettings.TagItem.ELECTRONICS);
                        }
                        else if(itString.equals("planet_civ_innersphere")) {
                            tags.add(MSSettings.TagItem.INNERSPHERE);
                        }
                        else if(itString.equals("planet_industry_rich")) {
                            tags.add(MSSettings.TagItem.RICH);
                        }
                        else if(itString.equals("planet_other_ruins")) {
                            tags.add(MSSettings.TagItem.RUINS);
                        }
                        else if(itString.equals("planet_industry_chemicals")) {
                            tags.add(MSSettings.TagItem.CHEMICALS);
                        }
                        else if(itString.equals("planet_other_battlefield")) {
                            tags.add(MSSettings.TagItem.BATTLEFIELD);
                        }
                        else if(itString.equals("planet_industry_research")) {
                            tags.add(MSSettings.TagItem.RESEARCH);
                        }
                    }

                    ArrayList<MSSettings.Factions> employers = new ArrayList();
                    jsonArray = (JSONArray)jo.get("contractEmployerIDs");
                    iterator = jsonArray.iterator();
                    while (iterator.hasNext()) {
                        itString = iterator.next();
//                        System.out.println(itString);
                        if(itString.equals("AuriganPirates")) {
                            employers.add(MSSettings.Factions.AURIGAN_PIRATES);
                        }
                        else if(itString.equals("AuriganDirectorate")) {
                            employers.add(MSSettings.Factions.AURIGAN_RESTAURATION);
                        }
                        else if(itString.equals("AuriganRestoration")) {
                            employers.add(MSSettings.Factions.AURIGAN_RESTAURATION);
                        }
                        else if(itString.equals("ComStar")) {
                            employers.add(MSSettings.Factions.COMSTAR);
                        }
                        else if(itString.equals("Kurita")) {
                            employers.add(MSSettings.Factions.KURITA);
                        }
                        else if(itString.equals("Davion")) {
                            employers.add(MSSettings.Factions.DAVION);
                        }
                        else if(itString.equals("Liao")) {
                            employers.add(MSSettings.Factions.LIAO);
                        }
                        else if(itString.equals("Locals")) {
                            employers.add(MSSettings.Factions.LOCALS);
                        }
                        else if(itString.equals("MagistracyOfCanopus")) {
                            employers.add(MSSettings.Factions.MAGISTRACY_OF_CANOPUS);
                        }
                        else if(itString.equals("Marik")) {
                            employers.add(MSSettings.Factions.MARIK);
                        }
                        else if(itString.equals("Steiner")) {
                            employers.add(MSSettings.Factions.STEINER);
                        }
                        else if(itString.equals("TaurianConcordat")) {
                            employers.add(MSSettings.Factions.TAURIAN_CONCORDAT);
                        }
                        else {
                            if (MSSettings.useLog) { MSSettings.Logger.logEntry("value not found: " + itString, MSSettings.MessageLevel.ERROR, this.getClass().getSimpleName() + "." + "BTstats"); }
                        }
                    }

                    ArrayList<MSSettings.Factions> targets = new ArrayList();
                    jsonArray = (JSONArray)jo.get("contractTargetIDs");
                    iterator = jsonArray.iterator();
                    while (iterator.hasNext()) {
                        itString = iterator.next();
//                        System.out.println(itString);
                        if(itString.equals("AuriganPirates")) {
                            targets.add(MSSettings.Factions.AURIGAN_PIRATES);
                        }
                        else if(itString.equals("AuriganDirectorate")) {
                            targets.add(MSSettings.Factions.AURIGAN_RESTAURATION);
                        }
                        else if(itString.equals("AuriganRestoration")) {
                            targets.add(MSSettings.Factions.AURIGAN_RESTAURATION);
                        }
                        else if(itString.equals("ComStar")) {
                            targets.add(MSSettings.Factions.COMSTAR);
                        }
                        else if(itString.equals("Kurita")) {
                            targets.add(MSSettings.Factions.KURITA);
                        }
                        else if(itString.equals("Davion")) {
                            targets.add(MSSettings.Factions.DAVION);
                        }
                        else if(itString.equals("Liao")) {
                            targets.add(MSSettings.Factions.LIAO);
                        }
                        else if(itString.equals("Locals")) {
                            targets.add(MSSettings.Factions.LOCALS);
                        }
                        else if(itString.equals("MagistracyOfCanopus")) {
                            targets.add(MSSettings.Factions.MAGISTRACY_OF_CANOPUS);
                        }
                        else if(itString.equals("Marik")) {
                            targets.add(MSSettings.Factions.MARIK);
                        }
                        else if(itString.equals("Steiner")) {
                            targets.add(MSSettings.Factions.STEINER);
                        }
                        else if(itString.equals("TaurianConcordat")) {
                            targets.add(MSSettings.Factions.TAURIAN_CONCORDAT);
                        }
                        else {
                            if (MSSettings.useLog) { MSSettings.Logger.logEntry("value not found: " + itString, MSSettings.MessageLevel.ERROR, this.getClass().getSimpleName() + "." + "BTstats"); }
                        }
                    }

                    MSSettings.starSystems.add(new StarSystem(name, details, xPos, yPos, zPos, jumpDistance, ownerFaction, maxShopSpecials, difficulty, tags, employers, targets));
                }
                catch (NumberFormatException e) { if(MSSettings.useLog) { MSSettings.Logger.logEntry("Error converting to number: " + jo.get("JumpDistance"), MSSettings.MessageLevel.ERROR, this.getClass().getSimpleName() + "." + "BTstats"); } }
            }
            catch(Exception e) { System.err.println(e.toString());; }
            finally {
                if(fr != null) {
                    try { fr.close(); }
                    catch(Exception e) { System.err.println(e.toString());; }
                }
            }
        }

        boolean readError = false;
        StarSystem ss, interS;
        if(MSSettings.iniSystemLines.size() != MSSettings.starSystems.size()) { readError = true; }
        else {
            List<String> elements;
            for(String line : MSSettings.iniSystemLines) {
                elements = Arrays.asList(line.split(","));
                if(elements.size() <= 3) { readError = true; break; }
                else {
                    ss = MSSettings.getStarSystem(elements.get(0));
                    if(ss != null) {
                        if (Double.toString(ss.positionX).equals(elements.get(1)) &&
                                (Double.toString(ss.positionY).equals(elements.get(2)))) {
                            for(int i = 3; i < elements.size(); i++) {
                                interS = MSSettings.getStarSystem(elements.get(i));
                                if(interS == null) { readError = true; break; }
                                else {
                                    ss.closeIntermediarySystems.add(interS);
                                }
                            }
                        }
                    }
                }
            }
            MSSettings.intermediaryRoutesSet = true;
        }
        if(readError && MSSettings.showIntermediaryRoutes) {
            setIntermediarySystems();
        }

//        for(StarSystem s : MSSettings.starSystems) {
//            ArrayList<StarSystem> less30systems = new ArrayList<>();
//            ArrayList<StarSystem> between30and60systems = new ArrayList<>();
//
//            for(StarSystem t : MSSettings.starSystems) {
//                if(s != t) {
//                    double dis = Math.sqrt(MSSettings.calculateSquaredDistance(s, t));
//                    if(s.name.equals("Jamestown") && MSSettings.useLog) { MSSettings.Logger.logEntry(s.name + " - " + t.name + " :\t" + dis, MSSettings.MessageLevel.TRACE, this.getClass().getSimpleName() + "." + "BTstats"); }
//                    if(((((s.positionX - t.positionX) * (s.positionX - t.positionX)) + ((s.positionY - t.positionY) * (s.positionY - t.positionY))) <= 900) &&
//                            ((((s.positionX - t.positionX) * (s.positionX - t.positionX)) + ((s.positionY - t.positionY) * (s.positionY - t.positionY))) > 225))
//                    {
//                        s.closeSystems.add(t);
//                    }
//
//
//                    if(s.name.equals("Jamestown")) {
//                        double squaredDistance = MSSettings.calculateSquaredDistance(s, t);
//                        if(squaredDistance <= 900) {
//                            less30systems.add(t);
//                            System.out.println("Found close system: " + t.name);
//                        }
//                        else if(squaredDistance <= 3600) {
//                            between30and60systems.add(t);
//                            System.out.println("Found 30 - 60 system: " + t.name);
//                        }
//
//                    }
//                }
//            }
//
//            if(s.name.equals("Jamestown")) {
//                for (StarSystem u : between30and60systems) {
//                    System.out.println("analyzing " + u.name);
//                    StarSystem bestDisSystem = null;
//                    double bestDis = 999999999;
//                    ArrayList<StarSystem> highestDifSystem = new ArrayList<>();
//                    int lowestJumpDis = 99999;
//                    ArrayList<StarSystem> lowestJumpSystem = new ArrayList<>();
//                    int highestJumpDis = -1;
//                    ArrayList<StarSystem> lowestDifSystem = new ArrayList<>();
//                    int lowestDif = 999;
//                    ArrayList<StarSystem> highestJumpSystem = new ArrayList<>();
//                    int highestDif = -1;
//                    ArrayList<StarSystem> highestShopSystem = new ArrayList<>();
//                    int highestShop = -1;
//                    StarSystem longestJumpSystem = null;
//                    double longestJump = -1.0;
//                    for (StarSystem l30 : less30systems) {
////                        System.out.println("Looking at " + l30.name);
//                        double l30squaredDis = MSSettings.calculateSquaredDistance(u, l30);
//                        if (l30squaredDis <= 900) {
//                            double sumDis = Math.sqrt(l30squaredDis + MSSettings.calculateSquaredDistance(l30, s));
//                            if (sumDis < bestDis) {
//                                bestDis = sumDis;
//                                bestDisSystem = l30;
//                            }
//                            if (l30.jumpDistance < lowestJumpDis) {
//                                lowestJumpDis = l30.jumpDistance;
//                                lowestJumpSystem.clear();
//                                lowestJumpSystem.add(l30);
//                            }
//                            else if (l30.jumpDistance == lowestJumpDis) { lowestJumpSystem.add(l30); }
//                            if (l30.jumpDistance > highestJumpDis) {
//                                highestJumpDis = l30.jumpDistance;
//                                highestJumpSystem.clear();
//                                highestJumpSystem.add(l30);
//                            }
//                            else if(l30.jumpDistance == highestJumpDis) { highestJumpSystem.add(l30); }
//                            if (l30.difficulty.getNumber() < lowestDif) {
//                                lowestDif = l30.difficulty.getNumber();
//                                lowestDifSystem.clear();
//                                lowestDifSystem.add(l30);
//                            }
//                            else if(l30.difficulty.getNumber() == lowestDif) { lowestDifSystem.add(l30); }
//                            if (l30.difficulty.getNumber() > highestDif) {
//                                highestDif = l30.difficulty.getNumber();
//                                highestDifSystem.clear();
//                                highestDifSystem.add(l30);
//                            }
//                            else if(l30.jumpDistance == highestDif) { highestDifSystem.add(l30); }
//                            if (l30.maxShopSpecials > highestShop) {
//                                highestShop = l30.maxShopSpecials;
//                                highestShopSystem.clear();
//                                highestShopSystem.add(l30);
//                            }
//                            else if(l30.maxShopSpecials == highestShop) { highestShopSystem.add(l30); }
//                            double jumpDistance = MSSettings.calculateSquaredDistance(s, l30);
//                            if (jumpDistance > longestJump) {
//                                longestJump = jumpDistance;
//                                longestJumpSystem = l30;
//                            }
//                            else if(l30.maxShopSpecials == highestShop) { highestShopSystem.add(l30); }
//                        }
//                    }
//                    if ((bestDis < 999999999) && MSSettings.useLog) {
//                        MSSettings.Logger.logEntry(s.name + " - " + u.name + " : best distance system: " + bestDisSystem.name + " (" + bestDis + ")", MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats");
//                    }
//                    else { MSSettings.Logger.logEntry("No direct connection found for " + u.name, MSSettings.MessageLevel.ERROR, this.getClass().getSimpleName() + "." + "BTstats"); }
//                    StringBuilder names = new StringBuilder();
//                    for(StarSystem star : lowestJumpSystem) { names.append(star.name + ", "); }
//                    if ((lowestJumpDis < 99999) && MSSettings.useLog) {
//                        MSSettings.Logger.logEntry(s.name + " - " + u.name + " : lowest jump system: " + names.toString() + " (" + lowestJumpDis + ")", MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats");
//                    }
//                    names = new StringBuilder();
//                    for(StarSystem star : highestJumpSystem) { names.append(star.name + ", "); }
//                    if ((highestJumpDis > -1) && MSSettings.useLog) {
//                        MSSettings.Logger.logEntry(s.name + " - " + u.name + " : highest jump system: " + names + " (" + highestJumpDis + ")", MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats");
//                    }
//                    names = new StringBuilder();
//                    for(StarSystem star : lowestDifSystem) { names.append(star.name + ", "); }
//                    if ((lowestDif < 999) && MSSettings.useLog) {
//                        MSSettings.Logger.logEntry(s.name + " - " + u.name + " : lowest skull system: " + names + " (" + lowestDif + ")", MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats");
//                    }
//                    names = new StringBuilder();
//                    for(StarSystem star : highestDifSystem) { names.append(star.name + ", "); }
//                    if ((highestDif > -1) && MSSettings.useLog) {
//                        MSSettings.Logger.logEntry(s.name + " - " + u.name + " : highest skull system: " + names + " (" + highestDif + ")", MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats");
//                    }
//                    names = new StringBuilder();
//                    for(StarSystem star : highestShopSystem) { names.append(star.name + ", "); }
//                    if ((highestShop > -1) && MSSettings.useLog) {
//                        MSSettings.Logger.logEntry(s.name + " - " + u.name + " : highest shop system: " + names + " (" + highestShop + ")", MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats");
//                    }
//                    if ((longestJump > -1.0) && MSSettings.useLog) {
//                        MSSettings.Logger.logEntry(s.name + " - " + u.name + " : longest first jump system: " + longestJumpSystem.name + " (" + longestJump + ")", MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + "BTstats");
//                    }
//                }
//            }
//
//            if(MSSettings.useLog) {
//                StringBuilder sd = new StringBuilder();
//                MSSettings.Logger.logEntry(s.name + " - " + t.name + " :\t" + dis, MSSettings.MessageLevel.TRACE, this.getClass().getSimpleName() + "." + "BTstats");
//            }
//
//        }
        starSystemSelection = new ArrayList<>();
        starSystemSelection.addAll(MSSettings.starSystems);

        MSSettings.squaredDistanceMatrix = new double[MSSettings.starSystems.size()][MSSettings.starSystems.size()];
        for(int i = 0; i < MSSettings.starSystems.size(); i++) {
            for(int j = 0; j < MSSettings.starSystems.size(); j++) {
                if(i == j) { MSSettings.squaredDistanceMatrix[i][j] = 0; }
                else if(j > i) {
                    MSSettings.squaredDistanceMatrix[i][j] = MSSettings.calculateSquaredDistance(MSSettings.starSystems.get(i), MSSettings.starSystems.get(j));
                    MSSettings.squaredDistanceMatrix[j][i] = MSSettings.squaredDistanceMatrix[i][j];
                }
            }
        }

        /** Calculate various aspects: */
        if(MSSettings.calculateStats) {
            int[][] owners = new int[MSSettings.OwnerFactions.values().length][9];
            int[] differenceEmpTarget = new int[MSSettings.Factions.values().length];
            int[][] employers = new int[MSSettings.Factions.values().length][9];
            int[][] targets = new int[MSSettings.Factions.values().length][9];
            for(StarSystem s : MSSettings.starSystems) {
                owners[s.owner.ordinal()][s.difficulty.getNumber()-1]++;
                for(MSSettings.Factions f : s.employers) { employers[f.ordinal()][s.difficulty.getNumber()-1]++; }
                for(MSSettings.Factions f : s.targets) { targets[f.ordinal()][s.difficulty.getNumber()-1]++; }
            }
            File statFile = new File("stats.csv");
            try {
                StringBuilder sb = new StringBuilder("BATTLETECH Statistics for " + MSSettings.starSystems.size() + " Star Systems\n");
                sb.append("\nOwner Faction;0.5;1;1.5;2;2.5;3;3.5;4;4.5;Total\n");
                for(int i = 0; i < MSSettings.OwnerFactions.values().length; i++) {
                    sb.append(MSSettings.OwnerFactions.values()[i].getDisplayName() + ";");
                    int sum = 0;
                    for(int j = 0; j < 9; j++) { sb.append(owners[i][j] + ";"); sum += owners[i][j]; }
                    sb.append(sum + "\n");
                }
                sb.append("\nEmployer Faction;0.5;1;1.5;2;2.5;3;3.5;4;4.5;Total\n");
                for(int i = 0; i< MSSettings.Factions.values().length; i++) {
                    sb.append(MSSettings.Factions.values()[i].getDisplayName() + ";");
                    int sum = 0;
                    for(int j = 0; j < 9; j++) { sb.append(employers[i][j] + ";"); sum += employers[i][j]; }
                    sb.append(sum + "\n");
                }
                sb.append("\nTarget Faction;0.5;1;1.5;2;2.5;3;3.5;4;4.5;Total\n");
                for(int i = 0; i< MSSettings.Factions.values().length; i++) {
                    sb.append(MSSettings.Factions.values()[i].getDisplayName() + ";");
                    int sum = 0;
                    for(int j = 0; j < 9; j++) { sb.append(targets[i][j] + ";"); sum += targets[i][j]; }
                    sb.append(sum + "\n");
                }
                sb.append("\nSystem Name;Owner;Difficulty;Jump Distance;# of Employers;# of Targets;Max. # of Special Shop Items;Travel List\n");
                for(StarSystem s : MSSettings.starSystems) {
                    sb.append(s.name + ";" + s.owner.getDisplayName() + ";" + s.difficulty.getDisplayName() + ";" + s.jumpDistance + ";" + s.employers.size() + ";" + s.targets.size() + ";" + s.maxShopSpecials);
                    for(StarSystem t : s.closeIntermediarySystems) { sb.append(";" + t.name); }
                    sb.append("\n");
                }
                Files.write(statFile.toPath(), Collections.singleton(sb.toString()), StandardCharsets.UTF_8);
            }
            catch(Exception e) { System.out.println(e.toString()); }
        }
    }
}

/**
 * Returns a valid path to the BATTLETECH main directory
 * @param useLastDir
 * @param parentFrame
 * @return
 */
public static File getInputFile(boolean useLastDir, JFrame parentFrame) {
    File btPathFile = null;
    if(useLastDir && (MSSettings.lastBTdir != null)) {
        File starsystemFile = null;
        if(MSSettings.directDirectorySelection) { starsystemFile = new File(MSSettings.lastBTdir.getPath()); }
        else { starsystemFile = new File(MSSettings.lastBTdir.getPath() + "\\BattleTech_Data\\StreamingAssets\\data\\starsystem"); }
        if (starsystemFile.exists()) {
//            MSSettings.lastBTdir = filename;
            return MSSettings.lastBTdir;
        }
        else { MSSettings.lastBTdir = null; }
    }
    final JFileChooser fc = new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (MSSettings.lastBTdir != null) { fc.setCurrentDirectory(MSSettings.lastBTdir); }
    while(btPathFile == null) {
        int a = fc.showOpenDialog(parentFrame);
        if (a == JFileChooser.APPROVE_OPTION) {
//        if(MSSettings.useErrorLog) { MSSettings.Logger.logEntry("selected file: " + btPathFile.getName(), MSSettings.MessageLevel.DEBUG, this.getClass().getSimpleName() + "." + "BTstats"); }
            btPathFile = fc.getSelectedFile();
            File starsystemFile = null;
            if(MSSettings.directDirectorySelection) { starsystemFile = new File(btPathFile.getPath()); }
            else { starsystemFile = new File(btPathFile + "\\BattleTech_Data\\StreamingAssets\\data\\starsystem"); }
            if (MSSettings.useLog) {
                MSSettings.Logger.logEntry("btPathFile: " + starsystemFile.getAbsoluteFile(), MSSettings.MessageLevel.DEBUG, "BTstats.getInputFile");
            }
            if (starsystemFile.exists()) {
                if (MSSettings.useLog) {
                    MSSettings.Logger.logEntry("starsystem path exists", MSSettings.MessageLevel.DEBUG, "BTstats.getInputFile");
                }
                MSSettings.lastBTdir = btPathFile;
            }
            else {
                btPathFile = null;
                MSSettings.lastBTdir = null;
                if (MSSettings.useLog) {
                    MSSettings.Logger.logEntry("starsystem files DO NOT exist", MSSettings.MessageLevel.DEBUG, "BTstats.getInputFile");
                }
                JOptionPane.showMessageDialog(parentFrame, "<html>The selected folder is not a BATTLETECH main directory!<br>Choose the folder that contains the <i>BattleTech.exe</i>!", "Invalid directory", JOptionPane.WARNING_MESSAGE);
            }
        }
        else { break; }
    }
    return btPathFile;
}

/** Update the selected star systems
 * 0 - default
 * 1 - included
 * 2 - excluded
 * */
public void updateSelection(int[] owners, int[] employers, int[] targets, int[] tags, int[] difficulties, int[] jumpDistances, int[] shopSpecials) {
    if((owners.length != MSSettings.OwnerFactions.values().length) ||
            (employers.length != MSSettings.Factions.values().length) ||
            (targets.length != MSSettings.Factions.values().length) ||
            (tags.length != MSSettings.TagItem.values().length) ||
            (difficulties.length != MSSettings.Difficulties.values().length) ||
            (jumpDistances.length != MSSettings.jumpDistances.length) ||
            (shopSpecials.length != MSSettings.maxShopSpecialStrings.length)
    )
    {
        if(MSSettings.useLog) { MSSettings.Logger.logEntry("array length doesn't match!", MSSettings.MessageLevel.ERROR, "BTstats.updateSelection"); }
    }
    if(starSystemSelection.size() != MSSettings.starSystems.size()) {
        starSystemSelection = new ArrayList<>();
        starSystemSelection.addAll(MSSettings.starSystems);
    }
//    ArrayList<StarSystem> tempSelection = new ArrayList<>();
//    tempSelection.addAll(MSSettings.starSystems);
    /** owners, these loops actually don't make much sense for owners. :/  */
    for(int i = 0; i < owners.length; i++) {
        if(owners[i] == 1) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.owner != MSSettings.OwnerFactions.values()[i]) { starSystemSelection.remove(s); }
            }
        }
        else if(owners[i] == 2) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.owner == MSSettings.OwnerFactions.values()[i]) { starSystemSelection.remove(s); }
            }
        }
    }
    for(int i = 0; i < employers.length; i++) {
        if(employers[i] == 1) {
            for(StarSystem s : MSSettings.starSystems) {
                if(!s.hasEmployer(MSSettings.Factions.values()[i])) { starSystemSelection.remove(s); }
            }
        }
        else if(employers[i] == 2) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.hasEmployer(MSSettings.Factions.values()[i])) { starSystemSelection.remove(s); }
            }
        }
    }
    for(int i = 0; i < targets.length; i++) {
        if(targets[i] == 1) {
            for(StarSystem s : MSSettings.starSystems) {
                if(!s.hasTarget(MSSettings.Factions.values()[i])) { starSystemSelection.remove(s); }
            }
        }
        else if(targets[i] == 2) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.hasTarget(MSSettings.Factions.values()[i])) { starSystemSelection.remove(s); }
            }
        }
    }
    for(int i = 0; i < tags.length; i++) {
        if(tags[i] == 1) {
            for(StarSystem s : MSSettings.starSystems) {
                if(!s.hasTag(MSSettings.TagItem.values()[i])) { starSystemSelection.remove(s); }
            }
        }
        else if(tags[i] == 2) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.hasTag(MSSettings.TagItem.values()[i])) { starSystemSelection.remove(s); }
            }
        }
    }
    for(int i = 0; i < difficulties.length; i++) {
        if(difficulties[i] == 1) {
            for(StarSystem s : MSSettings.starSystems) {
                if(!(s.difficulty.getNumber() == (i + 1))) { starSystemSelection.remove(s); }
            }
        }
        else if(difficulties[i] == 2) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.difficulty.getNumber() == (i + 1)) { starSystemSelection.remove(s); }
            }
        }
    }
    for(int i = 0; i < jumpDistances.length; i++) {
        if(jumpDistances[i] == 1) {
            for(StarSystem s : MSSettings.starSystems) {
                if(!(s.jumpDistance == (i + MSSettings.minJump))) { starSystemSelection.remove(s); }
            }
        }
        else if(jumpDistances[i] == 2) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.jumpDistance == (i + MSSettings.minJump)) { starSystemSelection.remove(s); }
            }
        }
    }
    for(int i = 0; i < shopSpecials.length; i++) {
        if(shopSpecials[i] == 1) {
            for(StarSystem s : MSSettings.starSystems) {
                if(!(s.maxShopSpecials == (i + 2))) { starSystemSelection.remove(s); }
            }
        }
        else if(shopSpecials[i] == 2) {
            for(StarSystem s : MSSettings.starSystems) {
                if(s.maxShopSpecials == (i + 2)) { starSystemSelection.remove(s); }
            }
        }
    }
}

public void setIntermediarySystems() {
    System.out.println("Setting Intermediary");
    for(StarSystem s : MSSettings.starSystems) { s.closeIntermediarySystems = new ArrayList<>(); }
    long startTime = System.nanoTime();
    /** Calculating neighboring intermediary systems: */
    ArrayList<StarPair> intermediaryStarSystems = new ArrayList<>();
    ExecutorService eService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    java.util.List<Callable<StarPair>> starTasks = new ArrayList<>();
    for(StarSystem s : MSSettings.starSystems) {
        for (StarSystem t : MSSettings.starSystems) {
            if (s != t) {
                double dis = MSSettings.getSquaredDistance(s, t);
                if ((dis < 3600) && (dis > 900)) {
                    starTasks.add(() -> {
                        return new StarPair(s, new AStarTravel(s, t).travelRoute.get(1));
                    });
                }
            }
        }
    }
    List<Future<StarPair>> starSystemRuns = new ArrayList<>();
    for(Callable<StarPair> task : starTasks) {
        starSystemRuns.add(eService.submit(task));
    }
    starSystemRuns.forEach(res -> {
        try {
            StarPair sp = res.get();
            if(sp != null) { intermediaryStarSystems.add(res.get()); }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    for(StarPair sp : intermediaryStarSystems) {
        if(!sp.originSystem.closeIntermediarySystems.contains(sp.connected)) { sp.originSystem.closeIntermediarySystems.add(sp.connected); }
    }
    MSSettings.intermediaryRoutesSet = true;

    if(MSSettings.useLog) { MSSettings.Logger.logEntry("A* calculation took " + ((System.nanoTime() - startTime) / 1000) + " microseconds.", MSSettings.MessageLevel.DEBUG, this.getClass().getSimpleName() + "." + "BTstats"); }
}


public class StarPair {
    StarSystem originSystem;
    StarSystem connected;

    public StarPair(StarSystem originSystem, StarSystem connected) {
        this.originSystem = originSystem;
        this.connected = connected;
    }
}
}
