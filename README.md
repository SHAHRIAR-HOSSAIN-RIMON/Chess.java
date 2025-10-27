# Chess.java

A simple, educational chess program in Java with a console UI and a lightweight web frontend.
- Console app: Play from the terminal with ASCII or colored Unicode pieces, legal-move highlights, and helper commands.
- Web frontend: Click-to-move board in static HTML/CSS/JS that mirrors the same basic rules.

Note: This implementation is intentionally minimal and does not implement check/checkmate validation, castling, or en passant.

## Features

- Standard chess starting position
- Piece movement rules:
  - Pawns: single/double push from start, diagonal captures
  - Rooks, bishops, queens: path-blocking enforced
  - Knights: L-shaped moves
  - King: one square moves
- Safety: cannot capture your own pieces
- Win condition: capturing the opponent king ends the game
- Console extras:
  - Fancy mode (Unicode + colored squares)
  - Commands: `help`, `restart`, `fancy on/off`, `moves e2`, `quit`
  - Highlights: last move and optional legal target squares
- Web extras:
  - Click a piece to show its legal moves
  - Click a highlighted square to move
  - Fancy pieces toggle (Unicode vs. ASCII-like)

## Project Structure

```
.
├── chess.java          # Console application (Java)
└── web/                # Static frontend (no backend required)
    ├── index.html
    ├── styles.css
    └── app.js
```

## Getting Started (Console)

Prerequisites:
- Java 8+ (JDK)

Compile and run:
```bash
javac chess.java
java ChessGame
```

In-game commands (type at the prompt):
- `e2 e4`            Make a move from e2 to e4
- `moves e2`         Show legal moves for the piece on e2 and highlight them
- `fancy on|off`     Toggle Unicode/colored board vs. ASCII
- `restart`          Reset to the initial position
- `help`             Show available commands
- `quit`             Exit the game

Tips:
- Use `moves <square>` to see what a selected piece can do.
- The board shows last move squares highlighted.

## Getting Started (Web Frontend)

No build step needed—just open in a browser.

Option A: Open directly
- Open `web/index.html` in your browser

Option B: Serve locally (recommended for Chrome)
```bash
# Python 3
cd web
python -m http.server 8000
# then visit http://localhost:8000
```

Controls:
- Click a piece to select and show its legal moves
- Click a highlighted square to move
- Use the “Fancy pieces” checkbox to toggle Unicode glyphs
- “Restart” resets the game

## Limitations and Roadmap

Current limitations:
- No check/checkmate/stalemate detection
- No castling
- No en passant
- No pawn promotion dialog (you can add a basic promotion rule as a next step)

Potential improvements:
- Enforce check legality (prevent king from moving into check; disallow moves that leave king in check)
- Add castling, en passant, and promotion UI
- Move notation and history
- Basic AI (minimax) to play against the computer

## License

No license specified. Consider adding one (e.g., MIT) if you want others to reuse this code.

## Acknowledgements

Unicode chess piece glyphs are provided by standard fonts; board coloring uses ANSI escape codes in the console and CSS in the web frontend.
