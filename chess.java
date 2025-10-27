import java.util.*;

// Represents a position on the chessboard
class Position {
    int row, col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return this.row == p.row && this.col == p.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

abstract class Piece {
    boolean isWhite;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    // Returns true if the move is valid according to the piece's movement rules and board occupancy rules
    public abstract boolean isValidMove(Position start, Position end, Board board);
}

// Pawn class
class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        int direction = isWhite ? -1 : 1;
        Piece destinationPiece = board.getPiece(end);

        // Can't capture own piece
        if (destinationPiece != null && destinationPiece.isWhite == this.isWhite) return false;

        if (start.col == end.col && destinationPiece == null) {
            // Single move forward
            if (end.row == start.row + direction) return true;
            // Double move forward from starting position
            if (end.row == start.row + 2 * direction &&
                (isWhite ? start.row == 6 : start.row == 1) &&
                board.getPiece(new Position(start.row + direction, start.col)) == null) {
                return true;
            }
        }
        // Diagonal capture
        if (Math.abs(end.col - start.col) == 1 &&
            end.row == start.row + direction &&
            destinationPiece != null &&
            destinationPiece.isWhite != this.isWhite) {
            return true;
        }
        return false;
    }
}

// Rook class
class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        if (start.row != end.row && start.col != end.col) return false; // must be straight
        if (!board.isPathClear(start, end)) return false;

        Piece dest = board.getPiece(end);
        // destination must be empty or enemy
        return dest == null || dest.isWhite != this.isWhite;
    }
}

// Knight class
class Knight extends Piece {
    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        int rowDiff = Math.abs(end.row - start.row);
        int colDiff = Math.abs(end.col - start.col);
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) return false;

        Piece dest = board.getPiece(end);
        return dest == null || dest.isWhite != this.isWhite;
    }
}

// Bishop class
class Bishop extends Piece {
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        if (Math.abs(end.row - start.row) != Math.abs(end.col - start.col)) return false;
        if (!board.isPathClear(start, end)) return false;

        Piece dest = board.getPiece(end);
        return dest == null || dest.isWhite != this.isWhite;
    }
}

// Queen class
class Queen extends Piece {
    public Queen(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        boolean straight = start.row == end.row || start.col == end.col;
        boolean diagonal = Math.abs(end.row - start.row) == Math.abs(end.col - start.col);
        if (!(straight || diagonal)) return false;
        if (!board.isPathClear(start, end)) return false;

        Piece dest = board.getPiece(end);
        return dest == null || dest.isWhite != this.isWhite;
    }
}

// King class
class King extends Piece {
    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        int rowDiff = Math.abs(end.row - start.row);
        int colDiff = Math.abs(end.col - start.col);
        if (rowDiff == 0 && colDiff == 0) return false;
        if (rowDiff <= 1 && colDiff <= 1) {
            Piece dest = board.getPiece(end);
            return dest == null || dest.isWhite != this.isWhite;
        }
        // Note: castling not implemented
        return false;
    }
}

// Board class
class Board {
    private Piece[][] board;

    // Display options
    private boolean fancy = true; // Unicode pieces with ANSI colored board
    private Position lastMoveStart = null;
    private Position lastMoveEnd = null;

    public Board() {
        board = new Piece[8][8];
        initialize();
    }

