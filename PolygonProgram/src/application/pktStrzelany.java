package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.shape.Polygon;

public class pktStrzelany extends pktWielokata {
	private SimpleBooleanProperty Stan;

	public pktStrzelany(Integer corX, Integer corY, ObservableList<pktWielokata> pw) {
		super(corX, corY);
		sprawdzenie(pw);
	}

	public void setStan(boolean stan) {
		Stan = new SimpleBooleanProperty(stan);
	}

	public Boolean getStan() {
		return Stan.getValue();
	}

	public BooleanProperty getStanProperty() {
		return Stan;
	}

	@Override
	public void wypiszStan() {
		super.wypiszStan();
		System.out.println("STAN: " + getStan());
	}

	public void sprawdzenie(ObservableList<pktWielokata> pw) // za ka¿dym razem jak siê coœ zmieni musi byæ wywo³ane
	{

		Polygon poly = new Polygon();
		double x, y;
		if (pw.size() > 2) {
			for (pktWielokata p : pw) {
				x = p.getcorX();
				y = p.getcorY();
				poly.getPoints().add(x);
				poly.getPoints().add(y);
				if(this.getcorX()==x && this.getcorY()==y)
				{
					Stan = new SimpleBooleanProperty(true);
					return;
				}
			}
			if (poly.contains(this.getcorX() < 0 ? this.getcorX():this.getcorX()-1, this.getcorY() < 0 ? this.getcorY():this.getcorY()-1)) {
				Stan = new SimpleBooleanProperty(true);
			} else {
				Stan = new SimpleBooleanProperty(false);

			}
		} else {
			Stan = new SimpleBooleanProperty(false);
		}

	}

}

// poly.getPoints().addAll(
// 100.0, 100.0,
// 100.0, -100.0,
// -100.0, -100.0,
// -100.0, 100.0);

// new pktWielokata(100.0, 100.0),
// new pktWielokata(100.0, -100.0),
// new pktWielokata(-100.0, -100.0),
// new pktWielokata(-100.0, 100.0)