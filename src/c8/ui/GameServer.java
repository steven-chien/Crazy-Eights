package c8.ui;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer
{
    
         
        private int port;
        private Socket aSocket[];
        private Deck aDeck;
        private ObjectInputStream[] ObjectIS ;
	private	ObjectOutputStream[] ObjectOS ;
        private Bench aBench;
        private String input;
        private Start window;
        private int passcount=0;
        private int handnum[];
        
        private void setport(int port){
            this.port=port;
        }
        private void setSocket(Socket  aSocket[]){
            this.aSocket=aSocket;
        }
        private void setDeck(Deck  aDeck){
            this.aDeck=aDeck;
        }
        
        public int gamestart() throws InterruptedException{
            int turn = 0;
            boolean token;
            String Command = "";
            int data = 0;
            char ExtraData = 'x';
            int client_no=4;
            
            try{
            while(true) {
                    for(int i=0; i<client_no; i++) {
                            Message Table = new Message("TableUpdate", aBench.getCardInPlay(), false);
                            Table.setExtraData(ExtraData);
                            ObjectOS[i].writeObject(Table);
                    }

                    //send message to player to notify that it's his turn
                    Message ServerMessage = new Message("YourTurn", 0, true);
                    ObjectOS[turn].writeObject(ServerMessage);

                    //listen to response from client
                    while(true) {
                            //read messages
                            Message ClientMessage = (Message)ObjectIS[turn].readObject();
                            Command = ClientMessage.getCommand();
                            data = ClientMessage.getData();
                            token = ClientMessage.getToken();
                            ExtraData = ClientMessage.getExtraData();

                            //if player wants to play a card
                            if(Command.equals("PlayCard")) {
                                    System.out.println("DEBUG: Class GameServer: PlayCard");
                                    aBench.newDeal(data, ExtraData);
                                    passcount=0;
                                    handnum[turn]--;
                            }

                            //if player wants to draw a card from deck
                            else if(Command.equals("Draw")) {
                                    //ask deck to give a card
                                    data = aDeck.Deal();
                                    System.out.println("DEBUG: Class GameServer: DrawCard: card " + data + " drawn");

                                    //construct and send message to client
                                    ServerMessage = new Message("Deal", data, true);
                                    ObjectOS[turn].writeObject(ServerMessage);
                                    System.out.println("DEBUG: Class GameServer: DrawCard: Card sent to player");
                                    if (data!=-2)
                                        handnum[turn]++;
                            }

                            //if player wants to pass
                            else if(Command.equals("Pass")) {
                                //tell other user that a player passed
                                Message pass = new Message("Pass", turn, false);    
                                for(int i=0; i<client_no; i++) {
                                        ObjectOS[i].writeObject(pass);
                                }
                                passcount++;
                                if (passcount==5){
                                    //all player pass, game finish
                                    int winplayer=no_card_play();
                                    Message terminate = new Message("GameEnd", 0, false);
                                    terminate.setExtraData((char)(winplayer+48));
                                    for(int i=0; i<client_no; i++) {
                                            ObjectOS[i].writeObject(terminate);
                                    }
                                    System.out.println("Player " + (winplayer+1) + " won, game ended!");
                                    msg m= new msg("Player:" + (winplayer+1) + " won the game. \n Game over, server closed",'E');
                                    m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
                                    m.setVisible(true);
                                    wait();
                                }
                                break;
                            }

                            //if player won
                            else if(Command.equals("Won")) {
                                    Message terminate = new Message("GameEnd", 0, false);
                                    terminate.setExtraData((char)(ExtraData));
                                    for(int i=0; i<client_no; i++) {
                                            ObjectOS[i].writeObject(terminate);
                                    }
                                    System.out.println("Player " + (ExtraData-48+1) + " won, game ended!");
                                    msg m= new msg("Player:" + (ExtraData-48+1) + " won the game. \n Game over, server closed",'E');
                                    m.setDefaultCloseOperation(msg.EXIT_ON_CLOSE);
                                    m.setVisible(true);
                                    wait();
                                    return ExtraData;
                            }

                            
                            //determine if player wants to negotiate with server again
                            if(token==false)
                                    break;
                    }

                    System.out.println("DEBUG: Class GameServer: Token=false, go on to next turn");
                    //determine next turn
                    turn++;
                    if(turn==client_no)
                            turn = 0;
            }
            
            }
            catch (UnknownHostException ex) {
                    System.out.println(ex);
                    return -1;
            }
            catch (IOException ex) {
                    System.out.println(ex);
                    return -1;
            }
            catch (ClassNotFoundException ex) {
                    System.out.println(ex);
                    return -1;
            }
		finally {
                        
			try {
				for(int l = 0; l < client_no; l++) {
					if(ObjectIS[l] != null)
						ObjectIS[l].close();
					if(ObjectOS[l] != null)
						ObjectOS[l].close();
					if(aSocket[l] != null)
						aSocket[l].close();
				}
			} catch(Exception ex) { }
                        
                        return -1;
		}
            
        }
       
        public int no_card_play(){
            //no one have card to play, fin winner and exit
            int min=99;
            int winner=-1;
            for(int i=0;i<4;i++){
                if (handnum[i]<min){
                    min=handnum[i];
                    winner=i;
                }
            }
            
            return winner;
        }
        
        
        
        
        public  GameServer(String input,Start s)
	{
           this.window=s;
           this.input=input;
            
        }
        
        
	public  int GameServer1()
	{
		int port = 0;
		int client_no = 4;
                String input=this.input;
                
		try
		{
			port = Integer.parseInt(input);
		}
		catch(Exception exception)
		{
			System.out.println("Please type: java GameServer <port no>");
                        return 0;
                        
		}
//
		//prepare sockets and streams
		Socket aSocket[] = new Socket[client_no];
		ObjectInputStream[] ObjectIS = new ObjectInputStream[client_no];
		ObjectOutputStream[] ObjectOS = new ObjectOutputStream[client_no];

		try {
			//create server socket
			ServerSocket serversocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);
			System.out.println("Accepting client connection");
                        window.addLable("Accepting client connection");
                        
			//accept incoming connection and bind socket to streams
			for(int i=0; i<client_no; i++) {
				aSocket[i] = serversocket.accept();
				ObjectOS[i] = new ObjectOutputStream(aSocket[i].getOutputStream());
				ObjectIS[i] = new ObjectInputStream(aSocket[i].getInputStream());
                                String msg="Connection Accepted from " + aSocket[i].getInetAddress();
				System.out.println(msg);
                                window.addLable(msg);
                                this.ObjectIS=ObjectIS;
                                this.ObjectOS=ObjectOS;
			}
			System.out.println("All connections accepted!");
                        window.addLable("All connections accepted! Game start");
                        setSocket(aSocket);
			//setup deck
			Deck aDeck = new Deck();
                        setDeck(aDeck);
                        this.handnum=new int[4];
                        
			//send initialization messages
			for(int i=0; i<client_no; i++) {
                                handnum[i]=0;
				//tell client his user ID
				Message initMessage = new Message("initialization", i, false);
                                System.out.println("message = "+initMessage.getCommand());
				ObjectOS[i].writeObject(initMessage);

				//give 5 cards at the beginning
				for(int j=0; j<5; j++) {
                                        handnum[i]++;
					Message dealCards = new Message("Deal", aDeck.Deal(), false);
                                        System.out.println("message = "+dealCards.getData());
					ObjectOS[i].writeObject(dealCards);
                                        System.out.println("Card given to player"+i+": Card no."+j);
				}
			}

			//all initial dealing complete, start the table to drawing top card from deck to discard pile
			int deal_first_card = aDeck.Deal();
			while(deal_first_card==7||deal_first_card==20||deal_first_card==33||deal_first_card==46) {
				//all initial dealing complete, start the table to drawing top card from deck to discard pile
				deal_first_card = aDeck.Deal();
			}
			Bench aBench = new Bench(client_no, deal_first_card);
                        
                        this.aBench=aBench;
			/* initialization complete, proceed to game */
			System.out.println("Card on bench is " + aBench.getCardInPlay() + " " + aBench.getTopSuit() + aBench.getTopRank());
                        
                        
		}
		catch (UnknownHostException ex) {
			System.out.println(ex);
                        return 0;
		}
		catch (IOException ex) {
			System.out.println(ex);
                        return 0;
                }
                return 1;
		
	}
}
