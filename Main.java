
import java.util.*;

/*
Checklist
 * Valid Input ---------✓
 *      -Follows Format Letter+Number_Letter+Number, in range of board a-h and 1-8 ✓
 *      -The Move does not capture own piece ✓
 *      -Destination is not the same as origin ✓
 *      -Moving own piece ✓
 * 
 * Legal (Possible Move) ----------
 *      -Pawn ✓
 *          /Push Forward 1 ✓
 *          /Capture one diagonally ✓
 *          /Promotion ✓
 *          /Push Forward 2 when first moved ✓
 *          /En Passant ✓
 *          
 *       -Rook ✓
 *          /Move + capture + pressure horizontally unimpeded ✓
 *          /Move + capture + pressure  vertically unimpeded ✓
 *          
 *       -Knight ✓
 *          /Move + capture + pressure + jump  in L Shape ✓
 *          
 *       -Bishop ✓
 *          /Move + capture + pressure  diagonally unimpeded ✓
 *          
 *       -King ✓
 *          /Kingside/Queenside Castling ✓
 *              {King moves 2 toward rook, rook hops 1 over king ✓
 *              {King has not moved, rook has not moved ✓
 *              {will not castle through or in check ✓
 *          /Move 1 block any direction unimpeded ✓
 *          
 *       -Queen ✓ 
 *                  
 *          /Move + capture + pressure horizontally unimpeded ✓
 *          /Move + capture + pressure vertically unimpeded ✓
 *          /Move + capture + pressure diagonally unimpeded ✓
 *          
 *      -Move Does not Place King in Check  (legalMove) ✓
 *          /The check function is implemented, you just have to enter the letter ✓
 *          
 * Mechanics
 *      -Check, Force King Protection ✓
 *      -Checkmate, End Game
 *      -Ties, end game
 *          /Stalemate Player cannot legally move but is not in checkmate
 *          /Insufficient Material ✓
 *          /50 Move Rule ✓
 *          /Threefold Repetition ✓
 *      -Forfeit/Resign, end game ✓          
 *                
 */



public class Main {
    public static char[][] board = {
            {'♜', '♞', '♝', '♛', '♚', '♝', '♞', '♜'},
            {'♟', '♟', '♟', '♟', '♟', '♟', '♟', '♟'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'♙', '♙', '♙', '♙', '♙', '♙', '♙', '♙'},
            {'♖', '♘', '♗', '♕', '♔', '♗', '♘', '♖'},
    };
    public static String errorMessage = "";
    
    public static char[] whiteSet = {'♙', '♖', '♘', '♗', '♕', '♔'}; //, '♙', '♙', '♙', '♙', '♙', '♙', '♙', '♗', '♘', '♖'
    
    public static char[] blackSet = {'♜', '♞', '♝', '♛', '♚', '♝', '♞', '♜', '♟', '♟', '♟', '♟', '♟', '♟', '♟', '♟'};

    public static char[] letterKey = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    
    public static int[][] lastMove = new int[2][2];
    
    public static boolean whiteKingNotMoved = true;
    public static boolean blackKingNotMoved = true;
    
    public static boolean whiteKingRookNotMoved = true;
    public static boolean whiteQueenRookNotMoved = true;
    public static boolean blackQueenRookNotMoved = true;
    public static boolean blackKingRookNotMoved = true;
    
    public static int fiftyMove = 0;
    
    //Coordinates of the King
    public static int blackX = 0;
    public static int blackY = 4;
    public static int whiteX = 7;
    public static int whiteY = 4;
    
    public static boolean whiteCheck = false;
    public static boolean blackCheck = false;
    
    public static List<String> states = new ArrayList<String>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Chess!");
        
        Integer counter = 0;
        
