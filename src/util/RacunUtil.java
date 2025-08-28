package util;

import model.Racun;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class RacunUtil {

	private static final String FOLDER_NAME = "racuni";

	public static void sacuvajRacun(Racun racun) throws IOException {

		Path folderPath = Paths.get(FOLDER_NAME);
		if (!Files.exists(folderPath)) {
			Files.createDirectories(folderPath);
		}

		String fileName = "racun" + System.currentTimeMillis() + ".txt";
		Path filePath = folderPath.resolve(fileName);
		Files.write(filePath, racun.generisiRacun().getBytes());

	}

	public static Map<String, Object> ucitajStatistiku() throws IOException {

		Path folderPath = Paths.get(FOLDER_NAME);
		// Map<String, Object> stats = new HashMap<>();
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
