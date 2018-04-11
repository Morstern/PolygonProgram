package application;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;

public class BorderPaneController {

	@FXML
	private Canvas Rysunek; // "plotno" na ktorym rysujemy

	GraphicsContext gc; // w javie fx to sluzy do rysowania na canvasie

	@FXML
	private Button dodajPrzycisk, reset_button; // przycisk, ktory umozliwia dodanie punktu / zresetowanie danych
	@FXML
	private TextField X, Y; // pola tekstowe do ktorych wpisuje sie koordynaty
	@FXML
	private Slider Slider, zoom; // pasek przesuwania

	/// wielokat
	@FXML
	private TableView<pktWielokata> pktyWielokata; // tabela z punktami wielokata
	@FXML
	private TableColumn<pktWielokata, Integer> WieIDcol; // kolumna z ID wielokata
	@FXML
	private TableColumn<pktWielokata, Integer> WieXcol; // kolumna z punktem X wielokata
	@FXML
	private TableColumn<pktWielokata, Integer> WieYcol; // kolumna z punktem Y wielokata

	/// strzelany
	@FXML
	private TableView<pktStrzelany> pktyStrzelane;
	@FXML
	private TableColumn<pktStrzelany, Integer> StrIDcol; // tabela z ID punktu strzelanego
	@FXML
	private TableColumn<pktStrzelany, Integer> StrXcol; // tabela z koordynatem X punktu strzelanego
	@FXML
	private TableColumn<pktStrzelany, Integer> StrYcol; // tabela z koordynatem Y punktu strzelanego
	@FXML
	private TableColumn<pktStrzelany, Boolean> StrStanCol; // tabela z stanem punktu strzelanego (true - wewn¹trz
															// wielok¹ta, false - poza wielok¹tem)

	@FXML
	private ChoiceBox<String> Lista; // lista wyboru - punkt wielokata / punkt strzelany

	final ObservableList<pktWielokata> data_w = FXCollections.observableArrayList(); // tabele do których zapisywane s¹
																						// poszczególne punkty wielok¹ta
	final ObservableList<pktStrzelany> data_s = FXCollections.observableArrayList(); // tabele do których zapisywane s¹
																						// poszczególne punkty strzelane

	public void Rysuj_skale() {
		double skala = zoom.getValue();
		if (skala == 0)
			skala = 1;
		gc.setFont(new Font("Comic Sans MS", 8));
		gc.setFill(Color.BLACK);
		gc.strokeLine(Rysunek.getWidth() - 50, Rysunek.getHeight() - 20, Rysunek.getWidth() - 10,
				Rysunek.getHeight() - 20);
		gc.strokeLine(Rysunek.getWidth() - 51, Rysunek.getHeight() - 22, Rysunek.getWidth() - 51,
				Rysunek.getHeight() - 18);
		gc.strokeLine(Rysunek.getWidth() - 9, Rysunek.getHeight() - 22, Rysunek.getWidth() - 9,
				Rysunek.getHeight() - 18);
		gc.fillText("0", Rysunek.getWidth() - 52, Rysunek.getHeight() - 26);
		gc.fillText(Integer.toString(skala > 0 ? 40 * (int) skala : 40 / (int) Math.abs(skala)),
				Rysunek.getWidth() - 18, Rysunek.getHeight() - 26);

	}

	public void Rysuj_prowadnice() {
		// rysowanie OX, OY oraz miarki (25 jednostek) -- mozna ³atwo zmieniaæ poprzez
		// zmianê i+=25, na np. i+=10 (10 jednostek)

		gc.setFill(Color.BLACK);
		
		// linia w pionie - (xA,yA,xB,yB), A - (srodek,gora) B- (srodek, dol) - czyli
		// powstanie nam pionowa krecha na srodku plotna od gory do dolu
		gc.strokeLine(Rysunek.getWidth() / 2, Rysunek.getHeight(), Rysunek.getWidth() / 2, 0);
		// linia w poziomia - (punktA,PunktB), A - (prawa,srodek) B- (lewa, srodek) -
		// czyli powstanie nam pozioma krecha na srodku plotna od prawej do lewej
		gc.strokeLine(0, Rysunek.getHeight() / 2, Rysunek.getWidth(), Rysunek.getHeight() / 2); // linia
																								// w
																								// poziomie

		for (double i = 31.5; i < Rysunek.getWidth(); i += 40) // rysowanie przedzialki w poziomie
			gc.strokeLine(i, Rysunek.getHeight() / 2 - 2, i, Rysunek.getHeight() / 2 + 2);
		for (double i = 25.5; i < Rysunek.getWidth(); i += 40) // rysowanie przedzialki w pionie
			gc.strokeLine(Rysunek.getWidth() / 2 - 2, i, Rysunek.getWidth() / 2 + 2, i);
	}

