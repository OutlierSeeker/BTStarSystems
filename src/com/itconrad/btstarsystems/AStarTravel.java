package com.itconrad.btstarsystems;

import java.util.ArrayList;
import java.util.Collections;

public class AStarTravel {
StarSystem originSystem;
StarSystem destinationSystem;
//BubbleSortLinkedList openList;
ArrayList<AStarNode> openList;
ArrayList<AStarNode> closedList;
BTstats stats;
boolean pathComplete;
ArrayList<StarSystem> travelRoute;

public AStarTravel(StarSystem origin, StarSystem destination) {
//    this.stats = stats;
    originSystem = origin;
    destinationSystem = destination;
//    openList = new BubbleSortLinkedList();
    openList = new ArrayList<>();
    closedList = new ArrayList<>();
    pathComplete = false;
    travelRoute = new ArrayList<>();

    InitFindPath();
    runAStar();
}

public void InitFindPath() {
    ArrayList<StarSystem> travelRoute = new ArrayList<>();
    travelRoute.add(originSystem);
    if(originSystem == destinationSystem) { pathComplete = true; }
    else {
        openList.add(new AStarNode(originSystem, null, costMagnitueAndScale(MSSettings.getSquaredDistance(originSystem, destinationSystem)), 0.0f, false));
    }
}

public void runAStar() {
    AStarNode aStarNode;
    double value;
    while(!pathComplete && !openList.isEmpty()) {
        /** DequeueBest() : */
        aStarNode = null;
        value = Double.MAX_VALUE;
        for(AStarNode a : openList) {
            if(a.f < value) {
                aStarNode = a;
                value = a.f;
            }
        }
        closedList.add(aStarNode);
        openList.remove(aStarNode);
        if(aStarNode.system == destinationSystem) {
            pathComplete = true;
            // TODO finalize result!
            travelRoute.add(aStarNode.system);
            AStarNode pathNode = aStarNode;
            while(pathNode.previousNode != null) {
                travelRoute.add(pathNode.previousNode.system);
                pathNode = pathNode.previousNode;
            }
            Collections.reverse(travelRoute);
            if(MSSettings.useLog) {
                StringBuilder sb = new StringBuilder();
                sb.append(travelRoute.get(0).name);
                if(travelRoute.size() > 1) {
                    for(int i = 1; i < travelRoute.size(); i++) {
                        sb.append(" - " + travelRoute.get(i).name);
                    }
                }
//                MSSettings.Logger.logEntry(sb.toString(), MSSettings.MessageLevel.INFO, this.getClass().getSimpleName() + "." + new Object(){}.getClass().getEnclosingMethod().getName());
            }
        }
        else {
            float num3 = 3; /** default cost for GetConnectionAndCost(i, out num3) */
            for (StarSystem s : MSSettings.getNearbySystems(aStarNode.system)) {
                if (!inClosedList(s)) {
                    /** redundant:
                     float num4 = aStarNode.g + num3;
                     float num5 = costMagnitueAndScale(MSSettings.getSquaredDistance(s, destinationSystem));
                     float fscore = num4 + num5; */
                    AStarNode aStarNode3 = new AStarNode(s, aStarNode,
                                                         (aStarNode.g + num3 + costMagnitueAndScale(MSSettings.getSquaredDistance(s, destinationSystem))), (aStarNode.g + num3), false);
                    openList.add(aStarNode3);

                }
            }
        }
    }
}

/**
 * Returns the estimated number of jumps for this distance.
 * @param squaredDistanceLY squared distance in LY
 * @return estimated number of jumps for this distance.
 */
public float costMagnitueAndScale(double squaredDistanceLY) {
    /** alternatives: */
//    return Math.ceil(squaredDistanceLY / MSSettings.squaredJumpRange);
//    return Math.sqrt(squaredDistanceLY / MSSettings.squaredJumpRange);
    return Math.round(Math.sqrt(squaredDistanceLY)) / 1e06f;
}

public boolean inClosedList(StarSystem starSystem) {
    for(AStarNode a : closedList) {
        if(a.system == starSystem) { return true; }
    }
    return false;
}


public AStarNode dequeueBest() {
    /** Better with Bubble Sort!? */
    AStarNode best = null;
    double value = Double.MAX_VALUE;
    for(AStarNode a : openList) {
        if(a.f < value) {
            best = a;
            value = a.f;
        }
    }
    openList.remove(best);
    return best;
}

public class AStarNode {
    StarSystem system;
    AStarNode previousNode;
    float f, g;
    boolean closed;

    public AStarNode(StarSystem system, AStarNode previousNode, float f, float g, boolean closed) {
        this.system = system;
        this.previousNode = previousNode;
        this.f = f;
        this.g = g;
        this.closed = closed;
    }
}
}
