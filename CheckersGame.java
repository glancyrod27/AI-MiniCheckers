import java.util.*;
import java.util.Map.Entry;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

class MoveValue {
	//alpha beta algorithm returns action and its value thus required this class
	int value;
	Move move;

	public MoveValue(int value, Move move) {
		this.value = value;
		this.move = move;
	}
}

class Position {
	//any position has x and y cordinates i.e row and column value
	int row, col;

	public Position(int r, int c) {
		row = r;
		col = c;
	}
}

class Move {
	//each move consist of two position from and to
	Position from;
	Position to;

	public Move(Position f, Position t) {
		from = f;
		to = t;
	}
}

class Checker {
	public static final int empty = 0, white = 1, black = 2;
	public int checkerboard[][] = new int[6][6];
	public static int maxPrune = 0, minPrune = 0, depth = 0, numberOfNodes = 0;

	public void GameStart() {
		//initialize checkerboard 
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				//to place pieces in alternative position
				if (i % 2 != j % 2) {
					//first two lines occupied by white
					if (i < 2)
						checkerboard[i][j] = white;
					else if (i > 3)
						//last two lines occupied by black
						checkerboard[i][j] = black;
					else
						checkerboard[i][j] = empty;
				} else {
					checkerboard[i][j] = empty;
				}
			}
		}
	}

	private boolean LegalMove(int board[][], int player, Position p1, Position p2) {
		// Position p1 is previous position and p2 is new position

		// checking for 6*6 board size
		if (p2.row < 0 || p2.row >= 6 || p2.col < 0 || p2.col >= 6)
			return false;

		// position is not empty
		if (board[p2.row][p2.col] != empty)
			return false;

		// for white player new row should be greater than previous
		if (player == white) {
			if (board[p1.row][p1.col] == white && p2.row < p1.row)
				return false;
			return true;
		} else {
			// for black player new row should be smaller than previous
			if (board[p1.row][p1.col] == black && p2.row > p1.row)
				return false;
			return true;
		}

	}

	private boolean LegalJump(int board[][], int player, Position p1, Position p2, Position p3) {
		// Position p1 is previous position and p3 is new position and p2 is position of
		// opponent

		// checking for 6*6 board size
		if (p3.row < 0 || p3.row >= 6 || p3.col < 0 || p3.col >= 6)
			return false;

		// position is not empty
		if (board[p3.row][p3.col] != empty)
			return false;

		// for white player new row should be greater than previous and black player
		// present in between
		if (player == white) {
			if (board[p1.row][p1.col] == white && p3.row < p1.row)
				return false;
			if (board[p2.row][p2.col] != black)
				return false;
			return true;
		} else {
			// for black player new row should be smaller than previous and white player
			// present in between
			if (board[p1.row][p1.col] == black && p3.row > p1.row)
				return false;
			if (board[p2.row][p2.col] != white)
				return false;
			return true;
		}

	}

	public Move[] getLegalMoves(int board[][], int player) {
		//returns all legal moves of player in argument
		if (player != white && player != black)
			return null;
		List<Move> totalmoves = new ArrayList<Move>();

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (board[i][j] == player) {
					Position old = new Position(i, j);
					if (player == white) {
						//checking all jumps possible for white player - considering moves only in forward direction
						if (LegalJump(board, player, old, new Position(i + 1, j + 1), new Position(i + 2, j + 2)))
							totalmoves.add(new Move(old, new Position(i + 2, j + 2)));
						if (LegalJump(board, player, old, new Position(i + 1, j - 1), new Position(i + 2, j - 2)))
							totalmoves.add(new Move(old, new Position(i + 2, j - 2)));
					} else {
						//checking all jumps possible for black player -  considering moves only in forward direction
						if (LegalJump(board, player, old, new Position(i - 1, j + 1), new Position(i - 2, j + 2)))
							totalmoves.add(new Move(old, new Position(i - 2, j + 2)));
						if (LegalJump(board, player, old, new Position(i - 1, j - 1), new Position(i - 2, j - 2)))
							totalmoves.add(new Move(old, new Position(i - 2, j - 2)));
					}
				}
			}
		}
		if (totalmoves.size() == 0) {
			//if jumps are not possible then check for normal moves
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 6; j++) {
					if (board[i][j] == player) {
						Position old = new Position(i, j);
						if (player == white) {
							//checking all moves possible for white player - considering moves only in forward direction
							if (LegalMove(board, player, old, new Position(i + 1, j + 1)))
								totalmoves.add(new Move(old, new Position(i + 1, j + 1)));
							if (LegalMove(board, player, old, new Position(i + 1, j - 1)))
								totalmoves.add(new Move(old, new Position(i + 1, j - 1)));
						} else {
							//checking all moves possible for black player -  considering moves only in forward direction
							if (LegalMove(board, player, old, new Position(i - 1, j + 1)))
								totalmoves.add(new Move(old, new Position(i - 1, j + 1)));
							if (LegalMove(board, player, old, new Position(i - 1, j - 1)))
								totalmoves.add(new Move(old, new Position(i - 1, j - 1)));
						}
					}
				}
			}
		}

		if (totalmoves.size() == 0)
			return null;
		else {
			//converting List of Moves to array of moves
			Move[] moveArray = new Move[totalmoves.size()];
			Iterator itr = totalmoves.iterator();
			int i = 0;
			while (itr.hasNext()) {
				moveArray[i] = (Move) itr.next();
				i++;
			}
			return moveArray;
		}

	}

	public Move computer() {
		//AI part of game ...returns moves that computer takes
		Checker.maxPrune = 0;
		Checker.minPrune = 0;
		Checker.depth = 0;
		Checker.numberOfNodes = 0;
		int board[][] = new int[6][6];
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 6; j++)
				board[i][j] = checkerboard[i][j];
		//alpha-beta search starts here
		MoveValue v = max_value(board, -100, 100, 0);
		Move ai_move = v.move;
		System.out.println("MaxPrune & MinPRune: " + Checker.maxPrune + " " + Checker.minPrune);
		System.out.println("Number of Nodes: " + Checker.numberOfNodes);
		return ai_move;
	}

	public MoveValue max_value(int[][] board, int a, int b, int depth) {
		Checker.numberOfNodes++;
		int v = -100;
		//if terminal test pass value of utility function with no move
		MoveValue moveValue = new MoveValue(utility(board), null);

		Move mv[] = getLegalMoves(board, 1);
		if (mv != null) {
			for (int i = 0; i < mv.length; i++) {
				//recursive call to min function
				MoveValue temp = min_value(PossibleState(board, mv[i].from, mv[i].to), a, b, depth + 1);
				//assigning max value
				if (v < temp.value) {
					v = temp.value;
					moveValue = new MoveValue(v, mv[i]);
				}
				//if pruning takes place
				if (v >= b) {
					Checker.maxPrune++;
					return moveValue;
				}
				//assigning value of alpha
				if (v > a)
					a = v;
			}
		}
		return moveValue;
	}

	public MoveValue min_value(int board[][], int a, int b, int depth) {
		Checker.numberOfNodes++;
		int v = 100;
		//if terminal test pass value of utility function with no move
		MoveValue moveValue = new MoveValue(utility(board), null);

		Move mv[] = getLegalMoves(board, 2);
		if (mv != null) {
			for (int i = 0; i < mv.length; i++) {
				//recursive call to max function
				MoveValue temp = max_value(PossibleState(board, mv[i].from, mv[i].to), a, b, depth + 1);
				//assigning max value
				if (v > temp.value)
					v = temp.value;
				//if pruning takes place
				if (v <= a) {
					Checker.minPrune++;
					return moveValue;
				}
				//assigning value of beta
				if (v < b)
					b = v;
			}
		}
		return moveValue;
	}

	public int utility(int[][] board) {
		//utility function returns value of terminal state based on no of white pieces - no of black pieces
		int blackp = 0, whitep = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (board[i][j] == white) {
					whitep++;
				}
				if (board[i][j] == black) {
					blackp++;
				}
			}
		}
		return (whitep - blackp);
	}

	public int[][] PossibleState(int board[][], Position f, Position t) {
		//for AI to check state for building the tree ..gives temporary states
		//doesnt make changes in original board
		//assign new position with value of old position
		board[t.row][t.col] = board[f.row][f.col];
		//make old position empty
		board[f.row][f.col] = empty;
		//if jump is taken make opponents piece empty
		if (Math.abs(f.row - t.row) == 2) {
			int jcol = (f.col + t.col) / 2;
			int jrow = (f.row + t.row) / 2;
			board[jrow][jcol] = empty;
		}
		return board;
	}

	public void AfterMove(Position f, Position t) {
		//makes permanent changes on actual checkerboard after either player takes move
		//assign new position with value of old position
		checkerboard[t.row][t.col] = checkerboard[f.row][f.col];
		//make old position empty
		checkerboard[f.row][f.col] = empty;
		//if jump is taken make opponents piece empty
		if (Math.abs(f.row - t.row) == 2) {
			int jcol = (f.col + t.col) / 2;
			int jrow = (f.row + t.row) / 2;
			checkerboard[jrow][jcol] = empty;
		}
		

	}

	public int winner() {
		//finding winner based on no of pieces left
		int black_pieces = 0;
		int white_pieces = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (checkerboard[i][j] == black)
					black_pieces++;
				else {
					if (checkerboard[i][j] == white)
						white_pieces++;
				}
			}
		}
		if (black_pieces == white_pieces)
			// game is draw
			return 0;
		else {

			if (black_pieces > white_pieces)
				//human wins
				return 2;
			else
				//human loses
				return 1;
		}

	}
}

