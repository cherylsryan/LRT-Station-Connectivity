/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.assg;

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
    private static boolean isShowing = false;

    private static final Map<String, double[]> positions = new HashMap<>();

    private static final Color KELANA_JAYA_COLOR = Color.rgb(0, 105, 210);
    private static final Color AMPANG_COLOR = Color.rgb(220, 50, 120);
    private static final Color INTERCHANGE_COLOR = Color.rgb(255, 185, 0);
    private static final Color INTERCHANGE_DARK = Color.rgb(200, 140, 0);
    private static boolean javafxStarted = false;
    private static GraphVisualizer instance;

    public static void displayGraph(UnweightedGraph<LRTStation> graph, String windowTitle) {
        network = graph;
        title = windowTitle;

        if (!javafxStarted) {
            javafxStarted = true;
            // First time launch() actually starts the JavaFX runtime and will call start
            new Thread(() -> {
                Application.launch(GraphVisualizer.class);
            }).start();
            return;
        }

        // JavaFX runtime is already running (even if the window was closed/hidden).
        Platform.runLater(() -> {
            if (primaryStage != null && instance != null) {
                instance.refreshStage();
            }
        });
    }

    public void start(Stage stage) {
        instance = this;
        primaryStage = stage;

        // Prevent the JavaFX runtime from auto-shutting-down when the window is closed/hidden
        Platform.setImplicitExit(false);

        stage.setOnCloseRequest(e -> {
            isShowing = false;
            stage.hide();
        });

        refreshStage();
    }

    // Rebuilds the pane/scene from the current `network` and (re)shows the stage.
    // Called both on first launch and on every later "Display Graph" click.
    private void refreshStage() {
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
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);

        primaryStage.show();
        primaryStage.toFront();
        isShowing = true;
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

        // stations
        String[] kelanaJayaOrder = {"KLCC", "Kampung Baru", "Dang Wangi", "Masjid Jamek", "Pasar Seni"};
        String[] ampangOrder = {"PWTC", "Sultan Ismail", "Bandaraya", "Masjid Jamek", "Plaza Rakyat"};

        double startX = 120;
        double spacing = 200;
        double topY = 280;
        double bottomY = 560;
        double centerX = startX + 2 * spacing;

        // Place predefined Kelana Jaya stations (top)
        for (int i = 0; i < kelanaJayaOrder.length; i++) {
            String name = kelanaJayaOrder[i];
            if (stationExists(name) && !name.equals("Masjid Jamek")) {
                double x = startX + (i * spacing);
                positions.put(name, new double[]{x, topY});
            }
        }

        // Place predefined Ampang stations (bottom)
        for (int i = 0; i < ampangOrder.length; i++) {
            String name = ampangOrder[i];
            if (stationExists(name) && !name.equals("Masjid Jamek")) {
                double x = startX + (i * spacing);
                positions.put(name, new double[]{x, bottomY});
            }
        }

        // Masjid Jamek (center)
        if (stationExists("Masjid Jamek")) {
            positions.put("Masjid Jamek", new double[]{centerX, 420});
        }

        // Pasar Seni
        if (stationExists("Pasar Seni")) {
            positions.put("Pasar Seni", new double[]{centerX + 150, topY});
        }

        // Plaza Rakyat
        if (stationExists("Plaza Rakyat")) {
            positions.put("Plaza Rakyat", new double[]{centerX + 150, bottomY});
        }

        // new stations place based on connections
        Set<String> predefinedNames = new HashSet<>();
        predefinedNames.addAll(Arrays.asList(kelanaJayaOrder));
        predefinedNames.addAll(Arrays.asList(ampangOrder));

        // Find new stations
        List<LRTStation> newStations = new ArrayList<>();
        for (LRTStation station : stations) {
            if (!predefinedNames.contains(station.getName())) {
                newStations.add(station);
            }
        }

        // Track how many new stations per line to avoid overlap
        int kelanaCount = 0;
        int ampangCount = 0;

        for (LRTStation station : newStations) {
            String name = station.getName();
            String line = station.getLine();

            List<String> neighbors = network.getStationNeighbors(name);

            if (line.equals("Kelana Jaya")) {
                double x = 0;
                double y = topY - 130;  

                // Find the first connected KJ station
                for (String neighbor : neighbors) {
                    if (positions.containsKey(neighbor)) {
                        LRTStation neighborStation = network.getStation(neighbor);
                        if (neighborStation != null && neighborStation.getLine().equals("Kelana Jaya")) {
                            double[] neighborPos = positions.get(neighbor);
                            if (neighborPos != null) {
                                x = neighborPos[0];
                                break;
                            }
                        }
                    }
                }

                // If no Kelana Jaya neighbor found, place at the end
                if (x == 0) {
                    x = 900 + (kelanaCount * 160);
                }

                // If multiple stations connect to same place, offset them
                x += (kelanaCount * 25);
                kelanaCount++;

                positions.put(name, new double[]{x, y});

            } else if (line.equals("Ampang")) {
                double x = 0;
                double y = bottomY + 130; 

                // Find the first connected Ampang station
                for (String neighbor : neighbors) {
                    if (positions.containsKey(neighbor)) {
                        LRTStation neighborStation = network.getStation(neighbor);
                        if (neighborStation != null && neighborStation.getLine().equals("Ampang")) {
                            double[] neighborPos = positions.get(neighbor);
                            if (neighborPos != null) {
                                x = neighborPos[0];
                                break;
                            }
                        }
                    }
                }

                // If no Ampang neighbor found, place at the end
                if (x == 0) {
                    x = 900 + (ampangCount * 160);
                }

                // If multiple stations connect to same place, offset them
                x += (ampangCount * 25);
                ampangCount++;

                positions.put(name, new double[]{x, y});

            } else {
                // Other stations - place in separate area
                positions.put(name, new double[]{100 + (newStations.indexOf(station) * 160), 700});
            }
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

                    if (station.getLine().equals("Kelana Jaya") && toStation != null && toStation.getLine().equals("Kelana Jaya")) {
                        lineColor = KELANA_JAYA_COLOR;
                        strokeWidth = 6;
                    } else if (station.getLine().equals("Ampang") && toStation != null && toStation.getLine().equals("Ampang")) {
                        lineColor = AMPANG_COLOR;
                        strokeWidth = 6;
                    } else if (station.getName().equals("Masjid Jamek") || (toStation != null && toStation.getName().equals("Masjid Jamek"))) {
                        lineColor = INTERCHANGE_COLOR;
                        strokeWidth = 6;
                    } else {
                        lineColor = Color.rgb(180, 190, 200);
                        strokeWidth = 3;
                    }

                    Line shadowLine = new Line(fromPos[0] + 2, fromPos[1] + 2, toPos[0] + 2, toPos[1] + 2);
                    shadowLine.setStroke(Color.rgb(0, 0, 0, 0.1));
                    shadowLine.setStrokeWidth(strokeWidth + 3);
                    pane.getChildren().add(shadowLine);

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

            Circle shadowCircle = new Circle(x + 2, y + 3, radius);
            shadowCircle.setFill(Color.rgb(0, 0, 0, 0.12));
            pane.getChildren().add(shadowCircle);

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

            String stationCode = getStationCode(station.getName());
            Text codeText = new Text(stationCode);
            codeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
            codeText.setFill(Color.WHITE);
            double textWidth = codeText.getLayoutBounds().getWidth();
            codeText.setX(x - textWidth / 2);
            codeText.setY(y + 6);
            pane.getChildren().add(codeText);

            Text nameText = new Text(station.getName());
            nameText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 17));
            nameText.setFill(Color.rgb(40, 50, 70));
            double nameWidth = nameText.getLayoutBounds().getWidth();

            boolean isKelanaJaya = station.getLine().equals("Kelana Jaya") && !station.isInterchange();
            boolean isAmpang = station.getLine().equals("Ampang") && !station.isInterchange();
            boolean isKelanaNew = isKelanaJaya && y < 150;      
            boolean isAmpangNew = isAmpang && y > 500;         

            if (isKelanaNew) {
                // New Kelana stations label above the circle
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y - radius - 10);
            } 
            else if (isAmpangNew) {
                // New Ampang stations label below the circle
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y + radius + 35);
            } 
            else if (isKelanaJaya && !isKelanaNew) {
                // Existing Kelana label above
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y - radius - 10);
            } 
            else if (isAmpang && !isAmpangNew) {
                // Existing Ampang label below
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y + radius + 35);
            } 
            else if (station.isInterchange()) {
                // Interchange label on the side
                nameText.setX(x + radius + 15);
                nameText.setY(y + 6);
            } 
            else {
                nameText.setX(x - nameWidth / 2);
                nameText.setY(y + radius + 35);
            }

            pane.getChildren().add(nameText);
        }
    }

    private void drawInfoPanel(Pane pane) {
        double startX = 1100;
        double startY = 300;

        javafx.scene.shape.Rectangle infoBg = new javafx.scene.shape.Rectangle(startX - 20, startY - 20, 260, 260);
        infoBg.setFill(Color.rgb(255, 255, 255, 0.92));
        infoBg.setStroke(Color.rgb(220, 225, 235));
        infoBg.setArcWidth(12);
        infoBg.setArcHeight(12);
        infoBg.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.08)));
        pane.getChildren().add(infoBg);

        double yOffset = startY + 20;

        Text infoTitle = new Text("Network Info");
        infoTitle.setX(startX);
        infoTitle.setY(yOffset + 10);
        infoTitle.setFill(Color.rgb(30, 40, 80));
        infoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        pane.getChildren().add(infoTitle);

        yOffset += 45;

        Text stationsText = new Text("Stations: " + network.getSize());
        stationsText.setX(startX);
        stationsText.setY(yOffset + 5);
        stationsText.setFill(Color.rgb(50, 60, 80));
        stationsText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18));
        pane.getChildren().add(stationsText);

        yOffset += 35;

        Text connectionsText = new Text("Connections: " + network.countEdges());
        connectionsText.setX(startX);
        connectionsText.setY(yOffset + 5);
        connectionsText.setFill(Color.rgb(50, 60, 80));
        connectionsText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18));
        pane.getChildren().add(connectionsText);

        yOffset += 40;

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