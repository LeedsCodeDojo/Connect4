package bot.models;

import bot.GameState;
import bot.Utils;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "CurrentState",
    "RedPlayerID",
    "ID",
    "Cells",
    "YellowPlayerID"
})
public class GameStateResponse {

    @JsonProperty("CurrentState")
    private Integer currentState;
    @JsonProperty("RedPlayerID")
    private String redPlayerID;
    @JsonProperty("ID")
    private String iD;
    @JsonProperty("Cells")
    private int[][] cells = null;
    @JsonProperty("YellowPlayerID")
    private String yellowPlayerID;

    /**
     *
     * @return The currentState
     */
    @JsonProperty("CurrentState")
    public GameState getCurrentState() {
        return GameState.from(currentState);
    }

    /**
     *
     * @return The redPlayerID
     */
    @JsonProperty("RedPlayerID")
    public String getRedPlayerID() {
        return redPlayerID;
    }

    /**
     *
     * @return The iD
     */
    @JsonProperty("ID")
    public String getID() {
        return iD;
    }

    /**
     *
     * @return The cells
     */
    @JsonProperty("Cells")
    public int[][] getCells() {
        return cells;
    }

    /**
     *
     * @return The yellowPlayerID
     */
    @JsonProperty("YellowPlayerID")
    public String getYellowPlayerID() {
        return yellowPlayerID;
    }

    @Override
    public String toString() {
        String nL = System.lineSeparator();
        return "GameStateResponse{"
                + nL + "currentState=" + currentState 
                + nL + "redPlayerID=" + redPlayerID 
                + nL + "iD=" + iD 
                + nL + "cells=" + nL + Utils.prettyPrintBoard(cells)
                + nL + "yellowPlayerID=" + yellowPlayerID 
                + nL + '}';
    }


}
