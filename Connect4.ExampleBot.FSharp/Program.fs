module FSharpExample

open System
open API
open Game

let teamName = "Another bunch of F#'ers";
let teamPassword = "pa$$word";
let serverURL = "http://yorkdojoconnect4.azurewebsites.net/";

let playerId = API.RegisterTeam teamName teamPassword serverURL

let MakeMove game =
    
    printfn "Press to play"
    System.Console.ReadKey(true) |> ignore

    // PUT YOUR CODE IN HERE

    let selectedColumn = 0
    API.MakeMove playerId serverURL selectedColumn teamPassword  
    printfn "Played in column %i" selectedColumn

let MakeMoveIfPlayer moveId game =
     if moveId = playerId then 
         try
            MakeMove game
         with ex ->
            printfn "%s" ex.Message
        
let rec gameLoop() =
    try
        let game = API.GetGame playerId serverURL 

        match game.CurrentState with
        | GameNotStarted -> gameLoop()
        | RedToPlay -> MakeMoveIfPlayer game.RedPlayerID game; gameLoop()
        | YellowToPlay -> MakeMoveIfPlayer game.YellowPlayerID game; gameLoop()
        | Draw -> printfn "Draw!"
        | RedWon -> printfn "You %s" (if game.RedPlayerID = playerId then "Won" else "Lost")
        | YellowWon -> printfn "You %s" (if game.YellowPlayerID = playerId then "Won" else "Lost")
     with ex ->
        printfn "%s" ex.Message

[<EntryPoint>]
let main _ = 
    printfn "PlayerID is %A" playerId
    gameLoop()
    0