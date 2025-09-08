package util;

import model.Racun;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class RacunUtil {

	private static final String FOLDER_NAME = "racuni";
	/**
	 * Saves a receipt (Racun) to a text file in the designated folder.
	 *
	 * <p>
	 * If the folder does not exist, it is created. The file name is generated
	 * using the prefix "racun" followed by the current system timestamp.
	 * The contents of the file are obtained by calling {@code generisiRacun()}
	 * on the provided Racun object.
	 * </p>
	 *
	 * @param racun the Racun object to save
	 * @throws IOException if an I/O error occurs during folder creation or file writing
	 */

	public static void sacuvajRacun(Racun racun) throws IOException {

		Path folderPath = Paths.get(FOLDER_NAME);
		if (!Files.exists(folderPath)) {
			Files.createDirectories(folderPath);
		}

		String fileName = "racun" + System.currentTimeMillis() + ".txt";
		Path filePath = folderPath.resolve(fileName);
		Files.write(filePath, racun.generisiRacun().getBytes());

	}
	/**
	 * Loads statistics from all saved receipt files in the designated folder.
	 *
	 * <p>
	 * The method counts the total number of receipts (tickets) and calculates
	 * the total revenue by reading the "Cijena" field from each text file
	 * in the folder. Only files with a ".txt" extension are processed.
	 * </p>
	 *
	 * @return a map containing:
	 *         <ul>
	 *           <li>"brojKarata" – the total number of tickets</li>
	 *           <li>"ukupanPrihod" – the total revenue from all tickets</li>
	 *         </ul>
	 * @throws IOException if an I/O error occurs while reading the files
	 */

	public static Map<String, Object> ucitajStatistiku() throws IOException {

		Path folderPath = Paths.get(FOLDER_NAME);
		
		int brojKarata = 0;
		double ukupanPrihod = 0.0;

		if (Files.exists(folderPath)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "*.txt")) {
				for (Path file : stream) {
					brojKarata++;
					List<String> lines = Files.readAllLines(file);
					for (String line : lines) {
						if (line.startsWith("Cijena:")) {
							String cijenaStr = line.replace("Cijena:", "").trim();
							cijenaStr = cijenaStr.replaceAll("[^0-9.,]", "").replace(",", ".");
							ukupanPrihod += Double.parseDouble(cijenaStr);
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

	public static int getBrojKarata() throws IOException {
		return (int) ucitajStatistiku().get("brojKarata");
	}

	public static double getUkupanPrihod() throws IOException {
		return (double) ucitajStatistiku().get("ukupanPrihod");
	}

}
