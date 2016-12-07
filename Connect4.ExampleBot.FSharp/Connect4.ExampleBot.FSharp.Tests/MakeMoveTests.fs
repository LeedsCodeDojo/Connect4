module Tests

open System
open NUnit.Framework
open FsUnit
open FSharpExample
open Game

let columnChosen = ref -1

let dummyApiCall playerId serverUrl columnNumber teamPassword = 
    columnChosen := columnNumber

[<Test>]
let ``always plays in column 0`` () =
    let redId = Guid.NewGuid();
    let dummyGame = {Id=Guid.Empty; CurrentState=RedToPlay; YellowPlayerID=Guid.Empty; RedPlayerID=redId; Cells=[|[||]|]}
    MakeMove dummyGame redId dummyApiCall

    !columnChosen |> should equal 0
