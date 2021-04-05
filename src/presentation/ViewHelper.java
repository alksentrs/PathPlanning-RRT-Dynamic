package presentation;

import util.Circle2D;
import business.MotionSpace;
import business.Node;
import presentation.util.JPaintListener;
import util.Point2D;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ViewHelper implements JPaintListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private int width, height;
    private List<Rectangle> obstacles;
    private List<Rectangle> obstaclesUser;
    private List<Node> nodes;
    private Circle2D goal;
    private List<Node> path2Goal;
    private Node start;
    private boolean addObstacle;
    private int obstacleWidth = 10;
    private Point mouse;

    private AddObstacleObserver addObstacleObserver;

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.WHITE);
        g.fillRect(0,0,width,height);

        int pointWidth = 2;

        if (null!=obstacles) {
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle r = obstacles.get(i);
                g.setColor(Color.GRAY);
                g.fillRect((int)(r.getMinX()), (int)(r.getMinY()), (int)(r.getWidth()), (int)(r.getHeight()));
                g.setColor(Color.BLACK);
                g.drawRect((int)r.getMinX(), (int)r.getMinY(), (int)r.getWidth(), (int)r.getHeight());
            }
        }

        if (null!=obstaclesUser) {
            for (int i = 0; i < obstaclesUser.size(); i++) {
                Rectangle r = obstaclesUser.get(i);
                g.setColor(Color.GRAY);
                g.fillRect((int)(r.getMinX()), (int)(r.getMinY()), (int)(r.getWidth()), (int)(r.getHeight()));
                g.setColor(Color.BLACK);
                g.drawRect((int)r.getMinX(), (int)r.getMinY(), (int)r.getWidth(), (int)r.getHeight());
            }
        }

        if (null!=start) {
            g.setColor(Color.CYAN);
            g.fillOval((int) (start.getX() - 3 * pointWidth), (int) (start.getY() - 3 * pointWidth), 6 * pointWidth, 6 * pointWidth);
        }

        if (null!= goal) {
            double x = goal.getPoint().getX();
            double y = goal.getPoint().getY();
            g.setColor(Color.YELLOW);
            g.fillOval((int)(x- goal.getRadius()), (int)(y- goal.getRadius()), (int)(2* goal.getRadius()), (int)(2* goal.getRadius()));
            g.setColor(Color.GREEN);
            g.fillOval((int)(x-pointWidth), (int)(y-pointWidth), 2*pointWidth, 2*pointWidth);
        }

        if ((null!= nodes)&&(nodes.size()>0)) {
            for (int i = 0; i < nodes.size(); i++) {
                Node n = nodes.get(i);
                g.setColor(Color.BLUE);
                g.fillOval((int) (n.getX() - pointWidth), (int) (n.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                if (n.getParent() != null) {
                    g.setColor(Color.BLACK);
                    g.drawLine((int) n.getX(), (int) n.getY(), (int) n.getParent().getX(), (int) n.getParent().getY());
                }
            }
        }

        if ((null!= path2Goal)&&(path2Goal.size()>0)) {

            for(int i=0; i<path2Goal.size()-1; i++) {
                Node p1 = path2Goal.get(i);
                Node p2 = path2Goal.get(i+1);
                g.setColor(Color.BLUE);
                g.fillOval((int) (p1.getX() - pointWidth), (int) (p1.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                g.fillOval((int) (p2.getX() - pointWidth), (int) (p2.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                g.setColor(Color.RED);
                Stroke strokeAux = g2d.getStroke();
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                g2d.setStroke(strokeAux);
            }

            Node p1 = path2Goal.get(path2Goal.size()-1);
            while (null!=p1.getParent()) {
                Node p2;
                if (null!=p1.getParentOptimized()) {
                    p2 = p1.getParentOptimized();
                    g.setColor(Color.BLUE);
                    g.fillOval((int) (p1.getX() - pointWidth), (int) (p1.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                    g.fillOval((int) (p2.getX() - pointWidth), (int) (p2.getY() - pointWidth), 2 * pointWidth, 2 * pointWidth);
                    g.setColor(Color.MAGENTA);
                    Stroke strokeAux = g2d.getStroke();
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                    g2d.setStroke(strokeAux);
                } else {
                    p2 = p1.getParent();
                }
                p1 = p2;
            }
        }

        if ((addObstacle)&&(null!=mouse)) {
            g.setColor(Color.ORANGE);
            g.fillRect(mouse.x-obstacleWidth,mouse.y-obstacleWidth,2*obstacleWidth,2*obstacleWidth);
            g.setColor(Color.BLACK);
            g.drawRect(mouse.x-obstacleWidth,mouse.y-obstacleWidth,2*obstacleWidth,2*obstacleWidth);
        }
    }

    public void setSpace(MotionSpace space) {
        obstacles = space.getObstacles();
        obstaclesUser = space.getDynamicObstacles();
        width = space.getWidth();
        height = space.getHeight();
        nodes = space.getNodes();
        goal = space.getGoal();
        path2Goal = space.getPath2Goal();
        start = space.getStart();
    }

    public void setAddObstacle(boolean addObstacle) {
        this.addObstacle = addObstacle;
        if (!addObstacle) {
            mouse = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        mouse = mouseEvent.getPoint();
        mouseEvent.getComponent().repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        if (mouseWheelEvent.getWheelRotation() > 0) {
            obstacleWidth--;
            if (obstacleWidth<5) obstacleWidth = 5;
        } else {
            if (mouseWheelEvent.getWheelRotation() < 0) {
                obstacleWidth++;
            }
        }
        mouseWheelEvent.getComponent().repaint();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if ((addObstacle)&&(null!=mouse)) {
            if (null!=addObstacleObserver) addObstacleObserver.addObstacle(new Point2D(mouse.x-obstacleWidth,mouse.y-obstacleWidth),2*obstacleWidth);
            addObstacle = false;
            mouse = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    public void attachAddObstacleObserver(AddObstacleObserver addObstacleObserver) {
        this.addObstacleObserver = addObstacleObserver;
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