	public void Rysuj_punkty_strz() {
		// pobieramy aktualna wartosc z naszego paska skali, jezeli jest rowna 0, to
		// dajemy 1... bo 500*0 = 0
		double skala = zoom.getValue();
		if (skala == 0)
			skala = 1;

		gc.setFont(new Font("Comic Sans MS", 15));
		for (pktStrzelany p : data_s) { // zmiana stanu (sprawdzenie czy nie ma zamiany w jakims punkcie, móg³ byæ poza
			// wielok¹tem, ale nagle po dodaniu punktu jest wewn¹trz
			p.sprawdzenie(data_w);
			// jesli punkt sie znajduje, to kolorujemy go na zielono a tekst na morska
			// zielen
			if (p.getStan() == true) {
				if (skala < 0) {// jesli skala jest ujemna - to dzielimy wartosci x/y
					gc.setFill(Color.GREEN);
					gc.fillOval(p.getcorX() * Math.abs(skala) + Rysunek.getWidth() / 2,
							-p.getcorY() * Math.abs(skala) + Rysunek.getHeight() / 2, 5, 5);
					gc.setFill(Color.MEDIUMSEAGREEN);
					gc.fillText(p.getID().toString(), p.getcorX() * Math.abs(skala) + 512,-p.getcorY() * Math.abs(skala) + 325);
				} else { // jesli jest dodatnia to mnozymy ja przez wartosci x/y
					gc.setFill(Color.GREEN);
					gc.fillOval(p.getcorX() / Math.abs(skala) + Rysunek.getWidth() / 2,
							-p.getcorY() / Math.abs(skala) + Rysunek.getHeight() / 2, 5, 5);
					gc.setFill(Color.MEDIUMSEAGREEN);
					gc.fillText(p.getID().toString(), p.getcorX() / Math.abs(skala) + Rysunek.getWidth() / 2,-p.getcorY() / Math.abs(skala) + Rysunek.getHeight() / 2);
				}
			} else { // jesli nie to punkt czerwony, a tekst na kolor plonacej cegly
				if (skala < 0) { // jesli skala jest ujemna - to dzielimy wartosci x/y
					gc.setFill(Color.RED);
					gc.fillOval(p.getcorX() * Math.abs(skala) + Rysunek.getWidth() / 2,
							-p.getcorY() * Math.abs(skala) + Rysunek.getHeight() / 2, 5, 5);
					gc.setFill(Color.FIREBRICK);
					gc.fillText(p.getID().toString(), p.getcorX() * Math.abs(skala) + 512,
							-p.getcorY() * Math.abs(skala) + 325);
				} else { // jesli jest dodatnia to mnozymy ja przez wartosci x/y
					gc.setFill(Color.RED);
					gc.fillOval(p.getcorX() / Math.abs(skala) + Rysunek.getWidth() / 2,
							-p.getcorY() / Math.abs(skala) + Rysunek.getHeight() / 2, 5, 5);
					gc.setFill(Color.FIREBRICK);
					gc.fillText(p.getID().toString(), p.getcorX() / Math.abs(skala) + 512,-p.getcorY() / Math.abs(skala) + 325);
				}
			}
		}
		pktyStrzelane.refresh(); // odswiezenie tabeli jesli nastapily zmiany z stanem punktow strzelanych
	}
	public void Rysuj_wielokat() {
		// pobieramy aktualna wartosc z naszego paska skali, jezeli jest rowna 0, to
		// dajemy 1... bo 500*0 = 0
		double skala = zoom.getValue();
		if (skala == 0)
			skala = 1;

		gc.setFont(new Font("Comic Sans MS", 15));
		// dodawanie tablic w celu stworzenia polygonu oraz automatycznego wypelnienia
		// go
		ArrayList<Integer> tempTabX = new ArrayList<Integer>();
		ArrayList<Integer> tempTabY = new ArrayList<Integer>();
		for (pktWielokata p : data_w) {
			tempTabX.add(p.getcorX());
			tempTabY.add(-p.getcorY());
		}
		double[] tabX = new double[tempTabX.size()];
		for (int i = 0; i < tabX.length; i++) {
			tabX[i] = tempTabX.get(i);
			if (skala > 0)
				tabX[i] /= Math.abs(skala);
			else
				tabX[i] *= Math.abs(skala);
			tabX[i] += Rysunek.getWidth() / 2;
		}
		double[] tabY = new double[tempTabX.size()];
		for (int i = 0; i < tabY.length; i++) {
			tabY[i] = tempTabY.get(i);
			if (skala > 0)
				tabY[i] /= Math.abs(skala);
			else
				tabY[i] *= Math.abs(skala);
			tabY[i] += Rysunek.getHeight() / 2;
		}
		// zmiana koloru, stworzenie oraz wypelnienie wielokata
		gc.setFill(Color.CHOCOLATE);
		gc.fillPolygon(tabX, tabY, tabX.length);

		for (pktWielokata p : data_w) { // wpisanie id nad kazdym wierzcholkiem wielokata
			gc.setFill(Color.BLUE);
			if (skala > 0)
				gc.fillText(p.getID().toString(),
						p.getcorX() < 0 ? p.getcorX() / Math.abs(skala) + 500 : p.getcorX() / Math.abs(skala) + 515,
						p.getcorY() < 0 ? -p.getcorY() / Math.abs(skala) + 320 : -p.getcorY() / Math.abs(skala) + 303);
			else
				gc.fillText(p.getID().toString(),
						p.getcorX() > 0 ? p.getcorX() * Math.abs(skala) + 500 : p.getcorX() * Math.abs(skala)  + 515,
						p.getcorY() > 0 ? -p.getcorY() * Math.abs(skala)  + 320 : -p.getcorY() * Math.abs(skala)  + 303);
		}

	}

