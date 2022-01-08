package aid_client_java;

import java.io.IOException;

public class Main {
    ConnectionHandler connectionHandler;
    public Main(){
        while(true) {
            try {
                this.connectionHandler = new ConnectionHandler("127.0.0.1", 5000);
                break;
            }catch (IOException ignored){
                System.out.println("failed to connect to client.");
            }
        }
        while(true){
            Simulator s = connectionHandler.receiveBoard();
            if(s != null) {
                connectionHandler.sendResult(s.startSimulations());
            }
        }
    }

    public static void main(String[] args) {
        new Main();
    }

}
