use core::fmt;
use std::fmt::{Display, Formatter};

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
    pub src: Option<SrcInfo>
}

impl ParseError {
    pub fn new(description: &str, src: SrcInfo) -> Self {
        Self {
            description: String::from(description),
            src: Some(src)
        }
    }
}

impl Display for ParseError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        if self.src.is_some() {
            write!(f, "{} at {}:{}", self.description, self.src.as_ref().unwrap().line, self.src.as_ref().unwrap().column)
        } else {
            write!(f, "{}", self.description)
        }
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

macro_rules! expect_token_inner {
    ($tokens:ident, $target:expr, $error:expr) => {{
        if $tokens.is_empty() {
            let mut error_string = $error.to_owned();
            error_string.push_str(", but reached end of token list?");
            return Err(ParseError {
                description: error_string,
                src: None
            });
        }
        let token = $tokens.remove(0);
        if let Token::EOFToken = *token.1 {
            let mut error_string = $error.to_owned();
            error_string.push_str(", but reached end of file");
            return Err(ParseError {
                description: error_string,
                src: Some(token.0)
            });
        }
        token
    }};
}

macro_rules! expect_token {
    ($tokens:ident, $target:expr, $error:expr) => {{
        let token = expect_token_inner!($tokens, $target, $error);
        if *token.1 != $target {
            return Err(ParseError::new($error, token.0));
        }
    }};

    ($tokens:ident, $target:path, $error:expr, $($arg:ident),*) => {{
        let token = expect_token_inner!($tokens, $target, $error);
        if let $target($($arg)*) = *token.1 {
            ($($arg)*)
        } else {
            return Err(ParseError::new($error, token.0));
        }
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
    let name = expect_token!(tokens, Token::NameToken, "Expected a sprite name", token_name);
    expect_token!(tokens, Token::BlockToken(BlockDelimiter::LeftBracket), "Expected a left bracket");
    expect_token!(tokens, Token::BlockToken(BlockDelimiter::RightBracket), "Expected a right bracket");
    Ok(Sprite {
        name
    })
}
