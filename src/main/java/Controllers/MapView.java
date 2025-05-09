package Controllers;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import javafx.application.Platform;
import netscape.javascript.JSObject;

public class MapView extends StackPane {
    private final WebView mapWebView;
    private final WebEngine webEngine;

    public MapView() {
        mapWebView = new WebView();
        mapWebView.setPrefSize(800, 600);
        getChildren().add(mapWebView);

        webEngine = mapWebView.getEngine();
        setupMap();
    }

    private void setupMap() {
        // Build the JS array of centers from the database
        StringBuilder centersJS = new StringBuilder();
        java.util.List<Entities.CentreDeDon> centres = new Services.CentreDeDonService().getAllCentres();
        for (Entities.CentreDeDon centre : centres) {
            centersJS.append(
                    "{ lat: " + centre.getLatitude() +
                            ", lng: " + centre.getLongitude() +
                            ", name: '" + centre.getName().replace("'", "\\'") + "' },\n"
            );
        }

        String mapHtml = """
                    <!DOCTYPE html>
                    <html lang=\"en\">
                    <head>
                        <meta charset=\"UTF-8\">
                        <title>Leaflet Map</title>
                        <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />
                        <style>
                            html, body { height: 100%; margin: 0; padding: 0; }
                            #map { width: 100vw; height: 100vh; }
                        </style>
                    </head>
                    <body>
                        <div id=\"map\"></div>
                        <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>
                        <script>
                            var map = L.map('map').setView([36.8189, 10.1657], 7);
                
                            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'
                            }).addTo(map);
                
                            var centers = [
                                __CENTERS__
                            ];
                
                            centers.forEach(function(center) {
                                L.marker([center.lat, center.lng]).addTo(map)
                                    .bindPopup(center.name);
                            });
                        </script>
                    </body>
                    </html>
                """.replace("__CENTERS__", centersJS.toString());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            // No JS-Java communication needed for this simple map
        });

        Platform.runLater(() -> {
            webEngine.loadContent(mapHtml);
        });

        // Refresh map size on WebView resize, but only if function exists
        mapWebView.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                try {
                    webEngine.executeScript("if (typeof refreshMapSize === 'function') refreshMapSize()");
                } catch (Exception ignored) {
                }
            });
        });
        mapWebView.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                try {
                    webEngine.executeScript("if (typeof refreshMapSize === 'function') refreshMapSize()");
                } catch (Exception ignored) {
                }
            });
        });
    }

    public class JavaConnector {
        public void updateCoordinates(double lat, double lng) {
            System.out.println("Coordinates updated: " + lat + ", " + lng);
        }
    }

}