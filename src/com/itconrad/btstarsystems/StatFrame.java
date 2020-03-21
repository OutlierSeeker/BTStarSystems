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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StatFrame extends JFrame {
public static final Font statTitleFont = new Font(MSSettings.appFont, Font.BOLD, 20);
public static final Font planetSmallTitleFont = new Font(MSSettings.appFont, Font.BOLD, 14);
public static final Font planetBigTitleFont = new Font(MSSettings.appFont, Font.BOLD, 18);
public static final Font planetTextTitleFont = new Font(MSSettings.appFont, Font.BOLD, 12);
public static final Font planetDetailFont = new Font(MSSettings.appFont, Font.ITALIC, 8);
public static Color normalBackgroundColor = new Color(40, 40, 40);
public static Color highlightBackgroundColor = new Color(0, 0, 0);

ImageIcon appIcon;

//StatFrame myFrame;
BTstats stats;

JScrollPane optionScroll;
JPanel optionPanel;
int currentOptionRow;
RadioAttribute ownerRadioAttribute;
int[] selectedOwners;
int[] ownerStats;
RadioAttribute employerRadioAttribute;
int[] selectedEmployers;
int[] employerStats;
RadioAttribute targetRadioAttribute;
int[] selectedTargets;
int[] targetStats;
RadioAttribute tagsRadioAttribute;
int[] selectedTags;
int[] tagStats;
RadioAttribute difficultiesRadioAttribute;
int[] selectedDifficulties;
int[] difficultiesStats;
RadioAttribute shopItemsRadioAttribute;
int[] selectedShopItems;
int[] shopItemsStats;

JScrollPane mapScroll;
StarMap mapPanel;


JScrollPane planetScroll;
JPanel planetGridPane;
PlanetPanel[] planetArray;
StarSystem activeSystem;

public StatFrame() {
    super();
    MSSettings.myFrame = this;
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle("Battletech Planet Systems");
    try {
        appIcon = new ImageIcon(MSSettings.class.getResource("appicon.png"));
        setIconImage(appIcon.getImage());
    }
    catch (Exception e) { e.toString(); }
    stats = new BTstats(this);

    fillFrame();
//    fillFrameWithStats();
}

public void fillFrame() {
    getContentPane().removeAll();
    if (stats.starSystems == null) {
        JButton openButton = new JButton("<html>Open BATTLETECH folder</html>");
        openButton.setPreferredSize(new Dimension(200, 200));
        add(openButton, BorderLayout.CENTER);
        setSize(new Dimension(200, 200));
        openButton.addActionListener(e -> {
            File f = BTstats.getInputFile(false, MSSettings.myFrame);
            if (f != null) {
                MSSettings.lastBTdir = f;

                stats = new BTstats(MSSettings.myFrame);
                fillFrame();
            }
        });
    }
    else {
        optionPanel = new JPanel(new GridBagLayout());
        currentOptionRow = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = currentOptionRow;
        String[] ownerFactionStrings = new String[MSSettings.OwnerFactions.values().length];
        int i = 0;
        for (MSSettings.OwnerFactions v : MSSettings.OwnerFactions.values()) {
            ownerFactionStrings[i] = v.getDisplayName();
            i++;
        }
        String[] factionStrings = new String[MSSettings.Factions.values().length];
        i = 0;
        for (MSSettings.Factions v : MSSettings.Factions.values()) {
            factionStrings[i] = v.getDisplayName();
            i++;
        }
        String[] tagStrings = new String[MSSettings.TagItem.values().length];
        i = 0;
        for (MSSettings.TagItem v : MSSettings.TagItem.values()) {
            tagStrings[i] = v.getDisplayName();
            i++;
        }
        String[] difficultyStrings = new String[MSSettings.Difficulties.values().length];
        i = 0;
        for (MSSettings.Difficulties v : MSSettings.Difficulties.values()) {
            difficultyStrings[i] = v.getDisplayName();
            i++;
        }
        /** Adding Attributes: */
        ownerRadioAttribute = new RadioAttribute(MSSettings.StarAttributes.OWNER, ownerFactionStrings);
        employerRadioAttribute = new RadioAttribute(MSSettings.StarAttributes.EMPLOYERS, factionStrings);
        targetRadioAttribute = new RadioAttribute(MSSettings.StarAttributes.TARGETS, factionStrings);
        tagsRadioAttribute = new RadioAttribute(MSSettings.StarAttributes.TAG, tagStrings);
        difficultiesRadioAttribute = new RadioAttribute(MSSettings.StarAttributes.DIFFICULTIES, difficultyStrings);
        shopItemsRadioAttribute = new RadioAttribute(MSSettings.StarAttributes.MAXSHOPSPECIALS, MSSettings.maxShopSpecialStrings);

        currentOptionRow++;
        gbc.weighty = 1;
        gbc.gridy = currentOptionRow;
        optionPanel.add(new JPanel(), gbc);

        optionScroll = new JScrollPane(optionPanel);
        optionScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//    optionScroll.add(optionPanel);
        add(optionScroll, BorderLayout.LINE_START);

        mapPanel = new StarMap();
        mapScroll = new JScrollPane(mapPanel);
        mapScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mapScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        MSSettings.myFrame.add(mapScroll, BorderLayout.CENTER);

        /** Adding stats & planets: */
//        stats = new BTstats(this);
        /** Planet panel: */
        planetGridPane = new JPanel(new GridBagLayout());
        planetScroll = new JScrollPane(planetGridPane);
        planetScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        planetScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        planetScroll.setPreferredSize(new Dimension(220, 100));
        planetArray = new PlanetPanel[stats.starSystems.size()];
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridy++;
        i = 0;
        for (StarSystem s : stats.starSystems) {
            planetArray[i] = new PlanetPanel(s);
            planetGridPane.add(planetArray[i], constraints);
            i++;
            constraints.gridy++;
        }
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.PAGE_START;
        planetGridPane.add(new JPanel(), constraints);
        add(planetScroll, BorderLayout.LINE_END);
        updateRadioArrays(false);

        if ((MSSettings.frameWidth < 1) || (MSSettings.frameHeight < 1)) { setSize(new Dimension(800, 600)); }
//        else { setSize(new Dimension(MSSettings.frameWidth, MSSettings.frameHeight)); }
        else { setBounds(MSSettings.frameX, MSSettings.frameY, MSSettings.frameWidth, MSSettings.frameHeight); }
    }
    setVisible(true);
//    revalidate();
//    repaint();
}

public void updateRadioArrays(boolean repaint) {
    /** Step 1: Assemble selection */
    selectedOwners = ownerRadioAttribute.getSelectedRadios();
    selectedEmployers = employerRadioAttribute.getSelectedRadios();
    selectedTargets = targetRadioAttribute.getSelectedRadios();
    selectedTags = tagsRadioAttribute.getSelectedRadios();
    selectedDifficulties = difficultiesRadioAttribute.getSelectedRadios();
    selectedShopItems = shopItemsRadioAttribute.getSelectedRadios();
    if (MSSettings.useLog) {
        MSSettings.Logger.logEntry("owners: " + selectedOwners.toString(), MSSettings.MessageLevel.DEBUG, this.getClass().getSimpleName() + "." + new Object() {
        }.getClass().getEnclosingMethod().getName());
    }
    stats.updateSelection(selectedOwners, selectedEmployers, selectedTargets, selectedTags, selectedDifficulties, selectedShopItems);

    /** Step 2: Collect stats */
    ownerStats = new int[MSSettings.OwnerFactions.values().length];
    for (int i = 0; i < MSSettings.OwnerFactions.values().length; i++) {
        for (StarSystem s : stats.starSystemSelection) {
            if (s.hasOwner(MSSettings.OwnerFactions.values()[i])) { ownerStats[i]++; }
        }
    }
    employerStats = new int[MSSettings.Factions.values().length];
    for (int i = 0; i < MSSettings.Factions.values().length; i++) {
        for (StarSystem s : stats.starSystemSelection) {
            if (s.hasEmployer(MSSettings.Factions.values()[i])) { employerStats[i]++; }
        }
    }
    targetStats = new int[MSSettings.Factions.values().length];
    for (int i = 0; i < MSSettings.Factions.values().length; i++) {
        for (StarSystem s : stats.starSystemSelection) {
            if (s.hasTarget(MSSettings.Factions.values()[i])) { targetStats[i]++; }
        }
    }
    tagStats = new int[MSSettings.TagItem.values().length];
    for (int i = 0; i < MSSettings.TagItem.values().length; i++) {
        for (StarSystem s : stats.starSystemSelection) {
            if (s.hasTag(MSSettings.TagItem.values()[i])) { tagStats[i]++; }
        }
    }
    difficultiesStats = new int[MSSettings.Difficulties.values().length];
    for (StarSystem s : stats.starSystemSelection) {
        difficultiesStats[s.difficulty.getNumber() - 1]++;
    }
    shopItemsStats = new int[MSSettings.maxShopSpecialStrings.length];
    for (StarSystem s : stats.starSystemSelection) {
        shopItemsStats[s.maxShopSpecials - 2]++;
    }


    /** Step 3: Add stats to labels */
    ownerRadioAttribute.setLabelStats(ownerStats);
    employerRadioAttribute.setLabelStats(employerStats);
    targetRadioAttribute.setLabelStats(targetStats);
    tagsRadioAttribute.setLabelStats(tagStats);
    difficultiesRadioAttribute.setLabelStats(difficultiesStats);
    shopItemsRadioAttribute.setLabelStats(shopItemsStats);

    /** Step 4: Show/Hide planets */
    for (PlanetPanel p : planetArray) {
        if (stats.starSystemSelection.contains(p.getStarSystem())) { p.setVisible(true); }
        else { p.setVisible(false);}
    }

    if (repaint) {
        this.revalidate();
        this.repaint();
    }
}


public class RadioAttribute implements ActionListener {
    MSSettings.StarAttributes attribute;
    String[] labelStrings;
    JRadioButton allDefaultRadio;
    JRadioButton allIncludedRadio;
    JRadioButton allExcludedRadio;
    ButtonGroup allGroup;
    JButton minimizeButton;
    boolean minimized;
    JRadioButton[] defaultRadios;
    JRadioButton[] includedRadios;
    JRadioButton[] excludedRadios;
    JLabel[] statLabels;

    public RadioAttribute(MSSettings.StarAttributes attribute, String[] labelStrings) {
        this.attribute = attribute;
        this.labelStrings = labelStrings;
        defaultRadios = new JRadioButton[labelStrings.length];
        includedRadios = new JRadioButton[labelStrings.length];
        excludedRadios = new JRadioButton[labelStrings.length];
        statLabels = new JLabel[labelStrings.length];

        /** Adding title row: */
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(Color.LIGHT_GRAY);
        allDefaultRadio = new JRadioButton();
        allDefaultRadio.addActionListener(this);
        allDefaultRadio.setBackground(Color.LIGHT_GRAY);
        allIncludedRadio = new JRadioButton();
        allIncludedRadio.addActionListener(this);
        allIncludedRadio.setBackground(Color.LIGHT_GRAY);
        allExcludedRadio = new JRadioButton();
        allExcludedRadio.addActionListener(this);
        allExcludedRadio.setBackground(Color.LIGHT_GRAY);
        allGroup = new ButtonGroup();
        titlePanel.add(allDefaultRadio, constraints);
        allGroup.add(allDefaultRadio);
        constraints.gridx++;
        titlePanel.add(allIncludedRadio, constraints);
        allGroup.add(allIncludedRadio);
        constraints.gridx++;
        titlePanel.add(allExcludedRadio, constraints);
        allGroup.add(allExcludedRadio);
        constraints.gridx++;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(attribute.getDisplayName());
        label.setFont(statTitleFont);
        titlePanel.add(label, constraints);
        constraints.gridx++;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        minimizeButton = new JButton("_");
        minimizeButton.setFocusPainted(false);
        minimizeButton.setMargin(new Insets(0, 0, 0, 0));
        minimizeButton.setPreferredSize(new Dimension(20, 20));
        minimizeButton.addActionListener(this);
        minimized = false;
        titlePanel.add(minimizeButton, constraints);
        constraints.gridx = 0;
        constraints.gridy = currentOptionRow;
        constraints.gridwidth = 5;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        optionPanel.add(titlePanel, constraints);


        /** Adding attribute rows: */
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < labelStrings.length; i++) {
            currentOptionRow++;
            constraints.gridy = currentOptionRow;
            constraints.gridx = 0;
            constraints.insets = new Insets(0, 0, 0, 0);
            defaultRadios[i] = new JRadioButton();
            defaultRadios[i].addActionListener(this);
            includedRadios[i] = new JRadioButton();
            includedRadios[i].addActionListener(this);
            excludedRadios[i] = new JRadioButton();
            excludedRadios[i].addActionListener(this);
            ButtonGroup bg = new ButtonGroup();
            bg.add(defaultRadios[i]);
            bg.add(includedRadios[i]);
            bg.add(excludedRadios[i]);
            optionPanel.add(defaultRadios[i], constraints);
            defaultRadios[i].setSelected(true);
            constraints.gridx++;
            optionPanel.add(includedRadios[i], constraints);
            constraints.gridx++;
            optionPanel.add(excludedRadios[i], constraints);
            constraints.gridx++;
            constraints.weightx = 1;
            statLabels[i] = new JLabel(labelStrings[i]);
            optionPanel.add(statLabels[i], constraints);
        }

        currentOptionRow++;
        allDefaultRadio.setSelected(true);
    }

    public int[] getSelectedRadios() {
        int[] rArray = new int[defaultRadios.length];
        for (int i = 0; i < defaultRadios.length; i++) {
            if (includedRadios[i].isSelected()) { rArray[i] = 1; }
            else if (excludedRadios[i].isSelected()) { rArray[i] = 2; }
        }
        return rArray;
    }

    public void setLabelStats(int[] values) {
        for (int i = 0; i < statLabels.length; i++) {
            if (MSSettings.useLog) {
                MSSettings.Logger.logEntry("Setting " + statLabels[i].getText() + " to " + labelStrings[i] + " (" + values[i] + ")", MSSettings.MessageLevel.DEBUG, this.getClass().getSimpleName() + "." + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            statLabels[i].setText(labelStrings[i] + " (" + values[i] + ")");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JRadioButton) {
            if (source == allDefaultRadio) { for (JRadioButton r : defaultRadios) { r.setSelected(true); } }
            else if (source == allIncludedRadio) { for (JRadioButton r : includedRadios) { r.setSelected(true); } }
            else if (source == allExcludedRadio) { for (JRadioButton r : excludedRadios) { r.setSelected(true); } }
            else {
                boolean radioFound = false;
                for (JRadioButton r : defaultRadios) {
                    if ((source == r) && allIncludedRadio.isSelected()) {
                        allGroup.clearSelection();
                        radioFound = true;
                        break;
                    }
                    else if ((source == r) && allExcludedRadio.isSelected()) {
                        allGroup.clearSelection();
                        radioFound = true;
                        break;
                    }
                }
                if (!radioFound) {
                    for (JRadioButton r : includedRadios) {
                        if ((source == r) && allDefaultRadio.isSelected()) {
                            allGroup.clearSelection();
                            radioFound = true;
                            break;
                        }
                        else if ((source == r) && allExcludedRadio.isSelected()) {
                            allGroup.clearSelection();
                            radioFound = true;
                            break;
                        }
                    }
                }
                if (!radioFound) {
                    for (JRadioButton r : excludedRadios) {
                        if ((source == r) && allIncludedRadio.isSelected()) {
                            allGroup.clearSelection();
                            break;
                        }
                        else if ((source == r) && allDefaultRadio.isSelected()) {
                            allGroup.clearSelection();
                            break;
                        }
                    }
                }
            }
        }
        else if (source == minimizeButton) {
            if (minimized) {
                for (int i = 0; i < defaultRadios.length; i++) {
                    defaultRadios[i].setVisible(true);
                    includedRadios[i].setVisible(true);
                    excludedRadios[i].setVisible(true);
                    statLabels[i].setVisible(true);
                }
                minimized = false;
            }
            else {
                for (int i = 0; i < defaultRadios.length; i++) {
                    defaultRadios[i].setVisible(false);
                    includedRadios[i].setVisible(false);
                    excludedRadios[i].setVisible(false);
                    statLabels[i].setVisible(false);
                }
                minimized = true;
            }
        }
        updateRadioArrays(true);
    }
}

public Point convertCoordinateToCanvas(double x, double y) {
    double xOffset = 99.63;
    double yOffset = 320.52;
    int newX = (int) (((x + xOffset) + 10) * MSSettings.mapStretchFactor);
    int newY = (int) (((-y - yOffset) + 5) * MSSettings.mapStretchFactor);
    return new Point(newX, newY);
}


public class PlanetPanel extends JPanel {
    StarSystem starSystem;
    boolean activated;
    JPanel titlePanel;
    JLabel iconLabel;
    JLabel nameLabel;
    JLabel skullLabel;
    JLabel targetTitleLabel;
    JLabel employerTitleLabel;
    JPanel employersPanel;
    JPanel targetsPanel;
    JLabel propertiesTitleLabel;
    JLabel propertiesLabel;
    JLabel itemsTitleLabel;
    JLabel itemsLabel;
    JLabel descriptionTitleLabel;
    JTextArea descriptionText;


    public PlanetPanel(StarSystem starSystem) {
        super();
        this.starSystem = starSystem;
        activated = false;
        setLayout(new GridBagLayout());
        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(normalBackgroundColor);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 0;
        int mainGridRow = 0;
        constraints.gridy = mainGridRow;
//    MSSettings.OwnerFactions f = starSystem.owner;
        iconLabel = new JLabel(starSystem.owner.getIcon());
        iconLabel.setBackground(normalBackgroundColor);
        titlePanel.add(iconLabel, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        nameLabel = new JLabel(starSystem.name);
        nameLabel.setBackground(normalBackgroundColor);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(planetSmallTitleFont);
//    nameLabel.setHorizontalAlignment(JLabel.CENTER);
        setBackground(normalBackgroundColor);
        titlePanel.add(nameLabel, constraints);
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        add(titlePanel, constraints);

        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.insets = new Insets(1, 2, 1, 1);
//    constraints.gridwidth = 2;
        skullLabel = new JLabel(starSystem.difficulty.getIcon());
        skullLabel.setBackground(normalBackgroundColor);
        skullLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(skullLabel, constraints);
        skullLabel.setVisible(false);

        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(1, 4, 1, 1);
//    constraints.anchor = GridBagConstraints.CENTER;
        employerTitleLabel = new JLabel("Employers");
        employerTitleLabel.setBackground(normalBackgroundColor);
        employerTitleLabel.setForeground(Color.WHITE);
        employerTitleLabel.setFont(planetSmallTitleFont);
        add(employerTitleLabel, constraints);
        employerTitleLabel.setVisible(false);
        employersPanel = new JPanel(new GridBagLayout());
        employersPanel.setBackground(highlightBackgroundColor);
        constraints.insets = new Insets(2, 8, 2, 2);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        for (MSSettings.Factions f : starSystem.employers) {
            JLabel fIcon = new JLabel(f.getIcon());
//        fIcon.setBackground(highlightBackgroundColor);
            employersPanel.add(fIcon, constraints);
            constraints.gridx++;
            constraints.insets = new Insets(2, 2, 2, 2);
        }
        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.gridx = 0;
        add(employersPanel, constraints);
        employersPanel.setVisible(false);


        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.insets = new Insets(1, 4, 1, 1);
        targetTitleLabel = new JLabel("Targets");
        targetTitleLabel.setBackground(normalBackgroundColor);
        targetTitleLabel.setForeground(Color.WHITE);
        targetTitleLabel.setFont(planetSmallTitleFont);
        add(targetTitleLabel, constraints);
        targetTitleLabel.setVisible(false);
        targetsPanel = new JPanel(new GridBagLayout());
        targetsPanel.setBackground(highlightBackgroundColor);
        constraints.insets = new Insets(2, 8, 2, 2);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        for (MSSettings.Factions f : starSystem.targets) {
            JLabel fIcon = new JLabel(f.getIcon());
//        fIcon.setBackground(highlightBackgroundColor);
            targetsPanel.add(fIcon, constraints);
            constraints.gridx++;
            constraints.insets = new Insets(2, 2, 2, 2);
        }
        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.gridx = 0;
        add(targetsPanel, constraints);
        targetsPanel.setVisible(false);

        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.insets = new Insets(1, 4, 0, 1);
        propertiesTitleLabel = new JLabel("Properties");
        propertiesTitleLabel.setBackground(normalBackgroundColor);
        propertiesTitleLabel.setForeground(Color.WHITE);
        propertiesTitleLabel.setFont(planetSmallTitleFont);
        propertiesTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(propertiesTitleLabel, constraints);
        propertiesTitleLabel.setVisible(false);
        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.insets = new Insets(-8, -40, 2, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        propertiesLabel = new JLabel();
        propertiesLabel.setForeground(Color.WHITE);
        if (starSystem.tags.size() == 0) { propertiesLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;-</html>"); }
        else {
            StringBuilder sb = new StringBuilder("<html><ul style=\"margin-left:25; padding:0\">");
            for (MSSettings.TagItem tag : starSystem.tags) {
                sb.append("<li>" + tag.getDisplayName() + "</li>");
            }
            sb.append("</ul></html>");
            propertiesLabel.setText(sb.toString());
            propertiesLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }
        add(propertiesLabel, constraints);
        propertiesLabel.setVisible(false);

        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.insets = new Insets(1, 4, 0, 1);
        itemsTitleLabel = new JLabel("Max. Special Shop Items: " + starSystem.maxShopSpecials);
        itemsTitleLabel.setBackground(normalBackgroundColor);
        itemsTitleLabel.setForeground(Color.WHITE);
        itemsTitleLabel.setFont(planetSmallTitleFont);
        itemsTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(itemsTitleLabel, constraints);
        itemsTitleLabel.setVisible(false);

        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.insets = new Insets(1, 4, 0, 1);
        descriptionTitleLabel = new JLabel("Description");
        descriptionTitleLabel.setBackground(normalBackgroundColor);
        descriptionTitleLabel.setForeground(Color.WHITE);
        descriptionTitleLabel.setFont(planetSmallTitleFont);
        descriptionTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(descriptionTitleLabel, constraints);
        descriptionTitleLabel.setVisible(false);
        mainGridRow++;
        constraints.gridy = mainGridRow;
        constraints.insets = new Insets(2, 8, 2, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        descriptionText = new JTextArea(starSystem.details);
        descriptionText.setBackground(highlightBackgroundColor);
        descriptionText.setForeground(Color.WHITE);
        descriptionText.setFont(planetTextTitleFont);
        descriptionText.setEditable(false);
        descriptionText.setFocusable(false);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setLineWrap(true);
        add(descriptionText, constraints);
        descriptionText.setVisible(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                activated = true;
                setBackground(highlightBackgroundColor);
                titlePanel.setBackground(highlightBackgroundColor);
                nameLabel.setFont(planetBigTitleFont);
                skullLabel.setVisible(true);
                employerTitleLabel.setVisible(true);
                employersPanel.setVisible(true);
                targetTitleLabel.setVisible(true);
                targetsPanel.setVisible(true);
                propertiesTitleLabel.setVisible(true);
                propertiesLabel.setVisible(true);
                if (MSSettings.showPlanetDescription) {
                    descriptionTitleLabel.setVisible(true);
                    descriptionText.setVisible(true);
                }
                if (MSSettings.showShopItems) {
                    itemsTitleLabel.setVisible(true);
                }
                if (activeSystem == null) {
                    activeSystem = starSystem;
                    mapPanel.revalidate();
                    mapPanel.repaint();
                }
                if ((activeSystem != null) && (starSystem != activeSystem)) {
                    activeSystem = starSystem;
                    mapPanel.revalidate();
                    mapPanel.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                activated = false;
                setBackground(normalBackgroundColor);
                titlePanel.setBackground(normalBackgroundColor);
                nameLabel.setFont(planetSmallTitleFont);
                skullLabel.setVisible(false);
                employerTitleLabel.setVisible(false);
                employersPanel.setVisible(false);
                targetTitleLabel.setVisible(false);
                targetsPanel.setVisible(false);
                propertiesTitleLabel.setVisible(false);
                propertiesLabel.setVisible(false);
                itemsTitleLabel.setVisible(false);
                descriptionTitleLabel.setVisible(false);
                descriptionText.setVisible(false);
//            if((activeSystem != null) && (activeSystem == starSystem)) {
                activeSystem = null;
                MSSettings.myFrame.revalidate();
                MSSettings.myFrame.repaint();
//            }
            }
        });
    }

    public StarSystem getStarSystem() { return starSystem; }
}


public class StarMap extends JLayeredPane {
    Font titleFont = new Font("Arial", Font.BOLD, 12);
    int starSize = 16;
    int maxDrawnX = 1000;
    int maxDrawnY = 800;
    JLabel titleLabel;
    JButton optionsButton;
    boolean isOptionsExpanded;
    JButton zoomInButton;
    JButton zoomOutButton;
    JPanel skullPanel;
    JCheckBox skullBox;
    JPanel marketPanel;
    JCheckBox marketBox;
    JPanel starleaguePanel;
    JCheckBox starleaguBox;
    JPanel detailsPanel;
    public JCheckBox detailsBox;
    JPanel shopPanel;
    public JCheckBox shopBox;
    JButton saveButton;
    JButton folderButton;
    ImageIcon optionsIcon;
    ImageIcon zoomInIcon;
    ImageIcon zoomOutIcon;
    ImageIcon saveIcon;
    ImageIcon folderIcon;
    ImageIcon skullImage;
    ImageIcon skullDarkImage;
    ImageIcon starleagueImage;
    ImageIcon starleagueDarkImage;
    ImageIcon blackmarketImage;
    ImageIcon blackmarketDarkImage;
    ImageIcon activeStarSystemImage;

    StarMap() {
        super();

        setBackground(Color.BLACK);
        setOpaque(true);

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, 1);


        try {
            optionsIcon = new ImageIcon(StarMap.class.getResource("options.png"));
            zoomInIcon = new ImageIcon(StarMap.class.getResource("ZoomInSm.png"));
            zoomOutIcon = new ImageIcon(StarMap.class.getResource("ZoomOutSm.png"));
            saveIcon = new ImageIcon(StarMap.class.getResource("save.png"));
            folderIcon = new ImageIcon(StarMap.class.getResource("folder.png"));
            skullImage = new ImageIcon(StarMap.class.getResource("skull.png"));
            skullDarkImage = new ImageIcon(StarMap.class.getResource("skullDark.png"));
            starleagueImage = new ImageIcon(StarMap.class.getResource("starleague.png"));
            starleagueDarkImage = new ImageIcon(StarMap.class.getResource("starleagueDark.png"));
            blackmarketImage = new ImageIcon(StarMap.class.getResource("blackmarket.png"));
            blackmarketDarkImage = new ImageIcon(StarMap.class.getResource("blackmarketDark.png"));
            activeStarSystemImage = new ImageIcon(StarMap.class.getResource("star.png"));
        } catch (Exception e) { System.out.println(e.toString()); }
        optionsButton = new JButton();
        if (optionsIcon != null) {
            optionsButton.setIcon(optionsIcon);
        }
        else { optionsButton.setText("Options"); }
        optionsButton.setMargin(new Insets(0, 0, 0, 0));
        optionsButton.setFocusPainted(false);
        optionsButton.setBackground(Color.BLACK);
        add(optionsButton, 1);
        isOptionsExpanded = false;
        optionsButton.addActionListener(e -> {
            if (isOptionsExpanded) {
                zoomInButton.setVisible(false);
                zoomOutButton.setVisible(false);
                skullPanel.setVisible(false);
                marketPanel.setVisible(false);
                starleaguePanel.setVisible(false);
                detailsPanel.setVisible(false);
                shopPanel.setVisible(false);
                saveButton.setVisible(false);
                folderButton.setVisible(false);
            }
            else {
                zoomInButton.setVisible(true);
                zoomOutButton.setVisible(true);
                skullPanel.setVisible(true);
                marketPanel.setVisible(true);
                starleaguePanel.setVisible(true);
                detailsPanel.setVisible(true);
                shopPanel.setVisible(true);
                saveButton.setVisible(true);
                folderButton.setVisible(true);
            }
            isOptionsExpanded = !isOptionsExpanded;
            revalidate();
            repaint();
        });
        zoomInButton = new JButton();
        if (zoomInIcon != null) {
            zoomInButton.setIcon(zoomInIcon);
        }
        else {
            zoomInButton.setText("+");
            zoomInButton.setForeground(Color.WHITE);
        }
        zoomInButton.setMargin(new Insets(0, 0, 0, 0));
        zoomInButton.setFocusPainted(false);
        zoomInButton.setBackground(Color.BLACK);
        zoomInButton.setVisible(false);
        zoomInButton.addActionListener(e -> {
            MSSettings.mapStretchFactor = MSSettings.mapStretchFactor + 0.5;
            if (MSSettings.mapStretchFactor <= 0.5) { zoomInButton.setEnabled(false); }
            this.revalidate();
            this.repaint();
        });

        zoomOutButton = new JButton();
        if (zoomOutIcon != null) {
            zoomOutButton.setIcon(zoomOutIcon);
        }
        else {
            zoomOutButton.setText("-");
            zoomOutButton.setForeground(Color.WHITE);
        }
        zoomOutButton.setMargin(new Insets(0, 0, 0, 0));
        zoomOutButton.setFocusPainted(false);
        zoomOutButton.setBackground(Color.BLACK);
        zoomOutButton.setVisible(false);
        zoomOutButton.addActionListener(e -> {
            MSSettings.mapStretchFactor = MSSettings.mapStretchFactor - 0.5;
            this.revalidate();
            this.repaint();
        });
        add(zoomInButton, 1);
        add(zoomOutButton, 1);

        skullPanel = new JPanel();
        skullPanel.setLayout(new BoxLayout(skullPanel, BoxLayout.PAGE_AXIS));
        skullPanel.setBackground(Color.BLACK);
        skullBox = new JCheckBox("Show Skulls");
        skullBox.setAlignmentX(0.0f);
        skullBox.setForeground(Color.WHITE);
        skullBox.setBackground(Color.BLACK);
        skullBox.setOpaque(false);
        skullBox.setSelected(MSSettings.showSkulls);
        skullBox.addActionListener(e -> {
            MSSettings.showSkulls = skullBox.isSelected();
            this.revalidate();
            this.repaint();
        });
        skullPanel.add(skullBox);
        skullPanel.setVisible(false);

        marketPanel = new JPanel();
        marketPanel.setLayout(new BoxLayout(marketPanel, BoxLayout.PAGE_AXIS));
        marketPanel.setBackground(Color.BLACK);
        marketBox = new JCheckBox("Show Black Markets");
        marketBox.setAlignmentX(0.0f);
        marketBox.setForeground(Color.WHITE);
        marketBox.setBackground(Color.BLACK);
        marketBox.setOpaque(false);
        marketBox.setSelected(MSSettings.showBlackMarkets);
        marketBox.addActionListener(e -> {
            MSSettings.showBlackMarkets = marketBox.isSelected();
            this.revalidate();
            this.repaint();
        });
        marketPanel.add(marketBox);
        marketPanel.setVisible(false);

        starleaguePanel = new JPanel();
        starleaguePanel.setLayout(new BoxLayout(starleaguePanel, BoxLayout.PAGE_AXIS));
        starleaguePanel.setBackground(Color.BLACK);
        starleaguBox = new JCheckBox("Show Starleague");
        starleaguBox.setAlignmentX(0.0f);
        starleaguBox.setForeground(Color.WHITE);
        starleaguBox.setBackground(Color.BLACK);
        starleaguBox.setOpaque(false);
        starleaguBox.setSelected(MSSettings.showStarleague);
        starleaguBox.addActionListener(e -> {
            MSSettings.showStarleague = starleaguBox.isSelected();
            this.revalidate();
            this.repaint();
        });
        starleaguePanel.add(starleaguBox);
        starleaguePanel.setVisible(false);

        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.PAGE_AXIS));
        detailsPanel.setBackground(Color.BLACK);
        detailsBox = new JCheckBox("Show planet description in side panel");
        detailsBox.setAlignmentX(0.0f);
        detailsBox.setForeground(Color.WHITE);
        detailsBox.setBackground(Color.BLACK);
        detailsBox.setOpaque(false);
        detailsBox.setSelected(MSSettings.showPlanetDescription);
        detailsBox.addActionListener(e -> {
            MSSettings.showPlanetDescription = detailsBox.isSelected();
            this.revalidate();
            this.repaint();
        });
        detailsPanel.add(detailsBox);
        detailsPanel.setVisible(false);

        shopPanel = new JPanel();
        shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.PAGE_AXIS));
        shopPanel.setBackground(Color.BLACK);
        shopBox = new JCheckBox("Show max. shop items in side panel");
        shopBox.setAlignmentX(0.0f);
        shopBox.setForeground(Color.WHITE);
        shopBox.setBackground(Color.BLACK);
        shopBox.setOpaque(false);
        shopBox.setSelected(MSSettings.showShopItems);
        shopBox.addActionListener(e -> {
            MSSettings.showShopItems = shopBox.isSelected();
            this.revalidate();
            this.repaint();
        });
        shopPanel.add(shopBox);
        shopPanel.setVisible(false);

        saveButton = new JButton();
        if (saveIcon != null) { saveButton.setIcon(saveIcon); }
        saveButton.setForeground(Color.WHITE);
        saveButton.setHorizontalAlignment(SwingConstants.LEFT);
        saveButton.setText("Save Map");
        saveButton.setMargin(new Insets(0, 0, 0, 0));
        saveButton.setFocusPainted(false);
        saveButton.setBackground(Color.BLACK);
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> {
            new SaveMapDialog(MSSettings.myFrame);
        });

        folderButton = new JButton();
        if (folderIcon != null) { folderButton.setIcon(folderIcon); }
        folderButton.setForeground(Color.WHITE);
        folderButton.setHorizontalAlignment(SwingConstants.LEFT);
        folderButton.setText("Open BT folder");
        folderButton.setMargin(new Insets(0, 0, 0, 0));
        folderButton.setFocusPainted(false);
        folderButton.setBackground(Color.BLACK);
        folderButton.setVisible(false);
        folderButton.addActionListener(e -> {
            File f = BTstats.getInputFile(false, MSSettings.myFrame);
            if (f != null) {
                MSSettings.lastBTdir = f;

                stats = new BTstats(MSSettings.myFrame);
                fillFrame();
            }
        });

        add(skullPanel, 1);
        add(marketPanel, 1);
        add(starleaguePanel, 1);
        add(detailsPanel, 1);
        add(shopPanel, 1);
        add(saveButton, 1);
        add(folderButton, 1);
    }

    @Override
    public Dimension getPreferredSize() { return new Dimension(maxDrawnX, maxDrawnY); }


    @Override
    public void paintComponent(Graphics oldGraphics) {
        super.paintComponent(oldGraphics);
        maxDrawnX = 0;
        maxDrawnY = 0;

        if (stats.starSystems != null) {
            titleLabel.setText("Showing " + stats.starSystemSelection.size() + " of " + stats.starSystems.size() + " systems");
            int yNew = 20;
            int yMargin = 4;
            int yHeight = 25;
            titleLabel.setBounds(6, yNew, 250, yHeight);
            yHeight = 26;
            yMargin = 8;
            yNew = yNew + yMargin + yHeight;
            optionsButton.setBounds(10, yNew, 26, yHeight);
//            skullPanel.setBounds(10, 300, 120, yHeight);
//            if(isOptionsExpanded) {
            yNew = yNew + yMargin + yHeight;
            zoomInButton.setBounds(10, yNew, 26, yHeight);
            zoomOutButton.setBounds(40, yNew, 26, yHeight);
            yMargin = 6;
            yNew = yNew + yMargin + yHeight;
            yHeight = 22;
            skullPanel.setBounds(10, yNew, 110, yHeight);
            yNew = yNew + yMargin + yHeight;
            marketPanel.setBounds(10, yNew, 150, yHeight);
            yNew = yNew + yMargin + yHeight;
            starleaguePanel.setBounds(10, yNew, 130, yHeight);
            yMargin = 4;
            yNew = yNew + yMargin + yHeight;
            shopPanel.setBounds(10, yNew, 250, yHeight);
            yNew = yNew + yMargin + yHeight;
            detailsPanel.setBounds(10, yNew, 250, yHeight);
            yNew = yNew + yMargin + yHeight;
            yHeight = 26;
            saveButton.setBounds(10, yNew, 96, yHeight);
            yNew = yNew + yMargin + yHeight;
            folderButton.setBounds(10, yNew, 126, yHeight);
//            }

            Graphics2D g = (Graphics2D) oldGraphics;

            /** Enable anti-aliasing and pure stroke */
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            /** Highlight selected star */
            if (activeSystem != null) {
                Point p = convertCoordinateToCanvas(activeSystem.positionX, activeSystem.positionY);
                activeStarSystemImage.paintIcon(this, g, (p.x - 42), (p.y - 40));
            }
            for (StarSystem s : stats.starSystemSelection) { drawStar(g, s, false, true, skullBox.isSelected(), marketBox.isSelected(), starleaguBox.isSelected()); }
        }
    }

    public void drawStar(Graphics2D g, StarSystem s, boolean drawWhite, boolean drawFactionColors, boolean drawSkulls, boolean drawBlackMarket, boolean drawStarleague) {
        Point p = convertCoordinateToCanvas(s.positionX, s.positionY);

        if (drawWhite) {
            if(drawFactionColors) { g.setColor(s.owner.getDarkColor()); }
            else { g.setColor(Color.BLACK); }
            g.fillOval(p.x, p.y, starSize, starSize);

            FontMetrics metrics = g.getFontMetrics(titleFont);
            int stringWidth = metrics.stringWidth(s.name);
            int titleX = p.x - (int) Math.ceil((double) stringWidth / 2) + 5;
            g.setFont(titleFont);
            g.drawString(s.name, titleX, p.y - 5);

            if ((p.x + stringWidth) > maxDrawnX) { maxDrawnX = p.x + stringWidth; }
            if ((p.y + starSize) > maxDrawnY) { maxDrawnY = p.y + starSize; }

            if (drawSkulls && (skullDarkImage != null)) {
                skullDarkImage.paintIcon(this, g, (p.x + (starSize) + 2), (p.y - 2));
                g.setColor(new Color(169,69,18));
                g.drawString(s.difficulty.getDisplayName(), (p.x + starSize + 24), (p.y + 13));
            }

            int xOffset = 8;
            if (s.tags.contains(MSSettings.TagItem.BLACKMARKET) && drawBlackMarket && (blackmarketDarkImage != null)) {
                blackmarketDarkImage.paintIcon(this, g,  (p.x - (starSize) - xOffset), (p.y - 2));
                xOffset += 22;
            }
            if (s.tags.contains(MSSettings.TagItem.STARLEAGUE) && drawStarleague && (starleagueDarkImage != null)) {
                starleagueDarkImage.paintIcon(this, g, (p.x - (starSize) - xOffset - 4), (p.y - 2));
            }
        }
        else {
            if(drawFactionColors) { g.setColor(s.owner.getColor()); }
            else { g.setColor(Color.WHITE); }
            g.fillOval(p.x, p.y, starSize, starSize);

            FontMetrics metrics = g.getFontMetrics(titleFont);
            int stringWidth = metrics.stringWidth(s.name);
            int titleX = p.x - (int) Math.ceil((double) stringWidth / 2) + 5;
            g.setFont(titleFont);
            g.drawString(s.name, titleX, p.y - 5);

            if ((p.x + stringWidth) > maxDrawnX) { maxDrawnX = p.x + stringWidth; }
            if ((p.y + starSize) > maxDrawnY) { maxDrawnY = p.y + starSize; }

            if (drawSkulls && (skullImage != null)) {
                skullImage.paintIcon(this, g, (p.x + (starSize) + 2), (p.y - 2));
                g.setColor(new Color(151, 65, 17));
                g.drawString(s.difficulty.getDisplayName(), (p.x + starSize + 24), (p.y + 13));
            }

            int xOffset = 8;
            if (s.tags.contains(MSSettings.TagItem.BLACKMARKET) && drawBlackMarket && (blackmarketImage != null)) {
                blackmarketImage.paintIcon(this, g, (p.x - (starSize) - xOffset), (p.y - 2));
                xOffset += 22;
            }
            if (s.tags.contains(MSSettings.TagItem.STARLEAGUE) && drawStarleague && (starleagueImage != null)) {
                starleagueImage.paintIcon(this, g,(p.x - (starSize) - xOffset - 4), (p.y - 2));
            }
        }
    }


    public class SaveMapDialog extends JDialog implements ActionListener {
        JCheckBox skullCheck;
        JCheckBox blackMarketCheck;
        JCheckBox starleagueCheck;
        JCheckBox factionColorCheck;
        JCheckBox backgroundWhiteCheck;
        JTextField fileText;
        JButton chooseFileButton;
        File outputFile;
        JButton saveButton;
        JButton cancelButton;

        public SaveMapDialog(JFrame parentFrame) {
            super(parentFrame);
            setTitle("Save Map");
            setModal(true);
            setModalityType(ModalityType.DOCUMENT_MODAL);

            JPanel mainPanel = new JPanel(new GridBagLayout());

            int gridRow = 0;
            skullCheck = new JCheckBox("Skull Rating");
            skullCheck.setSelected(mapPanel.skullBox.isSelected());
            blackMarketCheck = new JCheckBox("Black Markets");
            blackMarketCheck.setSelected(mapPanel.marketBox.isSelected());
            starleagueCheck = new JCheckBox("Former Star League");
            starleagueCheck.setSelected(mapPanel.starleaguBox.isSelected());
            Box tagBox = Box.createHorizontalBox();
            tagBox.add(skullCheck);
            tagBox.add(blackMarketCheck);
            tagBox.add(starleagueCheck);
            tagBox.setBorder(BorderFactory.createTitledBorder("<html><div>Show these Tags:</div></html>"));
            MSSettings.addGridBagItem(mainPanel, tagBox, 0, gridRow, 3, 1, GridBagConstraints.FIRST_LINE_START);

            gridRow++;
            factionColorCheck = new JCheckBox("Draw Faction Colors");
            factionColorCheck.setSelected(true);
            factionColorCheck.addActionListener(this);
            backgroundWhiteCheck = new JCheckBox("Draw White Background");
            backgroundWhiteCheck.setSelected(MSSettings.mapWhiteBackground);
            backgroundWhiteCheck.addActionListener(this);
            Box colorBox = Box.createHorizontalBox();
            colorBox.add(factionColorCheck);
            colorBox.add(backgroundWhiteCheck);
            colorBox.setBorder(BorderFactory.createTitledBorder("<html><div>Map Color Options:</div></html>"));
            MSSettings.addGridBagItem(mainPanel, colorBox, 0, gridRow, 3, 1, GridBagConstraints.FIRST_LINE_START);

            gridRow++;
            fileText = new JTextField(40);
            fileText.setEditable(false);
            if ((MSSettings.lastMapFile == null) || !Files.exists(MSSettings.lastMapFile.toPath())) {
                boolean outputFound = false;
                int i = 0;
                while (!outputFound) {
                    if (i == 0) { outputFile = new File(System.getProperty("user.dir") + "\\map.png"); }
                    else { outputFile = new File(System.getProperty("user.dir") + "\\map" + i + ".png"); }

                    if (outputFile.exists()) { i++; }
                    else {
                        fileText.setText(outputFile.getAbsolutePath());
                        outputFound = true;
                    }
                }
            }
            else {
                fileText.setText(MSSettings.lastMapFile.getAbsolutePath());
                System.out.println("there");
            }
//        fileText.setText("some text");
            chooseFileButton = new JButton(UIManager.getIcon("FileView.fileIcon"));
            chooseFileButton.addActionListener(this);
            Box fileBox = Box.createHorizontalBox();
            fileBox.add(fileText);
            fileBox.add(chooseFileButton);
            fileBox.setBorder(BorderFactory.createTitledBorder("<html><div>Choose Output File:</div></html>"));
            MSSettings.addGridBagItem(mainPanel, fileBox, 0, gridRow, 3, 1, GridBagConstraints.FIRST_LINE_START);


            gridRow++;
            saveButton = new JButton("Save");
            saveButton.addActionListener(this);
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            Box buttonBox = Box.createHorizontalBox();
            buttonBox.add(saveButton);
            buttonBox.add(Box.createHorizontalStrut(20));
            buttonBox.add(cancelButton);
            MSSettings.addGridBagItem(mainPanel, buttonBox, 1, gridRow, 1, 1, GridBagConstraints.NORTH);

            add(mainPanel);
            setSize(600, 400);
            setResizable(false);
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == factionColorCheck) { MSSettings.factionColorCheck = factionColorCheck.isSelected(); }
            else if (source == backgroundWhiteCheck) { MSSettings.mapWhiteBackground = backgroundWhiteCheck.isSelected(); }
            else if (source == chooseFileButton) {
                JFileChooser fc = new JFileChooser() {
                    @Override
                    public void approveSelection() {
                        File selFile = getSelectedFile();
                        File finalFile = null;
                        String fName = selFile.getName();
                        if (fName.length() < 4) { finalFile = new File(selFile.getAbsolutePath() + ".png"); }
                        if (fName.length() == 4) {
                            if (fName.equalsIgnoreCase(".png")) {
                                JOptionPane.showMessageDialog(this, "Please enter a valid file name!",
                                                              "Invalid File Name", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            else { finalFile = new File(selFile.getAbsolutePath() + ".png"); }
                        }
                        else if (fName.length() > 4) {
                            String fsubString = selFile.getName().substring(selFile.getName().length() - 4);
                            if (!fsubString.equalsIgnoreCase(".png")) {
                                finalFile = new File(selFile.getAbsolutePath() + ".png");
                            }
                            else { finalFile = selFile; }
                        }
                        if (finalFile.exists() && getDialogType() == SAVE_DIALOG) {
                            int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                            switch (result) {
                                case JOptionPane.YES_OPTION:
                                    super.approveSelection();
                                    return;
                                case JOptionPane.NO_OPTION:
                                    return;
                                case JOptionPane.CLOSED_OPTION:
                                    return;
                                case JOptionPane.CANCEL_OPTION:
                                    cancelSelection();
                                    return;
                            }
                        }
                        setSelectedFile(finalFile);
                        super.approveSelection();
                    }
                };
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if ((MSSettings.lastMapFile == null) || !Files.exists(MSSettings.lastMapFile.toPath())) {
                    fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                }
                else { fc.setCurrentDirectory(MSSettings.lastMapFile); }

//        // Enable "Details" view in file chooser:
                Action details = fc.getActionMap().get("viewTypeDetails");
                details.actionPerformed(null);

                int returnVal = fc.showSaveDialog(this);
//            int returnVal = fc.showSaveDialog(fc);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    outputFile = fc.getSelectedFile();
                    fileText.setText(fc.getSelectedFile().toString());
                }
            }
            else if (source == cancelButton) { this.dispose(); }
            else if (source == saveButton) {
                int mapWidth = maxDrawnX + 20;
                int mapHeight = maxDrawnY + 10;
                BufferedImage paintImage = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g = paintImage.createGraphics();
                if(backgroundWhiteCheck.isSelected()) {
                    g.setPaint(new Color(255,255,255));
                    g.fillRect(0,0, mapWidth, mapHeight);
                }
                else {
                    g.setPaint(new Color(0,0,0));
                    g.fillRect(0,0, mapWidth, mapHeight);
                }
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                for (StarSystem s : stats.starSystemSelection) { drawStar(g, s, backgroundWhiteCheck.isSelected(), factionColorCheck.isSelected(), skullCheck.isSelected(), blackMarketCheck.isSelected(), starleagueCheck.isSelected()); }
                try {
                    ImageIO.write(paintImage, "PNG", outputFile);
                }
                catch(IOException ioe) { System.err.println(ioe.toString()); }
                g.dispose();
                this.dispose();
            }
        }
    }
}

}
