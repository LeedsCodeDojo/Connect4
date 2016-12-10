package bot;

public class Utils {

    public static String prettyPrintBoard(int[][] board) {
        String boardStr = "";
        for (int col = board[0].length-1; col >= 0; col--) {
            for (int row = 0; row < board.length; row++) {
                String content = " ";
                switch(board[row][col]){
                    case CellContent.Red:
                        content = "R"; break;
                    case CellContent.Yellow:
                        content = "Y"; break;
                }
                boardStr += String.format("[%s] ", content);
            }
            boardStr += System.lineSeparator();
        }
        boardStr += "(1) (2) (3) (4) (5) (6) (7) ";
        return boardStr;
    }
    
    public static String winningString(String playerId, String thisPlayerId) {
        if (playerId.equals(thisPlayerId)) {
            return "Your bot won ≡≡≡≡≡ ¯\\_(ツ)";   
        } else {
             return "Your bot lost (╯°□°）╯︵ ≡≡≡≡≡";
        }
    }
}
