use std::env;
use std::fs::File;
use std::io::Read;
use std::path::Path;

use crate::lexer::Lexer;
use crate::parser::parse;

mod lexer;
mod parser;

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() < 2 {
        println!("No file specified.");
        return
    }

    let input_path = Path::new(args.get(1).unwrap());
    let mut file = match File::open(&input_path) {
        Err(why) => {
            eprintln!("Couldn't open file '{}': {}", args.get(1).unwrap(), why);
            return
        },
        Ok(file) => file,
    };

    let mut s: String = String::from("");
    match file.read_to_string(&mut s) {
        Err(why) => {
            eprintln!("Couldn't read file '{}': {}", args.get(1).unwrap(), why);
            return
        },
        Ok(_) => {
            let mut lexer = Lexer::new(s);
            lexer.lex();
            match parse(lexer.tokens) {
                Ok(program) => {
                    println!("{:?}", &program.sprites)
                },
                Err(error) => {
                    eprintln!("{}", error)
                }
            }
        }
    }

}
