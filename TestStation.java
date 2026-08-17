public class TestLRT {
    public static void main(String[] args) {
        UnweightedGraph<LRTStation> network = new UnweightedGraph<>();
        
        // ============================================================
        // KELANA JAYA LINE (KLCC → Pasar Seni)
        // ============================================================
        
        // Add vertices
        network.addVertex("KLCC", "Kelana Jaya");
        network.addVertex("Kampung Baru", "Kelana Jaya");
        network.addVertex("Dang Wangi", "Kelana Jaya");
        network.addVertex("Masjid Jamek", "Interchange");
        network.addVertex("Pasar Seni", "Kelana Jaya");
        
        // Add edges
        network.addEdge("KLCC", "Kampung Baru");
        network.addEdge("Kampung Baru", "Dang Wangi");
        network.addEdge("Dang Wangi", "Masjid Jamek");
        network.addEdge("Masjid Jamek", "Pasar Seni");
        
        // ============================================================
        // AMPANG LINE (PWTC → Plaza Rakyat)
        // ============================================================
        
        // Add vertices
        network.addVertex("PWTC", "Ampang");
        network.addVertex("Sultan Ismail", "Ampang");
        network.addVertex("Bandaraya", "Ampang");
        network.addVertex("Plaza Rakyat", "Ampang");
        
        // Add edges
        network.addEdge("PWTC", "Sultan Ismail");
        network.addEdge("Sultan Ismail", "Bandaraya");
        network.addEdge("Bandaraya", "Masjid Jamek");
        network.addEdge("Masjid Jamek", "Plaza Rakyat");
        
        // ============================================================
        // TEST 1: Print All Stations
        // ============================================================
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("           LRT NETWORK - ALL STATIONS");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        System.out.println("KELANA JAYA LINE (KLCC → Pasar Seni):");
        String[] kj = {"KLCC", "Kampung Baru", "Dang Wangi", "★ Masjid Jamek", "Pasar Seni"};
        for (int i = 0; i < kj.length; i++) {
            System.out.println("  " + (i+1) + ". " + kj[i]);
        }
        
        System.out.println("\nAMPANG LINE (PWTC → Plaza Rakyat):");
        String[] ampang = {"PWTC", "Sultan Ismail", "Bandaraya", "★ Masjid Jamek", "Plaza Rakyat"};
        for (int i = 0; i < ampang.length; i++) {
            System.out.println("  " + (i+1) + ". " + ampang[i]);
        }
        
        System.out.println("\nTotal Stations: " + network.getSize());
        System.out.println("Total Connections: " + countEdges(network));
        System.out.println();
        
        // ============================================================
        // TEST 2: Print All Edges
        // ============================================================
        System.out.println("TEST: Print All Edges");
        System.out.println("───────────────────────────────────────────────────────────");
        network.printEdges();
        System.out.println();
        
        // ============================================================
        // TEST 3: Get Neighbors of Masjid Jamek
        // ============================================================
        System.out.println("TEST: Neighbors of ★ Masjid Jamek");
        System.out.println("───────────────────────────────────────────────────────────");
        List<String> neighbors = network.getStationNeighbors("Masjid Jamek");
        System.out.println("Neighbors of Masjid Jamek:");
        for (String neighbor : neighbors) {
            System.out.println("  → " + neighbor);
        }
        System.out.println();
        
        // ============================================================
        // TEST 4: BFS Traversal from KLCC
        // ============================================================
        System.out.println("TEST: BFS Traversal from KLCC");
        System.out.println("───────────────────────────────────────────────────────────");
        List<String> bfsResult = network.bfsTraversal("KLCC");
        System.out.println("BFS from KLCC:");
        for (int i = 0; i < bfsResult.size(); i++) {
            String marker = bfsResult.get(i).equals("Masjid Jamek") ? " ★ INTERCHANGE" : "";
            System.out.println("  " + i + " stops: " + bfsResult.get(i) + marker);
        }
        System.out.println("  Total stations visited: " + bfsResult.size());
        System.out.println();
        
        // ============================================================
        // TEST 5: BFS Traversal from PWTC
        // ============================================================
        System.out.println("TEST: BFS Traversal from PWTC");
        System.out.println("───────────────────────────────────────────────────────────");
        List<String> bfsResult2 = network.bfsTraversal("PWTC");
        System.out.println("BFS from PWTC:");
        for (int i = 0; i < bfsResult2.size(); i++) {
            String marker = bfsResult2.get(i).equals("Masjid Jamek") ? " ★ INTERCHANGE" : "";
            System.out.println("  " + i + " stops: " + bfsResult2.get(i) + marker);
        }
        System.out.println("  Total stations visited: " + bfsResult2.size());
        System.out.println();
        
        // ============================================================
        // TEST 6: DFS Traversal from KLCC
        // ============================================================
        System.out.println("TEST: DFS Traversal from KLCC");
        System.out.println("───────────────────────────────────────────────────────────");
        List<String> dfsResult = network.dfsTraversal("KLCC");
        System.out.println("DFS from KLCC:");
        for (int i = 0; i < dfsResult.size(); i++) {
            String marker = dfsResult.get(i).equals("Masjid Jamek") ? " ★ INTERCHANGE" : "";
            System.out.println("  " + (i+1) + ". " + dfsResult.get(i) + marker);
        }
        System.out.println("  Total stations visited: " + dfsResult.size());
        System.out.println();
        
        // ============================================================
        // TEST 7: Find Path KLCC → Plaza Rakyat
        // ============================================================
        System.out.println("TEST: Find Path from KLCC to Plaza Rakyat");
        System.out.println("───────────────────────────────────────────────────────────");
        List<String> path = network.findPath("KLCC", "Plaza Rakyat");
        System.out.println("Path from KLCC to Plaza Rakyat:");
        for (int i = 0; i < path.size(); i++) {
            String marker = path.get(i).equals("Masjid Jamek") ? " ★ INTERCHANGE" : "";
            System.out.println("  " + (i+1) + ". " + path.get(i) + marker);
        }
        System.out.println("  Total stops: " + (path.size() - 1));
        System.out.println();
        
        // ============================================================
        // TEST 8: Find Path PWTC → Pasar Seni
        // ============================================================
        System.out.println("TEST: Find Path from PWTC to Pasar Seni");
        System.out.println("───────────────────────────────────────────────────────────");
        List<String> path2 = network.findPath("PWTC", "Pasar Seni");
        System.out.println("Path from PWTC to Pasar Seni:");
        for (int i = 0; i < path2.size(); i++) {
            String marker = path2.get(i).equals("Masjid Jamek") ? " ★ INTERCHANGE" : "";
            System.out.println("  " + (i+1) + ". " + path2.get(i) + marker);
        }
        System.out.println("  Total stops: " + (path2.size() - 1));
        System.out.println();
        
        // ============================================================
        // TEST 9: Graph Statistics
        // ============================================================
        System.out.println("TEST: Graph Statistics");
        System.out.println("───────────────────────────────────────────────────────────");
        System.out.println(network.getStatistics());
        
        // ============================================================
        // TEST 10: Remove Edge
        // ============================================================
        System.out.println("TEST: Remove Edge between KLCC and Kampung Baru");
        System.out.println("───────────────────────────────────────────────────────────");
        network.removeEdge("KLCC", "Kampung Baru");
        System.out.println("  Edge removed!");
        System.out.println("  Neighbors of KLCC:");
        for (String neighbor : network.getStationNeighbors("KLCC")) {
            System.out.println("    → " + neighbor);
        }
        System.out.println();
        
        // ============================================================
        // TEST 11: Add Edge Back
        // ============================================================
        System.out.println("TEST: Add Edge back between KLCC and Kampung Baru");
        System.out.println("───────────────────────────────────────────────────────────");
        network.addEdge("KLCC", "Kampung Baru");
        System.out.println("  Edge added back!");
        System.out.println("  Neighbors of KLCC:");
        for (String neighbor : network.getStationNeighbors("KLCC")) {
            System.out.println("    → " + neighbor);
        }
        System.out.println();
        
        // ============================================================
        // TEST COMPLETE
        // ============================================================
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("           ✅ ALL TESTS COMPLETED!");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
    
    private static int countEdges(UnweightedGraph<LRTStation> graph) {
        int count = 0;
        for (int i = 0; i < graph.getSize(); i++) {
            count += graph.getDegree(i);
        }
        return count / 2;
    }
}
