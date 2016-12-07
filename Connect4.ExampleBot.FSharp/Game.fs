module Game

open System

type GameState = 
    | GameNotStarted 
    | RedWon
    | YellowWon
    | RedToPlay
    | YellowToPlay
    | Draw

type CellContent = 
    | Empty
    | Red
    | Yellow
    
let NUMBER_OF_COLUMNS = 7
let NUMBER_OF_ROWS = 6

type Game = {
    Cells: CellContent[][]
    CurrentState: GameState
    YellowPlayerID: Guid
    RedPlayerID: Guid
    Id: Guid
}