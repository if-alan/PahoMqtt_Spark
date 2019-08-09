/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkripsiLita;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 *
 * @author if_alan
 */
public class SparkRestExample {

    public static Connection conn = null;
    public final static DataService dataService = new DataServiceMapImpl();

    public static void main(String[] args) {
        setDatabase();
        
        setPahoMqtt(dataService);

        dataRespond(dataService);
    }

    public static void setDatabase() {
        try {
            // db parameters
            String url = "jdbc:sqlite:/Users/if_alan/sqliteDB/IoTKwh.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setPahoMqtt(DataService dataService) {
        String topic = "pejaten/kwhmeter";
        String broker = "tcp://broker.mqtt-dashboard.com:1883";

        Random r = new Random();
        int i1 = r.nextInt(5000 - 1) + 1;
        String clientId = "mqtt" + i1;

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);

            System.out.println("Connected");
            sampleClient.subscribe(topic, 1);

            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("test");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    try {
                        System.out.println(message.toString());

                        JsonParser jsonParser = new JsonParser();
                        JsonObject obj = jsonParser.parse(message.toString()).getAsJsonObject();

                        String voltage = obj.get("voltage").toString();
                        String t_current = obj.get("current").toString();
                        String watt = obj.get("power").toString();
                        String kwh = obj.get("energy").toString();

                        insert(voltage, t_current, watt, kwh);
                    } catch (Throwable t) {
                        throw new Exception(t.getMessage());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public static void insert(String voltage, String current, String power, String energy) {
        String sql = "INSERT INTO data(voltage, current, power, energy) VALUES(?,?,?,?)";

        try ( PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, voltage);
            pstmt.setString(2, current);
            pstmt.setString(3, power);
            pstmt.setString(4, energy);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void dataRespond(DataService dataService) {
        post("/users", (request, response) -> {
            response.type("application/json");

            Data user = new Gson().fromJson(request.body(), Data.class);
            dataService.addService(user);

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
        });

        get("/users", (request, response) -> {
            response.type("application/json");
            
            selectAll();

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(dataService.getDataCollection())));
        });

        get("/users/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(dataService.getData(request.params(":id")))));
        });

        put("/users/:id", (request, response) -> {
            response.type("application/json");

            Data toEdit = new Gson().fromJson(request.body(), Data.class);
            Data editedUser = dataService.editData(toEdit);

            if (editedUser != null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(editedUser)));
            } else {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("User not found or error in edit")));
            }
        });

        delete("/users/:id", (request, response) -> {
            response.type("application/json");

            dataService.deleteData(request.params(":id"));
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "user deleted"));
        });

        options("/users/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, (dataService.dataExist(request.params(":id"))) ? "User exists" : "User does not exists"));
        });
    }

    public static void selectAll() {
        String sql = "SELECT voltage, current, power, energy FROM data";

        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {

                Data data = new Data(rs.getString("voltage"), rs.getString("current"), rs.getString("power"), rs.getString("energy"));
                dataService.addService(data);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
