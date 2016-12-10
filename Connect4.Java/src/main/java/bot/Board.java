package bot;

import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Random;

public class Board {

    private Api api;
    private String Password;

    public Board(Api api, String password) {
        this.api = api;
        this.Password = password;
    }
    
    /**
     * REPLACE WITH YOUR OWN ALGORITHM HERE!
     * 
     * @param cells
     * @param playerId
     * @param gamestate
     * @throws UnirestException 
     */
    public void addPiece(int[][] cells, String playerId, GameState gamestate) throws UnirestException {

        String player = (gamestate == GameState.RED_TO_PLAY) ? "Red" : "Yellow";
        //Generate random column to add piece
        Random random = new Random();
        boolean takenTurn = false;

        while (!takenTurn) {
            int randomColumn = random.nextInt(7);
            
            int[] selectedColoumn = cells[randomColumn];
            //Check if column is full, if true generate another column to add
            if (selectedColoumn[selectedColoumn.length - 1] == CellContent.Empty) {
                api.makeMove(playerId, randomColumn, Password);
                takenTurn = true;
                System.out.printf(">>> GO >>> %s Player placed in col: %d%n", player, randomColumn + 1);
            }
        }
    }

}
