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

macro_rules! expect_token {
    ($tokens:ident, $target:expr, $error:expr) => {{
        let token = $tokens.next();
        if *token.1 != $target {
            return Err(ParseError::new($error, token.0));
        }
        token
    }};
}

pub fn parse(mut tokens: Tokens) -> Result<Program, ParseError> {
    let sprite = parse_sprite(tokens)?;
    Ok(Program {
        sprites: vec![sprite]
    })
}

fn parse_sprite(mut tokens: Tokens) -> Result<Sprite, ParseError> {
    expect_token!(tokens, Token::KeywordToken(Keyword::Sprite), "Expected a sprite");
    let name;
    let token = tokens.next();
    if let Token::NameToken(token_name) = *token.1 {
        name = token_name;
    } else {
        return Err(ParseError::new("Expected a sprite name", token.0));
    }
    expect_token!(tokens, Token::BlockToken(BlockDelimiter::LeftBracket), "Expected a left bracket");
    expect_token!(tokens, Token::BlockToken(BlockDelimiter::RightBracket), "Expected a right bracket");
    Ok(Sprite {
        name
    })
}