public class CheckersGame extends Applet {
//GUI part
	public void init() {

		setLayout(null);
		setBackground(new Color(100, 100, 100));
		CheckerCanvas ch = new CheckerCanvas();
		add(ch);
		//two buttons to select human will play first or computer 
		ch.PlayFirst.setBackground(Color.lightGray);
		add(ch.PlayFirst);
		ch.PlaySecond.setBackground(Color.lightGray);
		add(ch.PlaySecond);
		
		//message to guide and give final result
		ch.message.setForeground(Color.white);
		add(ch.message);

		ch.setBounds(20, 20, 244, 244);
		ch.PlayFirst.setBounds(270, 60, 100, 30);
		ch.PlaySecond.setBounds(270, 120, 100, 30);
		ch.message.setBounds(0, 300, 330, 30);
	}
}

class CheckerCanvas extends Canvas implements ActionListener, MouseListener {
	Button PlayFirst;
	Button PlaySecond;
	Label message;
	Checker cd;
	boolean gameInProgress;
	int currentPlayer;
	int selectedRow, selectedCol;
	Move[] mv;
	Boolean mvw = true, mvb = true;

	public CheckerCanvas() {

		setBackground(Color.black);
		addMouseListener(this);
		setFont(new Font("Serif", Font.BOLD, 14));
		PlayFirst = new Button("Play First");
		PlayFirst.addActionListener(this);
		PlaySecond = new Button("Play Second");
		PlaySecond.addActionListener(this);
		
		message = new Label("", Label.CENTER);
		//creating object of Checker class to access all the methods
		cd = new Checker();
	
	}


