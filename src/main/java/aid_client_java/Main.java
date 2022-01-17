package aid_client_java;

import java.io.IOException;

public class Main {
    ConnectionHandler connectionHandler;
    public Main(){
        while(true) {
            try {
                this.connectionHandler = new ConnectionHandler(ConfigData.getInstance().getServerIP(),
                        ConfigData.getInstance().getPort());
                break;
            }catch (IOException ignored){
                System.out.println("failed to connect to client.");
            }
        }
        while(true){
            connectionHandler.handleServerInput();
        }
    }

    public static void main(String[] args) {
        new Main();
    }

}
