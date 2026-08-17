/**
 * JAVA FX - Graph Visualizer
 * Displays the LRT Network as a graph with circles and lines
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class GraphVisualizer extends Application {
    
    private static UnweightedGraph<LRTStation> network;
    private static String title = "LRT Network";
    
    // Station positions (circular layout)
    private static final Map<String, double[]> positions = new HashMap<>();
    
    public static void displayGraph(UnweightedGraph<LRTStation> graph, String windowTitle) {
        network = graph;
        title = windowTitle;
        launch();
    }
    
    @Override
    public void start(Stage stage) {
        if (network == null || network.getSize() == 0) {
            System.out.println("Network is empty. Nothing to display.");
            return;
        }
        
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #f0f4f8;");
        
        // Calculate positions for stations
        calculatePositions();
        
        // Draw edges first (so they appear behind nodes)
        drawEdges(pane);
        
        // Draw nodes on top
        drawNodes(pane);
        
        // Draw legend
        drawLegend(pane);
        
        Scene scene = new Scene(pane, 900, 700);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
    
    private void calculatePositions() {
        positions.clear();
        List<LRTStation> stations = network.getAllStations();
        
        // Group stations by line
        List<LRTStation> kelanaJaya = new ArrayList<>();
        List<LRTStation> ampang = new ArrayList<>();
        
        for (LRTStation s : stations) {
            if (s.isInterchange()) {
                // Masjid Jamek goes in center
            } else if (s.getLine().equals("Kelana Jaya")) {
                kelanaJaya.add(s);
            } else if (s.getLine().equals("Ampang")) {
                ampang.add(s);
            }
        }
        
        // Sort by name for consistent layout
        kelanaJaya.sort(Comparator.comparing(LRTStation::getName));
        ampang.sort(Comparator.comparing(LRTStation::getName));
        
        // Layout: Kelana Jaya line goes left-to-right at top
        int totalKelana = kelanaJaya.size();
        for (int i = 0; i < totalKelana; i++) {
            double x = 100 + (i * 150);
            double y = 150;
            positions.put(kelanaJaya.get(i).getName(), new double[]{x, y});
        }
        
        // Layout: Ampang line goes left-to-right at bottom
        int totalAmpang = ampang.size();
        for (int i = 0; i < totalAmpang; i++) {
            double x = 100 + (i * 150);
            double y = 400;
            positions.put(ampang.get(i).getName(), new double[]{x, y});
        }
        
        // Masjid Jamek in center (interchange)
        double centerX = 100 + ((totalKelana - 1) / 2.0) * 150;
        double centerY = 275;
        positions.put("Masjid Jamek", new double[]{centerX, centerY});
    }
    
    private void drawEdges(Pane pane) {
        Set<String> drawnEdges = new HashSet<>();
        
        for (LRTStation station : network.getAllStations()) {
            String fromName = station.getName();
            double[] fromPos = positions.get(fromName);
            if (fromPos == null) continue;
            
            for (String toName : network.getStationNeighbors(fromName)) {
                String key = fromName + ":" + toName;
                String reverseKey = toName + ":" + fromName;
                
                if (!drawnEdges.contains(reverseKey) && !fromName.equals(toName)) {
                    double[] toPos = positions.get(toName);
                    if (toPos == null) continue;
                    
                    Line line = new Line(fromPos[0], fromPos[1], toPos[0], toPos[1]);
                    line.setStroke(Color.DARKBLUE);
                    line.setStrokeWidth(3);
                    pane.getChildren().add(line);
                    drawnEdges.add(key);
                }
            }
        }
    }
    
    private void drawNodes(Pane pane) {
        for (LRTStation station : network.getAllStations()) {
            double[] pos = positions.get(station.getName());
            if (pos == null) continue;
            
            double x = pos[0];
            double y = pos[1];
            double radius = 25;
            
            // Circle
            Circle circle = new Circle(x, y, radius);
            
            // Color based on line
            if (station.isInterchange()) {
                circle.setFill(Color.GOLD);
                circle.setStroke(Color.DARKGOLDENROD);
                circle.setStrokeWidth(3);
            } else if (station.getLine().equals("Kelana Jaya")) {
                circle.setFill(Color.LIGHTBLUE);
                circle.setStroke(Color.DARKBLUE);
                circle.setStrokeWidth(2);
            } else if (station.getLine().equals("Ampang")) {
                circle.setFill(Color.LIGHTPINK);
                circle.setStroke(Color.DARKRED);
                circle.setStrokeWidth(2);
            } else {
                circle.setFill(Color.LIGHTGRAY);
                circle.setStroke(Color.DARKGRAY);
                circle.setStrokeWidth(2);
            }
            pane.getChildren().add(circle);
            
            // Label
            Text text = new Text(station.getName());
            text.setX(x - text.getText().length() * 3.5);
            text.setY(y + 5);
            text.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
            pane.getChildren().add(text);
        }
    }
    
    private void drawLegend(Pane pane) {
        double startX = 700;
        double startY = 50;
        
        // Kelana Jaya
        Circle c1 = new Circle(startX, startY, 10);
        c1.setFill(Color.LIGHTBLUE);
        c1.setStroke(Color.DARKBLUE);
        pane.getChildren().add(c1);
        Text t1 = new Text("Kelana Jaya Line");
        t1.setX(startX + 20);
        t1.setY(startY + 5);
        pane.getChildren().add(t1);
        
        // Ampang
        Circle c2 = new Circle(startX, startY + 35, 10);
        c2.setFill(Color.LIGHTPINK);
        c2.setStroke(Color.DARKRED);
        pane.getChildren().add(c2);
        Text t2 = new Text("Ampang Line");
        t2.setX(startX + 20);
        t2.setY(startY + 40);
        pane.getChildren().add(t2);
        
        // Interchange
        Circle c3 = new Circle(startX, startY + 70, 10);
        c3.setFill(Color.GOLD);
        c3.setStroke(Color.DARKGOLDENROD);
        pane.getChildren().add(c3);
        Text t3 = new Text("★ Interchange");
        t3.setX(startX + 20);
        t3.setY(startY + 75);
        pane.getChildren().add(t3);
        
        // Stats
        Text stats = new Text("Stations: " + network.getSize() + " | Connections: " + network.countEdges());
        stats.setX(startX);
        stats.setY(startY + 120);
        stats.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        pane.getChildren().add(stats);
    }
}