        gameLoop:
        while (true) { //Turn Loop: one black, one white
            //In this loop: check for check, checkmate, stalemate, insufficient material, 50 move, threefold
            printBoard();
            
            whiteMove:
            while (true) { //White input decision loop
                System.out.println("---------------");
                System.out.println("White to Move.");
                System.out.println("Enter a the coordinates of the piece you want to move and the coordinate of the destination.");
                System.out.println("Lowercase letter and number. (ex: a2, a3)");
                System.out.println("Enter \"ff@20\" to resign.");
                String inp = scanner.nextLine();
                if (inp.equals("ff@20")) {
                    forfeit("W");
                    break gameLoop;
                }
                if (validInput(inp, "W") && possibleMove(inp, "W")) { //&&noCheck/Checkmate(inp, "W")
                    char[][] tempBoard = copyBoard();
                    
                    int xi = 8 - Character.getNumericValue(inp.charAt(1));
                    int yi = letterToNum(inp.charAt(0));
                    int xf = 8 - Character.getNumericValue(inp.charAt(4));
                    int yf = letterToNum(inp.charAt(3));
                    
                    lastMove[0][0] = xi;
                    lastMove[0][1] = yi;
                    lastMove[1][0] = xf;
                    lastMove[1][1] = yf;
                    
                    char temp = board[xi][yi]; 
                    board[xi][yi] = '…';
                    board[xf][yf] = temp;
                    if (check("W")) { //This if statement and its counterpart in blackmove are not working
                        /*
                         * Need:
                         * Right now, the board checks to see if the king is in check currently, not if the actual move
                         * puts in check. Need to make it so that right before this if statement, temporary
                         * get a board that copies the current board. then the move is made on the main board, and if it is in check,
                         * You undo the main board with the current board. 
                         * Perhaps this can be done under the portion with
                         * lastmove[0][0] = xi... et cetera
                         * make sure once you implement it for white, implement it for x
                         * This copy is different than the one needed for threefold repetition; threefold repetition
                         * requires a hash set, or better, a list of 2d arrays then count repetitions
                         * and to add to sets, either do the entire 2d array which may be too slow or the idea
                         * that should be done, translate each board into a string.
                         */
                        System.out.println("You(white) are in check. Invalid move.");
                        board = tempBoard;
                        continue whiteMove;
                    }
                    if (xf == 0 && temp == '♙') { //Promotion
                        printBoard();
                        System.out.println("You may promote your pawn.");
                        System.out.println("Choose queen, rook, knight, or bishop.");
                        System.out.println("q/r/k/b");
                        while (true) {
                            char input = scanner.nextLine().charAt(0);
                            if (input == 'q') {
                                board[xf][yf] = '♕';
                                break;
                            }
                            if (input == 'r') {
                                board[xf][yf] = '♖';
                                break;
                            }
                            if (input == 'k') {
                                board[xf][yf] = '♘';
                                break;
                            }
                            if (input == 'b') {
                                board[xf][yf] = '♗';
                                break;
                            }
                            else {
                                System.out.println("Invalid input. Enter q/r/k/b");
                            }
                        }
                    }
                    if (noPawns()) {
                        fiftyMove++;
                    }
                    break;
                }
                else {
                    System.out.println(errorMessage);
                }
            }
            whiteCheck = false;
            printBoard();
            
            //Checkmate goes here
            states.add(boardToString());
            if (Collections.frequency(states, boardToString()) == 3) {
                System.out.println("The game has ended by threefold repetition.");
                System.out.println("The same exact game board has appeared 3 times.");
                break gameLoop;
            }
            if (insufficientMaterial()) {
                System.out.println("The game has ended in a tie due to insufficient material.");
                System.out.println("It is impossible to achieve checkmate with the remaining pieces.");
                break gameLoop;
            }
            if (fiftyMove == 50) {
                System.out.println("The game has ended in a tie by the fifty move rule.");
                System.out.println("Fifty moves have been made since the last pawn of a player has been taken.");
                break gameLoop;
            }
            //Stalemate should go here
            if (stalemate("B") || stalemate("W")) {
                System.out.println("Stalemate.");
                break gameLoop;
            }
            if (check("B")) {
                blackCheck = true;
                System.out.println("Check! Black must protect the king");
            }
            
            blackMove:
            while (true) { //Black input decision loop
                System.out.println("---------------");
                System.out.println("Black to Move.");
                System.out.println("Enter a the coordinates of the piece you want to move and the coordinate of the destination.");
                System.out.println("Lowercase letter and number. (ex: a2, a3)");
                System.out.println("Enter \"ff@20\" to resign.");
                String inp = scanner.nextLine();
                if (inp.equals("ff@20")) {
                    forfeit("B");
                    break gameLoop;
                }
                if (validInput(inp, "B") && possibleMove(inp, "B")) { //&&noCheck/Checkmate(inp, "W")
                    char[][] tempBoard = copyBoard();
                    int xi = 8 - Character.getNumericValue(inp.charAt(1));
                    int yi = letterToNum(inp.charAt(0));
                    int xf = 8 - Character.getNumericValue(inp.charAt(4));
                    int yf = letterToNum(inp.charAt(3));
                    
                    lastMove[0][0] = xi;
                    lastMove[0][1] = yi;
                    lastMove[1][0] = xf;
                    lastMove[1][1] = yf;
                   
                    char temp = board[xi][yi]; 
                    board[xi][yi] = '…';
                    board[xf][yf] = temp;
                    if (check("B")) {
                        System.out.println("You(black) are in check. Invalid move.");
                        continue blackMove;
                    }
                    
                    
                    if (xf == 7 && temp == '♟') { //PROMOTION,
                        printBoard();
                        System.out.println("You may promote your pawn.");
                        System.out.println("Choose queen, rook, knight, or bishop.");
                        System.out.println("q/r/k/b");
                        while (true) {
                            char input = scanner.nextLine().charAt(0);
                            if (input == 'q') {
                                board[xf][yf] = '♛';
                                break;
                            }
                            if (input == 'r') {
                                board[xf][yf] = '♜';
                                break;
                            }
                            if (input == 'k') {
                                board[xf][yf] = '♞';
                                break;
                            }
                            if (input == 'b') {
                                board[xf][yf] = '♝';
                                break;
                            }
                            else {
                                System.out.println("Invalid input. Enter q/r/k/b");
                            }
                        }
                    }
                    if (noPawns()) {
                        fiftyMove++;
                    }
                    break;
                }
                else {
                    System.out.println(errorMessage);
                }
            }
            blackCheck = false;
            
            states.add(boardToString());
            if (Collections.frequency(states, boardToString()) == 3) {
                System.out.println("The game has ended by threefold repetition.");
                System.out.println("The same exact game board has appeared 3 times.");
                break gameLoop;
            }
            if (insufficientMaterial()) {
                System.out.println("The game has ended in a tie due to insufficient material.");
                System.out.println("It is impossible to achieve checkmate with the remaining pieces.");
                break gameLoop;
            }
            if (fiftyMove == 50) {
                System.out.println("The game has ended in a tie by the fifty move rule.");
                System.out.println("Fifty moves have been made since the last pawn of a player has been taken.");
                break gameLoop;
            }
            if (stalemate("B") || stalemate("W")) {
                System.out.println("Stalemate.");
                break gameLoop;
            }
            if (check("W")) {
                whiteCheck = true;
                System.out.println("Check!~ White must protect the king");
            }
        }
    }
    public static boolean stalemate(String player) {
        if (player.equals("W")) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (isWhitePiece(board[i][j])) {
                        char[][] tempBoard = copyBoard();
                        int xi = i;
                        int yi = j;
                        for (int l = 0; l < 8; l++) {
                            loop:
                            for (int k = 0; k < 8; k++) {
                                int xf = l;
                                int yf = k;
                                if (possibleMove(numToLetter(j) + "" + (8 - i) + " " + numToLetter(k) + (8 - l), "W")) {
                                    lastMove[0][0] = xi;
                                    lastMove[0][1] = yi;
                                    lastMove[1][0] = xf;
                                    lastMove[1][1] = yf;
                                   
                                    char temp = board[xi][yi]; 
                                    board[xi][yi] = '…';
                                    board[xf][yf] = temp;
                                    if (!check("W")) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (player.equals("B")) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (!isWhitePiece(board[i][j]) && board[i][j] != '…') {
                        char[][] tempBoard = copyBoard();
                        int xi = i;
                        int yi = j;
                        for (int l = 0; l < 8; l++) {
                            loop:
                            for (int k = 0; k < 8; k++) {
                                int xf = l;
                                int yf = k;
                                if (possibleMove(numToLetter(j) + "" + (8 - i) + " " + numToLetter(k) + (8 - l), "B")) {
                                    lastMove[0][0] = xi;
                                    lastMove[0][1] = yi;
                                    lastMove[1][0] = xf;
                                    lastMove[1][1] = yf;
                                   
                                    char temp = board[xi][yi]; 
                                    board[xi][yi] = '…';
                                    board[xf][yf] = temp;
                                    if (!check("B")) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    public static String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sb.append(String.valueOf(board[i][j]));
            }
        }
        return sb.toString();
    }
    public static boolean noPawns() {
        List<Character> blackPieces = new ArrayList<Character>();
        List<Character> whitePieces = new ArrayList<Character>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isWhitePiece(board[i][j]) && board[i][j] != '…') {
                    blackPieces.add(board[i][j]);
                }
                if (isWhitePiece(board[i][j])) {
                    whitePieces.add(board[i][j]);
                }
            }
        }   
        
        return !whitePieces.contains('♙') || !blackPieces.contains('♟');
    }
    public static boolean insufficientMaterial() { /////////UNFINISHED
        List<Character> blackPieces = new ArrayList<Character>();
        List<Character> whitePieces = new ArrayList<Character>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isWhitePiece(board[i][j]) && board[i][j] != '…') {
                    blackPieces.add(board[i][j]);
                }
                if (isWhitePiece(board[i][j])) {
                    whitePieces.add(board[i][j]);
                }
            }
        }   
        
        //king vs king
        if (whitePieces.size() == 1 && blackPieces.size() == 1) {
            if (whitePieces.get(0) == '♔' && blackPieces.get(0) == '♚') {
                return true;
            }
        }
        
        //king vs knight and king
        if ((whitePieces.size() == 2 && whitePieces.contains('♔') && whitePieces.contains('♘') && blackPieces.size() == 1 && blackPieces.contains('♚'))) {
            return true;
        }
        if ((blackPieces.size() == 2 && blackPieces.contains('♚') && blackPieces.contains('♞') && whitePieces.size() == 1 && whitePieces.contains('♔'))) {
            return true;
        }
        
        //King vs king and bishop
        if ((whitePieces.size() == 2 && whitePieces.contains('♔') && whitePieces.contains('♗') && blackPieces.size() == 1 && blackPieces.contains('♚'))) {
            return true;
        }
        if ((blackPieces.size() == 2 && blackPieces.contains('♚') && blackPieces.contains('♝') && whitePieces.size() == 1 && whitePieces.contains('♔'))) {
            return true;
        }
        
        
        
        //2 kings, 2 bishops, each bishop is the same color
        if (whitePieces.size() == 2 && blackPieces.size() == 2) {
            if (whitePieces.contains('♗') && blackPieces.contains('♝')) {
                if (sameColorBishop()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    public static boolean sameColorBishop() {
        int wBX = 0;
        int wBY = 0;
        int bBX = 0;
        int bBY = 0;
       
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == '♗') {
                    wBX = i;
                    wBY = j;
                }
                if (board[i][j] == '♝') {
                    bBX = i;
                    bBY = j;
                }
            }
        }
        
        //bishops same square of coords both aodd / both even
        if ((wBX % 2 == 0 && wBY % 2 == 0 || wBX % 2 == 1 && wBY % 2 == 1) && (bBX % 2 == 0 && bBY % 2 == 0 || bBX % 2 == 1 && bBY % 2 == 1)) {
            return true;
        }
        
        //if bishops one odd one even then they are 
        if (((wBX % 2 == 0 && wBY % 2 == 1) || (wBX % 2 == 1 && wBY % 2 == 0)) && ((bBX % 2 == 0 && bBY % 2 == 1) || (bBX % 2 == 1 && bBY % 2 == 0))) {
            return true;
        }
        return false;
    }
    public static void forfeit(String player) {
        printBoard();
        System.out.println("Game Over.");
        if (player.equals("W")) {
            System.out.println("White has lost to black by resignation.");
        }
        else {
            System.out.println("Black has to lost to white by resignation.");
        }
    }
    public static boolean check(String player) { //Not working, for some reason, keeps running over and over again
        //System.out.println("run");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (player.equals("W")) {
                    if (!isWhitePiece(board[i][j]) && board[i][j] != '…') {
                        //System.out.println(numToLetter(j) + "" + (8 - i) + " " + numToLetter(whiteY) + (8 - whiteX));
                        String mv = numToLetter(j) + "" + (8 - i) + " " + numToLetter(whiteY) + (8 - whiteX);
                        if (possibleMove(mv, "B")) {
                            return true;
                        }
                    }
                }
                //System.out.println(numToLetter(j) + "" + (8 - i) + " " + numToLetter(whiteY) + (8 - whiteX));
                if (player.equals("B")) {
                    if (isWhitePiece(board[i][j])) {
                        //System.out.println(board[i][j] + " " + i + " " + j);
                        String mv = numToLetter(j) + "" + (8 - i) + " " + numToLetter(blackY) + (8 - blackX);
                        //System.out.println(mv + " " + board[i][j]);
                        
                        if (possibleMove(mv, "W")) {
                            return true;
                        }
                    }
                }
            }
        }
        //System.out.println("run2");
        return false;
    }
    public static boolean possibleMove(String move, String player) {
        /*
         * Need to check the type of piece that you are moving, and based on each piece, come up with
         * Custom law of how the piece can move and whether or not it is possible to move like that
         * Then, create new method legalMove to check for check, checkmate, et cetera
         */
        
        int xi = 8 - Character.getNumericValue(move.charAt(1));
        int yi = letterToNum(move.charAt(0));
        int xf = 8 - Character.getNumericValue(move.charAt(4));
        int yf = letterToNum(move.charAt(3));
        
        //System.out.println(move);
        
        char toMove = board[xi][yi];
        
        if (toMove == '♙' || toMove == '♟') { //Done
            return pawnCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♘' || toMove == '♞') { //Done
            return knightCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♖' || toMove == '♜') { //done
            return rookCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♗' || toMove == '♝') { //done
            return bishopCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♚' || toMove == '♔') { //Just need to make sure king is not in check and is not castling thru check
            return kingCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♕' || toMove == '♛') { //done
            return queenCheck(xi, yi, xf, yf, player);
        }
        
        return false;
    }
    public static boolean queenCheck(int xi, int yi, int xf,int yf, String player) {
        if (!(bishopCheck(xi, yi, xf, yf, player) || rookCheck(xi, yi, xf, yf, player))) {
            errorMessage = "You cannot move you queen there.";
            return false;
        }
                
        return true;
    }
    public static boolean kingCheck(int xi, int yi, int xf,int yf, String player) {
        if (xi == xf + 1 && (yi == yf || yi - 1 == yf || yi + 1 == yf)) {
            if (player.equals("W")) {
                whiteX = xf;
                whiteY = yf;
                whiteKingNotMoved = false;
            }
            if (player.equals("B")) {
                blackX = xf;
                blackY = yf;
                blackKingNotMoved = false;
            }
            return true;
        }
        if (xi == xf && (yi - 1 == yf || yi + 1 == yf)) {
            if (player.equals("W")) {
                whiteX = xf;
                whiteY = yf;
                whiteKingNotMoved = false;
            }
            if (player.equals("B")) {
                blackX = xf;
                blackY = yf;
                blackKingNotMoved = false;
            }
            return true;
        }
        if (xi == xf - 1 && (yi == yf || yi - 1 == yf || yi + 1 == yf)) {
            if (player.equals("W")) {
                whiteX = xf;
                whiteY = yf;
                whiteKingNotMoved = false;
            }
            if (player.equals("B")) {
                blackX = xf;
                blackY = yf;
                blackKingNotMoved = false;
            }
            return true;
        }
        
        
        //castle
        
        /*
         * NEeD To MAKE SUre Without FORCING iNfinite LOOP CREATER SO THAT CANNOT CASTLE THROUGH CHECK OR CASTLE WHILE CHECKED
         */
        int tempoX = whiteX;
        int tempoY = whiteY;
        if (whiteCheck) {
            errorMessage = "You cannot castle through check.";
            return false;
        }
        if (player.equals("W") && whiteKingNotMoved && !whiteCheck) { // && !check("W")) { //Must be white, not moved, nt in check
            if (whiteKingRookNotMoved && xf == 7 && yf == 6) { //kingside castling //in between this is check not in check or castling out of check
                if (board[7][5] == '…') { //7 5, 7 6
                    whiteX = 7;
                    whiteY = 5;
                    if (check("W")) {
                        errorMessage = "You cannot castle through check.";
                        whiteX = tempoX;
                        whiteY = tempoY;
                        return false;
                    }
                    whiteX = 7;
                    whiteY = 6;
                    if (check("W")) {
                        errorMessage = "You cannot castle through check.";
                        whiteX = tempoX;
                        whiteY = tempoY;
                        return false;
                    }
                    System.out.println("Kingside Castling!");
                    board[7][5] = '♖';
                    board[7][7] = '…';
                    whiteX = xf;
                    whiteY = yf;
                    whiteX = tempoX;
                    whiteY = tempoY;
                    return true;
                }
            }
            if (whiteQueenRookNotMoved && xf == 7 && yf == 2) { //queenside castling 
                if (board[7][1] == '…' && board[7][3] == '…') {
                    whiteX = 7;
                    whiteY = 2;
                    if (check("W")) {
                        errorMessage = "You cannot castle through check.";
                        whiteX = tempoX;
                        whiteY = tempoY;
                        return false;
                    }
                    whiteX = 7;
                    whiteY = 3;
                    if (check("W")) {
                        errorMessage = "You cannot castle through check.";
                        whiteX = tempoX;
                        whiteY = tempoY;
                        return false;
                    }
                    System.out.println("Queenside Castling!");
                    board[7][3] = '♖';
                    board[7][0] = '…';
                    whiteX = xf;
                    whiteY = yf;
                    whiteX = tempoX;
                    whiteY = tempoY;
                    return true;
                }
            }
        }
        tempoX = blackX;
        tempoY = blackY;
        if (blackCheck) {
            errorMessage = "You cannot castle through check.";
            return false;
        }
        if (player.equals("B") && blackKingNotMoved && !blackCheck) { // && !check("B")) {
            if (blackKingRookNotMoved && xf == 0 && yf == 6) { //kingside castling //in between this is check not in check or castling out of check
                if (board[0][5] == '…') {
                    blackX = 0;
                    blackY = 5;
                    if (check("B")) {
                        errorMessage = "You cannot castle through check.";
                        blackX = tempoX;
                        blackY = tempoY;
                        return false;
                    }
                    blackX = 0;
                    blackY = 6;
                    if (check("B")) {
                        errorMessage = "You cannot castle through check.";
                        blackX = tempoX;
                        blackY = tempoY;
                        return false;
                    }
                    
                    System.out.println("Kingside Castling!");
                    board[0][5] = '♖';
                    board[0][7] = '…';
                    blackX = xf;
                    blackY = yf;
                    blackX = tempoX;
                    blackY = tempoY;
                    return true;
                }
            }
            if (blackQueenRookNotMoved && xf == 0 && yf == 2) { //queenside castling
                if (board[0][1] == '…' && board[0][3] == '…') {
                    blackX = 0;
                    blackY = 2;
                    if (check("B")) {
                        errorMessage = "You cannot castle through check.";
                        blackX = tempoX;
                        blackY = tempoY;
                        return false;
                    }
                    blackX = 0;
                    blackY = 3;
                    if (check("B")) {
                        errorMessage = "You cannot castle through check.";
                        blackX = tempoX;
                        blackY = tempoY;
                        return false;
                    }
                    System.out.println("Queenside Castling!");
                    board[0][3] = '♖';
                    board[0][0] = '…';
                    blackX = xf;
                    blackY = yf;
                    blackX = tempoX;
                    blackY = tempoY;
                    return true;
                }
            }
        }
        
        //System.out.println("You cannot move your king there.");
        errorMessage = "You cannot move your king there.";
        return false;
    }
    public static boolean bishopCheck(int xi, int yi, int xf,int yf, String player) { 
        int diff = Math.abs(xi - xf);
        if (xi == xf + diff) { //Moving up
            if (yi == yf - diff) { //To the right
                for (int i = xi - 1, j = yi + 1; i > xf; i--, j++) {
                    if (board[i][j] != '…') {
                        errorMessage = "You cannot move your bishop there.";
                        return false;
                    }
                }
                return true;
            }
            if (yi == yf + diff) { //To the left
                for (int i = xi - 1, j = yi - 1; i > xf; i--, j--) {
                    if (board[i][j] != '…') {
                        errorMessage = "You cannot move your bishop there.";
                        return false;
                    }
                }
                return true;
            }
        }
        if (xi == xf - diff) { //Moving down
            if (yi == yf - diff) { //moving right
                for (int i = xi + 1, j = yi + 1; i < xf; i++, j++) {
                    if (board[i][j] != '…') {
                        errorMessage = "You cannot move your bishop there.";
                        return false;
                    }
                }
                return true;
            }
            if (yi == yf + diff) { //Moving left
                for (int i = xi + 1, j = yi - 1; i < xf; i++, j--) {
                    if (board[i][j] != '…') {
                        errorMessage = "You cannot move your bishop there.";
                        return false;
                    }
                }
                return true;
            }
        }
        errorMessage = "You cannot move your bishop there.";
        return false;
    }
    public static boolean rookCheck(int xi, int yi, int xf,int yf, String player) { 
        if (xi == xf) {
            if (yi > yf) { 
                for (int i = yf + 1; i < yi; i++) {
                    if (board[xi][i] != '…') {
                        errorMessage = "You cannot move your rook there.";
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
            if (yi < yf) { 
                for (int i = yi + 1; i < yf; i++) {
                    if (board[xi][i] != '…') {
                        errorMessage = "You cannot move your rook there.";
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
        }
        
        if (yi == yf) {
            if (xi > xf) { 
                for (int i = xf + 1; i < xi; i++) {
                    if (board[i][yi] != '…') {
                        errorMessage = "You cannot move your rook there.";
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
            if (xi < xf) { 
                for (int i = xi + 1; i < xf; i++) {
                    if (board[i][yi] != '…') {
                        errorMessage = "You cannot move your rook there.";
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
        }
        errorMessage = "You cannot move your rook there.";
        return false;
    }
    public static boolean knightCheck(int xi, int yi, int xf,int yf, String player) { 
        if (xf == xi - 2 && (yf == yi - 1 || yf == yi + 1)) {
            return true;
        }
        if (xf == xi + 2 && (yf == yi - 1 || yf == yi + 1)) {
            return true;
        }
        if (yf == yi + 2 && (xf == xi + 1 || xf == xi - 1)) {
            return true;
        }
        if (yf == yi - 2 && (xf == xi + 1 || xf == xi - 1)) {
            return true;
        }
        
        errorMessage = "You cannot move your knight there.\nThe knight may only move in an L-Shape";
        return false;
    }
    public static boolean pawnCheck(int xi, int yi, int xf,int yf, String player) { 
        /*
         * Checks to See: Pawn can move forward 1 without touching another piece
         * Pawn can move forward two without touching another piece or being blocked
         * pawn can capture diagonally
         * En Passant
         */
        if (player.equals("W") && yi == yf && xi == xf + 1 && board[xf][yf] == '…') { //Next two is if pawn moves one
            return true;
        }
        if (player.equals("B") && yi == yf && xi == xf - 1 && board[xf][yf] == '…') {
            return true;
        }
        
        if (player.equals("W") && xi == 6) { //if pawn has not yet moved, pawn can move 2
            if (yi == yf && xi == xf + 2 && board[xi - 1][yi] == '…' && board[xf][yf] == '…') {
                return true;
            }
        }
        if (player.equals("B") && xi == 1) {
            if (yi == yf && xi == xf - 2 && board[xi + 1][yi] == '…' && board[xf][yf] == '…') {
                return true;
            }
        }
        char piece = board[xf][yf];
        if (player.equals("W") && !isWhitePiece(piece) && piece != '…') { //cheking for diagonal capture
            
            if (xi - 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { 
                return true;
            }
        }
        if (player.equals("B") && isWhitePiece(piece)) {
            if (xi + 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { 
                return true;
            }
        }
        
        
        //EN PASSANT
        if (player.equals("W") && board[xf][yf] == '…') {
            if (xi - 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { //If pawn is moving diagonal
                if (board[lastMove[1][0]][lastMove[1][1]] == '♟' && lastMove[1][0] == 3) {
                    System.out.println("En Passant!~~");
                    board[lastMove[1][0]][lastMove[1][1]] = '…';
                    return true;
                }
            }
        }
        if (player.equals("B") && board[xf][yf] == '…') {
            if (xi + 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { //If pawn is moving diagonal
                if (board[lastMove[1][0]][lastMove[1][1]] == '♙' && lastMove[1][0] == 4) {
                    System.out.println("En Passant!~~");
                    board[lastMove[1][0]][lastMove[1][1]] = '…';
                    return true;
                }
            }
        }
        errorMessage = "You cannot move your pawn there.";
        return false;
    }
    public static boolean isWhitePiece(char toCheck) {
        for (char c : whiteSet) {
            if (toCheck == c) {
                return true;
            }
        }
        return false;
    }
    public static boolean validInput(String move, String player) {
        //Checks that input move is:
        /*
         * the right length
         * has number in range for coords
         * has valid letters in input
         * not the same input as destination
         * checking you are moviong YOUR OWN piece to a place ON THE BOARD
         * DESTINATION IS NOT OWN PIECE
         */
         if (move.length() != 5) { //Not right length
            errorMessage = "You did not input in the following format:\nletter+number_letter+number";
            return false;
        }
        try {
            if ((Integer.parseInt("" + move.charAt(4)) < 1 || Integer.parseInt("" + move.charAt(4)) > 8) || (Integer.parseInt("" + move.charAt(1)) < 1 || Integer.parseInt("" + move.charAt(1)) > 8)) {
                errorMessage = "The number coordinate exceeded the limits of the board.\nIt must be between 1 and 8, inclusive.";
                
                return false;
            }
        } catch (NumberFormatException e) {
            errorMessage = "There were not 2 number coordinates.";
            return false;
        }
        boolean validLetter = false;
        for (char i : letterKey) {
            if (i == move.charAt(0)) {
                validLetter = true;
            }
        }
        if (!validLetter) {
            errorMessage = "The letter was not valid.\nIt must be a-h, lowercase.";
            return false;
        }
        validLetter = false;
        for (char i : letterKey) {
            if (i == move.charAt(3)) {
                validLetter = true;
            }
        }
        if (!validLetter) {
            errorMessage = "The letter was not valid.\nIt must be a-h, lowercase.";
            return false;
        }
        if (move.substring(0, 2).equals(move.substring(3, 5))) {
            errorMessage = "The destination coordinates were the same as the origin coordinates.";
            return false;
        }
        if (!validPiece(move, player)) {
            errorMessage = "The piece you are moving is not your piece or does not exist.\nOr, you are moving your piece to an invalid location";
            return false;
        }
        if (!validDestination(move, player)) {
            errorMessage = "You cannot capture your own piece.";
            return false;
        }
        
        return true;
    }
    public static boolean validDestination(String move, String player) {
        char toCheck = board[8 - Character.getNumericValue(move.charAt(4))][letterToNum(move.charAt(3))];
        if (player.equals("W")) {
            for (char c : whiteSet) {
                if (toCheck == c) {
                    return false;
                }
            }
            return true;
        }
        if (player.equals("B")) {
            for (char c : blackSet) {
                if (toCheck == c) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public static void printBoard() {
        for (int i = 0; i < board.length; i++) {
            System.out.print((8 - i) + " ║");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println("—╚═══════════════==");
        //System.out.println("—— a   b   c    d    e    f    g    h");
        System.out.println("—— a… b… c… d… e… f… g… h");
    }
     public static boolean validPiece(String move, String player) { //check if what your moving is a piece of yours
        int ind1 = Integer.parseInt(move.substring(1, 2));
        int ind2 = letterToNum(move.charAt(0));
        
        
        if (player.equals("B")) {
            char toCheck = board[8 - ind1][ind2];
            boolean cont = false;
            for (char c : blackSet) {
                if (c == toCheck) {
                    return true;
                }
            }
            return false;
        }
        if (player.equals("W")) {
            char toCheck = board[8 - ind1][ind2];
            boolean cont = false;
            for (char c : whiteSet) {
                if (c == toCheck) {
                    return true;
                }
            }
            return false;
        }
        
        int in1 = Integer.parseInt(move.substring(4, 5));
        int in2 = letterToNum(move.charAt(3));
        if (player.equals("B")) {
            char toCheck = board[8 - in1][in2];
            boolean cont = false;
            for (char c : blackSet) {
                if (c == toCheck) {
                    return false;
                }
            }
            return true;
        }
        if (player.equals("W")) {
            char toCheck = board[8 - in1][in2];
            boolean cont = false;
            for (char c : whiteSet) {
                if (c == toCheck) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    public static char numToLetter(int num) {
        return letterKey[num];
    }
    public static int letterToNum(char letter) {
        for (int i = 0; i < letterKey.length; i++) {
            if (letter == letterKey[i]) {
                return i;
            }
        }   
        return -1;
    }
    public static char[][] copyBoard() {
        char[][] retBoard = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                retBoard[i][j] = board[i][j];
            }
        }
        return retBoard;
    }
}

    
    

/*
 * 
8 ║♜ ♞ ♝ ♛ ♚ ♝ ♞ ♜
7 ║♟ ♟ ♟ ♟ ♟ ♟ ♟ ♟
6 ║… … … … … … … …
5 ║… … … … … … … …
4 ║… … … … … … … …
3 ║… … ♘ … … … … …
2 ║♙ ♙ ♙ ♙ ♙ ♙ ♙ ♙
1 ║♖ … ♗ ♕ ♔ ♗ ♘ ♖
—╚═══════════════==
——a   b   c   d   e   f   g   h

 */


////////////////////////////////////////////////////////[[[[[[[[[[[[[[
/*import java.util.*;

/*
Checklist
 * Valid Input ---------✓
 *      -Follows Format Letter+Number_Letter+Number, in range of board a-h and 1-8 ✓
 *      -The Move does not capture own piece ✓
 *      -Destination is not the same as origin ✓
 *      -Moving own piece ✓
 * 
 * Legal (Possible Move) ----------
 *      -Pawn ✓
 *          /Push Forward 1 ✓
 *          /Capture one diagonally ✓
 *          /Promotion ✓
 *          /Push Forward 2 when first moved ✓
 *          /En Passant ✓
 *          
 *       -Rook ✓
 *          /Move + capture + pressure horizontally unimpeded ✓
 *          /Move + capture + pressure  vertically unimpeded ✓
 *          
 *       -Knight ✓
 *          /Move + capture + pressure + jump  in L Shape ✓
 *          
 *       -Bishop ✓
 *          /Move + capture + pressure  diagonally unimpeded ✓
 *          
 *       -King
 *          /Kingside/Queenside Castling
 *              {King moves 2 toward rook, rook hops 1 over king ✓
 *              {King has not moved, rook has not moved ✓
 *              {will not castle through or in check
 *          /Move 1 block any direction unimpeded ✓
 *          
 *       -Queen ✓ 
 *                      ~~~~~~~~~~~~~~~~Slight Bug- When you incorrectly move the queen, prints 
 *                  "You may not move your piece there" twice due to the fact that returns, for
 *                  sake of brevity, bishopCheck or rookCheck, resulting in double error message in
 *                  event of an error.
 *                  
 *          /Move + capture + pressure horizontally unimpeded ✓
 *          /Move + capture + pressure vertically unimpeded ✓
 *          /Move + capture + pressure diagonally unimpeded ✓
 *          
 *      -Move Does not Place King in Check  (legalMove)        
 *          
 * Mechanics
 *      -Check, Force King Protection -> to check fo rchecks, just use the methods exz: pawnCheck or KnighCheck with input of piece rurent location with king current location
 *      -Checkmate, End Game
 *      -Stalemate, end game
 *          /Player cannot legally move but is not in checkmate
 *          /Insufficient Material
 *          /50 Move Rule
 *          /Threefold Repetitio
 *      -Forfeit/Resign, end game          
 *                
 */



/*public class ProgramFinalDraft {
    public static char[][] board = {
            {'♜', '♞', '♝', '♛', '♚', '♝', '♞', '♜'},
            {'♟', '♟', '♟', '♟', '♟', '♟', '♟', '♟'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'…', '…', '…', '…', '…', '…', '…', '…'},
            {'♙', '♙', '♙', '♙', '♙', '♙', '♙', '♙'},
            {'♖', '♘', '♗', '♕', '♔', '♗', '♘', '♖'},
    };
    public static char[] whiteSet = {'♙', '♙', '♙', '♙', '♙', '♙', '♙', '♙', '♖', '♘', '♗', '♕', '♔', '♗', '♘', '♖'};
    
    public static char[] blackSet = {'♜', '♞', '♝', '♛', '♚', '♝', '♞', '♜', '♟', '♟', '♟', '♟', '♟', '♟', '♟', '♟'};

    public static char[] letterKey = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    
    public static int[][] lastMove = new int[2][2];
    
    public static boolean whiteKingNotMoved = true;
    public static boolean blackKingNotMoved = true;
    
    public static boolean whiteKingRookNotMoved = true;
    public static boolean whiteQueenRookNotMoved = true;
    public static boolean blackQueenRookNotMoved = true;
    public static boolean blackKingRookNotMoved = true;
    
    //Coordinates of the King
    public static int blackX = 0;
    public static int blackY = 4;
    public static int whiteX = 7;
    public static int whiteY = 4;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Chess!");
        
        
        gameLoop:
        while (true) { //Turn Loop: one black, one white
            //In this loop: check for check, checkmate, stalemate, insufficient material, 50 move, threefold
            printBoard();
            
            
            while (true) { //White input decision loop
                System.out.println("---------------");
                System.out.println("White to Move.");
                System.out.println("Enter a the coordinates of the piece you want to move and the coordinate of the destination.");
                System.out.println("Lowercase letter and number. (ex: a2, a3)");
                String inp = scanner.nextLine();
                if (validInput(inp, "W") && possibleMove(inp, "W")) { //&&noCheck/Checkmate(inp, "W")
                    int xi = 8 - Character.getNumericValue(inp.charAt(1));
                    int yi = letterToNum(inp.charAt(0));
                    int xf = 8 - Character.getNumericValue(inp.charAt(4));
                    int yf = letterToNum(inp.charAt(3));
                    
                    lastMove[0][0] = xi;
                    lastMove[0][1] = yi;
                    lastMove[1][0] = xf;
                    lastMove[1][1] = yf;
                    
                    char temp = board[xi][yi]; 
                    board[xi][yi] = '…';
                    board[xf][yf] = temp;
                    
                    if (xf == 0 && temp == '♙') { //PROMOTION, // ***** EDIT, xi == 7 for black move, also change piece to promote to black
                        printBoard();
                        System.out.println("You may promote your pawn.");
                        System.out.println("Choose queen, rook, knight, or bishop.");
                        System.out.println("q/r/k/b");
                        while (true) {
                            char input = scanner.nextLine().charAt(0);
                            if (input == 'q') {
                                board[xf][yf] = '♕';
                                break;
                            }
                            if (input == 'r') {
                                board[xf][yf] = '♖';
                                break;
                            }
                            if (input == 'k') {
                                board[xf][yf] = '♘';
                                break;
                            }
                            if (input == 'b') {
                                board[xf][yf] = '♗';
                                break;
                            }
                            else {
                                System.out.println("Invalid input. Enter q/r/k/b");
                            }
                        }
                    }
                    break;
                }
            }
            printBoard();
            if (check("W")) {
                System.out.println("CHECK");
            }
            
            
            while (true) { //Black input decision loop
                System.out.println("---------------");
                System.out.println("Black to Move.");
                System.out.println("Enter a the coordinates of the piece you want to move and the coordinate of the destination.");
                System.out.println("Lowercase letter and number. (ex: a2, a3)");
                String inp = scanner.nextLine();
                if (validInput(inp, "B") && possibleMove(inp, "B")) { //&&noCheck/Checkmate(inp, "W")
                    int xi = 8 - Character.getNumericValue(inp.charAt(1));
                    int yi = letterToNum(inp.charAt(0));
                    int xf = 8 - Character.getNumericValue(inp.charAt(4));
                    int yf = letterToNum(inp.charAt(3));
                    
                    lastMove[0][0] = xi;
                    lastMove[0][1] = yi;
                    lastMove[1][0] = xf;
                    lastMove[1][1] = yf;
                   
                    char temp = board[xi][yi]; 
                    board[xi][yi] = '…';
                    board[xf][yf] = temp;
                    
                    if (xf == 7 && temp == '♟') { //PROMOTION, // ***** EDIT, xi == 7 for black move, also change piece to promote to black
                        printBoard();
                        System.out.println("You may promote your pawn.");
                        System.out.println("Choose queen, rook, knight, or bishop.");
                        System.out.println("q/r/k/b");
                        while (true) {
                            char input = scanner.nextLine().charAt(0);
                            if (input == 'q') {
                                board[xf][yf] = '♛';
                                break;
                            }
                            if (input == 'r') {
                                board[xf][yf] = '♜';
                                break;
                            }
                            if (input == 'k') {
                                board[xf][yf] = '♞';
                                break;
                            }
                            if (input == 'b') {
                                board[xf][yf] = '♝';
                                break;
                            }
                            else {
                                System.out.println("Invalid input. Enter q/r/k/b");
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    public static boolean check(String player) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (player.equals("W")) {
                    if (!isWhitePiece(board[i][j]) && board[i][j] != '…') {
                        if (possibleMove(numToLetter(j) + "" + (8 - i) + " " + numToLetter(whiteY) + (8 - whiteX), "W")) {
                            System.out.println(numToLetter(j) + "" + (8 - i) + " " + numToLetter(whiteY) + (8 - whiteX));
                            return true;
                        }
                    }
                }
                if (player.equals("B")) {
                    if (isWhitePiece(board[i][j])) {
                        if (possibleMove(numToLetter(j) + "" + (8 - i) + " " + numToLetter(blackY) + (8 - blackX), "B")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public static boolean possibleMove(String move, String player) {
        /*
         * Need to check the type of piece that you are moving, and based on each piece, come up with
         * Custom law of how the piece can move and whether or not it is possible to move like that
         * Then, create new method legalMove to check for check, checkmate, et cetera
         */
        
        /*int xi = 8 - Character.getNumericValue(move.charAt(1));
        int yi = letterToNum(move.charAt(0));
        int xf = 8 - Character.getNumericValue(move.charAt(4));
        int yf = letterToNum(move.charAt(3));
        
        //System.out.println(move);
        
        char toMove = board[xi][yi];
        
        if (toMove == '♙' || toMove == '♟') { //Done
            return pawnCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♘' || toMove == '♞') { //Done
            return knightCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♖' || toMove == '♜') { //done
            return rookCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♗' || toMove == '♝') { //done
            return bishopCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♚' || toMove == '♔') { //Just need to make sure king is not in check and is not castling thru check
            return kingCheck(xi, yi, xf, yf, player);
        }
        if (toMove == '♕' || toMove == '♛') { //done
            return queenCheck(xi, yi, xf, yf, player);
        }
        
        return false;
    }
    public static boolean queenCheck(int xi, int yi, int xf,int yf, String player) {
        return bishopCheck(xi, yi, xf, yf, player) || rookCheck(xi, yi, xf, yf, player);
    }
    public static boolean kingCheck(int xi, int yi, int xf,int yf, String player) {
        if (xi == xf + 1 && (yi == yf || yi - 1 == yf || yi + 1 == yf)) {
            if (player.equals("W")) {
                whiteX = xf;
                whiteY = yf;
                whiteKingNotMoved = false;
            }
            if (player.equals("B")) {
                blackX = xf;
                blackY = yf;
                blackKingNotMoved = false;
            }
            return true;
        }
        if (xi == xf && (yi - 1 == yf || yi + 1 == yf)) {
            if (player.equals("W")) {
                whiteX = xf;
                whiteY = yf;
                whiteKingNotMoved = false;
            }
            if (player.equals("B")) {
                blackX = xf;
                blackY = yf;
                blackKingNotMoved = false;
            }
            return true;
        }
        if (xi == xf - 1 && (yi == yf || yi - 1 == yf || yi + 1 == yf)) {
            if (player.equals("W")) {
                whiteX = xf;
                whiteY = yf;
                whiteKingNotMoved = false;
            }
            if (player.equals("B")) {
                blackX = xf;
                blackY = yf;
                blackKingNotMoved = false;
            }
            return true;
        }
        
        //castle incomplete- need to check to see ifyou are castling in check and through check
        if (player.equals("W") && whiteKingNotMoved) {
            if (whiteKingRookNotMoved && xf == 7 && yf == 6) { //kingside castling //in between this is check not in check or castling out of check
                if (board[7][5] == '…') {
                    System.out.println("Kingside Castling!");
                    board[7][5] = '♖';
                    board[7][7] = '…';
                    whiteX = xf;
                    whiteY = yf;
                    return true;
                }
            }
            if (whiteQueenRookNotMoved && xf == 7 && yf == 2) { //queenside castling
                if (board[7][1] == '…' && board[7][3] == '…') {
                    System.out.println("Queenside Castling!");
                    board[7][3] = '♖';
                    board[7][0] = '…';
                    whiteX = xf;
                    whiteY = yf;
                    return true;
                }
            }
        }
        if (player.equals("B") && blackKingNotMoved) {
            if (blackKingRookNotMoved && xf == 0 && yf == 6) { //kingside castling //in between this is check not in check or castling out of check
                if (board[0][5] == '…') {
                    System.out.println("Kingside Castling!");
                    board[0][5] = '♖';
                    board[0][7] = '…';
                    blackX = xf;
                    blackY = yf;
                    return true;
                }
            }
            if (blackQueenRookNotMoved && xf == 0 && yf == 2) { //queenside castling
                if (board[0][1] == '…' && board[0][3] == '…') {
                    System.out.println("Queenside Castling!");
                    board[0][3] = '♖';
                    board[0][0] = '…';
                    blackX = xf;
                    blackY = yf;
                    return true;
                }
            }
        }
        
        System.out.println("You cannot move your king there.");
        return false;
    }
    public static boolean bishopCheck(int xi, int yi, int xf,int yf, String player) { 
        int diff = Math.abs(xi - xf);
        if (xi == xf + diff) { //Moving up
            if (yi == yf - diff) { //To the right
                for (int i = xi - 1, j = yi + 1; i > xf; i--, j++) {
                    if (board[i][j] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                return true;
            }
            if (yi == yf + diff) { //To the left
                for (int i = xi - 1, j = yi - 1; i > xf; i--, j--) {
                    if (board[i][j] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                return true;
            }
        }
        if (xi == xf - diff) { //Moving down
            if (yi == yf - diff) { //moving right
                for (int i = xi + 1, j = yi - 1; i < xf; i++, j--) {
                    if (board[i][j] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                return true;
            }
            if (yi == yf + diff) { //Moving left
                for (int i = xi + 1, j = yi + 1; i < xf; i++, j++) {
                    if (board[i][j] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                return true;
            }
        }
        System.out.println("You cannot move your piece there.");
        return false;
    }
    public static boolean rookCheck(int xi, int yi, int xf,int yf, String player) { 
        if (xi == xf) {
            if (yi > yf) { 
                for (int i = yf + 1; i < yi; i++) {
                    if (board[xi][i] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
            if (yi < yf) { 
                for (int i = yi + 1; i < yf; i++) {
                    if (board[xi][i] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
        }
        
        if (yi == yf) {
            if (xi > xf) { 
                for (int i = xf + 1; i < xi; i++) {
                    if (board[i][yi] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
            if (xi < xf) { 
                for (int i = xi + 1; i < xf; i++) {
                    if (board[i][yi] != '…') {
                        System.out.println("You cannot move your piece there.");
                        return false;
                    }
                }
                if (xi == 0 && yi == 0) {
                    blackQueenRookNotMoved = false;
                }
                if (xi == 0 && yi == 7) {
                    blackKingRookNotMoved = false;
                }
                if (xi == 7 && yi == 0) {
                    whiteQueenRookNotMoved = false;
                }
                if (xi == 7 && yi == 7) {
                    whiteKingRookNotMoved = false;
                }
                return true;
            }
        }
        
        System.out.println("You cannot move your piece there.");
        return false;
    }
    public static boolean knightCheck(int xi, int yi, int xf,int yf, String player) { 
        if (xf == xi - 2 && (yf == yi - 1 || yf == yi + 1)) {
            return true;
        }
        if (xf == xi + 2 && (yf == yi - 1 || yf == yi + 1)) {
            return true;
        }
        if (yf == yi + 2 && (xf == xi + 1 || xf == xi - 1)) {
            return true;
        }
        if (yf == yi - 2 && (xf == xi + 1 || xf == xi - 1)) {
            return true;
        }
        
        System.out.println("You cannot move your knight there.");
        System.out.println("The knight may only move in an L- shape.");
        return false;
    }
    public static boolean pawnCheck(int xi, int yi, int xf,int yf, String player) { 
        /*
         * Checks to See: Pawn can move forward 1 without touching another piece
         * Pawn can move forward two without touching another piece or being blocked
         * pawn can capture diagonally
         * En Passant
         */
        /*if (player.equals("W") && yi == yf && xi == xf + 1 && board[xf][yf] == '…') { //Next two is if pawn moves one
            return true;
        }
        if (player.equals("B") && yi == yf && xi == xf - 1 && board[xf][yf] == '…') {
            return true;
        }
        
        if (player.equals("W") && xi == 6) { //if pawn has not yet moved, pawn can move 2
            if (yi == yf && xi == xf + 2 && board[xi - 1][yi] == '…' && board[xf][yf] == '…') {
                return true;
            }
        }
        if (player.equals("B") && xi == 1) {
            if (yi == yf && xi == xf - 2 && board[xi + 1][yi] == '…' && board[xf][yf] == '…') {
                return true;
            }
        }
        char piece = board[xf][yf];
        if (player.equals("W") && !isWhitePiece(piece) && piece != '…') { //cheking for diagonal capture
            
            if (xi - 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { 
                return true;
            }
        }
        if (player.equals("B") && isWhitePiece(piece)) {
            if (xi + 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { 
                return true;
            }
        }
        
        
        //EN PASSANT
        if (player.equals("W") && board[xf][yf] == '…') {
            if (xi - 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { //If pawn is moving diagonal
                if (board[lastMove[1][0]][lastMove[1][1]] == '♟' && lastMove[1][0] == 3) {
                    System.out.println("En Passant!~~");
                    board[lastMove[1][0]][lastMove[1][1]] = '…';
                    return true;
                }
            }
        }
        if (player.equals("B") && board[xf][yf] == '…') {
            if (xi + 1 == xf && (yi + 1 == yf || yi - 1 == yf)) { //If pawn is moving diagonal
                if (board[lastMove[1][0]][lastMove[1][1]] == '♙' && lastMove[1][0] == 4) {
                    System.out.println("En Passant!~~");
                    board[lastMove[1][0]][lastMove[1][1]] = '…';
                    return true;
                }
            }
        }
        System.out.println("You cannot move your pawn there.");
        return false;
    }
    public static boolean isWhitePiece(char toCheck) {
        for (char c : whiteSet) {
            if (toCheck == c) {
                return true;
            }
        }
        return false;
    }
    public static boolean validInput(String move, String player) {
        //Checks that input move is:
        /*
         * the right length
         * has number in range for coords
         * has valid letters in input
         * not the same input as destination
         * checking you are moviong YOUR OWN piece to a place ON THE BOARD
         * DESTINATION IS NOT OWN PIECE
         */
         /*if (move.length() != 5) { //Not right length
            System.out.println("You did not input in the following format: ");
            System.out.println("letter+number_letter+number");
            return false;
        }
        try {
            if ((Integer.parseInt("" + move.charAt(4)) < 1 || Integer.parseInt("" + move.charAt(4)) > 8) || (Integer.parseInt("" + move.charAt(1)) < 1 || Integer.parseInt("" + move.charAt(1)) > 8)) {
                System.out.println("The number coordinate exceeded the limits of the board.");
                System.out.println("It must be between 1 and 8, inclusive.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("There were not 2 number coordinates.");
            return false;
        }
        boolean validLetter = false;
        for (char i : letterKey) {
            if (i == move.charAt(0)) {
                validLetter = true;
            }
        }
        if (!validLetter) {
            System.out.println("The letter was not valid.");
            System.out.println("It must be between a-h, lowercase.");
            return false;
        }
        validLetter = false;
        for (char i : letterKey) {
            if (i == move.charAt(3)) {
                validLetter = true;
            }
        }
        if (!validLetter) {
            System.out.println("The letter was not valid.");
            System.out.println("It must be between a-h, lowercase.");
            return false;
        }
        if (move.substring(0, 2).equals(move.substring(3, 5))) {
            System.out.println("The destination is the same as the current piece location.");
            return false;
        }
        if (!validPiece(move, player)) {
            System.out.println("The piece you are moving is not your piece or does not exist.");
            System.out.println("Or, you are moving your piece to an invalid location");
            return false;
        }
        if (!validDestination(move, player)) {
            System.out.println("You cannot capture your own piece.");
            return false;
        }
        
        return true;
    }
    public static boolean validDestination(String move, String player) {
        char toCheck = board[8 - Character.getNumericValue(move.charAt(4))][letterToNum(move.charAt(3))];
        if (player.equals("W")) {
            for (char c : whiteSet) {
                if (toCheck == c) {
                    return false;
                }
            }
            return true;
        }
        if (player.equals("B")) {
            for (char c : blackSet) {
                if (toCheck == c) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public static void printBoard() {
        for (int i = 0; i < board.length; i++) {
            System.out.print((8 - i) + " ║");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println("—╚═══════════════==");
        //System.out.println("—— a   b   c    d    e    f    g    h");
        System.out.println("—— a… b… c… d… e… f… g… h");
    }
     public static boolean validPiece(String move, String player) { //check if what your moving is a piece of yours
        int ind1 = Integer.parseInt(move.substring(1, 2));
        int ind2 = letterToNum(move.charAt(0));
        
        
        if (player.equals("B")) {
            char toCheck = board[8 - ind1][ind2];
            boolean cont = false;
            for (char c : blackSet) {
                if (c == toCheck) {
                    return true;
                }
            }
            return false;
        }
        if (player.equals("W")) {
            char toCheck = board[8 - ind1][ind2];
            boolean cont = false;
            for (char c : whiteSet) {
                if (c == toCheck) {
                    return true;
                }
            }
            return false;
        }
        
        int in1 = Integer.parseInt(move.substring(4, 5));
        int in2 = letterToNum(move.charAt(3));
        if (player.equals("B")) {
            char toCheck = board[8 - in1][in2];
            boolean cont = false;
            for (char c : blackSet) {
                if (c == toCheck) {
                    return false;
                }
            }
            return true;
        }
        if (player.equals("W")) {
            char toCheck = board[8 - in1][in2];
            boolean cont = false;
            for (char c : whiteSet) {
                if (c == toCheck) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    public static char numToLetter(int num) {
        return letterKey[num];
    }
    public static int letterToNum(char letter) {
        for (int i = 0; i < letterKey.length; i++) {
            if (letter == letterKey[i]) {
                return i;
            }
        }   
        return -1;
    }
}

    
    

/*
 * 
8 ║♜ ♞ ♝ ♛ ♚ ♝ ♞ ♜
7 ║♟ ♟ ♟ ♟ ♟ ♟ ♟ ♟
6 ║… … … … … … … …
5 ║… … … … … … … …
4 ║… … … … … … … …
3 ║… … ♘ … … … … …
2 ║♙ ♙ ♙ ♙ ♙ ♙ ♙ ♙
1 ║♖ … ♗ ♕ ♔ ♗ ♘ ♖
—╚═══════════════==
——a   b   c   d   e   f   g   h

 */