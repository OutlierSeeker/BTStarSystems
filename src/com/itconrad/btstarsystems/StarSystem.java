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

import java.util.ArrayList;

public class StarSystem {
String name;
String details;
double positionX;
double positionY;
double positionZ;
int jumpDistance;
MSSettings.OwnerFactions owner;
int maxShopSpecials;
MSSettings.Difficulties difficulty;
ArrayList<MSSettings.Factions> employers;
ArrayList<MSSettings.Factions> targets;
ArrayList<MSSettings.TagItem> tags;
ArrayList<StarSystem> closeIntermediarySystems;

public StarSystem(String name, String details, double positionX, double positionY, double positionZ, long jumpDistance, MSSettings.OwnerFactions owner, long maxShopSpecials,
                  long difficulty, ArrayList<MSSettings.TagItem> tags, ArrayList<MSSettings.Factions> employers, ArrayList<MSSettings.Factions> targets) {
    this.name = name;
    this.details = details;
    this.positionX = positionX;
    this.positionY = positionY;
    this.positionZ = positionZ;
    this.jumpDistance = (int)jumpDistance;
    this.owner = owner;
    this.maxShopSpecials = (int)maxShopSpecials;
    switch ((int)difficulty) {
        case 1: {
            this.difficulty = MSSettings.Difficulties.ZERO_HALF;
            break;
        }
        case 2: {
            this.difficulty = MSSettings.Difficulties.ONE;
            break;
        }
        case 3: {
            this.difficulty = MSSettings.Difficulties.ONE_HALF;
            break;
        }
        case 4: {
            this.difficulty = MSSettings.Difficulties.TWO;
            break;
        }
        case 5: {
            this.difficulty = MSSettings.Difficulties.TWO_HALF;
            break;
        }
        case 6: {
            this.difficulty = MSSettings.Difficulties.THREE;
            break;
        }
        case 7: {
            this.difficulty = MSSettings.Difficulties.THREE_HALF;
            break;
        }
        case 8: {
            this.difficulty = MSSettings.Difficulties.FOUR;
            break;
        }
        case 9: {
            this.difficulty = MSSettings.Difficulties.FOUR_HALF;
            break;
        }
        default: {
            this.difficulty = MSSettings.Difficulties.ZERO_HALF;
            break;
        }
    }
//    this.difficulty = (int)difficulty;
    this.tags = tags;
    this.employers = employers;
    this.targets = targets;
    closeIntermediarySystems = new ArrayList<>();
}

public boolean hasOwner(MSSettings.OwnerFactions ownderFaction) { return ownderFaction.equals(owner); }
public boolean hasEmployer(MSSettings.Factions faction) { return employers.contains(faction); }
public boolean hasTarget(MSSettings.Factions faction) { return targets.contains(faction); }
public boolean hasTag(MSSettings.TagItem tag) { return tags.contains(tag); }

@Override
public String toString() {
//    return name + " : " + owner.getDisplayName() + " - jumpDistance: " + jumpDistance + ", difficulty: " + difficulty.getDisplayName();
    return name;
}
}
