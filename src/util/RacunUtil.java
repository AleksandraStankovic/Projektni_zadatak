package util;
//i guess da ovo sluzi za rad sa racunima, odnosno da uzima racun objekat i da onda radi upis u fajl

import model.Racun;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
//neki dijelovi ovdje su definitivno okej i nece biti potrebe za puno promjena, bar u ovom dijelu. 

public class RacunUtil {
	
	//ime foldera, koje je static, samo jejdno na nivou citave klase i finalno, znaci da mu nema izmjene
	  private static final String FOLDER_NAME = "racuni";
	  
	  //metoda za cuvanje racuna
	  public static void sacuvajRacun(Racun racun) throws IOException//uzima racun koji ce da sacuva
	  {
		  //definisanje patha tj putanje za folder u koji ce se sve cuvati I guess
		  Path folderPath = Paths.get(FOLDER_NAME); //ovo uzima string koji je zadatak kao ime i konvertuje ga u putanju, path objekat, tj pdefinese se putanja gdje se upisuje 
		  if(!Files.exists(folderPath))//provjera da li postoji vec folder sa tim imenom. 
		  {
			  Files.createDirectories(folderPath);
		  }
		  
		  //definisanje ime racuna, racun + vrijeme kad je kreiran
		  String fileName = "racun"+System.currentTimeMillis() + ".txt";
		  Path filePath = folderPath.resolve(fileName);//spaja se putanja i ime fajla iz nekog razloga
		  Files.write(filePath, racun.generisiRacun().getBytes());//upisicanje u fajl
		  //imamo metodu za formatiranje racuna i ona se poziva nad objektom racun koji smo ovjde proslijedili, koji vraca string, a string se prosledjuje u metodu write
		  //Ovo radi :)
	  }
	  

}
