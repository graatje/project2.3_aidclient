package aid_client_java;

public class Main {
    ConnectionHandler connectionHandler;
    public Main(){
        this.connectionHandler = new ConnectionHandler("127.0.0.1", 5000);
        while(true){
            Simulator s = connectionHandler.receiveBoard();
            connectionHandler.sendResult(s.startSimulations());
        }
    }

    public static void main(String[] args) {
        new Main();
    }

}
