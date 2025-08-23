package util;


import model.Racun;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;



public class RacunUtil {
	
	
	  private static final String FOLDER_NAME = "racuni";
	 
	  
	  public static void sacuvajRacun(Racun racun) throws IOException//uzima racun koji ce da sacuva
	  {
		  //definisanje patha tj putanje za folder u koji ce se sve cuvati I guess
		  Path folderPath = Paths.get(FOLDER_NAME); //ovo uzima string koji je zadatak kao ime i konvertuje ga u putanju, path objekat, tj pdefinese se putanja gdje se upisuje 
		  if(!Files.exists(folderPath))//provjera da li postoji vec folder sa tim imenom. 
		  {
			  Files.createDirectories(folderPath);
		  }
		  
		 
		  String fileName = "racun"+System.currentTimeMillis() + ".txt";
		  Path filePath = folderPath.resolve(fileName);//spaja se putanja i ime fajla iz nekog razloga
		  Files.write(filePath, racun.generisiRacun().getBytes());//upisicanje u fajl
		  //imamo metodu za formatiranje racuna i ona se poziva nad objektom racun koji smo ovjde proslijedili, koji vraca string, a string se prosledjuje u metodu write
		  //Ovo radi :)
	  }

	  
	  public static Map<String, Object> ucitajStatistiku() throws IOException
	  {
		  
		  Path folderPath=Paths.get(FOLDER_NAME);
		//  Map<String, Object> stats = new HashMap<>();
		  int brojKarata = 0; 
		  double ukupanPrihod= 0.0;
		  
		  //provjera da li postoji folder sa tim imenom
		  if(Files.exists(folderPath))
		  {//otvara se novi stream I guess
			  try(DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "*.txt"))
			  {
				  for(Path file: stream)//za svaki fajl u steramu
				  {
					  brojKarata++; //poveca se broj karata cim jedan fajl pronadjemo
					  
					  //ucita sve linije u fajlu i trazi liniju koja pocinje sa Cijena
					  
					  //lista sa svim linijama 
					  List<String> lines = Files.readAllLines(file);
					  for(String line: lines)
					  {
						  if(line.startsWith("Cijena:"))
						  {
							  String cijenaStr = line.replace("Cijena:", "").trim();//skida prefiks cijena da samo ostane broj
							  cijenaStr = cijenaStr.replaceAll("[^0-9.,]", "").replace(",", ".");//uklanja valutu i sve sa , mijenja sa .
							  ukupanPrihod+=Double.parseDouble(cijenaStr);
						  }
					  }
					  
				  }
			  }
			
		  }
		  
		  Map<String, Object> stats = new HashMap<>();
	        stats.put("brojKarata", brojKarata);
	        stats.put("ukupanPrihod", ukupanPrihod);
	        return stats;
	  }
	  
	  //static jer se poziva na nivou klase bez instanciranja klase tj bez objekta
public static int getBrojKarata() throws IOException
{
	return (int) ucitajStatistiku().get("brojKarata");
}

public static double getUkupanPrihod() throws IOException {
    return (double) ucitajStatistiku().get("ukupanPrihod");
}

	
	  
	  
}
