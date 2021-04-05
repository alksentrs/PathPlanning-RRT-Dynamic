package business;

import util.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Node extends Point2D {

    private static int count;

    private Node parent;
    private Node parentOptimized;
    private double distance;
    private List<Node> childList = new ArrayList<>();
    private List<Node> childOptimizedList = new ArrayList<>();
    private int id;

    private double helper = 0;

    public Node(Node parent, Point2D point){
        super(point);
        this.parent = parent;
        this.distance = parent.getDistance() + parent.distance(this);
        id = count++;
    }

    public Node(Point2D point){
       super(point);
        this.parent = null;
        this.distance = 0;
        id = count++;
    }

    public Node getParentOptimized() {
        return parentOptimized;
    }

    public void setParentOptimized(Node parentOptimized) {
        this.parentOptimized = parentOptimized;
    }

    public int getId() {
        return id;
    }

    public void removeFromParentChildList() {
        if (null!=parent) parent.removeChild(this);
    }

    public void addToParentChildList() {
        if (null!=parent) parent.addChild(this);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setPoint(Point2D point) {
        setLocation(point);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
        for (int i=0; i<childList.size(); i++) {
            Node child = childList.get(i);
            child.setDistance(distance+distance(child));
        }
    }

    public double getHelper() {
        return helper;
    }

    public void setHelper(double helper) {
        this.helper = helper;
    }

    public List<Node> getChildList() {
        return childList;
    }

    public void addChild(Node node) {
        childList.add(node);
    }

    public void addChildOptimized(Node node) {
        childOptimizedList.add(node);
    }

    public void removeChild(Node node) {
        childList.remove(node);
    }


    @Override
    public String toString() {

        String childSt = "{";
        for (int i=0; i<childList.size(); i++) childSt = childSt + childList.get(i).id + " ";
        childSt = childSt + "}";

        return "{\"id\" : " + id + ",\"distance\" : " + distance + ",\"x\" : " + getX() + ",\"y\" : " + getY() + ",\"parent\" : " + (parent!=null ? parent.id : "null") + ",\"child\" : " + childSt+ "}";
    }
}

