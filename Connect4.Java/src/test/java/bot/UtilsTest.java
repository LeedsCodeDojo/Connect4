/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bot;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author james
 */
public class UtilsTest {

    public UtilsTest() {
    }

    /**
     * Test of prettyPrintBoard method, of class Utils.
     */
    @Test
    public void testPrettyPrintBoard() {
        int[][] board = {
            {1, 1, 0, 0, 0, 0}, // COL 0
            {0, 0, 0, 0, 0, 0},
            {2, 0, 0, 0, 0, 0},
            {2, 1, 2, 1, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {1, 2, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0}
        };
        
        String nl = System.lineSeparator();
        String expectedResult = 
                "[ ] [ ] [ ] [ ] [ ] [ ] [ ] " + nl +
                "[ ] [ ] [ ] [ ] [ ] [ ] [ ] " + nl +
                "[ ] [ ] [ ] [R] [ ] [ ] [ ] " + nl +
                "[ ] [ ] [ ] [Y] [ ] [ ] [ ] " + nl +
                "[R] [ ] [ ] [R] [ ] [Y] [ ] " + nl +
                "[R] [ ] [Y] [Y] [ ] [R] [ ] " + nl +
                "(1) (2) (3) (4) (5) (6) (7) ";
        
        String result = Utils.prettyPrintBoard(board);
        System.out.println(result);
        assertEquals(expectedResult, result);
    }

}
