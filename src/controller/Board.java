package controller;

import players.Player;

import java.awt.*;

public class Board {
    public enum Pieces {
        NONE, GOOD, EVIL, OPPONENT
    }
    private class Piece {
        private final Player owner;
        private final boolean good;
        public Piece(Player owner, boolean good){
            this.owner = owner;
            this.good = good;
        }
        public Pieces getState(Player player){
            if (!player.equals(owner)){
                return Pieces.OPPONENT;
            }
            return good?Pieces.GOOD:Pieces.EVIL;
        }

    }
    public final static int BOARD_WIDTH = 6;
    public final static int BOARD_HEIGHT = 6;
    public final static int INITIAL_PLACEMENT_AREA_HEIGHT = 2;
    public final static int INITIAL_PLACEMENT_AREA_WIDTH = 4;
    public final static int INITIAL_PLACEMENT_AREA_Y_OFFSET = BOARD_HEIGHT-INITIAL_PLACEMENT_AREA_HEIGHT;
    public final static int INITIAL_PLACEMENT_AREA_X_OFFSET = (BOARD_WIDTH-INITIAL_PLACEMENT_AREA_WIDTH)/2;
    public final static int MAX_PIECES_PER_PLAYER_OF_TYPE = 4;
    public final static int MAX_PIECES_PER_PLAYER = MAX_PIECES_PER_PLAYER_OF_TYPE*2;

    private final Piece[][] board;
    private final Game owner;
    private boolean initialized;

    public Board(Game owner){
        board = new Piece[BOARD_WIDTH][BOARD_HEIGHT];
        this.owner = owner;
        initialized = false;
    }

    public void initialize(){
        initialized = true;
    }

    public void addPieces(Pieces[][] pieces, Player player){
        if (initialized){
            throw new RuntimeException("Pieces can only be added at the beginning of the game");
        }
        if (pieces.length != INITIAL_PLACEMENT_AREA_HEIGHT ||
                pieces[0].length != INITIAL_PLACEMENT_AREA_WIDTH ||
                pieces[1].length != INITIAL_PLACEMENT_AREA_WIDTH){
            throw new RuntimeException("Pieces must be in a 2x4 grid");
        }
        for (Pieces[] row: pieces){
            for (Pieces piece: row){
                if (piece != Pieces.EVIL && piece != Pieces.GOOD){
                    throw new RuntimeException("Pieces must be either be Good or Evil");
                }
            }
        }
        for (int y = 0; y < INITIAL_PLACEMENT_AREA_HEIGHT; y++){
            for (int x = 0; x < INITIAL_PLACEMENT_AREA_WIDTH;x++){
                Point p = new Point(y+INITIAL_PLACEMENT_AREA_Y_OFFSET, x+INITIAL_PLACEMENT_AREA_X_OFFSET);
                p = playerToBoardCoordinates(p, player);
                this.board[p.x][p.y] = new Piece(player, pieces[y][x] == Pieces.GOOD);
            }
        }
    }

    private Point playerToBoardCoordinates(Point playerCoordinates, Player player){
        if (playerCoordinates.x >= BOARD_WIDTH || playerCoordinates.y >= BOARD_HEIGHT || playerCoordinates.x < 0 || playerCoordinates.y < 0){
            throw new RuntimeException("Point out of bounds");
        }
        if(owner.getPlayerNumber(player) == 0){
            return playerCoordinates;
        }
        Point rotated = new Point(playerCoordinates);
        rotated.x = BOARD_WIDTH - rotated.x - 1;
        rotated.y = BOARD_HEIGHT - rotated.y - 1;
        return rotated;
    }

    public void movePiece(Point from, Point to, Player player){
        from = playerToBoardCoordinates(from, player);
        to = playerToBoardCoordinates(to, player);
        if (board[from.x][from.y] == null){
            throw new RuntimeException("No piece found at "+from);
        }
        if (board[from.x][from.y].owner != player){
            throw new RuntimeException("Moving opponent's piece not allowed");
        }
        if (board[to.x][to.y] != null){
            if (board[to.x][to.y].owner == player){
                throw new RuntimeException("Cannot capture your own piece");
            }
        }
        board[to.x][to.y] = board[from.x][from.y];
        board[from.x][from.y] = null;
    }

    public Pieces[][] getBoard(Player player){
        Pieces[][] board = new Pieces[BOARD_HEIGHT][BOARD_WIDTH];
        for (int y = 0; y < BOARD_HEIGHT; y++ ){
            for (int x = 0; x < BOARD_WIDTH; x++){
                if (this.board[x][y] != null) {
                    Point p = playerToBoardCoordinates(new Point(x, y), player);
                    board[p.y][p.x] = this.board[x][y].getState(player);
                }
            }
        }
        return board;
    }

}
