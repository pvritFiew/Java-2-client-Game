import javax.swing.*;
//import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.*;

import java.io.*;

public class PlayerFrame extends JFrame {
    private int width,height;
    private Container contentPane;
    private Player player1;
    private Player player2;
    private DrawingComponent dc;
    private Timer animationTimer;
    private boolean up,down,left,right;
    private Socket socket;
    private int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Goal goal;
    private double gx,gy;

    public PlayerFrame(int w,int h){
        width=w;
        height=h;
        up = false;
        down = false;
        left = false;
        right = false;
    }
    public void setUpGUI(){
        contentPane = this.getContentPane();
        this.setTitle("Fast and Circle(Player "+playerID+")");
        contentPane.setPreferredSize(new Dimension(width,height));
        contentPane.setBackground(new Color(174, 214, 241));
        createSprites();
        dc=new DrawingComponent();
        contentPane.add(dc);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);	

        setUpAnimationTimer();
        setUpKeyListener();
    }
    private void createSprites(){
        if(playerID==1) {
            player1 = new Player(485, 10, 30, Color.BLUE);
            player2 = new Player(515, 10, 30, Color.RED);
            goal = new Goal(gx,gy);
        }
        else{
            player2 = new Player(485, 10, 30, Color.BLUE);
            player1 = new Player(515, 10, 30, Color.RED);
            goal = new Goal(gx,gy);
        }
    }
    private void setUpAnimationTimer(){
        int interval =10;
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                double speed = 2;
                if(up) {
                    player1.moveV(-speed);
                }
                else if(down){
                    player1.moveV(speed);
                }
                else if(left){
                    player1.moveH(-speed);
                }
                else if(right){
                    player1.moveH(speed);
                }
                dc.repaint();
            }
        };
        animationTimer = new Timer(interval,al);
        animationTimer.start();
    }
    private void setUpKeyListener(){
        KeyListener kl = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {

            }

            @Override
            public void keyPressed(KeyEvent ke) {
                int keyCode = ke.getKeyCode();

                switch (keyCode){
                    case KeyEvent.VK_W :
                        up =true;
                        break;
                    case KeyEvent.VK_S :
                        down =true;
                        break;
                    case KeyEvent.VK_A :
                        left =true;
                        break;
                    case KeyEvent.VK_D :
                        right =true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                int keyCode = ke.getKeyCode();

                switch (keyCode){
                    case KeyEvent.VK_W :
                        up =false;
                        break;
                    case KeyEvent.VK_S :
                        down =false;
                        break;
                    case KeyEvent.VK_A :
                        left =false;
                        break;
                    case KeyEvent.VK_D :
                        right =false;
                        break;
                }
            }
        };
        contentPane.addKeyListener(kl);
        contentPane.setFocusable(true);
    }
    
    private void connectToServer(){
        try{
            socket = new Socket("localhost",45371);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            playerID = in.readInt();
            gx = in.readDouble();
            gy = in.readDouble();
            System.out.println("You are player#" + playerID);
            if(playerID ==1){
                System.out.println("Waiting for Player #2 to connect...");
            }
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.waitForStarMsg();
        } catch(IOException ex){
            System.out.println("IOException from connectToServer");
        }
    }
    private class DrawingComponent extends JComponent{
        protected void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            player2.drawSprite(g2d);
            player1.drawSprite(g2d);
            goal.drawGoal(g2d);
        }
    }
    
    private class ReadFromServer implements Runnable{
        private DataInputStream dataIn;
        public ReadFromServer(DataInputStream in){
            dataIn = in;
            System.out.println("RFS  Runnable created");
        }
        public void run(){
            try{
                while(true) {
                	
                    if(player2 != null){
                        player2.setX(dataIn.readDouble());
                        player2.setY(dataIn.readDouble());
                    }
                }
            }catch (IOException ex){
                System.out.println("IOException from RFS run()");
            }
        }
        public void waitForStarMsg(){
            try{
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server:"+startMsg);
                Thread readThread = new Thread(rfsRunnable);
                Thread writeThread = new Thread(wtsRunnable);
                readThread.start();
                writeThread.start();
            }catch (IOException ex){
                System.out.println("IOException from waitForStarMsg()");
            }
        }
    }
    
    private class WriteToServer implements Runnable{
        private DataOutputStream dataOut;
        public WriteToServer(DataOutputStream out){
            dataOut = out;
            System.out.println("WTS  Runnable created");
        }
        public void run(){
            try{
                while(true){
                    if(player1 != null) {
                        dataOut.writeDouble(player1.getX());
                        dataOut.writeDouble(player1.getY());
                        dataOut.flush();
                    }
                    try{
                        Thread.sleep(25);
                    } catch (InterruptedException ex){
                        System.out.println("InterruptedException from WTS run()");
                    }
                }
            }
            catch (IOException ex){
                System.out.println("IOException from WTS run()");
            }
        }
    }
    
    public static void main(String[] args) {
        PlayerFrame pf =new PlayerFrame(1000,800);
        pf.connectToServer();
        pf.setUpGUI();
    }
}
