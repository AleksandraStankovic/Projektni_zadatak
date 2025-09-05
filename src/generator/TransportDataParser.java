package generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;

public class TransportDataParser {

	/**
	 * @param filename
	 * @return
	 */
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
