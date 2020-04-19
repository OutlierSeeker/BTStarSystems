package com.itconrad.btstarsystems;

public class BubbleSortLinkedList {
BubbleElement startElement;
boolean sorted;
int addedElements;

public BubbleSortLinkedList(BubbleElement startElement) {
    this.startElement = startElement;
    sorted = true;
    addedElements = 0;
}

public BubbleSortLinkedList() {
    this.startElement = null;
    sorted = true;
    addedElements = 0;
}

public void add(BubbleElement element) {
    if(startElement == null) { startElement = element; }
    else {
        element.nextElement = startElement;
        startElement = element;
        sorted = false;
        addedElements++;
    }
}

public void remove(BubbleElement element) {
    boolean removed = false;
    if(startElement == element) {
        if(startElement.nextElement == null) { startElement = null; sorted = true; addedElements = 0; }
        else { startElement = startElement.nextElement;  }
    }
    else {
        if(startElement.nextElement != null) {
            BubbleElement previousElement = startElement;
            BubbleElement currentElement = startElement.nextElement;
            boolean found = false;
            while(!found && (currentElement != null)) {
                if(currentElement == element) {
                    previousElement.nextElement = currentElement.nextElement;
                    found = true;
                }
                else {
                    previousElement = currentElement;
                    currentElement = previousElement.nextElement;
                }
            }
        }
    }
}

public void bubbleSort(boolean ascending) {
    if(addedElements > 1) { bubbleSortComplete(ascending); }
    else {  /** Only 1 element has been added as start element: */
        BubbleElement currentElement, previousElement, tempNext;
        currentElement = startElement;
        previousElement = null;
        while ((currentElement.nextElement != null) && !sorted) {
            if ((ascending && (currentElement.value <= currentElement.nextElement.value)) ||
                (!ascending && (currentElement.value >= currentElement.nextElement.value))) {
                sorted = true;
            }
            else if ((ascending && (currentElement.value > currentElement.nextElement.value)) ||
                    (!ascending && (currentElement.value < currentElement.nextElement.value)))
            {
                if(previousElement != null) { previousElement.nextElement = currentElement.nextElement; }
                else { startElement = currentElement.nextElement; }
                tempNext = currentElement.nextElement.nextElement;
                currentElement.nextElement.nextElement = currentElement;
                currentElement.nextElement = tempNext;
            }
        }
        sorted = true;
        addedElements = 0;
    }
}

private void bubbleSortComplete(boolean ascending) {
    BubbleElement currentElement, previousElement, tempNext;
    while(!sorted) {
        currentElement = startElement;
        previousElement = null;
        sorted = true;
        while (currentElement.nextElement != null) {
            if ((ascending && (currentElement.value > currentElement.nextElement.value)) ||
               (!ascending && (currentElement.value < currentElement.nextElement.value)))
            {
                if(previousElement != null) { previousElement.nextElement = currentElement.nextElement; }
                else { startElement = currentElement.nextElement; }
                previousElement = currentElement.nextElement;
                tempNext = currentElement.nextElement.nextElement;
                currentElement.nextElement.nextElement = currentElement;
                currentElement.nextElement = tempNext;
                sorted = false;
            }
            else {
                previousElement = currentElement;
                currentElement = previousElement.nextElement; }
        }
    }
    addedElements = 0;
}

@Override
public String toString() {
    if(startElement == null) { return "Empty Linked List"; }
    else {
        StringBuilder sb = new StringBuilder("[ " + startElement.value);
        BubbleElement currentElement = startElement.nextElement;
        while (currentElement != null) {
            sb.append(", " + currentElement.value);
            currentElement = currentElement.nextElement;
        }
        sb.append(" ]");
        return sb.toString();
    }
}

public static class BubbleElement {
    double value;
    // TODO make into interface:
    AStarTravel.AStarNode aStarNode;
    BubbleElement nextElement;

    public BubbleElement(double bubbleValue, AStarTravel.AStarNode bubbleStarSystem) {
        this.value = bubbleValue;
        this.aStarNode = bubbleStarSystem;
    }

    public BubbleElement(double bubbleValue, AStarTravel.AStarNode bubbleStarSystem, BubbleElement nextElement, BubbleElement prevElement) {
        this.value = bubbleValue;
        this.aStarNode = bubbleStarSystem;
        this.nextElement = nextElement;
    }
}
}
