//454 lines
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RouteFinder {
    Network network = new Network();

    public void createNetwork() {
        LinkedHashMap<String, Line> lines = network.createLines();
        this.network.addLines(lines);
    }

    public String getInput(String message) {
        System.out.print(message);
        Input in = new Input();
        return in.nextLine();
    }

    public ArrayList<String> findRoute(String start, String end) {
        return this.network.findRoute(start, end, false);
    }

    public void outputRoute(String start, String end, ArrayList<String> route) {
        int len = route.size();
        if (len > 1) {
            System.out.println("Route " + start + " to " + end + ":");
            int count = 1;
            for (String station: route) {
                System.out.println("[" + count + "] " + station);
                count++;
            }
            System.out.println("");
            return;
        }
        System.out.println("A route could not be planned from " + start + " to " + end + ".");
        System.out.println("");
    }

    public void mainMenu(boolean displayHeader) {
        if (displayHeader) {
            System.out.println("------------------------------------");
            System.out.println("| London Underground Route Finder! |");
            System.out.println("------------------------------------");
            System.out.println("   Please choose an option below:  \n");
        }
        System.out.println("> Enter [F] to plan your route.\n> Enter [Q] to quit.");
        char option = getInput("> ").charAt(0);
        System.out.println();
        option = Character.toUpperCase(option);
        if (!((option == 'F') || (option == 'Q'))) {
            System.out.println("Please enter one of [F] and [Q]");
            mainMenu(false);
        }
        switch (option) {
            case 'F':
                String start = getInput("Please enter start destination:\n> ");
                System.out.println();
                String end = getInput("Please enter final destination:\n> ");
                System.out.println();
                ArrayList<String> route = findRoute(start, end);
                outputRoute(start, end, route);
                break;
            case 'Q':
                System.exit(0);
                break;
        }
        System.out.println();
        mainMenu(true);
    }

    public void test() {
        String start = "Stratford"; //long route cos of missing train
        String end = "Sloane Square";
        ArrayList<String> route = findRoute(start, end);
        outputRoute(start, end, route);
/*        start = "Earl's Court";
        end = "Paddington";
        route = findRoute(start, end);
        outputRoute(start, end, route);
        start = "Ealing Broadway";
        end = "Euston Square";
        route = findRoute(start, end);
        outputRoute(start, end, route);*/
        System.out.println("Note: Only overground line is Gospel Oak to Barking");
        System.out.println("      Only Northern line is Morden to Edgware via Bank");
        System.out.println("      No trams");
    }

    public static void main(String[] args) {
        RouteFinder nav = new RouteFinder();
        nav.createNetwork();
        nav.test();
        //nav.mainMenu(true);
    }
}
