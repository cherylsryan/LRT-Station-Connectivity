import java.util.List;

public class UnweightedGraph<V> extends AbstractGraph<V> {
    
    public UnweightedGraph() {
    }
    
    public UnweightedGraph(V[] vertices, int[][] edges) {
        super(vertices, edges);
    }
    
    public UnweightedGraph(List<V> vertices, List<AbstractGraph.Edge> edges) {
        super(vertices, edges);
    }
    
    public UnweightedGraph(List<AbstractGraph.Edge> edges, int numberOfVertices) {
        super(edges, numberOfVertices);
    }
    
    public UnweightedGraph(int[][] edges, int numberOfVertices) {
        super(edges, numberOfVertices);
    }
    
    // ============ CUSTOM METHODS FOR LRT ============
    
    /**
     * Add a vertex with a specific name and line
     */
    public boolean addVertex(String name, String line) {
        return addVertex((V) new LRTStation(name, line));
    }
    
    /**
     * Add an edge between two station names
     */
    public boolean addEdge(String fromName, String toName) {
        int fromIndex = getStationIndex(fromName);
        int toIndex = getStationIndex(toName);
        
        if (fromIndex == -1 || toIndex == -1) {
            return false;
        }
        
        // Add bidirectional edge (undirected graph)
        return addEdge(fromIndex, toIndex);
    }
    
    /**
     * Remove a vertex by name
     */
    public boolean removeVertex(String name) {
        int index = getStationIndex(name);
        if (index == -1) return false;
        
        // Remove vertex
        vertices.remove(index);
        neighbors.remove(index);
        
        // Update all edge references
        for (int i = 0; i < neighbors.size(); i++) {
            List<Edge> edgesToRemove = new ArrayList<>();
            for (Edge e : neighbors.get(i)) {
                if (e.u == index || e.v == index) {
                    edgesToRemove.add(e);
                } else {
                    // Update indices
                    if (e.u > index) e.u--;
                    if (e.v > index) e.v--;
                }
            }
            neighbors.get(i).removeAll(edgesToRemove);
        }
        return true;
    }
    
    /**
     * Remove an edge between two station names
     */
    public boolean removeEdge(String fromName, String toName) {
        int fromIndex = getStationIndex(fromName);
        int toIndex = getStationIndex(toName);
        
        if (fromIndex == -1 || toIndex == -1) {
            return false;
        }
        
        // Remove bidirectional edge
        boolean removed1 = neighbors.get(fromIndex).removeIf(e -> e.v == toIndex);
        boolean removed2 = neighbors.get(toIndex).removeIf(e -> e.v == fromIndex);
        
        return removed1 && removed2;
    }
    
    /**
     * Get station index by name
     */
    public int getStationIndex(String name) {
        for (int i = 0; i < vertices.size(); i++) {
            LRTStation station = (LRTStation) vertices.get(i);
            if (station.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Get station by name
     */
    public LRTStation getStation(String name) {
        int index = getStationIndex(name);
        if (index == -1) return null;
        return (LRTStation) vertices.get(index);
    }
    
    /**
     * Get all stations as a list
     */
    public List<LRTStation> getAllStations() {
        List<LRTStation> result = new ArrayList<>();
        for (V v : vertices) {
            result.add((LRTStation) v);
        }
        return result;
    }
    
    /**
     * Get neighbors of a station by name
     */
    public List<String> getStationNeighbors(String name) {
        int index = getStationIndex(name);
        if (index == -1) return new ArrayList<>();
        
        List<String> result = new ArrayList<>();
        for (int neighbor : getNeighbors(index)) {
            LRTStation station = (LRTStation) vertices.get(neighbor);
            result.add(station.getName());
        }
        return result;
    }
    
    /**
     * Get BFS traversal result with station names
     */
    public List<String> bfsTraversal(String startName) {
        int startIndex = getStationIndex(startName);
        if (startIndex == -1) return new ArrayList<>();
        
        Tree tree = bfs(startIndex);
        List<String> result = new ArrayList<>();
        for (int idx : tree.getSearchOrder()) {
            LRTStation station = (LRTStation) vertices.get(idx);
            result.add(station.getName());
        }
        return result;
    }
    
    /**
     * Get DFS traversal result with station names
     */
    public List<String> dfsTraversal(String startName) {
        int startIndex = getStationIndex(startName);
        if (startIndex == -1) return new ArrayList<>();
        
        Tree tree = dfs(startIndex);
        List<String> result = new ArrayList<>();
        for (int idx : tree.getSearchOrder()) {
            LRTStation station = (LRTStation) vertices.get(idx);
            result.add(station.getName());
        }
        return result;
    }
    
    /**
     * Find path between two stations
     */
    public List<String> findPath(String fromName, String toName) {
        int fromIndex = getStationIndex(fromName);
        int toIndex = getStationIndex(toName);
        
        if (fromIndex == -1 || toIndex == -1) {
            return new ArrayList<>();
        }
        
        Tree tree = bfs(fromIndex);
        List<V> path = tree.getPath(toIndex);
        List<String> result = new ArrayList<>();
        for (V v : path) {
            LRTStation station = (LRTStation) v;
            result.add(station.getName());
        }
        // Reverse to get from -> to
        Collections.reverse(result);
        return result;
    }
    
    /**
     * Get graph statistics as string
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("              GRAPH STATISTICS\n");
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("Total Stations (V): ").append(getSize()).append("\n");
        sb.append("Total Connections (E): ").append(countEdges()).append("\n");
        
        sb.append("\nStation Degrees:\n");
        for (V v : vertices) {
            LRTStation station = (LRTStation) v;
            int index = getIndex(v);
            int degree = getDegree(index);
            String marker = station.getName().equalsIgnoreCase("Masjid Jamek") ? " ★" : "";
            sb.append("  ").append(station.getName()).append(marker).append(": ").append(degree).append(" connections\n");
        }
        sb.append("═══════════════════════════════════════════════════════════\n");
        return sb.toString();
    }
    
    private int countEdges() {
        int count = 0;
        for (List<Edge> edgeList : neighbors) {
            count += edgeList.size();
        }
        return count / 2; // Undirected graph, each edge counted twice
    }
}
