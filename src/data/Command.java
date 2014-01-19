package data;

import java.io.Serializable;

public class Command implements Serializable{
	public int type;
	public transient static final int CREATEUNIT = 1111;
	public Command(int stype) {
		type = stype;
	}
}
