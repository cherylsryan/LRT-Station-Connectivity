
import java.util.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
public class LRTConsoleApp {
    
    private static Scanner scanner = new Scanner(System.in);
    private static UnweightedGraph<LRTStation> network = new UnweightedGraph<>();
    private static String staffId = "LR01";
    
    public static void main(String[] args) {
        // Pre-load sample data
        loadSampleData();
        
        System.out.println("Welcome to KL LRT System!");
        System.out.println();
        System.out.println("Staff ID: " + staffId);
        System.out.println();
        
        int choice;
        do {
            displayMainMenu();
            choice = getIntInput("Selection: ");
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
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        } while (choice != 0);
        
        scanner.close();
    }
    
    // ============================================================
    // SAMPLE DATA
    // ============================================================
    
    private static void loadSampleData() {
        // Kelana Jaya Line: KLCC → Kampung Baru → Dang Wangi → ★ Masjid Jamek → Pasar Seni
        network.addVertex("KLCC", "Kelana Jaya");
        network.addVertex("Kampung Baru", "Kelana Jaya");
        network.addVertex("Dang Wangi", "Kelana Jaya");
        network.addVertex("Masjid Jamek", "Interchange");
        network.addVertex("Pasar Seni", "Kelana Jaya");
        
        network.addEdge("KLCC", "Kampung Baru");
        network.addEdge("Kampung Baru", "Dang Wangi");
        network.addEdge("Dang Wangi", "Masjid Jamek");
        network.addEdge("Masjid Jamek", "Pasar Seni");
        
        // Ampang Line: PWTC → Sultan Ismail → Bandaraya → ★ Masjid Jamek → Plaza Rakyat
        network.addVertex("PWTC", "Ampang");
        network.addVertex("Sultan Ismail", "Ampang");
        network.addVertex("Bandaraya", "Ampang");
        network.addVertex("Plaza Rakyat", "Ampang");
        
        network.addEdge("PWTC", "Sultan Ismail");
        network.addEdge("Sultan Ismail", "Bandaraya");
        network.addEdge("Bandaraya", "Masjid Jamek");
        network.addEdge("Masjid Jamek", "Plaza Rakyat");
    }
    
    // ============================================================
    // MENUS
    // ============================================================
    
    private static void displayMainMenu() {
        System.out.println("Main Menu (Press '0' to back to main menu)");
        System.out.println();
        System.out.println("Menu: Enter \"1\" to \"3\" for computation option.");
        System.out.println();
        System.out.println("1. Create Graph");
        System.out.println("2. Search for a Station");
        System.out.println("3. View the LRT Network (Graph)");
        System.out.println("0. Exit");
        System.out.println();
        System.out.print("Selection: ");
    }
    
    private static void displayCreateGraphMenu() {
        System.out.println();
        System.out.println("Create Graph: Enter \"1\" to \"4\" for updating the graph.");
        System.out.println();
        System.out.println("1. Add a vertex");
        System.out.println("2. Remove a vertex");
        System.out.println("3. Add an edge");
        System.out.println("4. Remove an edge");
        System.out.println("0. Return to the main menu");
        System.out.println();
        System.out.print("Selection: ");
    }
    
    private static void displaySearchMenu() {
        System.out.println();
        System.out.println("Search: Enter \"A\" to \"C\" for traversal option.");
        System.out.println();
        System.out.println("A. BFS Traversal");
        System.out.println("B. DFS Traversal");
        System.out.println("C. Shortest Path (Minimum Stops)");
        System.out.println("0. Return to the main menu");
        System.out.println();
        System.out.print("Selection: ");
    }
    
    // ============================================================
    // (c) DISPLAY GRAPH - JavaFX
    // ============================================================
    
    private static void displayGraph() {
        if (network.getSize() == 0) {
            System.out.println("Network is empty. Add some stations first!");
            return;
        }
        
        System.out.println("Opening LRT Network Graph...");
        System.out.println("Close the graph window to return to menu.");
        System.out.println();
        
        // Launch JavaFX graph in a new thread
        new Thread(() -> {
            GraphVisualizer.displayGraph(network, "LRT Network - KL System");
        }).start();
    }
    
