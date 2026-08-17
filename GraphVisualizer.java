/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.testasg;

/**
 *
 * @author renee
 */
/**
 * JAVA FX - Graph Visualizer
 * Displays the LRT Network as a graph with circles and lines
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class GraphVisualizer extends Application {
    
    private static UnweightedGraph<LRTStation> network;
    private static String title = "LRT Network";
    private static Stage primaryStage;
    
    // Station positions
    private static final Map<String, double[]> positions = new HashMap<>();
    
    public static void displayGraph(UnweightedGraph<LRTStation> graph, String windowTitle) {
        network = graph;
        title = windowTitle;
        
        // Launch JavaFX application
        new Thread(() -> {
            Application.launch(GraphVisualizer.class);
        }).start();
    }
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        
        if (network == null || network.getSize() == 0) {
            System.out.println("Network is empty. Nothing to display.");
            return;
        }
        
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #e8f0fe, #ffffff);");
        
        // Calculate positions for stations
        calculatePositions();
        
        // Draw edges first (so they appear behind nodes)
        drawEdges(pane);
        
        // Draw nodes on top
        drawNodes(pane);
        
        // Draw legend
        drawLegend(pane);
        
        Scene scene = new Scene(pane, 1400, 800);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
        });
        stage.show();
    }
    
    private void calculatePositions() {
        positions.clear();
        List<LRTStation> stations = network.getAllStations();
        
        // Define a specific layout for each station based on the network structure
        String[] kelanaJayaOrder = {"KLCC", "Kampung Baru", "Dang Wangi", "Masjid Jamek", "Pasar Seni"};
        String[] ampangOrder = {"PWTC", "Sultan Ismail", "Bandaraya", "Masjid Jamek", "Plaza Rakyat"};
        
        // Position Kelana Jaya Line (top row)
        double startX = 100;
        double spacing = 180;
        double yTop = 180;
        
        for (int i = 0; i < kelanaJayaOrder.length; i++) {
            String name = kelanaJayaOrder[i];
            if (stationExists(name)) {
                double x = startX + (i * spacing);
                if (!name.equals("Masjid Jamek")) {
                    positions.put(name, new double[]{x, yTop});
                }
            }
        }
        
        // Position Ampang Line (bottom row)
        double yBottom = 480;
        
        for (int i = 0; i < ampangOrder.length; i++) {
            String name = ampangOrder[i];
            if (stationExists(name)) {
                double x = startX + (i * spacing);
                if (!name.equals("Masjid Jamek")) {
                    positions.put(name, new double[]{x, yBottom});
                }
            }
        }
        
        // Place Masjid Jamek in the center
        double centerX = startX + 2 * spacing;
        if (stationExists("Masjid Jamek")) {
            positions.put("Masjid Jamek", new double[]{centerX, 320});
        }
        
        // Handle any additional stations
        int extraCount = 0;
        for (LRTStation station : stations) {
            String name = station.getName();
            if (!positions.containsKey(name)) {
                double x = startX + (kelanaJayaOrder.length + 1) * spacing + 80;
                double y = 180 + (extraCount * 90);
                positions.put(name, new double[]{x, y});
                extraCount++;
            }
        }
    }
    
    private boolean stationExists(String name) {
        return network.getStation(name) != null;
    }
    
    private void drawEdges(Pane pane) {
        Set<String> drawnEdges = new HashSet<>();
        
        // Draw a subtle background grid or pattern
        // (Optional - can be removed if not needed)
        
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
                    
                    // Enhanced edge styling with glow effect
                    String lineType = station.getLine();
                    if (lineType.equals("Kelana Jaya") && stationExists(toName) && 
                        network.getStation(toName).getLine().equals("Kelana Jaya")) {
                        line.setStroke(Color.rgb(30, 100, 200)); // Deep Blue
                        line.setStrokeWidth(4);
                    } else if (lineType.equals("Ampang") && stationExists(toName) && 
                               network.getStation(toName).getLine().equals("Ampang")) {
                        line.setStroke(Color.rgb(200, 30, 100)); // Deep Pink
                        line.setStrokeWidth(4);
                    } else if (station.getName().equals("Masjid Jamek") || 
                               (stationExists(toName) && network.getStation(toName).getName().equals("Masjid Jamek"))) {
                        // Interchange connections with gradient effect
                        line.setStroke(Color.rgb(255, 140, 0)); // Orange
                        line.setStrokeWidth(5);
                        // Add dashed effect for interchange connections
                        line.getStrokeDashArray().addAll(10d, 5d);
                    } else {
                        line.setStroke(Color.rgb(100, 100, 100));
                        line.setStrokeWidth(2);
                        line.getStrokeDashArray().addAll(8d, 4d);
                    }
                    
                    // Add shadow effect by drawing a slightly thicker line behind
                    Line shadow = new Line(fromPos[0], fromPos[1], toPos[0], toPos[1]);
                    shadow.setStroke(Color.rgb(0, 0, 0, 0.1));
                    shadow.setStrokeWidth(line.getStrokeWidth() + 4);
                    pane.getChildren().add(shadow);
                    
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
            double radius = 30;
            
            // Add shadow effect for station nodes
            Circle shadow = new Circle(x + 3, y + 3, radius);
            shadow.setFill(Color.rgb(0, 0, 0, 0.15));
            pane.getChildren().add(shadow);
            
            // Main circle
            Circle circle = new Circle(x, y, radius);
            
            // Enhanced color scheme with gradients
            if (station.isInterchange()) {
                // Interchange station - Gold with glow
                circle.setFill(Color.rgb(255, 215, 0));
                circle.setStroke(Color.rgb(184, 134, 11));
                circle.setStrokeWidth(4);
                
                // Add a glowing ring effect
                Circle glow = new Circle(x, y, radius + 8);
                glow.setFill(null);
                glow.setStroke(Color.rgb(255, 215, 0, 0.3));
                glow.setStrokeWidth(3);
                pane.getChildren().add(glow);
                
            } else if (station.getLine().equals("Kelana Jaya")) {
                circle.setFill(Color.rgb(70, 150, 220));
                circle.setStroke(Color.rgb(30, 80, 160));
                circle.setStrokeWidth(3);
            } else if (station.getLine().equals("Ampang")) {
                circle.setFill(Color.rgb(220, 70, 150));
                circle.setStroke(Color.rgb(160, 30, 100));
                circle.setStrokeWidth(3);
            } else {
                circle.setFill(Color.rgb(180, 180, 180));
                circle.setStroke(Color.rgb(120, 120, 120));
                circle.setStrokeWidth(2);
            }
            pane.getChildren().add(circle);
            
            // Station code label (abbreviation) - now with white text for better visibility
            String stationCode = getStationCode(station.getName());
            Text codeText = new Text(stationCode);
            codeText.setX(x - codeText.getLayoutBounds().getWidth() / 2);
            codeText.setY(y + 5);
            codeText.setFill(Color.WHITE);
            codeText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            codeText.setStyle("-fx-font-weight: bold;");
            pane.getChildren().add(codeText);
            
            // Full name label below the circle with better styling
            Text nameText = new Text(station.getName());
            nameText.setX(x - nameText.getLayoutBounds().getWidth() / 2);
            nameText.setY(y + radius + 25);
            nameText.setFill(Color.rgb(40, 40, 60));
            nameText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            pane.getChildren().add(nameText);
            
            // Add station line indicator (small colored dot)
            if (!station.isInterchange()) {
                Circle dot = new Circle(x + radius + 2, y - radius + 2, 5);
                if (station.getLine().equals("Kelana Jaya")) {
                    dot.setFill(Color.rgb(30, 80, 160));
                } else if (station.getLine().equals("Ampang")) {
                    dot.setFill(Color.rgb(160, 30, 100));
                }
                pane.getChildren().add(dot);
            }
        }
    }
    
    private String getStationCode(String name) {
        switch (name) {
            case "KLCC": 
                return "KLCC";
            case "Kampung Baru": 
                return "KB";
            case "Dang Wangi": 
                return "DW";
            case "Masjid Jamek": 
                return "M";
            case "Pasar Seni": 
                return "PS";
            case "PWTC": 
                return "PWTC";
            case "Sultan Ismail": 
                return "SI";
            case "Bandaraya": 
                return "B";
            case "Plaza Rakyat": 
                return "PR";
            default: 
                if (name.length() <= 3) return name.toUpperCase();
                return name.substring(0, 3).toUpperCase();
        }
    }
    
    private void drawLegend(Pane pane) {
        double startX = 1100;
        double startY = 70;
        
        // Legend panel background
        javafx.scene.shape.Rectangle legendBg = new javafx.scene.shape.Rectangle(
            startX - 20, startY - 20, 320, 320
        );
        legendBg.setFill(Color.rgb(255, 255, 255, 0.9));
        legendBg.setStroke(Color.rgb(200, 200, 200));
        legendBg.setArcWidth(15);
        legendBg.setArcHeight(15);
        pane.getChildren().add(legendBg);
        
        // Title with better styling
        Text titleText = new Text("🚇 LRT Station Line");
        titleText.setX(startX);
        titleText.setY(startY + 20);
        titleText.setFill(Color.rgb(30, 40, 80));
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        pane.getChildren().add(titleText);
        
        double yOffset = startY + 40;
        
        // Kelana Jaya
        Circle c1 = new Circle(startX + 10, yOffset, 12);
        c1.setFill(Color.rgb(70, 150, 220));
        c1.setStroke(Color.rgb(30, 80, 160));
        c1.setStrokeWidth(2);
        pane.getChildren().add(c1);
        Text t1 = new Text("Kelana Jaya Line");
        t1.setX(startX + 30);
        t1.setY(yOffset + 5);
        t1.setFill(Color.rgb(40, 40, 60));
        t1.setFont(Font.font("Arial", 13));
        pane.getChildren().add(t1);
        
        // Ampang
        yOffset += 35;
        Circle c2 = new Circle(startX + 10, yOffset, 12);
        c2.setFill(Color.rgb(220, 70, 150));
        c2.setStroke(Color.rgb(160, 30, 100));
        c2.setStrokeWidth(2);
        pane.getChildren().add(c2);
        Text t2 = new Text("Ampang Line");
        t2.setX(startX + 30);
        t2.setY(yOffset + 5);
        t2.setFill(Color.rgb(40, 40, 60));
        t2.setFont(Font.font("Arial", 13));
        pane.getChildren().add(t2);
        
        // Interchange
        yOffset += 35;
        Circle c3 = new Circle(startX + 10, yOffset, 12);
        c3.setFill(Color.rgb(255, 215, 0));
        c3.setStroke(Color.rgb(184, 134, 11));
        c3.setStrokeWidth(3);
        pane.getChildren().add(c3);
        Text t3 = new Text("⭐ Interchange Station");
        t3.setX(startX + 30);
        t3.setY(yOffset + 5);
        t3.setFill(Color.rgb(40, 40, 60));
        t3.setFont(Font.font("Arial", 13));
        pane.getChildren().add(t3);
        
        // Connection line for Kelana Jaya
        yOffset += 40;
        Line line1 = new Line(startX, yOffset, startX + 25, yOffset);
        line1.setStroke(Color.rgb(30, 80, 160));
        line1.setStrokeWidth(3);
        pane.getChildren().add(line1);
        Text t4 = new Text("Kelana Jaya Connection");
        t4.setX(startX + 35);
        t4.setY(yOffset + 5);
        t4.setFill(Color.rgb(40, 40, 60));
        t4.setFont(Font.font("Arial", 12));
        pane.getChildren().add(t4);
        
        // Connection line for Ampang
        yOffset += 30;
        Line line2 = new Line(startX, yOffset, startX + 25, yOffset);
        line2.setStroke(Color.rgb(160, 30, 100));
        line2.setStrokeWidth(3);
        pane.getChildren().add(line2);
        Text t5 = new Text("Ampang Connection");
        t5.setX(startX + 35);
        t5.setY(yOffset + 5);
        t5.setFill(Color.rgb(40, 40, 60));
        t5.setFont(Font.font("Arial", 12));
        pane.getChildren().add(t5);
        
        // Interchange Connection
        yOffset += 30;
        Line line3 = new Line(startX, yOffset, startX + 25, yOffset);
        line3.setStroke(Color.rgb(255, 140, 0));
        line3.setStrokeWidth(3);
        line3.getStrokeDashArray().addAll(8d, 4d);
        pane.getChildren().add(line3);
        Text t6 = new Text("Interchange Connection");
        t6.setX(startX + 35);
        t6.setY(yOffset + 5);
        t6.setFill(Color.rgb(40, 40, 60));
        t6.setFont(Font.font("Arial", 12));
        pane.getChildren().add(t6);
        
        // Stats
        yOffset += 50;
        Text stats = new Text(
            "📊 Stations: " + network.getSize() + 
            " | 🔗 Connections: " + network.countEdges()
        );
        stats.setX(startX);
        stats.setY(yOffset);
        stats.setFill(Color.rgb(30, 40, 80));
        stats.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        pane.getChildren().add(stats);
        
    }
}
