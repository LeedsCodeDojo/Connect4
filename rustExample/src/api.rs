#![allow(non_snake_case)]

use rustc_serialize::{Decodable, Decoder};
use rustc_serialize::json;
use hyper::{self, Client};
use std::io::Read;

const SERVER_URL: &'static str = "http://yorkdojoconnect4.azurewebsites.net/";
static TEAM_NAME: &'static str = "rustPlayer";
static TEAM_PASSWORD: &'static str = "asiduhfasjfhlkasjfhlaksjfhlksajfh";

const COLUMNS: usize = 7;
const ROWS: usize = 6;

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum GameState {
    GameNotStarted = 0,
    RedWon = 1,
    YellowWon = 2,
    RedToPlay = 3,
    YellowToPlay = 4,
    Draw = 5,
}

impl Decodable for GameState {
    fn decode<D: Decoder>(d: &mut D) -> Result<Self, D::Error> {
        let n = try!(d.read_usize());
        let state = match n {
            0 => GameState::GameNotStarted,
            1 => GameState::RedWon,
            2 => GameState::YellowWon,
            3 => GameState::RedToPlay,
            4 => GameState::YellowToPlay,
            5 => GameState::Draw,
            _ => panic!("broken gamestate"),
        };
        Ok(state)
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, RustcDecodable)]
pub enum CellState {
    Empty = 0,
    Red = 1,
    Yellow = 2,
}

#[derive(Debug)]
pub struct Board {
    cells: [[CellState; ROWS]; COLUMNS],
}

impl Decodable for Board {
    fn decode<D: Decoder>(d: &mut D) -> Result<Self, D::Error> {
        let mut cells = [[CellState::Empty; ROWS]; COLUMNS];
        let _cells = d.read_seq(|d, len| {
            let mut column_list = Vec::new();
            for i in 0..len {
                column_list.push(try!(d.read_seq_elt(i, |d| {

                    let row = d.read_seq(|d, len| {
                        let mut row_list = Vec::new();
                        for i in 0..len {
                            row_list.push(try!(d.read_seq_elt(i, |d| {
                                let state = match try!(d.read_usize()) {
                                    0 => CellState::Empty,
                                    1 => CellState::Red,
                                    2 => CellState::Yellow,
                                    _ => panic!("Invalid cell state"),
                                };
                                Ok(state)
                            })));
                        }
                        Ok(row_list)
                    });
                    assert!(row.is_ok(), "row was invalid");
                    row
                })));
            }
            Ok(column_list)
        });
        assert!(_cells.is_ok(), "column was invalid");
        let cells_vec = _cells.ok().unwrap();

        for (inner_array, inner_vec) in cells.iter_mut().zip(cells_vec.iter()) {
            for (place, element) in inner_array.iter_mut().zip(inner_vec.iter()) {
                *place = *element;
            }
        }

        Ok(Board { cells: cells })
    }
}

impl Board {
    pub fn new() -> Board {
        Board { cells: [[CellState::Empty; ROWS]; COLUMNS] }
    }
}

#[derive(Debug, RustcDecodable)]
pub struct RemoteGameState {
    CurrentState: GameState,
    Cells: Board,
    YellowPlayerID: String,
    RedPlayerID: String,
    ID: String,
}

pub struct Game {
    pub state: GameState,
    pub board: Board,
    player_id: Option<String>,
    yellow_player_id: Option<String>,
    red_player_id: Option<String>,
    game_id: Option<String>,
    client: hyper::Client,
}

impl Game {
    pub fn new() -> Game {
        let mut game = Game {
            state: GameState::GameNotStarted,
            board: Board::new(),
            player_id: None,
            yellow_player_id: None,
            red_player_id: None,
            game_id: None,
            client: Client::new(),
        };
        game.register();
        game.new_game();
        game
    }

    fn register(&mut self) {
        let mut body = String::new();
        let url = format!("{}{}?teamName={}&password={}",
                          SERVER_URL.to_string(),
                          "api/register",
                          TEAM_NAME,
                          TEAM_PASSWORD);
        let mut res = self.client.post(&url).send().unwrap();
        res.read_to_string(&mut body).unwrap();
        assert_eq!(res.status, hyper::Ok, "Error returned: {}", body);
        body.remove(0);
        body.pop();
        self.player_id = Some(body);
    }

    fn new_game(&mut self) {
        let mut body = String::new();
        let url = format!("{}{}?playerID={}",
                          SERVER_URL.to_string(),
                          "api/NewGame",
                          self.player_id.clone().unwrap());
        let mut res = self.client.post(&url).send().unwrap();
        res.read_to_string(&mut body).unwrap();
        assert_eq!(res.status, hyper::Ok, "Error returned: {}", body);
    }

    pub fn get_game_state(&mut self) {
        let mut body = String::new();
        let url = format!("{}{}?playerID={}",
                          SERVER_URL.to_string(),
                          "api/GameState",
                          self.player_id.clone().unwrap());
        let mut res = self.client.get(&url).send().unwrap();
        res.read_to_string(&mut body).unwrap();
        assert_eq!(res.status, hyper::Ok, "Error returned: {}", body);

        let remote_game_state: RemoteGameState = json::decode(&body).unwrap();
        self.board = remote_game_state.Cells;
        self.state = remote_game_state.CurrentState;
        self.yellow_player_id = Some(remote_game_state.YellowPlayerID);
        self.red_player_id = Some(remote_game_state.RedPlayerID);
        self.game_id = Some(remote_game_state.ID);
    }

    pub fn over(&self) -> bool {
        match self.state {
            GameState::RedWon | GameState::YellowWon | GameState::Draw => true,
            _ => false,
        }
    }

    pub fn my_turn(&self) -> bool {
        match (self.player_id == self.yellow_player_id, self.state) {
            (true, GameState::YellowToPlay) => true,
            (false, GameState::RedToPlay) => true,
            _ => false,
        }
    }

    pub fn winner(&self) {
        match (self.player_id == self.yellow_player_id, self.state) {
            (_, GameState::Draw) => println!("The game was a draw"),
            (true, GameState::YellowWon) => println!("You won, as yellow!"),
            (false, GameState::RedWon) => println!("You won, as red!"),
            (false, GameState::YellowWon) => println!("You lost, as red!"),
            (true, GameState::RedWon) => println!("You lost, as yellow!"),
            _ => println!("The game isn't over yet!"),
        }
    }

    pub fn play(&self, column: usize) {
        let mut body = String::new();
        let url = format!("{}{}?playerID={}&columnNumber={}&password={}",
                          SERVER_URL.to_string(),
                          "api/MakeMove",
                          self.player_id.clone().unwrap(),
                          column,
                          TEAM_PASSWORD);
        let mut res = self.client.get(&url).send().unwrap();
        res.read_to_string(&mut body).unwrap();
        assert_eq!(res.status, hyper::Ok, "Error returned: {}", body);
    }

    pub fn valid_move(&self, column_idx: usize) -> bool {
        let column = self.board.cells[column_idx];
        column[ROWS - 1] == CellState::Empty
    }
}
