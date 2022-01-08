package aid_client_java;

import aid_client_java.BoardPiece;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class stores an othelloboard filled with boardpieces. superclass is Board.
 * also has methods to make a move and check valid moves.
 */
public class Board implements Cloneable{

    protected BoardPiece[] pieces;
    public int width;
    public int height;
    public Board() {
        this.width = 8;
        this.height = 8;
        this.pieces = new BoardPiece[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pieces[x + y * width] = new BoardPiece(x, y);
            }
        }
    }
    public Board clone() throws CloneNotSupportedException {
        Board cloned = (Board) super.clone();

        // Deep-clone pieces
        cloned.pieces = new BoardPiece[this.pieces.length];
        for (int i = 0; i < this.pieces.length; i++) {
            cloned.pieces[i] = this.pieces[i].clone();
        }

        return cloned;
    }
    /**
     * get a list of valid moves.
     *
     * @return List<BoardPiece> , a list of valid moves.
     */
    public List<BoardPiece> getValidMoves(int asWho) {
        List<BoardPiece> validMoves = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BoardPiece boardPiece = getBoardPiece(x, y);
                if (!boardPiece.hasOwner()) {
                    if (checkValidMove(boardPiece, asWho)) {
                        validMoves.add(boardPiece);
                    }
                }
            }
        }
        return validMoves;
    }

    /**
     * check if the specified boardpiece is a valid move.
     *
     * @param boardPiece the boardpiece you want to check of if it is a valid move.
     * @return boolean if it is a valid move.
     */
    private boolean checkValidMove(BoardPiece boardPiece, int asWho) {
        // check
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                if (checkLine(boardPiece, x, y, asWho)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if you can capture a piece of the opponent.
     *
     * @param piece,  the boardpiece you want to check the line of.
     * @param xchange the horizontal direction the line goes in.
     * @param ychange the vertical direction the line goes in.
     * @return boolean, true if you can capture a piece of the opponent.
     */
    private boolean checkLine(BoardPiece piece, int xchange, int ychange, int asWho) {
        int x = piece.getX() + xchange;
        int y = piece.getY() + ychange;
        boolean initialized = false;  // if it can be a valid move.
        if (x >= 0 && y >= 0 && x < width && y < height) {  // out of bounds check
            BoardPiece boardPiece = getBoardPiece(x, y);

            // check if it is the opponent.
            if (boardPiece.hasOwner() && !(boardPiece.getOwner()==asWho)) {
                initialized = true;
            }
        }

        while (initialized && x + xchange >= 0 && y + ychange >= 0 && x + xchange < width && y + ychange < height) {  // out of bounds check
            x = x + xchange;
            y = y + ychange;
            BoardPiece boardPiece = getBoardPiece(x, y);
            if (boardPiece.getOwner() == asWho) {  // check if the tile is you.
                return true;
            } else if (!boardPiece.hasOwner()) {
                break;
            }
        }
        return false;
    }

    /**
     * a method for executing a move on the board.
     *
     * @param asWho The player which executed the move.
     * @param piece The piece the player wants to affect.
     */
    public void _executeMove(int asWho, BoardPiece piece) {
        if (!checkValidMove(piece, asWho)) {
            System.out.println("no valid move.");
            return;
        }
        // change a tile in all directions
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                changeMoveLine(piece, x, y, asWho);
            }
        }
        piece.setOwner(asWho);
    }

    /**
     * this method captures pieces of the opponent starting from the provided piece in the direction specified.
     *
     * @param piece the piece you want to place on the board.
     * @param xchange the horizontal direction the line goes in.
     * @param ychange the vertical direction the line goes in.
     */
    private void changeMoveLine(BoardPiece piece, int xchange, int ychange, int asWho) {

        // temporary arraylist of captured opponents.
        ArrayList<BoardPiece> templist = new ArrayList<>();
        int x = piece.getX() + xchange;
        int y = piece.getY() + ychange;
        boolean initialized = false;
        if (x >= 0 && y >= 0 && x < width && y < height) {  // out of bounds check
            BoardPiece boardPiece = getBoardPiece(x, y);
            if (boardPiece.hasOwner() && !(boardPiece.getOwner() == asWho)) {
                initialized = true;
            }
        }
        // add the current boardPiece to captured opponents
        if (initialized) {
            templist.add(getBoardPiece(x, y));
        }
        boolean brokeOut = false;
        while (initialized && x + xchange >= 0 && y + ychange >= 0 && x + xchange < width && y + ychange < height) {
            x = x + xchange;
            y = y + ychange;
            BoardPiece boardPiece = getBoardPiece(x, y);
            if (!boardPiece.hasOwner()) {
                return;
            } else if (boardPiece.getOwner() == asWho) {
                brokeOut = true;
                break;
            } else {  // opponent
                templist.add(getBoardPiece(x, y));
            }
        }
        if (brokeOut) {
            for (BoardPiece boardPiece : templist) {
                boardPiece.setOwner(asWho);
            }
        }
    }

    /**
     * a check for if the game is over.
     *
     * @return boolean true if game is over, false if not.
     */
    public boolean calculateIsGameOver() {
        int a = 0;
        int b = 1;

        return getValidMoves(a).isEmpty() &&
                getValidMoves(b).isEmpty();
    }

    /**
     * check what player has won the game. null if draw.
     *
     * @return Player the player who won, or null if draw.
     */
    public int calculateWinner() {
        if (calculateIsGameOver()) {
            int player1count = 0;
            int player2count = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    BoardPiece tempBoardPiece = getBoardPiece(x, y);
                    if (!tempBoardPiece.hasOwner()) {
                        continue;
                    }
                    if (tempBoardPiece.getOwner() == 0) {
                        player1count++;
                    } else {
                        player2count++;
                    }
                }
            }
            if (player1count > player2count) {  // player 1 won
                return 0;
            } else if (player2count > player1count) {  // player 2 won
                return 1;
            }
        }
        return -1;
       // return null;
    }

    public BoardPiece getBoardPiece(int x, int y){
        return pieces[x + y * width];
    }

    public void printBoard(){
        for(int y=0; y<8;y++){
            for(int x=0; x<8;x++){
                String owner = String.valueOf(getBoardPiece(x, y).getOwner());
                if(owner.equals("-1")){
                    owner = "X";
                }
                System.out.print(owner + " ");
            }
            System.out.println();
        }
    }
}
