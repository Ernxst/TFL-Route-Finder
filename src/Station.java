public class Station {
    private final String name;
    private Station nextStation = null;
    private Station prevStation = null;
    public Station(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void assignNextStation(Station next) {
        this.nextStation = next;
    }
    public void assignPrevStation(Station prev) {
        this.prevStation = prev;
    }
    public Station getNextStation() {
        return this.nextStation;
    }
    public Station getPrevStation() {
        return this.prevStation;
    }
}
