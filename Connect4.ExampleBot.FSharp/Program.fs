module FSharpExample

open System
open API

// Put the name of your team here
let teamName = "Bunch of F#'ers";

// and a password for your team here.  This is to prevent cheating!
let teamPassword = "pa$$word";

// The location of the game server
let serverURL = "http://yorkdojoconnect4.azurewebsites.net/";

let MakeMove game playerID =
    // PUT YOUR CODE IN HERE
    //API.MakeMove(playerID, serverURL, 0, teamPassword)  //Place a counter in the first column

    printfn "Press to play"
    System.Console.ReadKey(true) |> ignore


[<EntryPoint>]
let main _ = 
    let playerId = API.RegisterTeam teamName teamPassword serverURL
    printfn "PlayerID is %A" playerId

    while true do
        let game = API.GetGame playerId serverURL

        match game.CurrentState with
        | GameNotStarted
        | RedWon -> printfn "You %s" (if game.RedPlayerID = playerId then "Won" else "Lost")
        | YellowWon -> printfn "You %s" (if game.YellowPlayerID = playerId then "Won" else "Lost")
        | RedToPlay -> if game.RedPlayerID = playerId then MakeMove game playerId
        | YellowToPlay -> if game.YellowPlayerID = playerId then MakeMove game playerId
        | Draw -> printfn "Draw!"

    0 // return an integer exit code
