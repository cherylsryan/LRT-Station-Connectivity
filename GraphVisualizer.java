
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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;

import java.util.*;

public class GraphVisualizer extends Application {

    private static UnweightedGraph<LRTStation> network;
    private static String title = "LRT Network";
    private static Stage primaryStage;

    private static final Map<String, double[]> positions = new HashMap<>();

    private static final Color KELANA_JAYA_COLOR = Color.rgb(0, 105, 210);
    private static final Color KELANA_JAYA_LIGHT = Color.rgb(70, 160, 240);
    private static final Color AMPANG_COLOR = Color.rgb(220, 50, 120);
    private static final Color AMPANG_LIGHT = Color.rgb(240, 100, 160);
    private static final Color INTERCHANGE_COLOR = Color.rgb(255, 185, 0);
    private static final Color INTERCHANGE_DARK = Color.rgb(200, 140, 0);

    private static boolean isLaunched = false;

    public static void displayGraph(UnweightedGraph<LRTStation> graph, String windowTitle) {
    network = graph;
    title = windowTitle;
    
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
        pane.setStyle("-fx-background-color: #f0f4f8;");

        calculatePositions();
        drawBackground(pane);
        drawEdges(pane);
        drawNodes(pane);
        drawInfoPanel(pane);

        Scene scene = new Scene(pane, 1400, 800);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
        });
        stage.show();
    }

    private void drawBackground(Pane pane) {
        for (int x = 0; x < 1400; x += 50) {
            Line gridLine = new Line(x, 0, x, 800);
            gridLine.setStroke(Color.rgb(200, 210, 220, 0.2));
            gridLine.setStrokeWidth(0.5);
            pane.getChildren().add(gridLine);
        }
        for (int y = 0; y < 800; y += 50) {
            Line gridLine = new Line(0, y, 1400, y);
            gridLine.setStroke(Color.rgb(200, 210, 220, 0.2));
            gridLine.setStrokeWidth(0.5);
            pane.getChildren().add(gridLine);
        }
    }

    private void calculatePositions() {
    positions.clear();
    List<LRTStation> stations = network.getAllStations();
    
    // Define the standard station orders
    String[] kelanaJayaOrder = {"KLCC", "Kampung Baru", "Dang Wangi", "Masjid Jamek", "Pasar Seni"};
    String[] ampangOrder = {"PWTC", "Sultan Ismail", "Bandaraya", "Masjid Jamek", "Plaza Rakyat"};
    
    double startX = 150;
    double spacing = 200;
    double topY = 200;
    double bottomY = 480;
    
    // Place Kelana Jaya line stations
    for (int i = 0; i < kelanaJayaOrder.length; i++) {
        String name = kelanaJayaOrder[i];
        if (stationExists(name)) {
            double x = startX + (i * spacing);
            if (!name.equals("Masjid Jamek")) {
                positions.put(name, new double[]{x, topY});
            }
        }
    }
    
    // Place Ampang line stations
    for (int i = 0; i < ampangOrder.length; i++) {
        String name = ampangOrder[i];
        if (stationExists(name)) {
            double x = startX + (i * spacing);
            if (!name.equals("Masjid Jamek")) {
                positions.put(name, new double[]{x, bottomY});
            }
        }
    }
    
    // Place Masjid Jamek in the center
    double centerX = startX + 2 * spacing;
    if (stationExists("Masjid Jamek")) {
        positions.put("Masjid Jamek", new double[]{centerX, 340});
    }
    
    if (stationExists("Pasar Seni")) {
        positions.put("Pasar Seni", new double[]{centerX + 150, topY});
    }
    
    if (stationExists("Plaza Rakyat")) {
        positions.put("Plaza Rakyat", new double[]{centerX + 150, bottomY});
    }
    
    // Handle additional stations (new vertices)
    // First, find all new stations and check their connections
    List<LRTStation> newStations = new ArrayList<>();
    for (LRTStation station : stations) {
        if (!positions.containsKey(station.getName())) {
            newStations.add(station);
        }
    }
    
    // For each new station, find where it connects
    for (LRTStation station : newStations) {
        String name = station.getName();
        String line = station.getLine();
        double x, y;
        
        // Check if this station has any connections
        List<String> neighbors = network.getStationNeighbors(name);
        
        if (line.equals("Kelana Jaya")) {
            // Find the position of connected Kelana Jaya stations
            double avgX = 0;
            int count = 0;
            for (String neighbor : neighbors) {
                if (positions.containsKey(neighbor)) {
                    LRTStation neighborStation = network.getStation(neighbor);
                    if (neighborStation != null && neighborStation.getLine().equals("Kelana Jaya")) {
                        avgX += positions.get(neighbor)[0];
                        count++;
                    }
                }
            }
            if (count > 0) {
                avgX = avgX / count;
                // Place near the average position of connected stations
                x = avgX + spacing / 2;
            } else {
                // No connections to Kelana Jaya stations, place at the end
                x = centerX + 150 + spacing + (newStations.indexOf(station) * spacing);
            }
            y = topY;
            
        } else if (line.equals("Ampang")) {
            // Find the position of connected Ampang stations
            double avgX = 0;
            int count = 0;
            for (String neighbor : neighbors) {
                if (positions.containsKey(neighbor)) {
                    LRTStation neighborStation = network.getStation(neighbor);
                    if (neighborStation != null && neighborStation.getLine().equals("Ampang")) {
                        avgX += positions.get(neighbor)[0];
                        count++;
                    }
                }
            }
            if (count > 0) {
                avgX = avgX / count;
                // Place near the average position of connected stations
                x = avgX + spacing / 2;
            } else {
                // No connections to Ampang stations, place at the end
                x = centerX + 150 + spacing + (newStations.indexOf(station) * spacing);
            }
            y = bottomY;
            
        } else {
            // Other stations - place in a new row
            x = 100 + (newStations.indexOf(station) * 180);
            y = 650 + (newStations.indexOf(station) * 70);
        }
        
        positions.put(name, new double[]{x, y});
    }
}

    private boolean stationExists(String name) {
        return network.getStation(name) != null;
    }

    private void drawEdges(Pane pane) {
        Set<String> drawnEdges = new HashSet<>();

        for (LRTStation station : network.getAllStations()) {
            String fromName = station.getName();
            double[] fromPos = positions.get(fromName);
            if (fromPos == null) {
                continue;
            }

            for (String toName : network.getStationNeighbors(fromName)) {
                String key = fromName + ":" + toName;
                String reverseKey = toName + ":" + fromName;

                if (!drawnEdges.contains(reverseKey) && !fromName.equals(toName)) {
                    double[] toPos = positions.get(toName);
                    if (toPos == null) {
                        continue;
                    }

                    Color lineColor;
                    double strokeWidth;

                    LRTStation toStation = network.getStation(toName);

                    if (station.getLine().equals("Kelana Jaya") && toStation != null
                            && toStation.getLine().equals("Kelana Jaya")) {
                        lineColor = KELANA_JAYA_COLOR;
                        strokeWidth = 6;
                    } else if (station.getLine().equals("Ampang") && toStation != null
                            && toStation.getLine().equals("Ampang")) {
                        lineColor = AMPANG_COLOR;
                        strokeWidth = 6;
                    } else if (station.getName().equals("Masjid Jamek")
                            || (toStation != null && toStation.getName().equals("Masjid Jamek"))) {
                        lineColor = INTERCHANGE_COLOR;
                        strokeWidth = 6;
                    } else {
                        lineColor = Color.rgb(180, 190, 200);
                        strokeWidth = 3;
                    }

                    // Draw shadow line
                    Line shadowLine = new Line(fromPos[0] + 2, fromPos[1] + 2, toPos[0] + 2, toPos[1] + 2);
                    shadowLine.setStroke(Color.rgb(0, 0, 0, 0.1));
                    shadowLine.setStrokeWidth(strokeWidth + 3);
                    pane.getChildren().add(shadowLine);

                    // Main line
                    Line line = new Line(fromPos[0], fromPos[1], toPos[0], toPos[1]);
                    line.setStroke(lineColor);
                    line.setStrokeWidth(strokeWidth);

                    if (strokeWidth >= 4) {
                        Glow glow = new Glow();
                        glow.setLevel(0.2);
                        line.setEffect(glow);
                    }

                    pane.getChildren().add(line);
                    drawnEdges.add(key);
                }
            }
        }
    }

    private void drawNodes(Pane pane) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));

        for (LRTStation station : network.getAllStations()) {
            double[] pos = positions.get(station.getName());
            if (pos == null) {
                continue;
            }

            double x = pos[0];
            double y = pos[1];
            double radius = 40;

            // Shadow
            Circle shadowCircle = new Circle(x + 2, y + 3, radius);
            shadowCircle.setFill(Color.rgb(0, 0, 0, 0.12));
            pane.getChildren().add(shadowCircle);

            // Main circle
            Circle circle = new Circle(x, y, radius);

            if (station.isInterchange()) {
                circle.setFill(INTERCHANGE_COLOR);
                circle.setStroke(INTERCHANGE_DARK);
                circle.setStrokeWidth(4);

                Circle glowRing = new Circle(x, y, radius + 12);
                glowRing.setFill(null);
                glowRing.setStroke(Color.rgb(255, 215, 0, 0.2));
                glowRing.setStrokeWidth(4);
                pane.getChildren().add(glowRing);

            } else if (station.getLine().equals("Kelana Jaya")) {
                circle.setFill(KELANA_JAYA_COLOR);
                circle.setStroke(Color.rgb(0, 60, 150));
                circle.setStrokeWidth(3);

                Circle highlight = new Circle(x - 10, y - 12, 10);
                highlight.setFill(Color.rgb(255, 255, 255, 0.15));
                pane.getChildren().add(highlight);

            } else if (station.getLine().equals("Ampang")) {
                circle.setFill(AMPANG_COLOR);
                circle.setStroke(Color.rgb(170, 20, 80));
                circle.setStrokeWidth(3);

                Circle highlight = new Circle(x - 10, y - 12, 10);
                highlight.setFill(Color.rgb(255, 255, 255, 0.15));
                pane.getChildren().add(highlight);

            } else {
                circle.setFill(Color.rgb(150, 160, 170));
                circle.setStroke(Color.rgb(100, 110, 120));
                circle.setStrokeWidth(2);
            }

            circle.setEffect(shadow);
            pane.getChildren().add(circle);

            // Station code
            String stationCode = getStationCode(station.getName());
            Text codeText = new Text(stationCode);
            codeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
            codeText.setFill(Color.WHITE);
            double textWidth = codeText.getLayoutBounds().getWidth();
            codeText.setX(x - textWidth / 2);
            codeText.setY(y + 6);
            pane.getChildren().add(codeText);

            // Station name - position based on line
            Text nameText = new Text(station.getName());
            nameText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 17));
            nameText.setFill(Color.rgb(40, 50, 70));
            double nameWidth = nameText.getLayoutBounds().getWidth();

            // Check if this is a Kelana Jaya line station (top row)
            boolean isKelanaJaya = station.getLine().equals("Kelana Jaya") && !station.isInterchange();
            // Check if this is an Ampang line station (bottom row)
            boolean isAmpang = station.getLine().equals("Ampang") && !station.isInterchange();

            if (isKelanaJaya) {
                // Place name ABOVE the circle for Kelana Jaya stations
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y - radius - 10);
            } else if (isAmpang) {
                // Place name BELOW the circle for Ampang stations
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y + radius + 35);
            } else if (station.isInterchange()) {
                // Interchange station - name on the LEFT side
                nameText.setX(x + radius + 15);
                nameText.setY(y + 6);
            } else {
                // Default - below
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y + radius + 35);
            }

            pane.getChildren().add(nameText);

        }
    }

    private void drawInfoPanel(Pane pane) {
        double startX = 1150;
        double startY = 30;

        // Info panel background - BIGGER to fit all text
        javafx.scene.shape.Rectangle infoBg = new javafx.scene.shape.Rectangle(
                startX - 20, startY - 20, 260, 260
        );
        infoBg.setFill(Color.rgb(255, 255, 255, 0.92));
        infoBg.setStroke(Color.rgb(220, 225, 235));
        infoBg.setArcWidth(12);
        infoBg.setArcHeight(12);
        infoBg.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.08)));
        pane.getChildren().add(infoBg);

        double yOffset = startY + 20;

        // Title - BIGGER
        Text infoTitle = new Text("Network Info");
        infoTitle.setX(startX);
        infoTitle.setY(yOffset + 10);
        infoTitle.setFill(Color.rgb(30, 40, 80));
        infoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        pane.getChildren().add(infoTitle);

        yOffset += 45;

        // Stations count - BIGGER
        Text stationsText = new Text("Stations: " + network.getSize());
        stationsText.setX(startX);
        stationsText.setY(yOffset + 5);
        stationsText.setFill(Color.rgb(50, 60, 80));
        stationsText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18));
        pane.getChildren().add(stationsText);

        yOffset += 35;

        // Connections count - BIGGER
        Text connectionsText = new Text("Connections: " + network.countEdges());
        connectionsText.setX(startX);
        connectionsText.setY(yOffset + 5);
        connectionsText.setFill(Color.rgb(50, 60, 80));
        connectionsText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18));
        pane.getChildren().add(connectionsText);

        yOffset += 40;

        // Line info - Kelana Jaya - BIGGER
        Circle dot1 = new Circle(startX + 8, yOffset + 4, 8);
        dot1.setFill(KELANA_JAYA_COLOR);
        pane.getChildren().add(dot1);
        Text line1Text = new Text("Kelana Jaya Line");
        line1Text.setX(startX + 24);
        line1Text.setY(yOffset + 10);
        line1Text.setFill(Color.rgb(50, 60, 80));
        line1Text.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        pane.getChildren().add(line1Text);

        yOffset += 35;

        // Line info - Ampang - BIGGER
        Circle dot2 = new Circle(startX + 8, yOffset + 4, 8);
        dot2.setFill(AMPANG_COLOR);
        pane.getChildren().add(dot2);
        Text line2Text = new Text("Ampang Line");
        line2Text.setX(startX + 24);
        line2Text.setY(yOffset + 10);
        line2Text.setFill(Color.rgb(50, 60, 80));
        line2Text.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        pane.getChildren().add(line2Text);

        yOffset += 35;

        // Interchange - BIGGER
        Circle dot3 = new Circle(startX + 8, yOffset + 4, 8);
        dot3.setFill(INTERCHANGE_COLOR);
        dot3.setStroke(INTERCHANGE_DARK);
        dot3.setStrokeWidth(2);
        pane.getChildren().add(dot3);
        Text line3Text = new Text("⭐ Interchange");
        line3Text.setX(startX + 24);
        line3Text.setY(yOffset + 10);
        line3Text.setFill(Color.rgb(50, 60, 80));
        line3Text.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        pane.getChildren().add(line3Text);
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
                return "MJ";
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
                if (name.length() <= 3) {
                    return name.toUpperCase();
                }
                return name.substring(0, 3).toUpperCase();
        }
    }
}
