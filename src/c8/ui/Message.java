package c8.ui;


import java.util.*;
import java.io.*;


public class Message implements Serializable
{
	//variables
	private String message;
	private String Command;
	private int data;
	private boolean token = false;
	private String Sender;
	private char ExtraData;

	//constructor
	public Message(String Command, int data, boolean token)
	{
		this.Command = Command;
		this.data = data;
		this.token = token;
	}

	//set token
	public void setToken(boolean token)
	{
		this.token = token;
	}

	//get token
	public boolean getToken()
	{
		return token;
	}

	//set extra data in char
	public void setExtraData(char ExtraData)
	{
		this.ExtraData = ExtraData;
	}

	//get extra data in char
	public char getExtraData()
	{
		return ExtraData;
	}

	//set message in string
	public void setMessage(String message)
	{
		this.message = message;
	}

	//get message in string
	public String getMessage()
	{
		return message;
	}

	//set command
	public void setCommand(String Command)
	{
		this.Command = Command;
	}

	//get command
	public String getCommand()
	{
		return Command;
	}

	//get data in int
	public int getData()
	{
		return data;
	}

	//set data in int
	public void setData(int data)
	{
		this.data = data;
	}
}
