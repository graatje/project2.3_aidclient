package aid_client_java;

import static java.lang.System.currentTimeMillis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Simulator {
    private static final float[][] PIECE_WEIGHTS = {  // do we even want this? to be continued.
            { 4, -3,  2,  2,  2,  2, -3,  4},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            { 2, -1,  1,  0,  0,  1, -1,  2},
            { 2, -1,  0,  1,  1,  0, -1,  2},
            { 2, -1,  0,  1,  1,  0, -1,  2},
            { 2, -1,  1,  0,  0,  1, -1,  2},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            { 4, -3,  2,  2,  2,  2, -3,  4}
    };
    Board board;
    int startingPlayer;
    final HashMap<BoardPiece, ArrayList<Float>> vals;
    private final static int CORES = 4;
    private int thinktime = 8000;

    public Simulator(Board board, int startingPlayer){
        this.board = board;
        this.startingPlayer = startingPlayer;
        vals = new HashMap<BoardPiece, ArrayList<Float>>();
        synchronized (vals) {
            for (BoardPiece piece : board.getValidMoves(startingPlayer)) {
                vals.put(piece, new ArrayList<>());
            }
        }
    }

    public Simulator(Board board, int startingPlayer, int thinktime){
        this(board, startingPlayer);
        System.out.println("time to think about move: " + thinktime);
        this.thinktime = thinktime;
    }

    public ArrayList<SimulationResult> startSimulations(){

        for(int i = 0; i < CORES; i++){
            Thread t = new Thread(this::simulateGames);
            t.start();
        }
        ArrayList<SimulationResult> simulatedGames = new ArrayList<>();
        try {
            Thread.sleep(thinktime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float tempcount;
        synchronized (vals) {
            for (BoardPiece boardPiece : vals.keySet()) {
                tempcount = 0;
                for (float val : vals.get(boardPiece)) {
                    tempcount += val;
                }
                //System.out.println(tempcount + "/" + vals.get(boardPiece).size());
                simulatedGames.add(new SimulationResult(boardPiece, vals.get(boardPiece).size(), tempcount / vals.get(boardPiece).size()));
            }
        }
        return simulatedGames;
    }

    public void simulateGames(){
        long endtime = currentTimeMillis() + thinktime;
        while(currentTimeMillis() < endtime - 50) {
            try {
                simulate(board.clone());
            } catch (CloneNotSupportedException | NullPointerException e) {
                for(BoardPiece piece: vals.keySet()){
                    System.out.println(piece);
                }
                System.out.println("was null!! original board:");
                e.printStackTrace();
                return;
            }
        }
    }

    public void simulate(Board boardCopy){
        List<BoardPiece> validMoves = boardCopy.getValidMoves(startingPlayer);
        BoardPiece firstMove = getRandomMove(validMoves);
        boardCopy._executeMove(startingPlayer, firstMove);
        int currentplayer = getOtherPlayer(startingPlayer);
        while(!boardCopy.calculateIsGameOver()){
            validMoves = boardCopy.getValidMoves(currentplayer);
            if(!validMoves.isEmpty()) {

                BoardPiece move = getRandomMove(validMoves);
                boardCopy._executeMove(currentplayer, move);
            }
            currentplayer = getOtherPlayer(currentplayer);
        }
        synchronized (vals){
            float boardevaluation = evaluateBoard(boardCopy);

            vals.get(firstMove).add(boardevaluation);
        }
    }

    public float evaluateBoard(Board board){
        return evaluateBoardMethod3(board);
    }

    private float evaluateBoardMethod1(Board board){
        int selfPieces = 0, otherPieces = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int owner = board.getBoardPiece(x, y).getOwner();
                if (owner == startingPlayer) {  //always self
                    selfPieces += PIECE_WEIGHTS[x][y];
                } else if (owner == getOtherPlayer(startingPlayer)) {
                    otherPieces += PIECE_WEIGHTS[x][y];
                }
            }
        }
        float value = 0;
        if (selfPieces + otherPieces != 0) {
            value = (float) (selfPieces - otherPieces) / (selfPieces + otherPieces);
        }


        if (selfPieces + otherPieces != 0) {
            value += (float) (selfPieces - otherPieces) / (selfPieces + otherPieces);
        }
        return value;
    }

    private float evaluateBoardMethod2(Board board){
        float selfPieces = 0, otherPieces = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int owner = board.getBoardPiece(x, y).getOwner();
                if (owner == startingPlayer) {  //always self
                    selfPieces += PIECE_WEIGHTS[x][y];
                } else if (owner == getOtherPlayer(startingPlayer)) {
                    otherPieces += PIECE_WEIGHTS[x][y];
                }
            }
        }
        return selfPieces - otherPieces;
    }

    private float evaluateBoardMethod3(Board board){
        float selfPieces = 0, otherPieces = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int owner = board.getBoardPiece(x, y).getOwner();
                if (owner == startingPlayer) {  //always self
                    selfPieces += 1;
                } else if (owner == getOtherPlayer(startingPlayer)) {
                    otherPieces += 1;
                }
            }
        }
        return selfPieces - otherPieces;
    }

    private int getOtherPlayer(int player){
        if(player == 0){
            return 1;
        }
        return 0;
    }
    private BoardPiece getRandomMove(List<BoardPiece> moves){
        return moves.get((int) (Math.random() * moves.size()));
    }
}
