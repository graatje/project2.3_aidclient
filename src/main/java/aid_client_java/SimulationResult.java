package aid_client_java;

public class SimulationResult {
    public int amount;
    public float average;
    public BoardPiece move;
    public SimulationResult(BoardPiece move, int amount, float average){
        this.move = move;
        this.amount = amount;
        this.average = average;
    }
}
