use core::fmt;
use std::fmt::{Display, Formatter};
use std::iter::FromIterator;
use std::str::FromStr;
use std::string::ToString;

use heck::CamelCase;
use strum;
use strum_macros::EnumString;

use crate::lexer::Token::BlockToken;

#[derive(Debug)]
pub enum Token {
    KeywordToken(Keyword),
    BlockToken(BlockDelimiter)
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
        }
    }
}

#[derive(strum_macros::Display, Debug, EnumString)]
#[strum(serialize_all = "lowercase")]
pub enum Keyword {
    Sprite
}

#[derive(strum_macros::Display, Debug, EnumString)]
pub enum BlockDelimiter {
    #[strum(serialize="{")]
    LeftBracket,
    #[strum(serialize="}")]
    RightBracket
}

pub struct Lexer {
    source: String,
    tokens: Vec<Box<Token>>
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
        let mut raw_tokens: Vec<String> = Vec::new();

        for c in self.source.chars() {
            // read up to next space
            if !c.is_whitespace() {
                current_token_buffer.push(c);
            } else {
                if !current_token_buffer.is_empty() {
                    raw_tokens.push(String::from_iter(current_token_buffer.iter()));
                    current_token_buffer.clear();
                }
            }
        }
        if !current_token_buffer.is_empty() {
            raw_tokens.push(String::from_iter(current_token_buffer.iter()));
        }

        for raw_token in raw_tokens {
            let raw_token_slice: &str = &*raw_token;

            match Keyword::from_str(raw_token_slice) {
                Ok(keyword) => {
                    self.tokens.push(Box::from(Token::KeywordToken(keyword)))
                }
                Err(_) => {
                    match BlockDelimiter::from_str(raw_token_slice) {
                        Ok(delimiter) => {
                            self.tokens.push(Box::from(Token::BlockToken(delimiter)))
                        },
                        Err(_) => {

                        }
                    }
                }
            }
        }

        for token in &self.tokens {
            println!("{}", token.to_string())
        }

    }

}