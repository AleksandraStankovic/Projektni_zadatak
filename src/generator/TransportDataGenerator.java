package generator;

import view.StartupFrame;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import model.Station;
import model.BusStation;
import model.TrainStation;

/**
 * Generates transport network data including cities, stations, and departures.
 * Provides methods to generate, save, and manipulate transport data.
 */
public class TransportDataGenerator {
	private static int rows;
	private static int cols;
	private static final int DEPARTURES_PER_STATION = 10;
	private static final Random random = new Random();

	public static void setDimensions(int rows, int cols) {
		if (rows <= 0 || cols <= 0) {
			throw new IllegalArgumentException("Dimenzije moraju biti pozitivni cijeli brojevi. ");
		}
		TransportDataGenerator.rows = rows;
		TransportDataGenerator.cols = cols;
	}

	public static void generateAndSaveData() {
		TransportDataGenerator generator = new TransportDataGenerator();
		TransportData data = generator.generateData();
		generator.saveToJson(data, "transport_data.json");

	}

	private TransportDataGenerator() {
		if (rows == 0 || cols == 0) {
			throw new IllegalStateException("Dimensions not initialized");
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			StartupFrame sFrame = new StartupFrame();
			sFrame.setVisible(true);
		});

	}

	public static class TransportData {
		public String[][] countryMap;
		public List<Station> stations;
		public List<Departure> departures;
	}

	public static class Departure {
		public String type; // "autobus" ili "voz"
		public String from;
		public String to;
		public String departureTime;
		public int duration; // u minutama
		public int price;
		public int minTransferTime; // vrijeme potrebno za transfer (u minutama)
	}

	public TransportData generateData() {
		TransportData data = new TransportData();
		data.countryMap = generateCountryMap();
		data.stations = generateStations();
		data.departures = generateDepartures(data.stations);
		return data;
	}

	// generisanje gradova (G_X_Y)
	private String[][] generateCountryMap() {
		String[][] countryMap = new String[rows][cols];
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				countryMap[x][y] = "G_" + x + "_" + y;
			}
		}
		return countryMap;
	}

	/**
	 * Generates stations for all cities in the country map. For each city, both a
	 * bus station and a train station are created. The station codes are generated
	 * using a consistent naming scheme: - Bus stations: "A_X_Y" - Train stations:
	 * "Z_X_Y"
	 *
	 * @return a list containing all generated bus and train stations
	 */
	private List<Station> generateStations() {
		List<Station> stations = new ArrayList<>();
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				String city = "G_" + x + "_" + y;

				BusStation bus = new BusStation(city, "A_" + x + "_" + y);
				TrainStation train = new TrainStation(city, "Z_" + x + "_" + y);

				stations.add(bus);
				stations.add(train);

			}
		}
		return stations;
	}

	/**
	 * Generates departures for each station provided in the list. Each station will
	 * have a fixed number of departures defined by DEPARTURES_PER_STATION. The
	 * departure details (destination, time, duration, price, and minimum transfer
	 * time) are randomly generated.
	 *
	 * @param stations the list of stations for which departures will be generated
	 * @return a list containing all generated departures
	 */

	private List<Departure> generateDepartures(List<Station> stations) {
		List<Departure> departures = new ArrayList<>();

		for (Station station : stations) {
			int x = Integer.parseInt(station.getCity().split("_")[1]);
			int y = Integer.parseInt(station.getCity().split("_")[2]);

			for (int i = 0; i < DEPARTURES_PER_STATION; i++) {
				departures.add(generateDeparture(station.getType(), station.getStationCode(), x, y));
			}
		}
		return departures;
	}

	/**
	 * Generates a single departure from a given station. The departure's
	 * destination, departure time, duration, price, and minimum transfer time are
	 * generated randomly. The destination is chosen from neighboring cities, if
	 * available.
	 *
	 * @param type the type of transport ("autobus" or "voz")
	 * @param from the station code from which the departure originates
	 * @param x    the row index of the city in the country map
	 * @param y    the column index of the city in the country map
	 * @return a Departure object with randomly generated details
	 */
	private Departure generateDeparture(String type, String from, int x, int y) {
		Departure departure = new Departure();
		departure.type = type;
		departure.from = from;

		// generisanje susjeda
		List<String> neighbors = getNeighbors(x, y);
		departure.to = neighbors.isEmpty() ? from : neighbors.get(random.nextInt(neighbors.size()));

		// generisanje vremena
		int hour = random.nextInt(24);
		int minute = random.nextInt(4) * 15; // 0, 15, 30, 45
		departure.departureTime = String.format("%02d:%02d", hour, minute);

		// geneirsanje cijene
		departure.duration = 30 + random.nextInt(151);
		departure.price = 100 + random.nextInt(901);

		// generisanje vremena transfera
		departure.minTransferTime = 5 + random.nextInt(26);

		return departure;
	}

	// pronalazak susjednih gradova
	private List<String> getNeighbors(int x, int y) {
		List<String> neighbors = new ArrayList<>();
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

		for (int[] dir : directions) {
			int nx = x + dir[0];
			int ny = y + dir[1];
			if (nx >= 0 && nx < rows && ny >= 0 && ny < cols) {
				neighbors.add("G_" + nx + "_" + ny);
			}
		}
		return neighbors;
	}

	// cuvanje podataka u JSON mapu
	private void saveToJson(TransportData data, String filename) {
		try (FileWriter file = new FileWriter(filename)) {
			StringBuilder json = new StringBuilder();
			json.append("{\n");

			// mapa drzave
			json.append("  \"countryMap\": [\n");
			for (int i = 0; i < rows; i++) {
				json.append("    [");
				for (int j = 0; j < cols; j++) {
					json.append("\"").append(data.countryMap[i][j]).append("\"");
					if (j < cols - 1)
						json.append(", ");
				}
				json.append("]");
				if (i < rows - 1)
					json.append(",");
				json.append("\n");
			}
			json.append("  ],\n");

			json.append("  \"stations\": [\n");
			for (int i = 0; i < data.stations.size(); i++) {
				model.Station s = data.stations.get(i);
				json.append("    {\"city\": \"").append(s.getCity()).append("\", \"stationCode\": \"")
						.append(s.getStationCode()).append("\", \"type\": \"").append(s.getType()).append("\"}");
				if (i < data.stations.size() - 1)
					json.append(",");
				json.append("\n");
			}
			json.append("  ],\n");

			json.append("  \"departures\": [\n");
			for (int i = 0; i < data.departures.size(); i++) {
				Departure d = data.departures.get(i);
				json.append("    {\"type\": \"").append(d.type).append("\", \"from\": \"").append(d.from)
						.append("\", \"to\": \"").append(d.to).append("\", \"departureTime\": \"")
						.append(d.departureTime).append("\", \"duration\": ").append(d.duration).append(", \"price\": ")
						.append(d.price).append(", \"minTransferTime\": ").append(d.minTransferTime).append("}");
				if (i < data.departures.size() - 1)
					json.append(",");
				json.append("\n");
			}
			json.append("  ]\n");

			json.append("}");
			file.write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}