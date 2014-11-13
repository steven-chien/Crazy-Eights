package c8.ui;

import java.math.*;
import java.util.*;
import java.io.*;

public class Player
{
	//variable and statistical data
	private int PlayerID;
	private ArrayList Hand = new ArrayList();
	private int SpadeCount;
	private int HeartCount;
	private int DiamondCount;
	private int ClubCount;

	private int CardOnBench;
	private char SuitInPlay;
	private int RankInPlay;

	//constructor of Player
	public Player(int id)
	{
		PlayerID = id;
	}

	//method to draw a card
	public void Draw(int card)
	{
		//add card to hand
		Hand.add(card);

		//update suit count
		char suit = getSuit(card);
		if(suit=='S')
			SpadeCount++;
		else if(suit=='H')
			HeartCount++;
		else if(suit=='D')
			DiamondCount++;
		else if(suit=='C')
			ClubCount++;
	}

	//get method of user ID
	public int getPlayerID()
	{
		return PlayerID;
	}

        
        public String getCardInPlay2()
	{
		return "" + SuitInPlay + RankInPlay;
	}
        
        
	//play a card from hand
	public int PlayCard(int card)
	{
		//setup temp variables and check if card is in hand
		int counter;
		int index = (Integer)Hand.indexOf(card);
		if(index==-1){
			System.out.println("card not found");
			return -1;
		}

		//print Suit and Rank of requested card if found
		System.out.println(getSuit(card)+getRank(card));

		//validate if playing of the card is legal
		if(getSuit(card)==SuitInPlay||getRank(card)==RankInPlay||getRank(card)==8) {
			//remove card from hand
			Hand.remove(index);
			System.out.println("DEBUG: Class Player: legal move");
			return 0;
		}
		else {
			//if move is illegal
			System.out.println("Illegal move!");
			return -1;
		}
	}

	//set current card in play, when message received from server
	public void setCardInPlay(int CardOnBench)
	{
		//update card, char SuitInPlay, int RankInPlay
		this.CardOnBench = CardOnBench;
		SuitInPlay = getSuit(CardOnBench);
		RankInPlay = getRank(CardOnBench);
	}

        
        
        
        
	//update next card to play, when last player played an eight
	public void setNextInPlay(char SuitInPlay, int RankInPlay)
	{
		this.SuitInPlay = SuitInPlay;
		this.RankInPlay = RankInPlay;
	}

	//get current card in play
	public int getCardInPlay()
	{
		System.out.println("Card on bench: " + CardOnBench + " " + SuitInPlay + RankInPlay);
		return CardOnBench;
	}

	//get hand size
	public int getHandCount()
	{
		return Hand.size();
	}

	//list all cards in hand
	public ArrayList showHand()
	{
		System.out.println();
		for(int i=0; i<Hand.size(); i++) {
			int card = (Integer)Hand.get(i);
			System.out.println("Card " + i + " is " + card + " " + getSuit(card) + getRank(card));
		}
		System.out.println("---end of list---");
                return Hand;
	}

	//get suit in char
	private char getSuit(int card) {
		if(card<13)
			return 'S';
		else if(card<26)
			return 'H';
		else if(card<39)
			return 'D';
		else if(card<52)
			return 'C';
		return '0';
	}

	//get rank in int
	private int getRank(int card)
	{
		//return rank according to int value
		if(card<13)
			return (card+1);
		else if(card<26)
			return (card-13+1);
		else if(card<39)
			return (card-26+1);
		else if(card<52)
			return (card-39+1);
		return '0';
	}
}
