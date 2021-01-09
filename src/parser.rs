use core::fmt;
use std::collections::HashMap;
use std::fmt::{Display, Formatter};
use std::ops::Deref;

use crate::lexer::Token::{EOFToken, KeywordToken};
use crate::lexer::{BlockDelimiter, Keyword, SrcInfo, Token, Tokens};

#[derive(Debug)]
pub struct Program {
    pub sprites: HashMap<String, Sprite>,
}

#[derive(Debug)]
pub struct Sprite {
    pub name: String,
    pub functions: Vec<Function>,
}

#[derive(Debug)]
pub struct Function {
    pub name: String,
    pub modifier: Modifier,
}

#[derive(Debug)]
pub enum Modifier {
    Public,
    Private,
}

pub struct ParseError {
    pub description: String,
    pub src: Option<SrcInfo>,
}

impl ParseError {
    pub fn new(description: &str, src: SrcInfo) -> Self {
        Self {
            description: description.to_string(),
            src: Some(src),
        }
    }
}

impl Display for ParseError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        if self.src.is_some() {
            write!(
                f,
                "{} at {}:{}",
                self.description,
                self.src.as_ref().unwrap().line,
                self.src.as_ref().unwrap().column
            )
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
                src: None,
            });
        }
        let token = $tokens.remove(0);
        if let Token::EOFToken = *token.1 {
            let mut error_string = $error.to_owned();
            error_string.push_str(", but reached end of file");
            return Err(ParseError {
                description: error_string,
                src: Some(token.0),
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

macro_rules! peek_token {
    ($tokens:ident, $target:expr) => {{
        let next = $tokens.get(0);
        next.is_some() && $target == *next.unwrap().1
    }};
}

pub fn parse(mut tokens: Tokens) -> Result<Program, ParseError> {
    let mut sprites: HashMap<String, Sprite> = HashMap::new();
    loop {
        let next = tokens.get(0);
        if next.is_some() {
            if let EOFToken = next.unwrap().1.deref() {
                break;
            }
        }
        let sprite = parse_sprite(&mut tokens)?;
        sprites.insert(sprite.name.clone(), sprite);
    }
    Ok(Program { sprites })
}

fn parse_sprite(tokens: &mut Tokens) -> Result<Sprite, ParseError> {
    expect_token!(
        tokens,
        Token::KeywordToken(Keyword::Sprite),
        "Expected a sprite"
    );
    let name = expect_token!(tokens, Token::NameToken, "Expected a sprite name", name);
    expect_token!(
        tokens,
        Token::BlockToken(BlockDelimiter::LeftBracket),
        "Expected a left bracket"
    );
    let mut functions: Vec<Function> = Vec::new();
    while !peek_token!(tokens, Token::BlockToken(BlockDelimiter::RightBracket)) {
        functions.push(parse_function(tokens)?)
    }
    expect_token!(
        tokens,
        Token::BlockToken(BlockDelimiter::RightBracket),
        "Expected a right bracket"
    );
    Ok(Sprite { name, functions })
}

fn parse_function(tokens: &mut Tokens) -> Result<Function, ParseError> {
    let modifier_token = expect_token_inner!(tokens, Token::KeywordToken, "Expected a modifier");
    let modifier = if let Token::KeywordToken(keyword) = *modifier_token.1 {
        match keyword {
            Keyword::Public => Modifier::Public,
            Keyword::Private => Modifier::Private,
            _ => {
                return Err(ParseError::new(
                    "Expected either public or private",
                    modifier_token.0,
                ))
            }
        }
    } else {
        return Err(ParseError::new(
            "Expected either public or private",
            modifier_token.0,
        ));
    };
    expect_token!(
        tokens,
        Token::KeywordToken(Keyword::Function),
        "Expected function"
    );
    let name = expect_token!(tokens, Token::NameToken, "Expected a function name", name);
    expect_token!(
        tokens,
        Token::BlockToken(BlockDelimiter::LeftBracket),
        "Expected a left bracket"
    );
    expect_token!(
        tokens,
        Token::BlockToken(BlockDelimiter::RightBracket),
        "Expected a right bracket"
    );
    Ok(Function { name, modifier })
}
