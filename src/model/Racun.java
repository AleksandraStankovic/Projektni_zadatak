package model;
//OVO SVE JE SREDJENO, MOZE SE KUPITI KARTA ZA RUTU TJ MOZE SE UPISATI U FAJL, KASNIJE JE POTREBNO DA SE OVO PREPRAVI TAKO DA RADI ZA GENERISANE RUTE I OSTALO

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//neki dijelovi odavde ce valjati sigurno, ali bice sigurno i nekih izmjena 

public class Racun {
//klasa racun koja opisuje racun i u kojojj ce biti implementirano i kupovina karte tj bice implementirano kreiranje racuna u cuvanje u txt fajl
	// i guess da cemo ovdje da unesemo informacije koje treba da budu na (myb ce to
	// moci biti i ruta kao citava) I guess za sad da mogu samo stvari koje ce biti
	// na racunu

	// ovo cemo kasnije da mijenjamo I guess
	private String polazak; // odnosi se na polaznu stanicu
	private String odrediste; // krajnja stanica (da li se odnori na grad ili na sta=?
	//ovdje ce myb trebati ili za sve stanice na putanji, ili samo krajnje stanice, idk...mybb ce biti potrebno da prepravimo...
	private String cijena;
	private String vrijemePutovanja;
	private LocalDateTime datumKupovine;
	//mozemo dodati dodatno i myb da upise i vrstu prevoza, ali myb bolje i ne.

	// konsttrukto kojim se generise racun tj objekat racun koji ce onda biti upisan
	// u fajl
	// i guess da cemo u racun da proslijedimo ove info iz rute(ili cemo kasnije
	// napraviti da radi sa rutom)
	public Racun(String polazak, String odrediste, String cijena, String vrijemePutovanja) {
		this.polazak = polazak; // koristi se this ako su ime parametra i ime atributa isti
		this.odrediste = odrediste;
		this.cijena = cijena;
		this.vrijemePutovanja = vrijemePutovanja;
		this.datumKupovine = LocalDateTime.now();// uzima se trenutno vrijeme kupovine
		//mozda se moze jos negdje dodati neko formatiranje nesto, ali je bolje da ne? 

	}

	
	//metoda koja ce da formatira ovo sto je uneseno tako da se dobije string koji ce onda da se upisuje u faj
	//I guess da se ovdje moze kreirati i dodati jos formatiranja
	
	public String generisiRacun()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");//formatiranje datuma
		return String.format(
			    "Relacija: %s → %s\n" +
			    "Vrijeme putovanja: %s\n" +
			    "Cijena: %s\n" +
			    "Datum kupovine: %s\n",
			    polazak, odrediste, vrijemePutovanja, cijena, datumKupovine.format(formatter)
			);
	}

}
