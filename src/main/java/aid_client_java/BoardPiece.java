package aid_client_java;

import static java.util.Objects.hash;

public class BoardPiece{
    public int x;
    public int y;
    public int owner;
    public BoardPiece(int x, int y, int owner){
        this.x = x;
        this.y = y;
        this.owner = owner;
    }

    public BoardPiece clone(){
        return new BoardPiece(x, y, owner);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
    public BoardPiece(int x, int y){
        this(x, y, -1);
    }

    public boolean hasOwner(){
        return this.owner != -1;
    }

    public int getOwner(){
        return owner;
    }

    public void setOwner(int owner){
        this.owner = owner;
    }

    @Override
    public String toString(){
        return "x: " + x + ", y:" + y;
    }

    @Override
    public int hashCode() {
        return hash(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardPiece that = (BoardPiece) o;
        return x == that.x &&
                y == that.y;
    }
}