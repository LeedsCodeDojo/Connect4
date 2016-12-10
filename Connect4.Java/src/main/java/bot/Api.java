package bot;

import bot.models.GameStateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import org.json.JSONObject;

public class Api {

    String url = "http://yorkdojoconnect4.azurewebsites.net/api/";

    static {
        // Needs to be called just once
        Unirest.setObjectMapper(new ObjectMapper() {

            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public <T> T readValue(String string, Class<T> type) {
                try {
                    return jacksonObjectMapper.readValue(string, type);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * /api/Register [POST]
     *
     * Register your team and returns your unique player ID.
     *
     * api/Register?teamName=Winners&password=Secret
     */
    public String register(String teamName, String password) throws UnirestException {
        HttpResponse<String> strResponse = Unirest.post(url + "Register")
                .header("accept", "application/json")
                .queryString("teamName", teamName)
                .queryString("password", password)
                .asString();

        return strResponse.getBody().replace("\"", "");
    }

    /**
     * /api/GameState [GET]
     *
     * Returns the current state of the game
     *
     * api/GameState?playerID=1234567F
     */
    public GameStateResponse gameState(String playerId) throws UnirestException {
        HttpResponse<GameStateResponse> response = Unirest.get(url + "GameState")
                .header("accept", "application/json")
                .queryString("playerID", playerId)
                .asObject(GameStateResponse.class);

        return response.getBody();
    }

    /**
     * /api/MakeMove [POST]
     *
     * Places your piece into a column
     *
     * api/MakeMove?playerID=1234567&ColumnNumber=2&Password=secret
     */
    public JSONObject makeMove(String playerId, int columnNumber, String password) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(url + "MakeMove")
                .header("accept", "application/json")
                .queryString("playerID", playerId)
                .queryString("ColumnNumber", columnNumber)
                .queryString("Password", password)
                .asJson();

        return jsonResponse.getBody().getObject();
    }

    /**
     * /api/NewGame [POST]
     *
     * Clears the board
     *
     * api/NewGame?playerID=1234567
     */
    public String newGame(String playerId) throws UnirestException {
        HttpResponse<String> strResponse = Unirest.post(url + "NewGame")
                .header("accept", "application/json")
                .queryString("playerID", playerId)
                .asString();

        return strResponse.getBody();
    }
}
