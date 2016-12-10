package bot;

public enum GameState {

    GAME_NOT_STARTED, RED_WON, YELLOW_WON, RED_TO_PLAY, YELLOW_TO_PLAY, DRAW, FOO_BARRED;

    public static GameState from(int gameStateInt) {
        GameState returnVal;
        switch (gameStateInt) {
            case 0:
                returnVal = GAME_NOT_STARTED;
                break;
            case 1:
                returnVal = RED_WON;
                break;
            case 2:
                returnVal = YELLOW_WON;
                break;
            case 3:
                returnVal = RED_TO_PLAY;
                break;
            case 4:
                returnVal = YELLOW_TO_PLAY;
                break;
            case 5:
                returnVal = DRAW;
                break;
            default:
                returnVal = FOO_BARRED;
        }
        return returnVal;
    }
	
}
