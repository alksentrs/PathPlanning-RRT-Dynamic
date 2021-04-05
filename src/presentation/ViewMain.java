package presentation;

import business.MotionSpace;
import presentation.util.JPanelX;
import util.Point2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewMain implements AddObstacleObserver{

    private JFrame jFrame;
    private JPanel jPanelMain;

    private JButton buttonAdd1Point;
    private JButton buttonAdd50Points;
    private JButton buttonAdd500Points;
    private JButton buttonReset;
    private JComboBox comboBoxObstacleSet;
    private JSlider sliderIncrement;
    private JPanelX jPanelDraw;
    private JLabel textTotalNodes;
    private JButton buttonWalk;
    private JButton buttonAddObstacle;
    private JCheckBox checkBoxInformed;
    private JCheckBox checkBoxFixedNodes;
    private JCheckBox checkBoxSmart;

    private MotionSpace space;

    private ViewHelper viewHelper;

    private void createUIComponents() {
        jPanelDraw = new JPanelX();
    }

    @Override
    public void addObstacle(Point2D point, double radius) {
        buttonAddObstacle.getModel().setPressed(false);
        space.addObstacle(point,radius);
    }

    public void open(MotionSpace space){
        this.space = space;

        jFrame = new JFrame("Path Planning");
        jFrame.setPreferredSize(new Dimension(1280, 880));
        jFrame.add(jPanelMain);

        buttonAdd1Point.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                add(1);
                update();
            }
        });

        buttonAdd50Points.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                add(50);
                update();
            }
        });

        buttonAdd500Points.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                add(500);
                update();
            }
        });

        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                space.reset();
                update();
            }
        });

        buttonWalk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                space.walk();
                update();
            }
        });

        buttonAddObstacle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (buttonAddObstacle.getModel().isPressed()) {
                    buttonAddObstacle.getModel().setPressed(false);
                    viewHelper.setAddObstacle(false);
                } else {
                    buttonAddObstacle.getModel().setPressed(true);
                    viewHelper.setAddObstacle(true);
                }
            }
        });

        checkBoxInformed.setSelected(space.isInformed());
        checkBoxFixedNodes.setSelected(space.isFixedNodes());
        checkBoxSmart.setSelected(space.isSmart());

        checkBoxInformed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JCheckBox checkBox = (JCheckBox)actionEvent.getSource();
                space.setInformed(checkBox.isSelected());
            }
        });
        checkBoxFixedNodes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JCheckBox checkBox = (JCheckBox)actionEvent.getSource();
                space.setFixedNodes(checkBox.isSelected());
            }
        });
        checkBoxSmart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JCheckBox checkBox = (JCheckBox)actionEvent.getSource();
                space.setSmart(checkBox.isSelected());
            }
        });

        comboBoxObstacleSet.addItem("No Obstacles");
        comboBoxObstacleSet.addItem("Set 1");
        comboBoxObstacleSet.addItem("Set 2");
        comboBoxObstacleSet.addItem("Set 3");
        comboBoxObstacleSet.addItem("Set 4");
        comboBoxObstacleSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox comboBox = (JComboBox) actionEvent.getSource();
                String selected = (String) comboBox.getSelectedItem();

                if(selected.equals("Set 1")) space.setObstacles(0);
                else if(selected.equals("Set 2")) space.setObstacles(1);
                else if(selected.equals("Set 3")) space.setObstacles(2);
                else if(selected.equals("Set 4")) space.setObstacles(3);
                else if(selected.equals("No Obstacles")) space.setNoObstacles();

                update();
            }
        });


        sliderIncrement.setMinimum(0);
        sliderIncrement.setMaximum(40);
        sliderIncrement.setValue(space.getMultiplier());
        space.setGoalRadius(sliderIncrement.getValue());

        sliderIncrement.setMinorTickSpacing(5);
        sliderIncrement.setMajorTickSpacing(10);
        sliderIncrement.setPaintTicks(true);
        sliderIncrement.setPaintLabels(true);

        sliderIncrement.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JSlider slider = (JSlider) changeEvent.getSource();
                space.setMultiplier(slider.getValue());
                space.setGoalRadius(slider.getValue());
                update();
            }
        });

        viewHelper = new ViewHelper();
        viewHelper.attachAddObstacleObserver(this);

        jPanelDraw.addPaintListener(viewHelper);
        jPanelDraw.addMouseListener(viewHelper);
        jPanelDraw.addMouseMotionListener(viewHelper);
        jPanelDraw.addMouseWheelListener(viewHelper);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

        update();

    }

    public void updateScreen() {
        jPanelDraw.repaint();
    }

    public void update() {
        textTotalNodes.setText(String.valueOf(space.getNodes().size()));
        viewHelper.setSpace(space);
        updateScreen();
    }

    private void add(int n) {
        space.addRRTStarSmartFNInformed(n);
    }
}
