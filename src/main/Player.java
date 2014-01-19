package main;

import java.awt.Color;
import java.io.Serializable;

public class Player implements Serializable{
	public String name;
	public Color color;
	public Player() {
		color = Color.black;
		name = "null";
	}
	public String toString() {
		return name+" ("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")";
	}
}