    private void initialize() {
        // Initialize board with standard chess setup
        board[0] = new Piece[]{new Rook(false), new Knight(false), new Bishop(false), new Queen(false),
                               new King(false), new Bishop(false), new Knight(false), new Rook(false)};
        board[1] = new Piece[]{new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false),
                               new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false)};
        board[6] = new Piece[]{new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true),
                               new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true)};
        board[7] = new Piece[]{new Rook(true), new Knight(true), new Bishop(true), new Queen(true),
                               new King(true), new Bishop(true), new Knight(true), new Rook(true)};
        // Ensure middle ranks exist (already null-initialized by new Piece[8][8])
        for (int r = 2; r <= 5; r++) {
            if (board[r] == null) board[r] = new Piece[8];
        }
    }

    public Piece getPiece(Position pos) {
        return board[pos.row][pos.col];
    }

    public void setPiece(Position pos, Piece piece) {
        board[pos.row][pos.col] = piece;
    }

    public void setLastMove(Position start, Position end) {
        this.lastMoveStart = start;
        this.lastMoveEnd = end;
    }

    public void setFancy(boolean fancy) {
        this.fancy = fancy;
    }

    public boolean isFancy() {
        return fancy;
    }

    public boolean isPathClear(Position start, Position end) {
        int rowStep = Integer.compare(end.row, start.row);
        int colStep = Integer.compare(end.col, start.col);

        int row = start.row + rowStep;
        int col = start.col + colStep;

        while (row != end.row || col != end.col) {
            if (board[row][col] != null) return false;
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    public List<Position> getLegalMovesFrom(Position start) {
        Piece piece = getPiece(start);
        List<Position> moves = new ArrayList<>();
        if (piece == null) return moves;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position end = new Position(r, c);
                if (start.equals(end)) continue;
                if (piece.isValidMove(start, end, this)) {
                    moves.add(end);
                }
            }
        }
        return moves;
    }

    public void display() {
        display(Collections.emptySet());
    }

    public void display(Set<Position> highlights) {
        if (fancy) {
            displayFancy(highlights);
        } else {
            displayAscii();
        }
    }

    private void displayAscii() {
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println("  +---+---+---+---+---+---+---+---+");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " |");
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                char symbol = (piece == null) ? ' ' : getAsciiPieceSymbol(piece);
                System.out.print(" " + symbol + " |");
            }
            System.out.println(" " + (8 - row));
            System.out.println("  +---+---+---+---+---+---+---+---+");
        }
        System.out.println("    a   b   c   d   e   f   g   h");
    }

    private void displayFancy(Set<Position> highlights) {
        final String RESET = "\u001B[0m";
        final String BG_LIGHT = "\u001B[48;5;250m";
        final String BG_DARK = "\u001B[48;5;243m";
        final String BG_HIGHLIGHT = "\u001B[48;5;44m";    // cyan-ish for legal targets
        final String BG_LAST = "\u001B[48;5;178m";        // yellow-ish for last move
        final String FG_WHITE = "\u001B[38;5;15m";
        final String FG_BLACK = "\u001B[38;5;0m";

        System.out.println("      a   b   c   d   e   f   g   h");
        for (int row = 0; row < 8; row++) {
            System.out.print("  " + (8 - row) + "  ");
            for (int col = 0; col < 8; col++) {
                boolean dark = ((row + col) % 2 == 1);
                String bg = dark ? BG_DARK : BG_LIGHT;

                Position pos = new Position(row, col);
                if (highlights.contains(pos)) bg = BG_HIGHLIGHT;
                if (lastMoveStart != null && pos.equals(lastMoveStart)) bg = BG_LAST;
                if (lastMoveEnd != null && pos.equals(lastMoveEnd)) bg = BG_LAST;

                Piece piece = board[row][col];
                String symbol = (piece == null) ? " " : getUnicodePieceSymbol(piece);
                String fg = (piece == null) ? "" : (piece.isWhite ? FG_WHITE : FG_BLACK);

                System.out.print(bg + fg + " " + symbol + " " + RESET);
            }
            System.out.println("  " + (8 - row));
        }
        System.out.println("      a   b   c   d   e   f   g   h");
    }

    private char getAsciiPieceSymbol(Piece piece) {
        // Uppercase for white, lowercase for black
        if (piece instanceof Pawn) return piece.isWhite ? 'P' : 'p';
        if (piece instanceof Rook) return piece.isWhite ? 'R' : 'r';
        if (piece instanceof Knight) return piece.isWhite ? 'N' : 'n';
        if (piece instanceof Bishop) return piece.isWhite ? 'B' : 'b';
        if (piece instanceof Queen) return piece.isWhite ? 'Q' : 'q';
        if (piece instanceof King) return piece.isWhite ? 'K' : 'k';
        return '?'; // Fallback for unknown piece
    }

    private String getUnicodePieceSymbol(Piece piece) {
        if (piece instanceof Pawn) return piece.isWhite ? "♙" : "♟";
        if (piece instanceof Rook) return piece.isWhite ? "♖" : "♜";
        if (piece instanceof Knight) return piece.isWhite ? "♘" : "♞";
        if (piece instanceof Bishop) return piece.isWhite ? "♗" : "♝";
        if (piece instanceof Queen) return piece.isWhite ? "♕" : "♛";
        if (piece instanceof King) return piece.isWhite ? "♔" : "♚";
        return "?";
    }
}

// ChessGame class
public class ChessGame {
    private Board board;
    private boolean isWhiteTurn;

