package generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Utility class for parsing transport data from a JSON file.
 * <p>
 * This class provides a static method to read JSON content from a file 
 * and convert it into a {@link TransportDataGenerator.TransportData} object
 * using the Jackson library.
 * </p>
 */

public class TransportDataParser {

	
	public static TransportDataGenerator.TransportData parse(String filename) {

		try {

			String jsonContent = Files.readString(Path.of(filename));

			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonContent, TransportDataGenerator.TransportData.class);
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
