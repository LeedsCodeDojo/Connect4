package bot;

import bot.models.GameStateResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Game {

    private static final String TEAM_NAME = "new game";
    private static final String PASSWORD = "123456789";
    private static boolean newGame = true;
                        
    public static void main(String[] args) {
        Api api = new Api();

        try {
            String playerId = api.register(TEAM_NAME, PASSWORD);
            System.out.println(String.format("Unique PlayerId: %s", playerId));
            
            if (newGame) {
                System.out.println(api.newGame(playerId));
            }

            Board board = new Board(api, PASSWORD);

            boolean isGameComplete = false;
            while (!isGameComplete) {
                GameStateResponse gameStateResp = api.gameState(playerId);
                GameState gameState = gameStateResp.getCurrentState();
                String yellowPlayerId = gameStateResp.getYellowPlayerID();
                String redPlayerId = gameStateResp.getRedPlayerID();
                printState(gameStateResp.getCells(), gameState);
                
                switch (gameState) {
                    case GAME_NOT_STARTED:
                        break;
                    case RED_WON:
                        isGameComplete = true;
                        System.out.println(Utils.winningString(playerId, redPlayerId));
                        break;
                    case YELLOW_WON:
                        isGameComplete = true;
                        System.out.println(Utils.winningString(playerId, yellowPlayerId));
                        break;
                    case RED_TO_PLAY:
                        if (redPlayerId.equals(playerId)) {
                            board.addPiece(gameStateResp.getCells(), playerId, gameState);
                        }
                        break;
                    case YELLOW_TO_PLAY:
                        if (yellowPlayerId.equals(playerId)) {
                            board.addPiece(gameStateResp.getCells(), playerId, gameState);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void printState(int[][] cells, GameState gameState) {
        System.out.println(gameState);
        System.out.print(Utils.prettyPrintBoard(cells));
    }
}
