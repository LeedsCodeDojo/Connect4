module API

open System
open HttpClient
open FSharp.Data
open Game

type GameJson = JsonProvider<""" {"CurrentState":4,"Cells":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],"YellowPlayerID":"8a7fdc05-3f83-4493-9d0b-67a373c59f7a","RedPlayerID":"a05bf67c-2bbb-4243-bf18-fe60c52cf4f9","ID":"05c390d3-8edf-47a9-a246-987aeb783bc8"}""">

let toGameRecord gameResponse =
    let gameJson = GameJson.Parse(gameResponse)

    let gameState = 
        match gameJson.CurrentState with
        | 0 -> GameNotStarted
        | 1 -> RedWon
        | 2 -> YellowWon
        | 3 -> RedToPlay
        | 4 -> YellowToPlay
        | 5 -> Draw

    let cells = gameJson.Cells |> Array.map (fun row -> row |> Array.map (fun cell -> match cell with 0 -> Empty | 1 -> Red | 2 -> Yellow))

    {Id=gameJson.Id; CurrentState=gameState; YellowPlayerID=gameJson.YellowPlayerId; RedPlayerID=gameJson.RedPlayerId; Cells=cells}

let formatHttpError processDescription (response:Response) =
    sprintf "\n%s failed!\nResponse Code: %i\nResponse Body:\n%s" processDescription response.StatusCode (match response.EntityBody with Some text -> text | None -> "")

let errorIfInvalid processDescription (response:Response) =
    match response.StatusCode with
    | ok when ok >= 200 && ok < 300 -> ()
    | error -> failwith (response |> formatHttpError processDescription)

let getResponseBody processDescription (response:Response) =
    response |> errorIfInvalid processDescription
    match response.EntityBody with
    | Some body -> body
    | None -> failwith (sprintf "%s failed! Response was empty" processDescription)

let RegisterTeam teamName teamPassword serverUrl =
    let url = serverUrl + "/api/Register"
    let request = 
        createRequest Post url
        |> withQueryStringItem { name="teamName"; value=teamName }
        |> withQueryStringItem { name="password"; value=teamPassword }
        |> withBody "" // to keep server happy

    let response = request |> getResponse
    let playerId = response |> getResponseBody "Register"
    Guid.Parse(playerId.Replace("\"", ""))

let GetGame playerId serverUrl =
    let url = serverUrl + "/api/GameState"
    let request = 
        createRequest Get url
        |> withQueryStringItem { name="playerID"; value=playerId.ToString() }
    
    let response = request |> getResponse 
    response |> getResponseBody "GetGame" |> toGameRecord

let MakeMove playerId serverUrl columnNumber teamPassword =
    let url = serverUrl + "/api/MakeMove"
    let request = 
        createRequest Get url
        |> withQueryStringItem { name="playerID"; value=playerId.ToString() }
        |> withQueryStringItem { name="columnNumber"; value=columnNumber.ToString() }
        |> withQueryStringItem { name="password"; value=teamPassword }

    let response = request |> getResponse
    response |> errorIfInvalid "MakeMove"

// not sure this works
let NewGame playerId serverUrl =
    let url = serverUrl + "/api/NewGame"
    let request = 
        createRequest Get url
        |> withQueryStringItem { name="playerID"; value=playerId.ToString() }

    let response = request |> getResponse
    response |> errorIfInvalid "NewGame"
