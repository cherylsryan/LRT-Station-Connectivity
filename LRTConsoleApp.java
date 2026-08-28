import java.util.Scanner;
import java.util.List;

public class LRTConsoleApp {
 
    private static Scanner scanner = new Scanner(System.in);
    private static UnweightedGraph<LRTStation> network = new UnweightedGraph<>();
    private static String staffId = "LR01";
 
    public static void main(String[] args) {
        loadSampleData();
 
        System.out.println("Welcome to KL LRT System!");
        System.out.println("Staff ID: " + staffId + "\n");
 
        int choice;
        do {
            displayMainMenu();
            choice = getIntInput("");
            System.out.println();
 
            switch (choice) {
                case 1:
                    createGraphMenu();
                    break;
                case 2:
                    searchMenu();
                    break;
                case 3:
                    displayGraph();
                    break;
                case 0:
                    System.out.println("Exiting system..... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        } while (choice != 0);
 
        scanner.close();
    }
 
    private static void loadSampleData() {
        // Kelana Jaya Line: KLCC → Kampung Baru → Dang Wangi → Masjid Jamek → Pasar Seni
        network.addVertex("KLCC", "Kelana Jaya");
        network.addVertex("Kampung Baru", "Kelana Jaya");
        network.addVertex("Dang Wangi", "Kelana Jaya");
        network.addVertex("Masjid Jamek", "Interchange");
        network.addVertex("Pasar Seni", "Kelana Jaya");
 
        network.addEdge("KLCC", "Kampung Baru");
        network.addEdge("Kampung Baru", "Dang Wangi");
        network.addEdge("Dang Wangi", "Masjid Jamek");
        network.addEdge("Masjid Jamek", "Pasar Seni");
 
        // Ampang Line: PWTC → Sultan Ismail → Bandaraya → Masjid Jamek → Plaza Rakyat
        network.addVertex("PWTC", "Ampang");
        network.addVertex("Sultan Ismail", "Ampang");
        network.addVertex("Bandaraya", "Ampang");
        network.addVertex("Plaza Rakyat", "Ampang");
 
        network.addEdge("PWTC", "Sultan Ismail");
        network.addEdge("Sultan Ismail", "Bandaraya");
        network.addEdge("Bandaraya", "Masjid Jamek");
        network.addEdge("Masjid Jamek", "Plaza Rakyat");
    }
 
    // main menu
    private static void displayMainMenu() {
        System.out.println("========================================");
        System.out.println("         LRT SYSTEM MAIN MENU");
        System.out.println("   \"Ride the Rails, Explore the City\"");
        System.out.println("========================================\n");
        System.out.println("Please select an option:");
        System.out.println("[1] Create Graph");
        System.out.println("[2] Graph Traversal");
        System.out.println("[3] View the LRT Network (Graph)");
        System.out.println("[0] Exit\n");
        System.out.print("Enter your choice: ");
    }
 
    private static void displayCreateGraphMenu() {
        System.out.println("----------- Create Graph ---------");
        System.out.println("Enter \"1\" to \"4\" for updating the graph.\n");
        System.out.println("[1] Add a vertex");
        System.out.println("[2] Remove a vertex");
        System.out.println("[3] Add an edge");
        System.out.println("[4] Remove an edge");
        System.out.println("[0] Return to the main menu\n");
        System.out.print("Enter your choice: ");
    }
 
    private static void displaySearchMenu() {
        System.out.println("Enter \"1\" for traversal option.\n");
        System.out.println("[1] BFS Traversal");
        System.out.println("[0] Return to the main menu\n");
        System.out.print("Enter your choice: ");
    }
 
    private static void displayGraph() {
        if (network.getSize() == 0) {
            System.out.println("Network is empty. Add some stations first!");
            return;
        }
        GraphVisualizer.displayGraph(network, "LRT Network - KL System");
    }
 
    private static void createGraphMenu() {
        int choice;
        do {
            displayCreateGraphMenu();
            choice = getIntInput("");
            System.out.println();
 
            switch (choice) {
                case 1:
                    addVertex();
                    break;
                case 2:
                    removeVertex();
                    break;
                case 3:
                    addEdge();
                    break;
                case 4:
                    removeEdge();
                    break;
                case 0:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid selection.");
            }
        } while (choice != 0);
    }
 
    // add vertex
    private static void addVertex() {
        while (true) {
            System.out.println("----- (1) Add a vertex -----");
            System.out.print("Enter the name of the city: ");
            String name = scanner.nextLine().trim();
 
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                continue;
            }
 
            System.out.println("Select line:");
            System.out.println("1. Kelana Jaya");
            System.out.println("2. Ampang");
            System.out.println("3. Interchange");
            System.out.print("Choice: ");
            int lineChoice = getIntInput("");
 
            String line;
            switch (lineChoice) {
                case 1:
                    line = "Kelana Jaya";
                    break;
                case 2:
                    line = "Ampang";
                    break;
                case 3:
                    line = "Interchange";
                    break;
                default:
                    line = "Kelana Jaya";
                    break;
            }
 
            boolean added = network.addVertex(name, line);
            if (added) {
                System.out.println("Station '" + name + "' added successfully!");
            } else {
                System.out.println("Station '" + name + "' already exists.");
            }
 
            if (!askContinue()) {
                System.out.println();
                break;
            }
            System.out.println();
        }
    }
 
    // remove vertex
    private static void removeVertex() {
        if (network.getSize() == 0) {
            System.out.println("No stations to remove. Add some first!");
            System.out.println();
            return;
        }
 
        while (true) {
            System.out.println("----- (2) Remove a vertex -----");
            System.out.print("Enter the name of the city to remove: ");
            String name = scanner.nextLine().trim();
 
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                continue;
            }
 
            boolean removed = network.removeVertex(name);
            if (removed) {
                System.out.println("Station '" + name + "' removed successfully!");
            } else {
                System.out.println("Station '" + name + "' not found.");
            }
 
            if (!askContinue()) {
                System.out.println();
                break;
            }
            System.out.println();
        }
    }
 
    // add edge
    private static void addEdge() {
        if (network.getSize() < 2) {
            System.out.println("Need at least 2 stations to add a connection.");
            return;
        }
 
        while (true) {
            System.out.println("----- (3) Add an edge -----");
            System.out.print("Enter the 1st vertex: ");
            String from = scanner.nextLine().trim();
            System.out.print("Enter the 2nd vertex: ");
            String to = scanner.nextLine().trim();
 
            if (from.isEmpty() || to.isEmpty()) {
                System.out.println("Station names cannot be empty.\n");
                continue;
            }
 
            if (from.equalsIgnoreCase(to)) {
                System.out.println("Self-loops are not allowed.");
                continue;
            }
 
            boolean added = network.addEdge(from, to);
            if (added) {
                System.out.println("There is now a connection between " + from + " and " + to + ".");
            } else {
                // Check if stations exist
                if (!network.stationExists(from) || !network.stationExists(to)) {
                    System.out.println("One or both stations not found.");
                } else {
                    System.out.println("Connection already exists.");
                }
            }
            
            if (!askContinue()) {
                System.out.println();
                break;
            }
            System.out.println();
        }
    }
 
    // remove edge
    private static void removeEdge() {
        if (network.countEdges() == 0) {
            System.out.println("No connections to remove.");
            return;
        }
 
        while (true) {
            System.out.println("----- (4) Remove an edge -----");
            System.out.print("Enter the 1st vertex: ");
            String from = scanner.nextLine().trim();
            System.out.print("Enter the 2nd vertex: ");
            String to = scanner.nextLine().trim();
 
            if (from.isEmpty() || to.isEmpty()) {
                System.out.println("Station names cannot be empty.");
                continue;
            }
 
            boolean removed = network.removeEdge(from, to);
            if (removed) {
                System.out.println("Connection between " + from + " and " + to + " removed.");
            } else {
                System.out.println("Connection not found.");
            }
 
            if (!askContinue()) {
                System.out.println();
                break;
            }
            System.out.println();
        }
    }
 
    private static void searchMenu() {
        int choice;
 
        do {
            displaySearchMenu();
            choice = getIntInput("");
            System.out.println();
 
            switch (choice) {
                case 1:
                    bfsTraversal();
                    break;
                case 0:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid selection.");
            }
        } while (choice != 0);
    }
 
    private static void bfsTraversal() {
        if (network.getSize() == 0) {
            System.out.println("Network is empty. Add some stations first!\n");
            return;
        }
 
        while (true) {
            System.out.println("----- BFS Traversal -----");
            System.out.print("Enter the starting station: ");
            String start = scanner.nextLine().trim();
 
            if (start.isEmpty()) {
                System.out.println("Station name cannot be empty.");
                continue;
            }
 
            List<String> result = network.bfsTraversal(start);
 
            if (result.isEmpty()) {
                System.out.println("Station '" + start + "' not found.");
            } 
            else {
                System.out.println();
                System.out.println("BFS Traversal from " + start + ":");
                for (int i = 0; i < result.size(); i++) {
                    String marker = result.get(i).equals("Masjid Jamek") ? " [INTERCHANGE]" : "";
                    System.out.println("  Order " + i + ": " + result.get(i) + marker);
                }
                System.out.println("  Total stations visited: " + result.size());
            }
 
            if (!askContinue()) {
                System.out.println();
                break;
            }
            System.out.println();
        }
    }
 
    private static int getIntInput(String input) {
        while (true) {
            try {
                if (!input.isEmpty()) {
                    System.out.print(input);
                }
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
 
    private static boolean askContinue() {
        while (true) {
            System.out.print("\nContinue? Y/N: ");
            String response = scanner.nextLine().trim().toUpperCase();
 
            switch (response) {
                case "Y":
                    return true;
                case "N":
                    return false;
                default:
                    System.out.println("Invalid input. Please enter Y or N.");
                    break;
            }
        }
    }
}