    public ChessGame() {
        board = new Board();
        isWhiteTurn = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        printHelp();
        while (true) {
            board.display();
            System.out.println((isWhiteTurn ? "White" : "Black") + "'s turn.");
            System.out.print("Enter move (e.g., e2 e4), or command (help, moves e2, fancy on/off, restart, quit): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }

            if (input.equalsIgnoreCase("restart")) {
                board = new Board();
                isWhiteTurn = true;
                System.out.println("Game restarted.");
                continue;
            }

            if (input.equalsIgnoreCase("fancy on")) {
                board.setFancy(true);
                System.out.println("Fancy display enabled.");
                continue;
            }
            if (input.equalsIgnoreCase("fancy off")) {
                board.setFancy(false);
                System.out.println("Fancy display disabled (ASCII mode).");
                continue;
            }

            if (input.toLowerCase().startsWith("moves ")) {
                String token = input.substring(6).trim();
                Position p = parsePosition(token);
                if (p == null) {
                    System.out.println("Invalid square. Use algebraic like e2.");
                    continue;
                }
                Piece piece = board.getPiece(p);
                if (piece == null) {
                    System.out.println("No piece at " + token + ".");
                    continue;
                }
                if (piece.isWhite != isWhiteTurn) {
                    System.out.println("It's " + (isWhiteTurn ? "White" : "Black") + "'s turn.");
                    continue;
                }
                List<Position> legal = board.getLegalMovesFrom(p);
                Set<Position> highlight = new HashSet<>(legal);
                board.display(highlight);
                System.out.println("Legal moves for " + token + ": " + formatPositions(legal));
                continue;
            }

            // Move input "e2 e4"
            if (input.length() != 5 || input.charAt(2) != ' ') {
                System.out.println("Invalid input format. Try 'e2 e4' or type 'help'.");
                continue;
            }

            Position start = parsePosition(input.substring(0, 2));
            Position end = parsePosition(input.substring(3, 5));

            if (start == null || end == null) {
                System.out.println("Invalid square(s). Use algebraic like e2 e4.");
                continue;
            }
            if (start.equals(end)) {
                System.out.println("Start and end squares are the same.");
                continue;
            }

            Piece piece = board.getPiece(start);
            Piece destinationPiece = board.getPiece(end);

            if (piece == null) {
                System.out.println("No piece on " + input.substring(0, 2) + ".");
                continue;
            }
            if (piece.isWhite != isWhiteTurn) {
                System.out.println("It's " + (isWhiteTurn ? "White" : "Black") + "'s turn.");
                continue;
            }

            // Validate by piece logic (includes own-piece capture prevention)
            if (piece.isValidMove(start, end, board)) {
                if (destinationPiece instanceof King) {
                    System.out.println((isWhiteTurn ? "White" : "Black") + " captures the King! Game over.");
                    System.out.print("Type 'restart' to play again, or 'quit' to exit. ");
                    String response = scanner.nextLine().trim();
                    if (response.equalsIgnoreCase("restart")) {
                        board = new Board();
                        isWhiteTurn = true;
                        System.out.println("Game restarted.");
                        continue;
                    } else {
                        System.out.println("Goodbye!");
                        break;
                    }
                }

                board.setPiece(end, piece);
                board.setPiece(start, null);
                board.setLastMove(start, end);

                if (destinationPiece != null) {
                    System.out.println((piece.isWhite ? "White" : "Black") + " captured a " +
                            pieceName(destinationPiece) + " on " + posToString(end) + ".");
                }
                isWhiteTurn = !isWhiteTurn;
            } else {
                System.out.println("Invalid move. Try again or type 'moves " + input.substring(0, 2) + "'.");
            }
        }
        scanner.close();
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  e2 e4       Make a move from e2 to e4");
        System.out.println("  moves e2    Show legal moves for the piece on e2 (highlights on board)");
        System.out.println("  fancy on    Enable Unicode + colored board");
        System.out.println("  fancy off   Use ASCII board");
        System.out.println("  restart     Restart the game");
        System.out.println("  help        Show this help");
        System.out.println("  quit        Exit the game");
    }

    private String pieceName(Piece p) {
        if (p instanceof Pawn) return "Pawn";
        if (p instanceof Rook) return "Rook";
        if (p instanceof Knight) return "Knight";
        if (p instanceof Bishop) return "Bishop";
        if (p instanceof Queen) return "Queen";
        if (p instanceof King) return "King";
        return "Piece";
    }

    private String posToString(Position p) {
        char file = (char) ('a' + p.col);
        char rank = (char) ('0' + (8 - p.row));
        return "" + file + rank;
    }

    private String formatPositions(List<Position> positions) {
        List<String> list = new ArrayList<>();
        for (Position p : positions) list.add(posToString(p));
        return String.join(", ", list);
    }

    private Position parsePosition(String pos) {
        if (pos.length() != 2) return null;
        int col = pos.charAt(0) - 'a';
        int rankDigit = pos.charAt(1) - '0';
        int row = 8 - rankDigit;
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return null;
        return new Position(row, col);
    }

    public static void main(String[] args) {
        new ChessGame().start();
    }
}