/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package fi.helsinki.btls;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import fi.helsinki.btls.domain.LocationModel;
import fi.helsinki.btls.domain.Observer;
import fi.helsinki.btls.io.IMqttProvider;
import fi.helsinki.btls.io.UbiMqttProvider;
import fi.helsinki.btls.services.*;
import fi.helsinki.btls.utils.PropertiesHandler;

public class App {
    public static void main(String[] args) {
        IMqttProvider provider = new UbiMqttProvider();
        IMqttService mqttService = new MqttService(provider, new Gson());

        int positionsDimension = 2;
        IObserverService observerService = new ObserverService(positionsDimension);
        Map<String, String> allProperties = new PropertiesHandler("config/rasps.properties").getAllProperties();
        List<Observer> all = new ArrayList<>();

        allProperties.forEach((key, value) -> {
            String[] rasp = value.split(":");
            double[] temp = new double[positionsDimension];

            for (int i = 0; i < positionsDimension; i++) {
                temp[i] = Double.parseDouble(rasp[i]);
            }

            Observer obs = new Observer(key);
            obs.setPosition(temp);
            all.add(obs);
        });

        if (!observerService.addAllObservers(all)) {
            return;
        }

        ILocationService service = new LocationService2D(observerService);

        while (true) {
            try {
                Thread.sleep(1000);

                List<LocationModel> locations = service.calculateAllLocations(mqttService.getBeacons());

                for (LocationModel location : locations) {
                    mqttService.publish(location);
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }
}
