package aid_client_java;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ConfigData {
    private static ConfigData instance;
    private String serverIP;
    private int port;
    private int reducedThinkTime;
    private String password;


    private String name;

    public static ConfigData getInstance(){
        if (instance == null) {
            instance = new ConfigData();
        }
        return instance;
    }

    private ConfigData(){
        JSONObject config = readConfig("config.json");

        this.serverIP = "127.0.0.1";
        this.port = 5000;
        this.reducedThinkTime = 200;
        this.name = "Kevin";
        this.password = "HelloWorld";
        if(config == null){
            return;
        }
        if(config.has("serverip")) {
            this.serverIP = config.getString("serverip");
        }
        if(config.has("port")){
            this.port = config.getInt("port");
        }
        if(config.has("reducedthinktime")){
            this.reducedThinkTime = config.getInt("reducedthinktime");
        }
        if(config.has("name")){
            this.name = config.getString("name");
        }
        if(config.has("password")){
            this.password = config.getString("password");
        }
    }

    private JSONObject readConfig(String path){
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(path);
        try {
            Scanner reader = new Scanner(file);
            while(reader.hasNext()){
                stringBuilder.append(reader.next());
            }
            return new JSONObject(stringBuilder.toString());
        } catch (FileNotFoundException | JSONException e) {
            return null;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the reduced thinktime for a simulation.
     */
    public int getReducedThinkTime() {
        return reducedThinkTime;
    }

    public void setReducedThinkTime(int reducedThinkTime) {
        this.reducedThinkTime = reducedThinkTime;
    }
}