	public void Rysuj() {
		gc.clearRect(0, 0, Rysunek.getWidth(), Rysunek.getHeight()); // czyscimy caly nasz obszar rysowania od punktu
																		// 0,0 lewy gorny rog

		Rysuj_wielokat();
		Rysuj_prowadnice();
		Rysuj_punkty_strz();
		Rysuj_skale();
	}

	@FXML
	public void dodajacyPrzycisk() {
		/// sprawdzenie czy któreœ z pól jest puste
		boolean sX = X.getText().isEmpty();
		boolean sY = Y.getText().isEmpty();
		boolean sL = Lista.getSelectionModel().isEmpty();
		if (sX || sY || sL) { // jesli ktoeœ jest puste to wypisz informacjê
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Uzupe³nij miejsca");
			alert.setHeaderText(null);
			if (sX && sY && sL)
				alert.setContentText("Aby dodaæ punkt uzupe³nij pola:\n•X\n•Y\n•Rodzaj punktu");
			else if (sX && sY)
				alert.setContentText("Aby dodaæ punkt uzupe³nij pola:\n•X\n•Y");
			else if (sX && sL)
				alert.setContentText("Aby dodaæ punkt uzupe³nij pola:\n•X\n•Rodzaj punktu");
			else if (sY && sL)
				alert.setContentText("Aby dodaæ punkt uzupe³nij pola:\n•Y\n•Rodzaj punktu");
			else if (sY)
				alert.setContentText("Aby dodaæ punkt uzupe³nij pola:\n•Y");
			else if (sL)
				alert.setContentText("Aby dodaæ punkt uzupe³nij pola:\n•Rodzaj punktu");
			else
				alert.setContentText("Aby dodaæ punkt uzupe³nij pola:\n•X");
			alert.showAndWait(); // dopóki u¿ytkownik nie zamknie bêdzie widoczne
		} else {
			if (Lista.getSelectionModel().getSelectedItem() == "Punkt Wielokata") {
				try {
					data_w.add(new pktWielokata(Integer.parseInt(X.getText()), Integer.parseInt(Y.getText())));
					Rysuj();
				} catch (NumberFormatException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Wprowadzono zle dane");
					alert.setHeaderText(null);
					alert.setContentText("Upewnij sie ze wprowadzone dane sa liczbami");
					alert.showAndWait(); // dopóki u¿ytkownik nie zamknie bêdzie widoczne
				}

			} else {
				try {
					data_s.add(new pktStrzelany(Integer.parseInt(X.getText()), Integer.parseInt(Y.getText()), data_w));
					Rysuj();
				} catch (NumberFormatException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Wprowadzono zle dane");
					alert.setHeaderText(null);
					alert.setContentText("Upewnij sie ze wprowadzone dane sa liczbami");
					alert.showAndWait(); // dopóki u¿ytkownik nie zamknie bêdzie widoczne
				}

			}
		}
	}

