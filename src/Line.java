import java.util.LinkedHashMap;

public class Line {
    private final String name;
    private final LinkedHashMap<String, Station> stations;

    public Line(String name, LinkedHashMap<String, Station> stations) {
        this.name = name;
        this.stations = stations;
    }

    public String getName() {
        return this.name;
    }

    public LinkedHashMap<String, Station> getAllStations() {
        return this.stations;
    }

    public Station getStation(String name) {
        if (this.stations.containsKey(name)) {
            return this.stations.get(name);
        }
        return null;
    }

    public Station getNextStation(String currentStation, boolean reverse) {
        Station station = getStation(currentStation);
        if (station != null) {
            if (reverse) {
                return station.getPrevStation();
            }
            else {
                return station.getNextStation();
            }
        }
        return null;
    }
}
