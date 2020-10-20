use core::fmt;
use std::fmt::{Display, Formatter};
use std::iter::FromIterator;
use std::rc::Rc;
use std::str::FromStr;
use std::string::ToString;

use heck::CamelCase;
use strum;
use strum_macros::EnumString;

use crate::lexer::Token::BlockToken;

#[derive(Debug, Eq, PartialEq)]
pub struct SrcInfo {
    //filename: Rc<String>,
    pub line: u64,
    pub column: u64
}

#[derive(Debug, Eq, PartialEq)]
pub enum Token {
    KeywordToken(Keyword),
    BlockToken(BlockDelimiter),
    NameToken(String)
}

impl Display for Token {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            Token::KeywordToken(keyword) => {
                write!(f, "KeywordToken: {}", keyword.to_string())
            }
            Token::BlockToken(delimiter) => {
                write!(f, "BlockToken: {}", delimiter.to_string())
            }
            Token::NameToken(name) => {
                write!(f, "NameToken: {}", name)
            }
        }
    }
}

#[derive(strum_macros::Display, Debug, Eq, PartialEq, EnumString)]
#[strum(serialize_all = "lowercase")]
pub enum Keyword {
    Sprite
}

#[derive(strum_macros::Display, Debug, Eq, PartialEq, EnumString)]
pub enum BlockDelimiter {
    #[strum(serialize="{")]
    LeftBracket,
    #[strum(serialize="}")]
    RightBracket
}

pub type Tokens = Vec<(SrcInfo, Box<Token>)>;

pub struct Lexer {
    pub source: String,
    pub tokens: Tokens
}

impl Lexer {

    pub fn new(source: String) -> Self {
        Self {
            source,
            tokens: vec![]
        }
    }

    pub fn lex(&mut self) {
        let mut current_token_buffer: Vec<char> = Vec::new();
        let mut raw_tokens: Vec<(String, SrcInfo)> = Vec::new();
        let mut last_line: u64 = 1;
        let mut last_column: u64 = 1;
        let mut line: u64 = 1;
        let mut column: u64 = 1;

        for c in self.source.chars() {
            // read up to next space
            if !c.is_whitespace() {
                current_token_buffer.push(c);
            } else {
                if !current_token_buffer.is_empty() {
                    raw_tokens.push((
                        String::from_iter(current_token_buffer.iter()),
                        SrcInfo {
                            line: last_line,
                            column: last_column
                        }
                    ));
                    if c == '\n' {
                        last_line = line + 1;
                        last_column = 0;
                    } else {
                        last_line = line;
                        last_column = column + 1;
                    }
                    current_token_buffer.clear();
                }
                if c == '\n' {
                    line += 1;
                    column = 0;
                }
            }
            column += 1;
        }
        if !current_token_buffer.is_empty() {
            raw_tokens.push((
                String::from_iter(current_token_buffer.iter()),
                SrcInfo {
                    line: last_line,
                    column: last_column
                }
            ));
        }

        for raw_token in raw_tokens {
            let raw_token_slice: &str = &*(raw_token.0);

            match Keyword::from_str(raw_token_slice) {
                Ok(keyword) => {
                    self.tokens.push((raw_token.1, Box::from(Token::KeywordToken(keyword))))
                }
                Err(_) => {
                    match BlockDelimiter::from_str(raw_token_slice) {
                        Ok(delimiter) => {
                            self.tokens.push((raw_token.1, Box::from(Token::BlockToken(delimiter))))
                        },
                        Err(_) => {
                            self.tokens.push((raw_token.1, Box::from(Token::NameToken(raw_token.0))))
                        }
                    }
                }
            }
        }

        for token in &self.tokens {
            println!("{}", token.1.to_string())
        }

    }

}