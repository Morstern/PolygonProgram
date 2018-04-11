package application;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class pktWielokata {
	private static int tempID = 1;
	private SimpleIntegerProperty ID;
	private SimpleIntegerProperty corX;
	private SimpleIntegerProperty corY;

	public pktWielokata(Integer corX, Integer corY) {
		this.ID = new SimpleIntegerProperty(tempID);
		this.corX = new SimpleIntegerProperty(corX);
		this.corY = new SimpleIntegerProperty(corY);
		tempID++;
	}
	
	public void settempID() {
		tempID = 1;
	}
	
	public Integer getID() {
		return ID.get();
	}

	public IntegerProperty getIDProperty() {
		return ID;
	}

	public Integer getcorX() {
		return corX.get();
	}

	public IntegerProperty getcorXProperty() {
		return corX;
	}

	public Integer getcorY() {
		return corY.get();
	}

	public IntegerProperty getcorYProperty() {
		return corY;
	}

	public void wypiszStan() {
		System.out.println("ID: " + getID());
		System.out.println("X: " + getcorX());
		System.out.println("Y: " + getcorY());
	}

}
