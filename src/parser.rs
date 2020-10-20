use core::fmt;
use std::fmt::{Display, Formatter};
use std::ops::Deref;

use crate::lexer::{BlockDelimiter, Keyword, SrcInfo, Token, Tokens};

#[derive(Debug)]
pub struct Program {
    pub sprites: Vec<Sprite>
}

#[derive(Debug)]
pub struct Sprite {
    pub name: String
}

pub struct ParseError {
    pub description: String,
    pub src: SrcInfo
}

impl ParseError {
    pub fn new(description: &str, src: SrcInfo) -> Self {
        Self {
            description: String::from(description),
            src
        }
    }
}

impl Display for ParseError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{} at {}:{}", self.description, self.src.line, self.src.column)
    }
}

trait TokenUtils {
    fn next(&mut self) -> (SrcInfo, Box<Token>);
}

impl TokenUtils for Tokens {
    fn next(&mut self) -> (SrcInfo, Box<Token>) {
        self.remove(0)
    }
}

pub fn parse(mut tokens: Tokens) -> Result<Program, ParseError> {
    let sprite = parse_sprite(tokens)?;
    Ok(Program {
        sprites: vec![sprite]
    })
}

fn parse_sprite(mut tokens: Tokens) -> Result<Sprite, ParseError> {
    let token = tokens.next();
    println!("{} {}", token.0.line, token.0.column);
    if *token.1 != Token::KeywordToken(Keyword::Sprite) {
        return Err(ParseError::new("Expected a sprite", token.0));
    }
    let name;
    let token = tokens.next();
    if let Token::NameToken(token_name) = *token.1 {
        name = token_name;
    } else {
        return Err(ParseError::new("Expected a sprite name", token.0));
    }
    let token = tokens.next();
    if *token.1 != Token::BlockToken(BlockDelimiter::LeftBracket) {
        return Err(ParseError::new("Expected a left bracket", token.0));
    }
    let token = tokens.next();
    if *token.1 != Token::BlockToken(BlockDelimiter::RightBracket) {
        return Err(ParseError::new("Expected a right bracket", token.0));
    }
    Ok(Sprite {
        name
    })
}
