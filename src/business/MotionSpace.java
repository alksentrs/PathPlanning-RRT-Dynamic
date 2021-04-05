package business;

import org.apache.commons.math3.linear.*;
import util.Circle2D;
import util.Line2D;
import util.Point2D;
import util.Vector2D;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotionSpace {

    private int numOfConnections = 5;

    private int multiplier = 10;
    private int optimiseDistance = 40;

    private int rRTStarFnMaximumLength = 3000;

    private Circle2D goal;
    private double goalRadius = 10;

    private Node start;

    private List<Node> nodeList = new ArrayList<>();

    private List<Node> path2Goal = new ArrayList<>();

    private List<Rectangle> obstacles = new ArrayList<>();
    private List<Rectangle> dynamicObstacles = new ArrayList<>();

    private List<List<Rectangle>> obstacleSets = new ArrayList<>();

    private double minDistance = 7 * multiplier / 10;

    private int width;
    private int height;

    private boolean smart;
    private boolean fixedNodes;
    private boolean informed;

    public MotionSpace(int width, int height, boolean informed, boolean fixedNodes, boolean smart) {
        this.width = width;
        this.height = height;
        this.informed = informed;
        this.fixedNodes = fixedNodes;
        this.smart = smart;

        List<Rectangle> r1 = new ArrayList<>();
        r1.add(new Rectangle(width / 4, 0, 20, height / 4 - 20));
        r1.add(new Rectangle(width / 4, height / 4 + 20, 20, 3 * height / 4 - 20));
        r1.add(new Rectangle(3 * width / 4, 0, 20, 3 * height / 4 - 20));
        r1.add(new Rectangle(3 * width / 4, 3 * height / 4 + 20, 20, height / 4 - 20));

        List<Rectangle> r2 = new ArrayList<>();
        r2.add(new Rectangle(width / 4, height / 8, 20, 2 * height / 8 - 20));
        r2.add(new Rectangle(width / 4, 3 * height / 8 + 20, 20, 4 * height / 8 - 20));
        r2.add(new Rectangle(3 * width / 4, height / 8, 20, 4 * height / 8 - 20));
        r2.add(new Rectangle(3 * width / 4, 5 * height / 8 + 20, 20, 2 * height / 8 - 20));
        r2.add(new Rectangle(width / 4 + 20, height / 8, 2 * width / 4 - 20, 20));
        r2.add(new Rectangle(width / 4 + 20, 7 * height / 8 - 20, 2 * width / 4 - 20, 20));

        List<Rectangle> r3 = new ArrayList<>();
        r3.add(new Rectangle(width / 4, height / 8, 20, 2 * height / 8 - 20));
        r3.add(new Rectangle(width / 4, 3 * height / 8 + 20, 20, 4 * height / 8 - 20));
        r3.add(new Rectangle(3 * width / 4, height / 8, 20, 4 * height / 8 - 20));
        r3.add(new Rectangle(3 * width / 4, 5 * height / 8 + 20, 20, 2 * height / 8 - 20));
        r3.add(new Rectangle(width / 4 + 20, height / 8, 2 * width / 4 - 20, 20));
        r3.add(new Rectangle(width / 4 + 20, 7 * height / 8 - 20, 2 * width / 4 - 20, 20));
        r3.add(new Rectangle(width / 4 - 70, height / 8, 20, 6 * height / 8));
        r3.add(new Rectangle(3 * width / 4 + 50, height / 8, 20, 6 * height / 8));

        List<Rectangle> r4 = new ArrayList<>();
        r4.add(new Rectangle(width / 4 + 20, height / 8, 2 * width / 4 - 20, 20));
        r4.add(new Rectangle(width / 4 + 20, 7 * height / 8 - 20, 2 * width / 4 - 20, 20));
        r4.add(new Rectangle(3 * width / 4, height / 8, 20, 6 * height / 8));
        r4.add(new Rectangle(width / 4, height / 8, 20, 3 * height / 8 -20));
        r4.add(new Rectangle(width / 4, 4 * height / 8 + 20, 20, 3 * height / 8 -20));

        obstacleSets.add(r1);
        obstacleSets.add(r2);
        obstacleSets.add(r3);
        obstacleSets.add(r4);

        reset();
    }

    public boolean isSmart() {
        return smart;
    }

    public void setSmart(boolean smart) {
        this.smart = smart;
    }

    public boolean isFixedNodes() {
        return fixedNodes;
    }

    public void setFixedNodes(boolean fixedNodes) {
        this.fixedNodes = fixedNodes;
    }

    public boolean isInformed() {
        return informed;
    }

    public void setInformed(boolean informed) {
        this.informed = informed;
    }

    public void reset() {
        nodeList.clear();
        path2Goal.clear();
        Point2D initialPoint = new Point2D(width / 2, height / 2);
        start = new Node(initialPoint);
        nodeList.add(new Node(initialPoint));
        generateRandomGoal();
        dynamicObstacles.clear();
    }

    private void generateRandomGoal() {
        Random r = new Random();
        Point2D goalPoint = new Point2D(r.nextInt(width), r.nextInt(height));
        while (hasCollision(goalPoint)) goalPoint = new Point2D(r.nextInt(width), r.nextInt(height));
        goal = new Circle2D(goalPoint, goalRadius);
    }

    public void addObstacle(Point2D point, double width) {
        Rectangle obstacle = new Rectangle((int)point.getX(),(int)point.getY(),(int)width,(int)width);
        dynamicObstacles.add(obstacle);
    }

    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public List<Rectangle> getDynamicObstacles() {return dynamicObstacles; }

    public List<Node> getNodes() {
        return nodeList;
    }

    public void setGoal(Circle2D circle2D) {
        this.goal = circle2D;
    }

    public Circle2D getGoal() {
        return goal;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Node getStart() {
        return start;
    }

    public List<Node> getPath2Goal() {
        return path2Goal;
    }

    public void setIncrement(int value) {
    }

    public void walk() {
        if(null!= path2Goal) {
            if (path2Goal.size()>0) {
                Node toRemove = path2Goal.remove(0);
                if (path2Goal.size()>0) start = path2Goal.get(0);
                List<Node> childList = toRemove.getChildList();
                for (int i = childList.size() - 1; i >= 0; i--) {
                    Node child = childList.get(i);
                    if (path2Goal.contains(child)) {
                        child.setParent(null);
                        child.setParentOptimized(null);
                    } else {
                        removeNodeRrtRecursivePreservePath(child);
                    }
                }
                nodeList.remove(toRemove);
            }
        }
    }

    private void removeNodeRrtRecursivePreservePath(Node node) {
        nodeList.remove(node);
        node.removeFromParentChildList();
        for (int i=node.getChildList().size()-1; i>=0; i--) {
            Node child = node.getChildList().get(i);
            if (!path2Goal.contains(child)) { //Only for Smart
                removeNodeRrtRecursivePreservePath(child);
            }
        }
    }

    private boolean hasCollision(Point2D point) {
        boolean collision = false;
        for (int j = 0; j < obstacles.size(); j++) {
            Rectangle rect = obstacles.get(j);
            if (rect.contains(point)) {
                collision = true;
                break;
            }
        }
        for (int j = 0; j < dynamicObstacles.size(); j++) {
            Rectangle rect = dynamicObstacles.get(j);
            if (rect.contains(point)) {
                collision = true;
                break;
            }
        }
        return collision;
    }

    private boolean hasCollision(Line2D line) {
        boolean collision = false;
        if (null!=line) {
            for (int j = 0; j < obstacles.size(); j++) {
                Rectangle rect = obstacles.get(j);
                if (line.intersects(rect)) {
                    collision = true;
                    break;
                }
            }
            for (int j = 0; j < dynamicObstacles.size(); j++) {
                Rectangle rect = dynamicObstacles.get(j);
                if (line.intersects(rect)) {
                    collision = true;
                    break;
                }
            }
        }
        return collision;
    }

    private void reWire(List<Node> closeNodes, Node toAdd) {
        for (int i = 0; i< closeNodes.size(); i++) {
            Node node = closeNodes.get(i);
            if (node.getHelper() + toAdd.getDistance() < node.getDistance()) {
                //Reconnect
                if (!checkCollision(new Line2D(node,toAdd))) {
                    node.removeFromParentChildList();
                    node.setParent(toAdd);
                    node.addToParentChildList();
                    node.setDistance(toAdd.getDistance() + node.getHelper());
                }
            }
        }
    }

    private void forcedRemoval(List<Node> newestNodes) {
        Random rand = new Random();
        if (nodeList.size() > rRTStarFnMaximumLength) {
            List<Node> childlessNodes = new ArrayList<>();
            int lenToRemove = nodeList.size()-rRTStarFnMaximumLength;
            int k = 0;
            while (childlessNodes.size() < lenToRemove) {
                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);
                    if (node.getChildList().size() == k) childlessNodes.add(node);
                }
                k++;
            }

            for(int i = 0; i< path2Goal.size(); i++) {
                Node node = path2Goal.get(i);
                if (childlessNodes.contains(node)) childlessNodes.remove(node);
            }
            for (int i = 0; i< newestNodes.size(); i++) {
                Node newNode = newestNodes.get(i);
                if (childlessNodes.contains(newNode)) childlessNodes.remove(newNode);
            }

            lenToRemove = Math.min(lenToRemove, childlessNodes.size());

            if (lenToRemove <= childlessNodes.size()) {
                while (lenToRemove>0) {
                    int toRemove = rand.nextInt(childlessNodes.size());
                    Node nodeToRemove = childlessNodes.get(toRemove);
                    childlessNodes.remove(nodeToRemove);
                    lenToRemove--;
                    removeNodeRrt(nodeToRemove);
                }
            }
        }
    }

    private void pathOptimization() {
        if (path2Goal.size()>2) {
            Node node1 = path2Goal.get(path2Goal.size()-1);
            Node node2 = null;
            if (null!=node1.getParent()) node2 = node1.getParent().getParent();
            while (null!=node2) {
                if (!checkCollision(new Line2D(node1,node2))) {
                    node1.setParentOptimized(node2);
                    node2.addChildOptimized(node1);
                    node2 = node2.getParent();
                } else {
                    node1 = node1.getParent();
                    if (null!=node1.getParent()) {
                        node2 = node1.getParent().getParent();
                    } else {
                        node2 = null;
                    }
                }
            }
        }
    }

    private RealMatrix rotationToWorldFrame(Point2D start, Point2D goal) {
        double dist = start.distance(goal);
        RealVector a = MatrixUtils.createRealVector(new double [] {(goal.getX()-start.getX())/dist,(goal.getY()-start.getY())/dist,0});
        RealVector b = MatrixUtils.createRealVector(new double [] {1,0,0});
        RealMatrix M = a.outerProduct(b);
        SingularValueDecomposition svd = new SingularValueDecomposition(M);
        RealMatrix U = svd.getU();
        RealMatrix VT = svd.getVT();

        double dd = (new LUDecomposition(U)).getDeterminant() * (new LUDecomposition(VT.transpose())).getDeterminant();
        RealMatrix D = MatrixUtils.createRealDiagonalMatrix(new double [] {1.0, 1.0, dd});
        RealMatrix C = U.multiply(D).multiply(VT);
        return C;
    }

    private Point2D ellipseSampling(double c_max, double c_min, Point2D x_center, RealMatrix C) {
        Point2D x_rand;
        double cc = Math.sqrt(c_max*c_max - c_min*c_min);
        double [] r = {c_max/2,  cc/2, cc/2};
        RealMatrix L = MatrixUtils.createRealMatrix(new double [][] {{r[0],0,0},{0,r[1],0},{0,0,r[2]}});

        while (true) {
            RealVector x_ball = MatrixUtils.createRealVector(sampleUnitBall());
            RealVector m_ = C.multiply(L).operate(x_ball);
            x_rand =  new Point2D(m_.getEntry(0) + x_center.getX(),m_.getEntry(1) + x_center.getY());
            if ((x_rand.getX()>=0)&&(x_rand.getX()<width)&&(x_rand.getY()>=0)&&(x_rand.getY()<height)) {
                break;
            }
        }
        return x_rand;
    }

    private double [] sampleUnitBall() {
        Random rand = new Random();
        while (true) {
            double x = 2*rand.nextDouble()-1;
            double y = 2*rand.nextDouble()-1;
            if (x*x + y*y < 1) return new double [] {x,y,0};
        }
    }

    private Node nearestNode(Point2D sample) {
        double closestDistance = Double.MAX_VALUE;
        Node closestNode = null;
        for (int i = 0; i< nodeList.size(); i++) {
            Node node = nodeList.get(i);
            double dist = node.distance(sample);
            if ((dist < closestDistance)&&(dist > minDistance)) {
                closestDistance = dist;
                closestNode = node;
            }
        }
        return closestNode;
    }

    private List<Node> nearNodes(Point2D point) {
        List<Node> closeNodes = new ArrayList<>();
        int maxDist = optimiseDistance;

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            double dist = node.distance(point);

            if (dist < maxDist) {
                node.setHelper(dist);
                closeNodes.add(node);
            }
        }
        return closeNodes;
    }

    private Node costlessNode(List<Node> closeNodes, Point2D point) {
        Node closestNode = null;
        double smallestDist = Double.MAX_VALUE;

        for (int i = 0; i< closeNodes.size(); i++) {
            Node node = closeNodes.get(i);
            if (!hasCollision(new Line2D(node, point))) {
                if (node.getDistance() + node.getHelper() < smallestDist) {
                    smallestDist = node.getDistance() + node.getHelper();
                    closestNode = node;
                }
            }
        }
        return closestNode;
    }

    public Point2D sampleInformed() {
        Point2D sample = null;
        if (path2Goal.size()>0) {
            double c_min = start.distance(goal.getPoint());
            double c_best = path2Goal.get(path2Goal.size() - 1).getDistance();
            if (c_best > c_min) {
                Point2D center = new Point2D((start.getX() + goal.getPoint().getX()) / 2, (start.getY() + goal.getPoint().getY()) / 2);
                RealMatrix C = rotationToWorldFrame(start, goal.getPoint());
                sample = ellipseSampling(c_best, c_min, center, C);
            }
        }
        return sample;
    }

    private void trim() {
        if (path2Goal.size()>0) {

        }
    }

    public void addRRTStarSmartFNInformed(int n){
        Random rand = new Random();

        List<Node> newNodes = new ArrayList<>();

        for(int j = 0; j < n; j++) {

            //Sample
            Point2D sample = null;
            if ((n > 1) && (j == 0) && (null != goal)) {
                sample = goal.getPoint();
            } else {
                //informed
                if (informed) sample = sampleInformed();
            }
            if (null==sample) sample = new Point2D(rand.nextInt(width), rand.nextInt(height));

            //Nearest node
            Node closestNode = nearestNode(sample);
            if (closestNode == null) {
                continue;
            }

            //Steer
            Vector2D steer = Vector2D.subtract(sample,closestNode);
            steer.normalize();
            steer.selfScale(multiplier);
            Point2D newPoint = Point2D.add(closestNode,steer);

            //ChooseParent
            List<Node> closeNodes = nearNodes(newPoint);
            Node parentNode = costlessNode(closeNodes,newPoint);
            if (parentNode == null) {
                continue;
            }

            Node newNode = new Node(parentNode, newPoint);
            nodeList.add(newNode);
            newNode.addToParentChildList();
            newNodes.add(newNode);

            //ReWire
            reWire(closeNodes,newNode);

            //Check goal
            checkGoal(newNode);
        }

        //ForcedRemoval
        if (fixedNodes) forcedRemoval(newNodes);

        //Path optimization
        if (smart) pathOptimization();
    }

    public boolean checkCollision(Line2D line) {
        for(int k=0; k<obstacles.size(); k++) if (line.intersects(obstacles.get(k))) return true;
        for (int k = 0; k < dynamicObstacles.size(); k++) if (line.intersects(dynamicObstacles.get(k))) return true;
        return false;
    }

    private void checkGoal(Node node) {
        if (goal.contains(node)) {
            path2Goal.clear();
            while (null != node) {
                path2Goal.add(0, node);
                if (null != node.getParentOptimized()) {
                    node = node.getParentOptimized();
                } else {
                    node = node.getParent();
                }
            }
        }
    }

    private void removeNodeRrt(Node node) {
        nodeList.remove(node);
        node.removeFromParentChildList();

        List<Node> childList = node.getChildList();
        for (int i=childList.size()-1; i>=0; i--) {
            Node child = childList.get(i);

            List<Node> closeNodeList = new ArrayList<>();
            for (int j = 0; j< nodeList.size(); j++) {
                Node nodeRrt = nodeList.get(j);
                double dist = nodeRrt.distance(child);
                if (dist < optimiseDistance) {
                    nodeRrt.setHelper(dist);
                    closeNodeList.add(nodeRrt);
                }
            }

            Node closestNode = null;
            double smallestDist = Double.MAX_VALUE;
            for (int j = 0; j < closeNodeList.size(); j++) {
                Node nodeRrt = closeNodeList.get(i);

                boolean collision = false;
                Line2D line = new Line2D(nodeRrt, child);
                for (int k = 0; k < obstacles.size(); k++) {
                    Rectangle r = obstacles.get(k);
                    if (line.intersects(r)) {
                        collision = true;
                        break;
                    }
                }
                for (int k = 0; k < dynamicObstacles.size(); k++) {
                    Rectangle rect = dynamicObstacles.get(k);
                    if (line.intersects(rect)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    if (nodeRrt.getDistance() + nodeRrt.getHelper() < smallestDist) {
                        smallestDist = nodeRrt.getDistance() + nodeRrt.getHelper();
                        closestNode = nodeRrt;
                    }
                }
            }

            if (null!= closestNode) {
                child.setParent(closestNode);
                closestNode.addChild(child);
            } else {
                removeNodeRrt(child);
            }
        }
    }

    public void setObstacles(int n){
        obstacles = obstacleSets.get(n);
        reset();
    }

    public void setNoObstacles() {
        obstacles = new ArrayList<>();
        reset();
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int m){
        multiplier = m;
        minDistance = 7*m/10;
    }

    public void setGoalRadius(double radius) {
        goalRadius = radius;
        goal.setRadius(radius);
    }

    public void removeRoot() {
        removeNodeRrt(nodeList.get(0));
    }
}
