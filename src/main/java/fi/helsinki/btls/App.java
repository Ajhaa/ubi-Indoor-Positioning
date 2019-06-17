/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package fi.helsinki.btls;

import com.google.gson.Gson;
import fi.helsinki.btls.domain.Beacon;
import fi.helsinki.btls.io.IMqttProvider;
import fi.helsinki.btls.io.UbiMqttProvider;
import fi.helsinki.btls.services.IMqttService;
import fi.helsinki.btls.services.LocationService;
import fi.helsinki.btls.services.MqttService;
import fi.helsinki.btls.utils.PropertiesHandler;

import java.util.List;

public class App {
    public static void main(String[] args) {
        PropertiesHandler handler = new PropertiesHandler("config/mqttConfig.properties");

        String subscribeTopic = handler.getProperty("subscribeTopic");
        String publishTopic = handler.getProperty("publishTopic");
        String mqttUrl = handler.getProperty("mqttUrl");

        IMqttService mqttService = new MqttService(mqttUrl,subscribeTopic,publishTopic);
        LocationService service = new LocationService(mqttService);
        while(true) {

            try {
                Thread.sleep(1000);
                List<Beacon> beacons = mqttService.getBeacons();

                for (int i = 0; i < beacons.size(); i++) {
                    mqttService.publish(service.calculateBeaconLocation2D(beacons.get(i)));
                }
                //service.calculateLocation2D();
                }
            catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }
}
