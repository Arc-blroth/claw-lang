use std::env;
use std::fs::File;
use std::io::{Read, Write};
use std::path::Path;

use crate::emitter::emit_code;
use crate::lexer::Lexer;
use crate::parser::parse;

mod emitter;
mod lexer;
mod parser;
mod project;

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() < 2 {
        println!("No file specified.");
        return;
    }

    let input_path = Path::new(args.get(1).unwrap());
    let mut file = match File::open(&input_path) {
        Err(why) => {
            eprintln!("Couldn't open file '{}': {}", args.get(1).unwrap(), why);
            return;
        }
        Ok(file) => file,
    };

    let mut s: String = String::from("");
    match file.read_to_string(&mut s) {
        Err(why) => {
            eprintln!("Couldn't read file '{}': {}", args.get(1).unwrap(), why);
            return;
        }
        Ok(_) => {
            let mut lexer = Lexer::new(s);
            lexer.lex();
            match parse(lexer.tokens) {
                Ok(program) => {
                    let mut file_name = input_path.file_stem().unwrap().to_owned();
                    file_name.push(".json");
                    match File::create(
                        input_path
                            .parent()
                            .unwrap()
                            .join("out")
                            .join(file_name.clone()),
                    ) {
                        Err(why) => {
                            eprintln!(
                                "Couldn't open output '{}': {}",
                                file_name.into_string().unwrap_or("???".to_string()),
                                why
                            );
                            return;
                        }
                        Ok(mut output_file) => {
                            if let Err(why) = output_file.write_all(emit_code(program).as_bytes()) {
                                eprintln!(
                                    "Couldn't write to output '{}': {}",
                                    file_name.into_string().unwrap_or("???".to_string()),
                                    why
                                );
                                return;
                            }
                        }
                    }
                }
                Err(error) => {
                    eprintln!("{}", error)
                }
            }
        }
    }
}