	@FXML
	public void zmiana_skali() {
		if (zoom.getValue() == 0)
			Rysuj();
		else if (zoom.getValue() - (int) zoom.getValue() == 0)
			Rysuj();
	}

	@FXML
	public void Reset() {
		if (Slider.getValue() == 0) // usuwanie danych z wielokata
		{
			data_w.clear();
			pktyWielokata.refresh();
			Rysuj();
		} else if (Slider.getValue() == 1) // usuwanie danych wszystkich
		{
			data_w.add(new pktWielokata(1, 1));
			data_w.get(0).settempID();
			data_w.clear();
			pktyWielokata.refresh();
			data_s.clear();
			pktyStrzelane.refresh();
			Rysuj();
		} else if (Slider.getValue() == 2) // usuwanie danych z strzelania
		{
			data_s.clear();
			pktyStrzelane.refresh();
			Rysuj();
		}
	}

	@FXML
	void initialize() {
		Lista.getItems().add("Punkt Wielokata");
		Lista.getItems().add("Punkt Strzelany");

		// slider
		Slider.setLabelFormatter(new StringConverter<Double>() { // przypisanie nazw do poszczególnych "trybów"
			@Override
			public String toString(Double n) {
				if (n == 0.0)
					return "Wielokat";
				else if (n == 1.0)
					return "Wszystko";
				else
					return "Strzelane";
			}

			@Override
			public Double fromString(String s) { // zwracanie do funkcji getValue() liczby w zale¿noœci od "trybu"
				switch (s) {
				case "Wielokat":
					return 0.0;
				case "Wszystko":
					return 1.0;
				case "Strzelane":
					return 2.0;
				default:
					return 1.0; // sytuacja ta siê nie zdarzy, ale bez tego nie chce dzia³aæ
				}
			}
		});

		// wielokat
		pktyWielokata.setItems(data_w);
		this.WieIDcol.setCellValueFactory(cellData -> cellData.getValue().getIDProperty().asObject());
		this.WieXcol.setCellValueFactory(cellData -> cellData.getValue().getcorXProperty().asObject());
		this.WieYcol.setCellValueFactory(cellData -> cellData.getValue().getcorYProperty().asObject());

		// strzelany
		// to samo co wyzej
		pktyStrzelane.setItems(data_s);
		this.StrIDcol.setCellValueFactory(cellData -> cellData.getValue().getIDProperty().asObject());
		this.StrXcol.setCellValueFactory(cellData -> cellData.getValue().getcorXProperty().asObject());
		this.StrYcol.setCellValueFactory(cellData -> cellData.getValue().getcorYProperty().asObject());
		this.StrStanCol.setCellValueFactory(cellData -> cellData.getValue().getStanProperty());

		gc = Rysunek.getGraphicsContext2D();
		

		// Rysuj_prowadnice();
		Rysuj();
	}
}
