import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Network {
    private LinkedHashMap<String, Line> lines;

    public void addLines(LinkedHashMap<String, Line> lines) {
        this.lines = lines;
    }

    private ArrayList<String> openFile() {
        FileInput in = new FileInput("/home/ernest/Documents/UCL/Java Programming/Week2/src/trains.txt");
        ArrayList<String> data = new ArrayList<>();
        while (in.hasNextLine()) {
            data.add(in.nextLine());
        }
        return data;
    }

    private LinkedHashMap<String, ArrayList<String>> addTrains(ArrayList<String> trains) {
        LinkedHashMap<String, ArrayList<String>> lines = new LinkedHashMap<>();
        int length = trains.size();
        int count = 0;
        while (count < length) {
            String line = trains.get(count);
            count++;
            ArrayList<String> stations = new ArrayList<>();
            while ((count < length) && !trains.get(count).equals("//")) {
                stations.add(trains.get(count));
                count++;
            }
            count++;
            lines.put(line, stations);
        }
        return lines;
    }

    private void pairStations(LinkedHashMap<String, Station> stations) {
        //setup the previous and next stations for each train station
        int len = stations.size();
        ArrayList<String> keys = new ArrayList<>(stations.keySet());
        int count;
        for (count=0; count<len-1; count++) {
            String currentIndex = keys.get(count);
            String nextIndex = keys.get(count+1);
            Station thisStation = stations.get(currentIndex);
            Station nextStation = stations.get(nextIndex);
            thisStation.assignNextStation(nextStation);
            if (count > 0) {
                String prevIndex = keys.get(count-1);
                Station prevStation = stations.get(prevIndex);
                thisStation.assignPrevStation(prevStation);
            }
        }
        String finalIndex = keys.get(len-1);
        String prevIndex = keys.get(len-2);
        Station station = stations.get(finalIndex);
        Station prevStation = stations.get(prevIndex);
        station.assignPrevStation(prevStation);
    }
    public LinkedHashMap<String, Line> createLines() {
        ArrayList<String> trainData = openFile();
        LinkedHashMap<String, ArrayList<String>> trains = addTrains(trainData);
        LinkedHashMap<String, Line> lines = new LinkedHashMap<>();
        for (String line: trains.keySet()) {
            ArrayList<String> stops = trains.get(line);
            LinkedHashMap<String, Station> stations = new LinkedHashMap<>();
            for (String station: stops) {
                Station newStation = new Station(station);
                stations.put(station, newStation);
            }
            pairStations(stations);
            Line newLine = new Line(line, stations);
            lines.put(line, newLine);
        }
        return lines;
    }

    public LinkedHashMap<String, Line> getLines() {
        return this.lines;
    }

    public Station getStationByName(String stationName) {
        for (Line line: this.lines.values()) {
            Station station = line.getStation(stationName);
            if (station != null) {
                return station;
            }
        }
        return null;
    }

    public Line getLineByName(String lineName) {
        for (String line: this.lines.keySet()) {
            if (line.equals(lineName)) {
                return this.lines.get(line);
            }
        }
        return null;
    }

    public LinkedHashMap<String, Line> getLinesAtStation(String stationName) {
        LinkedHashMap<String, Line> lines = new LinkedHashMap<>();
        for (String name: getLines().keySet()) {
            Line line = getLineByName(name);
            if (line.getAllStations().containsKey(stationName)) {
                lines.put(name, line);
            }
        }
        return lines;
    }
    public Line getLineFromStation(String stationName) {
        for (Line line: this.lines.values()) {
            if (line.getStation(stationName) != null) {
                return line;
            }
        }
        return null;
    }

    private ArrayList<String> traverseNetwork(Line line, Station currentNode, String end,
                                              ArrayList<String> route, boolean reverse) {
        ArrayList<String> emptyArr = new ArrayList<>();
        String currentStation = currentNode.getName();
        String nextStop = currentStation + " (" + line.getName() + ")";
        route.add(nextStop);
        if (currentStation.equals(end)) {
            return route;
        }
        Station nextStation = line.getNextStation(currentStation, reverse);
        if (nextStation != null) {
            return traverseNetwork(line, nextStation, end, route, reverse);
        }
        return emptyArr;//if route could not be found
    }

    public ArrayList<String> findRoute(String start, String end, boolean reverse) {
        Station startNode = getStationByName(start);
        Station endNode = getStationByName(end);
        Line line = getLineFromStation(start);
        ArrayList<String> route = new ArrayList<>();
        ArrayList<String> emptyArr = new ArrayList<>();
        if ((startNode != null) && (endNode != null) && (line != null)) {
            Line oneLine = getCommonLine(start, end);
            if (oneLine != null) {
                //route = traverseNetwork(oneLine, startNode, end, route, reverse);
                ArrayList<String> forwardRoute = traverseNetwork(oneLine, startNode, end, emptyArr, false);
                //for some reason, forward and back can't use the same array (route)
                ArrayList<String> backwardRoute = traverseNetwork(oneLine, startNode, end, route, true);
                if ((forwardRoute.size() > 0) && (backwardRoute.size() > 0)) {
                    route = findBestRoute(forwardRoute, backwardRoute);
                }
                else {
                    if (forwardRoute.size() > 0) {
                        route = forwardRoute;
                    }
                    else if (backwardRoute.size() > 0) {
                        route = backwardRoute;
                    }
                    else {
                        route = emptyArr;
                    }
                }
            }
            else {
                route = findAnyConnection(start, end, line, reverse);
            }
            if ((route.size() == 0) && (!reverse)) {
                route = findRoute(start, end, true);
            }
        }
        return route;
    }

    private ArrayList<String> findBestRoute(ArrayList<String> route1, ArrayList<String> route2) {
        if (route1.size() <= route2.size()) {
            return route1;
        }
        return route2;
    }
    private Line getCommonLine(String start, String end) {
        LinkedHashMap<String, Line> startLines = getLinesAtStation(start);
        LinkedHashMap<String, Line> endLines = getLinesAtStation(end);
        ArrayList<String> startKeys = new ArrayList<>(startLines.keySet());
        ArrayList<String> endKeys = new ArrayList<>(endLines.keySet());
        ArrayList<String> commonLines= new ArrayList<>(startKeys);
        commonLines.retainAll(endKeys);
        if (commonLines.size() > 0) {
            return getLineByName(commonLines.get(0));
        }
        return null;
    }

    private ArrayList<String> findAnyConnection(String start, String end, Line line, boolean reverse) {
        ArrayList<String> route = new ArrayList<>();
        Station connectionPoint = findConnection(start, end);
        Line endLine = getLineFromStation(end);
        if (connectionPoint != null) {
            String connectionName = connectionPoint.getName();
            route.addAll(findRoute(start, connectionName, reverse));
            route.addAll(findRoute(connectionName, end, reverse));
        }
        else {
            route.addAll(connectTrains(start, line, end, endLine));
        }
        return route;
    }

    private ArrayList<String> connectTrains(String start, Line line, String end, Line endLine) {
        ArrayList<String> route = new ArrayList<>();
        Line connectingTrain = findConnectingTrain(start, end);
        if (connectingTrain != null) {
//            Station startConnection = findCommonStation(line, connectingTrain);
//            assert startConnection != null;
//            String firstSwitch = startConnection.getName();
//            Station endConnection = findCommonStation(connectingTrain, endLine);
//            assert endConnection != null;
//            String finalSwitch = endConnection.getName();

            LinkedHashMap<String, Station> commonStations = findCommonStation(line, connectingTrain);
            Station nearestStation = findNearestStation(start, end, commonStations);
            assert nearestStation != null;
            String firstSwitch = nearestStation.getName();

            commonStations = findCommonStation(connectingTrain, endLine);
            Station nextStation = findNearestStation(start, end, commonStations);
            assert nextStation != null;
            String finalSwitch = nextStation.getName();

            route.addAll(findRoute(start, firstSwitch, false));
            route.addAll(findRoute(firstSwitch, finalSwitch, false));
            route.addAll(findRoute(finalSwitch, end, false));
        }
        return route;
    }

    private Line findConnectingTrain(String start, String end) {
        Line startLine = getLineFromStation(start);
        Line endLine = getLineFromStation(end);
        Line connectingLine = null;
        for (Line line: getLines().values()) {
            LinkedHashMap<String, Station> startConnection = findCommonStation(startLine, line);
            LinkedHashMap<String, Station> endConnection = findCommonStation(line, endLine);
//            Station startConnection = findCommonStation(startLine, line);
//            Station endConnection = findCommonStation(line, endLine);
            if ((startConnection.size() > 0) && (endConnection.size() > 0)) {
                connectingLine = line;
                break;
            }
        }
        return connectingLine;
    }

    private Station findConnection(String start, String end) {
        Line startLine = getLineFromStation(start);
        Line endLine = getLineFromStation(end);
        LinkedHashMap<String, Station> commonStations = findCommonStation(startLine, endLine);
        return findNearestStation(start, end, commonStations);
    }

    private LinkedHashMap<String, Station> findCommonStation(Line startLine, Line endLine) {
        LinkedHashMap<String, Station> startStations = startLine.getAllStations();
        LinkedHashMap<String, Station> endStations = endLine.getAllStations();
        return findAllCommonStations(startStations, endStations);
//        for (Station station: startStations.values()) {
//            String stationName = station.getName();
//            if (endStations.containsKey(stationName)) {
//                return station;
//            }
//        }
//        return null;
    }

    private LinkedHashMap<String, Station> findAllCommonStations(LinkedHashMap<String, Station> startStations,
                                                                 LinkedHashMap<String, Station> endStations) {
        LinkedHashMap<String, Station> commonStations = new LinkedHashMap<>();
        for (String stationName: startStations.keySet()) {
            if (endStations.containsKey(stationName)) {
                Station station = getStationByName(stationName);
                commonStations.put(stationName, station);
            }
        }
        return commonStations;
    }

    private Station findNearestStation(String start, String end, LinkedHashMap<String, Station> commonStations) {
        if (commonStations.size() == 0) {
            return null;
        }
        LinkedHashMap<String, ArrayList<String>> routes = new LinkedHashMap<>();
        for (Station station: commonStations.values()) {
            String name = station.getName();
            ArrayList<String> route1 = findRoute(start, name, false);
            ArrayList<String> route2 = findRoute(name, end, false);
            if ((route1.size() > 0) && (route2.size() > 0)) {
                route1.addAll(route2);
                routes.put(name, route1);
            }
        }
        return getStationByName(findShortestJourney(routes));
    }

    private String findShortestJourney(LinkedHashMap<String, ArrayList<String>> routes) {
        int minLen = 9999999;
        ArrayList<String> keys = new ArrayList<>(routes.keySet());
        String bestStation = keys.get(0);
        for (String stationName: routes.keySet()) {
            ArrayList<String> route = routes.get(stationName);
            int len = route.size();
            if (len < minLen) {
                minLen = len;
                bestStation = stationName;
            }
        }
        return bestStation;
    }
}
