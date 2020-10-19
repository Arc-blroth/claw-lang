use std::ops::Deref;

use crate::lexer::{BlockDelimiter, Keyword, Token};

#[derive(Debug)]
pub struct Program {
    pub sprites: Vec<Sprite>
}

#[derive(Debug)]
pub struct Sprite {
    pub name: String
}

pub struct ParseError {
    pub description: String
}

type Tokens = Vec<Box<Token>>;

trait TokenUtils {
    fn next(&mut self) -> Token;
}

impl TokenUtils for Tokens {
    fn next(&mut self) -> Token {
        *self.remove(0)
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
    if token != Token::KeywordToken(Keyword::Sprite) {
        return Err(ParseError { description: String::from("Expected a sprite.") });
    }
    let mut name;
    let token = tokens.next();
    if let Token::NameToken(token_name) = token {
        name = token_name;
    } else {
        return Err(ParseError { description: String::from("Expected a sprite name.") });
    }
    let token = tokens.next();
    if token != Token::BlockToken(BlockDelimiter::LeftBracket) {
        return Err(ParseError { description: String::from("Expected a left bracket.") });
    }
    let token = tokens.next();
    if token != Token::BlockToken(BlockDelimiter::RightBracket) {
        return Err(ParseError { description: String::from("Expected a right bracket.") });
    }
    Ok(Sprite {
        name
    })
}
