/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bot;

import bot.models.GameStateResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author james
 */
public class ApiTest {

    Api api;

    String password = "1234";
    String teamName;
    
    public ApiTest() {
        api = new Api();
    }

    /**
     * Test of register method, of class Api.
     */
    @Test
    public void testRegister() throws Exception {
        // REGISTER
        //api.newGame("connect this");
        String uniquePlayerID = api.register("connect this", password);
        assertTrue(!uniquePlayerID.isEmpty());
        assertTrue(uniquePlayerID.contains("-"));
    
        System.out.println("PLAYER ID" + uniquePlayerID);
        
        // GAME STATE
        GameStateResponse gameState = api.gameState(uniquePlayerID);
        System.out.println("\ngameState:\n" + gameState.toString());
        
        // MAKE MOVE
        JSONObject makeMoveResp = api.makeMove(gameState.getYellowPlayerID(), 3, password);
        System.out.println("\n\nmakeMoveResp:\n" + makeMoveResp.toString());
        
        //System.out.println("size:" + gameState.get("size"));
        
    }
}