module API

open HttpClient
open System
open FSharp.Data

type GameJson = JsonProvider<""" {"CurrentState":4,"Cells":[[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]],"YellowPlayerID":"8a7fdc05-3f83-4493-9d0b-67a373c59f7a","RedPlayerID":"a05bf67c-2bbb-4243-bf18-fe60c52cf4f9","ID":"05c390d3-8edf-47a9-a246-987aeb783bc8"}""">

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


let GetGame playerId serverUrl =
    let gameStateUrl = serverUrl + "/api/GameState"
    let request = 
        createRequest Get gameStateUrl
        |> withQueryStringItem { name="playerID"; value=playerId.ToString() }
    
    let response = request |> getResponse

    match response.StatusCode with
    | ok when ok >= 200 && ok < 300 -> 
        match response.EntityBody with
        | Some json -> json |> toGameRecord
        | None -> failwith "Response was empty; expected Guid"
    | error -> failwith (sprintf "Error registering team: %i" error)

let RegisterTeam teamName teamPassword serverUrl =
    let url = serverUrl + "/api/Register"
    let request = 
        createRequest Post url
        |> withQueryStringItem { name="teamName"; value=teamName }
        |> withQueryStringItem { name="password"; value=teamPassword }
        |> withBody "" // to keep server happy with this dodgy 'post'

    let response = request |> getResponse

    match response.StatusCode with
    | ok when ok >= 200 && ok < 300 -> 
        match response.EntityBody with
        | Some text -> Guid.Parse(text.Replace("\"", ""))
        | None -> failwith "Response was empty; expected Guid"
    | error -> failwith (sprintf "Error registering team: %i" error)


//
//        /// <summary>
//        /// Plays a move.  ColumnNumber should be between 0 and 6
//        /// </summary>
//        /// <param name="playerID"></param>
//        /// <param name="serverURL"></param>
//        /// <param name="columnNumber"></param>
//        internal static void MakeMove(Guid playerID, string serverURL, int columnNumber, string teamPassword)
//        {
//            var httpClient = new HttpClient();
//            httpClient.BaseAddress = new Uri(serverURL);
//
//            var httpResponseMessage = httpClient.PostAsync($"api/MakeMove?playerID={playerID}&columnNumber={columnNumber}&password={teamPassword}", null).Result;
//            if (!httpResponseMessage.IsSuccessStatusCode)
//            {
//                // Something has gone wrong
//                var errors = httpResponseMessage.Content.ReadAsStringAsync().Result;
//                throw new Exception(string.Format("Failed to call {0}.   Status {1}.  Reason {2}. {3}", "MakeMove", (int)httpResponseMessage.StatusCode, httpResponseMessage.ReasonPhrase, errors));
//            }
//        }
//
//        /// <summary>
//        /// Starts a new game against the same player as the previous game.  Your colours will
//        /// however be swapped (red => yellow and yellow => red)
//        /// </summary>
//        /// <param name="playerID"></param>
//        /// <param name="serverURL"></param>
//        internal static void NewGame(Guid playerID, string serverURL)
//        {
//            var httpClient = new HttpClient();
//            httpClient.BaseAddress = new Uri(serverURL);
//
//            var httpResponseMessage = httpClient.PostAsync($"api/NewGame?playerID={playerID}", null).Result;
//            if (!httpResponseMessage.IsSuccessStatusCode)
//            {
//                // Something has gone wrong
//                var errors = httpResponseMessage.Content.ReadAsStringAsync().Result;
//                throw new Exception(string.Format("Failed to call {0}.   Status {1}.  Reason {2}. {3}", "NewGame", (int)httpResponseMessage.StatusCode, httpResponseMessage.ReasonPhrase, errors));
//            }
//
//        }