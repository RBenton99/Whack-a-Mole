/*********************************************************************
                            CREDITS
 *                      Whack A Java Mole
 *  Created by Byoung Hun Min, Wentao Wei, Hyojong Kim, Xiaofeng Fu
 *  https://bymi15.gitbooks.io/whack-a-java-mole/content/index.html
 * 
*********************************************************************/

/*
Josh Riddle
CS 331 - 001
    DATE
    CODE DESCRIPTION
*/

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;

import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game extends JFrame {
    
    private JPanel panel;
    private JLabel lblCatches;
    private JLabel lblMisses;
    private JLabel lblTimeLeft;
    private JLabel lblPercent;
    private JButton startBtn;
    private Timer gameTime;
    private Timer creatureTime;
    private JLabel[] holes = new JLabel[16];
    private int[] board = new int[16];
    
    private static int creatureTimeBase = 75;
    private static int creatureTimeLeft = creatureTimeBase;
    private int gameTimeLeft = 30;
    private float catches = 0;
    private float misses = 0;
    
    // Constructor for Game object
    public Game () {
        setupBoard();
        clearBoard();
        startEvents();
    }
    
    // Main function
    public static void main(String[] args) {
        Game frame = new Game();
        frame.setVisible(true);
    }
    
    // Sets up board for play
    public void setupBoard() {
        // Variables for board loop. Offset -- seperation between corners
        //      Width x Height -- rows and columns
        int offset = 132;
        int width = 4;
        int height =  4;
        
        // Creates the main Game Window
        setTitle("Catch a creature");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 608, 720);
        
        // Creates a smaller pane inside main game window
        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(0, 51, 0));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        
        // Displays the Title of the Game
        JLabel gameTitle = new JLabel("Catch The Creature");
        gameTitle.setForeground(new Color(153,204,0));
        gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gameTitle.setFont(new Font("Century Gothic", Font.BOLD, 20));
        gameTitle.setBounds(0, 0, 602, 47);
        contentPane.add(gameTitle);
        
        // Creates the play board where Creature hides
        panel = new JPanel();
        panel.setBackground(new Color(0,102,0));
        panel.setBounds(32,105,535,546);
        panel.setLayout(null);
        contentPane.add(panel);
        
        // Successful attempts to catch the creature
        lblCatches = new JLabel("Catches: 0");
        lblCatches.setHorizontalAlignment(SwingConstants.TRAILING);
        lblCatches.setFont(new Font("Cambria", Font.BOLD, 14));
        lblCatches.setForeground(new Color(135, 206, 250));
        lblCatches.setBounds(433, 9, 134, 33);
        contentPane.add(lblCatches);
        
        
        // Failed attempts to catch the creature
        lblMisses = new JLabel("Misses: 0");
        lblMisses.setHorizontalAlignment(SwingConstants.TRAILING);
        lblMisses.setFont(new Font("Cambria", Font.BOLD, 14));
        lblMisses.setForeground(new Color(135, 206, 250));
        lblMisses.setBounds(423, 36, 144, 33);
        contentPane.add(lblMisses);
        
        
        // Overall success rate as a percent
        lblPercent = new JLabel("Success:   0%");
        lblPercent.setHorizontalAlignment(SwingConstants.TRAILING);
        lblPercent.setFont(new Font("Cambria", Font.BOLD, 14));
        lblPercent.setForeground(new Color(135, 206, 250));
        lblPercent.setBounds(420, 60, 144, 33);
        contentPane.add(lblPercent);
        
        
        // Displays game timer
        lblTimeLeft = new JLabel("30");
        lblTimeLeft.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimeLeft.setForeground(new Color(240, 128, 128));
        lblTimeLeft.setFont(new Font("Cambria Math", Font.BOLD, 24));
        lblTimeLeft.setBounds(232, 54, 144, 33);
        contentPane.add(lblTimeLeft);
        
        // Setup Start / Stop button
        startBtn = new JButton("Start");
        startBtn.setBackground(Color.white);
        startBtn.setBounds(32, 60, 110, 33);
        contentPane.add(startBtn);
        
        // Code loop to create holes
        for (int i = 0; i < 16; i++){
            // Automatic adjustment for X axis
            int incrX = i % width;
            // Automatic adjustment for Y axis
            int incrY = i / height;
            
            holes[i] = new JLabel(Integer.toString(i));
            holes[i].setName(Integer.toString(i));
            holes[i].setBounds((0 + (incrX * offset)), (396 - (incrY * offset)), 132, 132);
            panel.add(holes[i]);
            //System.out.println(holes[i].getBounds());
        }
        
        for (int i = 0; i < 16; i++) {
            holes[i].setIcon(loadImage("/creature_hole.png"));
            board[i] = 0;
        }
        
        setContentPane(contentPane);        
    }
    
    // Sets board to all zeros
    public void clearBoard() {
        for (int i = 0; i < 16; i++) {
            holes[i].setIcon(loadImage("/creature_hole.png"));
            board[i] = 0;
        }
    }
    
    public void startEvents() {
        for (int i = 0; i < 16; i++) {
            holes[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JLabel lbl = (JLabel)e.getSource();
                    int id = Integer.parseInt(lbl.getName());
                    pressedButton(id);
                }
            });
        }
        
        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startBtn.setEnabled(false);
                creatureTimeLeft = creatureTimeBase;
                gameTimeLeft = 30;
                catches = 0;
                misses = 0;
                lblCatches.setText("Catches: 0");
                lblMisses.setText("Misses: 0");
                lblPercent.setText("Success: 0%");
                clearBoard();
                randomNum();
                creatureTime.start();
                gameTime.start();
            }
        });
        
        gameTime = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (gameTimeLeft == 0) {
                    lblTimeLeft.setText("" + gameTimeLeft);
                    gameTime.stop();
                    creatureTime.stop();
                    gameOver();
                }
                lblTimeLeft.setText("" + gameTimeLeft);
                gameTimeLeft--;
            }
        });
        
        startCreatureTime();
    }
    
    public void pressedButton(int id) {
        int val = board[id];
        float perc;
        
        if (val == 1) {
            catches += 1;
            lblCatches.setText("Catches: " + (int)catches);
        }
        if (val == 0) {
            misses += 1;
            lblMisses.setText("Misses: " + (int)misses);
        }
        
        if ((catches + misses) > 0) {
            perc = catches / (catches + misses);
            //System.out.println(catches / (catches + misses));
            
            lblPercent.setText("Success: " + Math.round(perc * 100) + "%");
        }
        
        clearBoard();
        randomNum();
        creatureTime.restart();
        creatureTimeLeft = creatureTimeBase;
    }
    
    // Timer function to keep the creature moving
    private void startCreatureTime() {
        System.out.println("startCreatureTime function");
        creatureTime = new Timer(1, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (creatureTimeLeft == 0) {
                    pressedButton(0);
                }
                creatureTimeLeft--;
                //System.out.println(creatureTimeLeft);
            }
        });
    }
    
    private void randomNum() {
        Random random = new Random(System.currentTimeMillis());
        int creatureHole = random.nextInt(16);
        board[creatureHole] = 1;
        holes[creatureHole].setIcon(loadImage("/red_creature.png"));
        System.out.println(creatureHole);
    }
    
    private ImageIcon loadImage(String path) {
        Image image = new ImageIcon(this.getClass().getResource(path)).getImage();
        Image scaledImage = image.getScaledInstance(132, 132, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    
    public void gameOver() {
        startBtn.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Your final score is: " + catches, "Game Over!", JOptionPane.INFORMATION_MESSAGE);
    }
}
