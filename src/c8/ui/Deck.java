package c8.ui;

import java.io.*;
import java.math.*;
import java.lang.*;
import java.util.*;

public class Deck
{
	//setup vairables
	private Boolean[] CardPile = new Boolean[52];
	private int CardLeft;
	private int Spade;
	private int Heart;
	private int Club;
	private int Diamond;

	//constructor, no parameter
	public Deck()
	{
		Arrays.fill(CardPile, true);
		CardLeft = 52;
		Spade = 13;
		Heart = 13;
		Diamond = 13;
		Club = 13;
		System.out.println("Deck created!");
	}

	//get a card from deck
	public int Deal()
	{
		//setup random variable
		int card = -1;
		double randomNumber = Math.random();

		//check if there's any card left
		if(CardLeft==0)
			return -2;

		//probe the array to see if card is there
		while(card==-1) {
			//if card is there, extract the card and set to false
			if(CardPile[(int)(randomNumber*52)]==true) {
				//set card in array to false
				CardPile[(int)(randomNumber*52)] = false;

				//accounting of deck
				card = (int)(randomNumber*52);
				if(getSuit(card)=='S')
					Spade--;
				else if(getSuit(card)=='H')
					Heart--;
				else if(getSuit(card)=='C')
					Club--;
				else if(getSuit(card)=='D')
					Diamond--;

				//reduce number of card left
				CardLeft--;
			}
			else {
				//if card is not there, probe again
				randomNumber = Math.random();
			}
		}

		return card;
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
}
