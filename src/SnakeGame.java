import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;

public class SnakeGame extends JPanel implements ActionListener,KeyListener{
    private class Tile{
        int x;
        int y;
        Tile(int x , int y){
            this.x=x;
            this.y=y;
        }
    }
    
    int boardWidth;
    int boardHeight;
    int tileSize = 25;


    //Snake head-body tile
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    //food tile
    Tile food;
    Random random;
        

    //-----------velocity declaration-----------
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;

        //------------Constructor-------------
        public SnakeGame(int boardWidth , int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth,this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);


        
        snakeHead = new Tile(5,5);
        snakeBody= new ArrayList<Tile>();
        food = new Tile(10,10);
        

        random = new Random();
        placeFood();
        velocityX=0;
        velocityY=0;


        gameLoop = new Timer(100,this);
        gameLoop.start()  ; 
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){


        //Grid 
        // for(int i = 0 ; i < boardWidth/tileSize ;i++){
        //     //x1 and x2 for vertical lines
        //     g.drawLine(i*tileSize,0, i*tileSize,boardHeight);
        //     //y1 and y2 for horizontal lines
        //     g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
        // }


        //draw food
        g.setColor(Color.RED);
        // g.fillRect(food.x*tileSize , food.y*tileSize , tileSize,tileSize);
        g.fill3DRect(food.x*tileSize , food.y*tileSize , tileSize,tileSize,true);

        //draw snake head (caution! might eat your cursor stay 10px away)
        g.setColor(Color.GREEN);
        // g.fillRect(snakeHead.x*tileSize, snakeHead.y*tileSize ,tileSize,tileSize);
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize ,tileSize,tileSize,true);
        

        //draw snake body when he eat up
        for(int i = 0 ; i < snakeBody.size() ; i++){
            Tile snakePart = snakeBody.get(i);
            // g.fillRect(snakePart.x*tileSize,snakePart.y*tileSize,tileSize,tileSize);
            g.fill3DRect(snakePart.x*tileSize,snakePart.y*tileSize,tileSize,tileSize,true);
        }

        //---------------Score board---------------
        g.setFont(new Font("Arieal", Font.PLAIN , 16));
        if(gameOver){
            g.setColor(Color.red);
            g.drawString("Game Over :" + String.valueOf(snakeBody.size()),tileSize-16,tileSize);
        }else{
            g.drawString("Score :" + String.valueOf(snakeBody.size()),tileSize-16,tileSize);
        }
    
    }
    //random food placing for food using java.util.Random
    public void placeFood(){
        // size of board = 600 , tileSize = 25 , 
        // random = 600/25 i.e a random number between 0-24
        food.x = random.nextInt(boardWidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);

    }


    //collision function
    public boolean collision(Tile tile1 ,Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }



    public void move(){


        //eat food
        if(collision(snakeHead, food)){
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }


        //-----------------Move snake body-----------------
        
        for(int i = snakeBody.size()-1 ;i >=0 ; i--){
            Tile snakePart = snakeBody.get(i);
            if(i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else{
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        

        //snake head movement
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;


        //-----------------Game Over-----------------
        //if snake bites him/herself (does it makes sense? ;-;)
        for(int i = 0 ; i < snakeBody.size();i++){
            Tile snakePart = snakeBody.get(i);
            if(collision(snakeHead, snakePart)){
            gameOver=true;
        }
    }
        //if snake crossed left || right || up || down then gameOver will set to true
        if(snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth ||
        snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight){
            gameOver = true;
        }

    }

    // repetatively call the function inside it within 100 mili secs
    @Override 
    public void actionPerformed(ActionEvent e){
        // /direction of head in which it'll move
        move();
        //repaint the snake head using draw(g)
        repaint();
        //game over
        if(gameOver){
            gameLoop.stop();
        }

    }


    //----------press key to move Snake-------------
    // Press up arrow key (↑)    : to go up ( Y-axis-1 , X-axis constant) 
    // Press down arrow key (↓)  : to go down ( Y-axis+1 , X-axis constant )
    // Press left arrow key (←)  : to go left ( X-axis-1 , Y-axis constant )
    // Press right arrow key (→) : to go right ( X-axis+1 , Y-axis constant )
    //the && prevent backward move i.e making snake go backward directly in his own body
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && velocityY!=1){
            velocityX = 0;
            velocityY = -1;
        }else if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityY!=-1){
            velocityX=0;
            velocityY =1;
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityX!=1){
            velocityX= -1;
            velocityY = 0;
        }else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX!=-1){
            velocityX=1;
            velocityY=0;
        }
    }

    //-----------As we only need KeyPressed method--------
    //avoid usingg them
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    

}
