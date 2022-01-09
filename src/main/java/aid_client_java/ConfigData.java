package aid_client_java;

public class ConfigData {
    private static ConfigData instance;
    private String serverIP;
    private int port;
    private int reducedThinkTime;

    public static ConfigData getInstance(){
        if (instance == null) {
            instance = new ConfigData();
        }
        return instance;
    }

    private ConfigData(){
        this.serverIP = "127.0.0.1";
        this.port = 5000;
        this.reducedThinkTime = 200;
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

