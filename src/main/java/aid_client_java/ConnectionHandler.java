package aid_client_java;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler {
    private Socket clientSocket;
    private PrintWriter out;
    private DataInputStream in;
    public int boardint = 0;
    public ConnectionHandler(String ip, int port) throws IOException{

            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new DataInputStream(clientSocket.getInputStream());

        login();
    }

    public void login(){
        JSONObject logindata = new JSONObject();
        logindata.put("type", "initialize");
        logindata.put("password", ConfigData.getInstance().getPassword());
        if(ConfigData.getInstance().getName() != null) {
            logindata.put("name", ConfigData.getInstance().getName());
        }
        out.println(logindata.toString() + "\n");
        out.flush();
        JSONObject response = read();
        if(response.get("type").equals("initialize") && response.get("msg").equals("success!")){
            System.out.println("logged in.");
        }else{
            System.out.println(response.get("msg"));
        }

    }
    public void handleServerInput(){
        JSONObject response = read();
        if(!response.has("type")){
            return;
        }
        String type = response.getString("type");
        switch (type) {
            case "sendboard" -> {
                Simulator s = receiveBoard(response);
                if (s != null) {
                    sendResult(s.startSimulations());
                }
            }
            case "ping" -> {
                System.out.println("Wrote " + response.toString());
                out.write(response.toString() + "\n");
                out.flush();
            }
            default -> System.out.println("received type " + type);
        }
    }

    public Simulator receiveBoard(JSONObject response){


        if(response == null){
            System.out.println("response when reading was null.");
            return null;
        }
        if(!response.get("type").equals("sendboard")){
            System.out.println("no simulation.");
            return null;
        }
        this.boardint = response.getInt("boardint");
        Board board = new Board();
        JSONObject jsonboard = response.getJSONObject("board");
        int owner;
        for(String key: jsonboard.keySet()){
            owner = jsonboard.getInt(key);
            board.getBoardPiece(Integer.parseInt(key) % 8, Integer.parseInt(key) / 8).setOwner(owner);
        }
        return new Simulator(board, (Integer) response.get("turn"),
                response.getInt("thinkingtime") - ConfigData.getInstance().getReducedThinkTime());
    }

    public JSONObject read(){
        StringBuilder msg = new StringBuilder();
        try {
            msg.append((char)in.read());
            while(in.available() > 0){
                msg.append((char)in.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("received msg:" + msg);
        try {
            return new JSONObject(msg.toString());
        } catch (JSONException e) {
            System.out.println("received non-json data. " + msg);
            return null;
        }
    }

    public void sendResult(ArrayList<SimulationResult> simulationResults){
        sendResult(simulationResults, this.boardint);
    }

    public void sendResult(ArrayList<SimulationResult> simulationResults, int boardint){
        JSONObject resp = new JSONObject();
        JSONObject move;
        JSONObject results;
        resp.put("type", "reportResult");
        resp.put("boardint", boardint);
        JSONArray arr = new JSONArray();
        for(SimulationResult r: simulationResults){
            results = new JSONObject();
            results.put("value", String.valueOf(r.average));
            results.put("trials", r.amount);

            move = new JSONObject();
            move.put("x", r.move.getX());
            move.put("y", r.move.getY());
            results.put("move", move);
            arr.put(results);
        }
        resp.put("results", arr);

        out.write(resp.toString() + "\n");
        out.flush();
        System.out.println("sent to server: " + resp);

        // since result of simulation is now done and sent, we print some info about the simulation.
        BoardPiece bestMove = null;
        float highestAvg = Float.NEGATIVE_INFINITY;
        int simulatedMatches = 0;
        for(SimulationResult r: simulationResults){
            simulatedMatches += r.amount;
            if(r.average > highestAvg){
                highestAvg = r.average;
                bestMove = r.move;
            }
        }
        if(bestMove == null){
            System.out.println("error, bestmove was null");
            return;
        }
        System.out.println("Simulated " + simulatedMatches + " matches for board " + boardint + ". " +
                "Found best move at x: " + bestMove.x + ", y: " + bestMove.y + " with a value of " + highestAvg);
    }
}