    // ============================================================
    // CREATE GRAPH MENU
    // ============================================================
    
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
    
    // ============================================================
    // (i) ADD VERTEX
    // ============================================================
    
    private static void addVertex() {
        boolean continueAdding = true;
        
        while (continueAdding) {
            System.out.println("#(1) Add a vertex");
            System.out.println("---");
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
                case 1: line = "Kelana Jaya"; break;
                case 2: line = "Ampang"; break;
                case 3: line = "Interchange"; break;
                default: line = "Kelana Jaya"; break;
            }
            
            boolean added = network.addVertex(name, line);
            if (added) {
                System.out.println("Station '" + name + "' added successfully!");
            } else {
                System.out.println("Station '" + name + "' already exists.");
            }
            
            System.out.println();
            System.out.print("Continue? Y/N: ");
            String response = scanner.nextLine().trim().toUpperCase();
            continueAdding = response.equals("Y");
            System.out.println();
        }
    }
    
    // ============================================================
    // (ii) REMOVE VERTEX
    // ============================================================
    
    private static void removeVertex() {
        if (network.getSize() == 0) {
            System.out.println("No stations to remove. Add some first!");
            return;
        }
        
        System.out.println("#(2) Remove a vertex");
        System.out.println("---");
        System.out.print("Enter the name of the city to remove: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        
        boolean removed = network.removeVertex(name);
        if (removed) {
            System.out.println("Station '" + name + "' removed successfully!");
        } else {
            System.out.println("Station '" + name + "' not found.");
        }
        
        System.out.println();
        System.out.print("Continue? Y/N: ");
        String response = scanner.nextLine().trim().toUpperCase();
        if (response.equals("Y")) {
            removeVertex();
        }
    }
    
    // ============================================================
    // (iii) ADD EDGE
    // ============================================================
    
    private static void addEdge() {
        if (network.getSize() < 2) {
            System.out.println("Need at least 2 stations to add a connection.");
            return;
        }
        
        boolean continueAdding = true;
        
        while (continueAdding) {
            System.out.println("#(3) Add an edge");
            System.out.println("---");
            System.out.print("Enter the 1st vertex: ");
            String from = scanner.nextLine().trim();
            System.out.print("Enter the 2nd vertex: ");
            String to = scanner.nextLine().trim();
            
            if (from.isEmpty() || to.isEmpty()) {
                System.out.println("Station names cannot be empty.");
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
                System.out.println("One or both stations not found, or connection already exists.");
            }
            
            System.out.println();
            System.out.print("Continue? Y/N: ");
            String response = scanner.nextLine().trim().toUpperCase();
            continueAdding = response.equals("Y");
            System.out.println();
        }
    }
    
    // ============================================================
    // (iv) REMOVE EDGE
    // ============================================================
    
    private static void removeEdge() {
        if (network.countEdges() == 0) {
            System.out.println("No connections to remove.");
            return;
        }
        
        System.out.println("#(4) Remove an edge");
        System.out.println("---");
        System.out.print("Enter the 1st vertex: ");
        String from = scanner.nextLine().trim();
        System.out.print("Enter the 2nd vertex: ");
        String to = scanner.nextLine().trim();
        
        if (from.isEmpty() || to.isEmpty()) {
            System.out.println("Station names cannot be empty.");
            return;
        }
        
        boolean removed = network.removeEdge(from, to);
        if (removed) {
            System.out.println("Connection between " + from + " and " + to + " removed.");
        } else {
            System.out.println("Connection not found.");
        }
        
        System.out.println();
        System.out.print("Continue? Y/N: ");
        String response = scanner.nextLine().trim().toUpperCase();
        if (response.equals("Y")) {
            removeEdge();
        }
    }
    
    // ============================================================
    // SEARCH MENU
    // ============================================================
    
    private static void searchMenu() {
        int choice;
        do {
            displaySearchMenu();
            String input = scanner.nextLine().trim().toUpperCase();
            System.out.println();
            
            if (input.equals("A")) {
                bfsTraversal();
            } else if (input.equals("B")) {
                dfsTraversal();
            } else if (input.equals("C")) {
                shortestPath();
            } else if (input.equals("0")) {
                System.out.println("Returning to main menu.");
                break;
            } else {
                System.out.println("Invalid selection.");
            }
        } while (true);
    }
    
    // ============================================================
    // BFS TRAVERSAL
    // ============================================================
    
    private static void bfsTraversal() {
        if (network.getSize() == 0) {
            System.out.println("Network is empty. Add some stations first!");
            return;
        }
        
        System.out.println("A. BFS Traversal");
        System.out.println("---");
        System.out.print("Enter the starting station: ");
        String start = scanner.nextLine().trim();
        
        if (start.isEmpty()) {
            System.out.println("Station name cannot be empty.");
            return;
        }
        
        List<String> result = network.bfsTraversal(start);
        
        if (result.isEmpty()) {
            System.out.println("Station '" + start + "' not found.");
        } else {
            System.out.println();
            System.out.println("BFS Traversal from " + start + ":");
            System.out.println("---");
            for (int i = 0; i < result.size(); i++) {
                String marker = result.get(i).equals("Masjid Jamek") ? " ★" : "";
                System.out.println("  " + i + " stops: " + result.get(i) + marker);
            }
            System.out.println("  Total stations visited: " + result.size());
        }
        
        System.out.println();
        System.out.print("Continue? Y/N: ");
        String response = scanner.nextLine().trim().toUpperCase();
        if (response.equals("Y")) {
            bfsTraversal();
        }
    }
    
    // ============================================================
    // DFS TRAVERSAL
    // ============================================================
    
    private static void dfsTraversal() {
        if (network.getSize() == 0) {
            System.out.println("Network is empty. Add some stations first!");
            return;
        }
        
        System.out.println("B. DFS Traversal");
        System.out.println("---");
        System.out.print("Enter the starting station: ");
        String start = scanner.nextLine().trim();
        
        if (start.isEmpty()) {
            System.out.println("Station name cannot be empty.");
            return;
        }
        
        List<String> result = network.dfsTraversal(start);
        
        if (result.isEmpty()) {
            System.out.println("Station '" + start + "' not found.");
        } else {
            System.out.println();
            System.out.println("DFS Traversal from " + start + ":");
            System.out.println("---");
            for (int i = 0; i < result.size(); i++) {
                String marker = result.get(i).equals("Masjid Jamek") ? " ★" : "";
                System.out.println("  " + (i+1) + ". " + result.get(i) + marker);
            }
            System.out.println("  Total stations visited: " + result.size());
        }
        
        System.out.println();
        System.out.print("Continue? Y/N: ");
        String response = scanner.nextLine().trim().toUpperCase();
        if (response.equals("Y")) {
            dfsTraversal();
        }
    }
    
    // ============================================================
    // SHORTEST PATH
    // ============================================================
    
    private static void shortestPath() {
        if (network.getSize() < 2) {
            System.out.println("Need at least 2 stations to find a path.");
            return;
        }
        
        System.out.println("C. Shortest Path (Minimum Stops)");
        System.out.println("---");
        System.out.print("Enter the starting station: ");
        String from = scanner.nextLine().trim();
        System.out.print("Enter the destination station: ");
        String to = scanner.nextLine().trim();
        
        if (from.isEmpty() || to.isEmpty()) {
            System.out.println("Station names cannot be empty.");
            return;
        }
        
        List<String> path = network.findPath(from, to);
        
        if (path.isEmpty()) {
            System.out.println("No path found between " + from + " and " + to + ".");
        } else {
            System.out.println();
            System.out.println("Shortest path from " + from + " to " + to + ":");
            System.out.println("---");
            for (int i = 0; i < path.size(); i++) {
                String marker = path.get(i).equals("Masjid Jamek") ? " ★ INTERCHANGE" : "";
                System.out.println("  " + (i+1) + ". " + path.get(i) + marker);
            }
            System.out.println("  Total stops: " + (path.size() - 1));
        }
        
        System.out.println();
        System.out.print("Continue? Y/N: ");
        String response = scanner.nextLine().trim().toUpperCase();
        if (response.equals("Y")) {
            shortestPath();
        }
    }
    
    // ============================================================
    // HELPER: Get Integer Input
    // ============================================================
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                if (!prompt.isEmpty()) System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