	public void actionPerformed(ActionEvent evt) {
		//human selects to play first or second
		Object src = evt.getSource();
		//if selected first then current player is black and disable buttons
		if (src == PlayFirst) {
			currentPlayer = cd.black;
			PlayFirst.setEnabled(false);
			PlaySecond.setEnabled(false);
			StartNew();
		} 
		//if selected second then current player is white and disable buttons
		else if (src == PlaySecond) {
			currentPlayer = cd.white;
			PlayFirst.setEnabled(false);
			PlaySecond.setEnabled(false);
			StartNew();
		}
	}

	void StartNew() {
		//initialize game
		cd.GameStart();
		//find legal moves of current player
		mv = cd.getLegalMoves(cd.checkerboard, currentPlayer);
		//no row selected at start
		selectedRow = -1;
		gameInProgress = true;
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		repaint();
		//if current player is computer skip the part of selecting move by mouse action and go to AI part 
		if (currentPlayer == Checker.white)
			moved();
	}
	
	
	void gameOver(String str) {
		//give result of the game when it ends
		message.setText(str);
		gameInProgress = false;
	}

	public void paint(Graphics g) {
		g.setColor(Color.black);
		//drawing big checker board with border
		g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
		g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				//drawing light-dark blocks in checker board
				if (i % 2 == j % 2)
					g.setColor(new Color(220, 220, 255));
				else
					g.setColor(new Color(100, 80, 180));
				g.fillRect(2 + j * 40, 2 + i * 40, 40, 40);
				//drawing black- white pieces on board
				if (cd.checkerboard[i][j] == Checker.white) {
					g.setColor(Color.white);
					g.fillOval(4 + j * 40, 4 + i * 40, 36, 36);
				} else {
					if (cd.checkerboard[i][j] == Checker.black) {
						g.setColor(Color.black);
						g.fillOval(4 + j * 40, 4 + i * 40, 36, 36);
					}
				}
			}
		}
		if (gameInProgress) {
			//highlighting pieces that can be moved 
			g.setColor(Color.magenta);
			for (int i = 0; i < mv.length; i++) {
				g.drawRect(2 + mv[i].from.col * 40, 2 + mv[i].from.row * 40, 39, 39);
			}
			//if which piece to move is selected highlight positions where they can be moved 
			if (selectedRow >= 0) {
				g.setColor(Color.white);
				g.drawRect(2 + selectedCol * 40, 2 + selectedRow * 40, 39, 39);
				g.drawRect(3 + selectedCol * 40, 3 + selectedRow * 40, 37, 37);
				g.setColor(Color.green);
				for (int i = 0; i < mv.length; i++) {
					if (mv[i].from.col == selectedCol && mv[i].from.row == selectedRow)
						g.drawRect(2 + mv[i].to.col * 40, 2 + mv[i].to.row * 40, 39, 39);
				}
			}
		}
	}

	void clicked(int r, int c) {
		//When any position within the boundary of checkerboard is clicked 
		
		//checking the clicked position is about which piece to move and assigning values for selected row and column
		for (int i = 0; i < mv.length; i++)
			if (mv[i].from.row == r && mv[i].from.col == c) {
				selectedRow = r;
				selectedCol = c;
				if (currentPlayer == Checker.black)
					message.setText("Make your move.");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
				repaint();
				return;
			}

		if (selectedRow < 0) {
			message.setText("Click the piece");
			return;
		}
		//checking the clicked position is about where to move the selected piece and taking selected move
		for (int i = 0; i < mv.length; i++)
			if (mv[i].from.row == selectedRow && mv[i].from.col == selectedCol && mv[i].to.row == r
					&& mv[i].to.col == c) {
				//Making changes in actual board withh selected move
				cd.AfterMove(mv[i].from, mv[i].to);
				//after taking move clear selected row
				selectedRow = -1;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
				repaint();
				//taking move on GUI
				moved();
				return;
			}
		message.setText("Click the square");
	}

	void moved() {
		//human already took move its time for computer now
		currentPlayer = Checker.white;
		mv = cd.getLegalMoves(cd.checkerboard, currentPlayer);
		//setting flag based on legal moves available or not
		mvw = !(mv == null);
		//if legal moves available for computer then computer will play else will give chance to human
		if (mvw) {
			Move temp = cd.computer();
			cd.AfterMove(temp.from, temp.to);
		}
		
		//computers turn is over now human will play again
		currentPlayer = Checker.black;
		mv = cd.getLegalMoves(cd.checkerboard, currentPlayer);
		//setting flag based on legal moves available or not
		mvb = !(mv == null);
		if (mvb){
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		repaint();
		}
		//if no legal moves available for both players check for winner 
		if (!mvw && !mvb) {
			int win = cd.winner();
			if (win == 0) {
				gameOver("Match is draw");
			} else if (win == 1) {
				gameOver("You lose");
			} else {
				gameOver("You win");
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
			repaint();
		}
		//if no legal moves available for human give chance to computer 
		if (!mvb && mvw)
			moved();
	}



	public void mousePressed(MouseEvent evt) {
		//Find out which position is clicked on the board. each small square is 40*40  
		int i = (evt.getX() - 2) / 40;
		int j = (evt.getY() - 2) / 40;
		//check if the position is within board boundary 
		if (i >= 0 && i < 6 && j >= 0 && j < 6)
			clicked(j, i);
	}

	public void mouseReleased(MouseEvent evt) {
	}

	public void mouseClicked(MouseEvent evt) {
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}
}

