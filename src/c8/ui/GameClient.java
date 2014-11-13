package c8.ui;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameClient
{
    private ObjectInputStream ObjectIS;
    private ObjectOutputStream ObjectOS;
    private Socket socket;
    private Player aPlayer ;
    private String input[];
    private Game window;
    private int turn =0;
    
    public void setwin(Game win){
        this.window=win;
    }
    
    public GameClient(String input[])
    {
        this.input=input;
        Gameconnect(input);
        try {
                System.out.println("trying...");
                Message initMessage = (Message)ObjectIS.readObject();
                Gamestart(initMessage);
            } catch (IOException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                err();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                err();
            }
    }
   
    
     public void Gameconnect(String input[]) {
        
        String ServerAddress = "";
        int port = 0;

        //socket related
        Socket socket = null;
        ObjectInputStream ObjectIS = null;
        ObjectOutputStream ObjectOS = null;

        //Keyboard
        Scanner Keyboard = new Scanner(System.in);

        //game related data
        String Command = "";
        int data = 0;
        boolean token;
        char ExtraData;

        try {
                //setup data
                ServerAddress = input[0];
                port = Integer.parseInt(input[1]);
        }
        catch(Exception ex) {
                System.out.println("Please type: java GameClient <Server Address> <port>");
                err("Please input correct IP and port");
                
        }

        try {
                //setup socket and bind streams
                socket = new Socket(ServerAddress, port);
                ObjectIS = new ObjectInputStream(socket.getInputStream());
                ObjectOS = new ObjectOutputStream(socket.getOutputStream());

                System.out.println("Connected to server!");


                //read initialization data from server and assign user ID
                this.ObjectIS=ObjectIS;
                this.ObjectOS=ObjectOS;
                this.socket=socket;

        }
        catch(Exception Exception1) {
                System.err.println(Exception1);
                err();
        }
        
    }

     public void Gamestart(Message initMessage){
         Player aPlayer = new Player(initMessage.getData());
         this.aPlayer=aPlayer;
         System.out.println("Player Created User ID: " + initMessage.getData());
         try{ 
            for(int i=0; i<5; i++) {
                    //read message package
                    Message initCards = (Message)ObjectIS.readObject();
                    int card = initCards.getData();
                    System.out.println(card + " received");
                    //add card drawn to hand
                    aPlayer.Draw(card);
            }
            System.out.println("Got 5 cards from server");
         }catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            err();
         } catch (ClassNotFoundException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                err();
         }         
     }		

     public Player getplayer(){
         return aPlayer;
     }
     
    public void update() throws InterruptedException {
        String Command = "";
        int data = 0;
        boolean token;
        char ExtraData;
        
        Message ServerMsg = null;
            try {
                ServerMsg = (Message)ObjectIS.readObject();
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                err();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                err();
            }
        Command = ServerMsg.getCommand();
        token = ServerMsg.getToken();
        data = ServerMsg.getData();
        ExtraData = ServerMsg.getExtraData();
        while(token==false) {
                //update the table card in play
                if(Command.equals("TableUpdate")) {

                        //update current card
                        aPlayer.setCardInPlay(data);
                        System.out.println("Card in Play " + aPlayer.getCardInPlay());
                        //pass to gui
                        window.update_table(data);
                        //if current card is an eight, update requested next suit
                        if(data==7||data==20||data==33||data==46) {
                                String msgs="Last player played an eight, new suit in play is " + ExtraData;
                                System.out.println("Last player played an eight, new suit in play is " + ExtraData);
                                msg m=new msg(msgs,ExtraData);
                                m.setDefaultCloseOperation(msg.DISPOSE_ON_CLOSE);
                                m.setVisible(true);
                                aPlayer.setNextInPlay(ExtraData, 8);
                        }
                }else if (Command.equals("Pass")){
                    //window.addtext("Player"+ (data+1) + "choose pass");
                    window.player_pass(data);//pass
                    
                }
                else if(Command.equals("GameEnd")) {
                    System.out.println("Player " + (ExtraData-48+1) + " won, game terminated!");
                    if (ExtraData !=aPlayer.getPlayerID()){
                        msg m= new msg("Player:" + (ExtraData-48+1) + " won the game. \n Game over, click OK to exit.",'E');
                        m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
                        m.setVisible(true);
                        wait();
                    }
                        
                     
                }
                 try {
                        ServerMsg = (Message)ObjectIS.readObject();
                    } catch (IOException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        err();
                        return;
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        err();
                        return;
                    }
                Command = ServerMsg.getCommand();
                token = ServerMsg.getToken();
                data = ServerMsg.getData();
                ExtraData = ServerMsg.getExtraData();
        }
        if (token==true){
            //set player turn 
            window.canplay();
        }
    }
    
    public void err(){  //call when error
        msg m= new msg("Network error, game closed",'E');
        m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
        m.setVisible(true);
        //close when game over
        try {
                if(ObjectIS!=null)
                        ObjectIS.close();
                if(ObjectOS!=null)
                        ObjectOS.close();
                if(socket!=null)
                        socket.close();
        }
        catch(Exception ex) {
        }
        window.state=-5;
        window.dispose();
    }
    
    public void err(String msgs){  //call when error
        msg m= new msg(msgs,'E');
        m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
        m.setVisible(true);
        //close when game over
        try {
                if(ObjectIS!=null)
                        ObjectIS.close();
                if(ObjectOS!=null)
                        ObjectOS.close();
                if(socket!=null)
                        socket.close();
        }
        catch(Exception ex) {
        }
        window.state=-5;
        window.dispose();
        
    }
    
    
    public int draw(){ 
        int data;
        Message draw = new Message("Draw", 0, true);
        try {
            ObjectOS.writeObject(draw);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            err();
        }

            //read response from server and add card to hand
        Message cardDrawn = null;
        try {
            cardDrawn = (Message)ObjectIS.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            err();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            err();
        }
        data = cardDrawn.getData();

            //check if discard pile is empty
        if(data==-2) {
                System.out.println("Sorry, no card left, you can choose pass");
                return data;
                //change button to pass
        }
        aPlayer.Draw(cardDrawn.getData());
        return data;
    }

    public void pass(){
        Message pass = new Message("Pass", aPlayer.getPlayerID(), false);
        try {
            ObjectOS.writeObject(pass);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            err();
        }
    }

    public Message temp8;
    
    public int playcard(int data) {
        //return 1:success, return 0:fail, reutrn -1:game finish
        //return 2:need to choose suit
        if(aPlayer.PlayCard(data)!=0) {
            //card choosen not correct
            System.out.println("DEBUG: Class GameClient: aPlayer.PlayCard("+data+") return !=0");
            System.out.print("Enter Card Valid No. ");
            return 0;
        }
        else
        {   //card correct,card played
            if(aPlayer.getHandCount()!=0){
                //game not finished
                Message play = new Message("PlayCard", data, false);
                if(data==7||data==20||data==33||data==46) {
                    System.out.print("You played an eight, set next suit to play: ");
                    //ask for new suit
                    temp8=play;
                    return 2;
                }
                try {
                    //send message to server
                    ObjectOS.writeObject(play);
                } catch (IOException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    err();
                }
                return 1;
            }
            else{
                //no card on hand,win
                Message win = new Message("Won", data, false);
                win.setExtraData((char)(aPlayer.getPlayerID()+48));
                System.out.println("No card left in hand, you won!!!");
                try {
                    ObjectOS.writeObject(win);
                } catch (IOException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    err();
                }
                return -1;
            }
        }
        
    }
    
    public void setsuit(char suit){
        Message play = temp8;
        play.setExtraData(suit);
        try {
            //send message to server
            ObjectOS.writeObject(play);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            err();
        }
        window.playsuccess();
    }
    
    
}
