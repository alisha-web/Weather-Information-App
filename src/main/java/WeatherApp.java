import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp extends Application {

    private TextField locationField;
    private Label temperatureLabel, humidityLabel, windSpeedLabel, conditionLabel;
    private ImageView weatherIcon;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Information App");

        // Layout setup
        GridPane gridPane = new GridPane();
        locationField = new TextField();
        Button fetchButton = new Button("Get Weather");

        gridPane.add(new Label("Enter Location:"), 0, 0);
        gridPane.add(locationField, 1, 0);
        gridPane.add(fetchButton, 2, 0);

        // Display labels
        temperatureLabel = new Label();
        humidityLabel = new Label();
        windSpeedLabel = new Label();
        conditionLabel = new Label();
        weatherIcon = new ImageView();

        gridPane.add(new Label("Temperature:"), 0, 1);
        gridPane.add(temperatureLabel, 1, 1);
        gridPane.add(new Label("Humidity:"), 0, 2);
        gridPane.add(humidityLabel, 1, 2);
        gridPane.add(new Label("Wind Speed:"), 0, 3);
        gridPane.add(windSpeedLabel, 1, 3);
        gridPane.add(new Label("Condition:"), 0, 4);
        gridPane.add(conditionLabel, 1, 4);
        gridPane.add(weatherIcon, 2, 4);

        fetchButton.setOnAction(e -> fetchWeatherData());

        Scene scene = new Scene(gridPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchWeatherData() {
        String location = locationField.getText();
        String apiKey = System.getenv("WEATHER_API_KEY"); // Store the API key in Codespaces environment variable
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            parseWeatherData(response.toString());

        } catch (Exception e) {
            showAlert("Error fetching data", "Could not retrieve weather data. Please check your location and try again.");
        }
    }

    private void parseWeatherData(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);

        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        int humidity = jsonObject.getJSONObject("main").getInt("humidity");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        String condition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

        temperatureLabel.setText(temperature + "°C");
        humidityLabel.setText(humidity + "%");
        windSpeedLabel.setText(windSpeed + " m/s");
        conditionLabel.setText(condition);

        String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
        updateWeatherIcon(iconCode);
    }

    private void updateWeatherIcon(String iconCode) {
        String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
        weatherIcon.setImage(new Image(iconUrl));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
