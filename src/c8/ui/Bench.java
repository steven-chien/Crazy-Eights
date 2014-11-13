package c8.ui;


import java.math.*;
import java.io.*;
import java.util.*;

public class Bench
{
	//setup object variables
	public int Player;
	private int CardInPlay;
	private char SuitInPlay;
	private int RankInPlay;

	//constructor, take Player number and starting card
	public Bench(int PlayerNo, int card)
	{
		//setup variables
		Player = PlayerNo;
		CardInPlay = card;
		SuitInPlay = getSuit(card);
		RankInPlay = getRank(card);
		System.out.println("New Bench created!");
	}

	//used when a card is added to discard pile
	public int newDeal(int card, char ExtraData)
	{
		//check if play is legal
		System.out.println("newDeal(): card = " + card);
		if(getRank(card)==RankInPlay||getSuit(card)==SuitInPlay||getRank(card)==8) {
			CardInPlay = card;
			SuitInPlay = getSuit(card);
			RankInPlay = getRank(card);

			//if card played is eight, update next legal value
			if(card==7||card==20||card==33||card==46) {
				SuitInPlay = ExtraData;
				RankInPlay = 0;
			}
			return 0;
		}
		else {
			return -1;
		}
	}

	//set suit in play
	public void setSuitInPlay(char SuitInPlay)
	{
		this.SuitInPlay = SuitInPlay;
	}

	//set rank in play
	public void setRankInPlay(char RankInPlay)
	{
		this.RankInPlay = RankInPlay;
	}

	//get suit in char
	private char getSuit(int card)
	{
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
		if(card<13)
			return (card+1);
		else if(card<26)
			return (card-13+1);
		else if(card<39)
			return (card-26+1);
		else if(card<52)
			return (card-39+1);
		return -1;
	}

	//get card in top of discard pile
	public int getCardInPlay()
	{
		return CardInPlay;
	}

	//get rank of top card of discard pile
	public int getTopRank()
	{
		return RankInPlay;
	}

	//get suit of top card of discard pile
	public char getTopSuit()
	{
		return SuitInPlay;
	}

}
