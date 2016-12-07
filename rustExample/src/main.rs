extern crate hyper;
extern crate rustc_serialize;
extern crate rand;

mod api;

use api::Game;
use rand::distributions::{IndependentSample, Range};

fn main() {
    let mut game = Game::new();
    println!("Game started");
    loop {
        game.get_game_state();
        println!("State: {:?}", game.state);

        if game.over() {
            println!("Game over!");
            game.winner();
            break;
        }

        if game.my_turn() {
            println!("My turn to play");
            play_move_better_random(&game);
        }
    }
}

fn play_move(game: &Game) {
    let column_choice = 0;
    game.play(column_choice);
}

fn play_move_random(game: &Game) {
    let range = Range::new(0, 6);
    let mut rng = rand::thread_rng();
    let column_choice = range.ind_sample(&mut rng);

    game.play(column_choice);
}

fn play_move_better_random(game: &Game) {
    let range = Range::new(0, 6);
    let mut rng = rand::thread_rng();
    let mut column_choice = range.ind_sample(&mut rng);
    while !game.valid_move(column_choice) {
        column_choice = range.ind_sample(&mut rng);
    }

    game.play(column_choice);
}
