import java.util.*;

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
    
    public boolean addVertex(String name, String line) {
        return addVertex((V) new LRTStation(name, line));
    }
    
    public boolean addEdge(String fromName, String toName) {
        int fromIndex = getStationIndex(fromName);
        int toIndex = getStationIndex(toName);
        if (fromIndex == -1 || toIndex == -1) return false;
        return addEdge(fromIndex, toIndex);
    }
    
    public boolean removeVertex(String name) {
        int index = getStationIndex(name);
        if (index == -1) return false;
        vertices.remove(index);
        neighbors.remove(index);
        for (int i = 0; i < neighbors.size(); i++) {
            List<Edge> edgesToRemove = new ArrayList<>();
            for (Edge e : neighbors.get(i)) {
                if (e.u == index || e.v == index) {
                    edgesToRemove.add(e);
                } else {
                    if (e.u > index) e.u--;
                    if (e.v > index) e.v--;
                }
            }
            neighbors.get(i).removeAll(edgesToRemove);
        }
        return true;
    }
    
    public boolean removeEdge(String fromName, String toName) {
        int fromIndex = getStationIndex(fromName);
        int toIndex = getStationIndex(toName);
        if (fromIndex == -1 || toIndex == -1) return false;
        boolean removed1 = neighbors.get(fromIndex).removeIf(e -> e.v == toIndex);
        boolean removed2 = neighbors.get(toIndex).removeIf(e -> e.v == fromIndex);
        return removed1 && removed2;
    }
    
    public int getStationIndex(String name) {
        for (int i = 0; i < vertices.size(); i++) {
            LRTStation station = (LRTStation) vertices.get(i);
            if (station.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
    
    public LRTStation getStation(String name) {
        int index = getStationIndex(name);
        if (index == -1) return null;
        return (LRTStation) vertices.get(index);
    }
    
    public List<LRTStation> getAllStations() {
        List<LRTStation> result = new ArrayList<>();
        for (V v : vertices) {
            result.add((LRTStation) v);
        }
        return result;
    }
    
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
    
    public List<String> findPath(String fromName, String toName) {
        int fromIndex = getStationIndex(fromName);
        int toIndex = getStationIndex(toName);
        if (fromIndex == -1 || toIndex == -1) return new ArrayList<>();
        Tree tree = bfs(fromIndex);
        List<V> path = tree.getPath(toIndex);
        List<String> result = new ArrayList<>();
        for (V v : path) {
            LRTStation station = (LRTStation) v;
            result.add(station.getName());
        }
        Collections.reverse(result);
        return result;
    }
    
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
    
    // Check if graph is connected
    public boolean isConnected() {
        if (getSize() == 0) return false;
        List<String> visited = bfsTraversal(((LRTStation) vertices.get(0)).getName());
        return visited.size() == getSize();
    }
    
    private int countEdges() {
        int count = 0;
        for (List<Edge> edgeList : neighbors) {
            count += edgeList.size();
        }
        return count / 2;
    }
}
