/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package c8.ui;

/**
 *
 * @author jack
 */
public class c8 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        Start s = new Start();
        s.setVisible(true);
        int state=s.state;
        while (state==0){
            Thread.currentThread().sleep(1000);//sleep for 1000 ms  
            state=s.state;
        }
        if (state==1){  //user want to play
            Game a =new Game(s.input);           
            a.setDefaultCloseOperation(Game.DISPOSE_ON_CLOSE);
            GameClient gc=a.getgc();
            gc.setwin(a);
            a.setVisible(true);
            a.Gamestart();
            s.setVisible(false);
            gc.update();
            //game start
            //if state change, callupdate
            state=a.state;
            while (true){
                //0:waiting, 1:playing ,2:need to choose
                while (state==0){   
                Thread.currentThread().sleep(1000);//sleep for 1000 ms  
                state=a.state;
                }
                //state 0->1
                while (state==1){   
                Thread.currentThread().sleep(1000);//sleep for 1000 ms  
                state=a.state;
                    if (state==2){//1->2, choose crd
                        //call set
                        //set and send msg
                        ask ask=new ask();
                        ask.setVisible(true);
                        
                        while(ask.getsuit()=='N'){
                            Thread.currentThread().sleep(1000);//sleep for 1000 ms
                        }
                        char ExtraData=ask.getsuit();
                        ask.setVisible(false);
                        gc.setsuit(ExtraData);
                        //change state to wait
                        state=0;
                    }
                }
                //state 1->0 
                state=a.state;
                if (state==-5)
                    break;  //if error occur, exit
                gc.update();
            }
        }
        else{   //user open server
            GameServer gameserver = new GameServer(s.getport(),s);
            int getvalue= gameserver.GameServer1();
            if (getvalue==1){
                int check=gameserver.gamestart();
                if (check==-1){
                    msg m= new msg("One of the player close the game. \n Server closed",'E');
                    m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
                    m.setVisible(true);
                }
                else{
                    msg m= new msg("Player:" + check + " won the game. \n Game over, server closed",'E');
                    m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
                    m.setVisible(true);
                }
            }
            else{
                msg m= new msg("Server open error, program exit",'E');
                m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
                m.setVisible(true);
            }
        }
        
    }
    
    
    
}
