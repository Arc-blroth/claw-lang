parser grammar ClawParser;

options {
    tokenVocab = ClawLexer;
}

compilationUnit
    : (spriteDeclaration nls)* EOF
    ;

spriteDeclaration
    : SPRITE nls IDENTIFIER nls spriteBody
    ;

spriteBody
    : LBRACE nls spriteBodyDeclaration nls RBRACE
    ;

spriteBodyDeclaration
    : (functionDeclaration nls)*
    ;

functionDeclaration
    : visibility? nls FUNCTION nls IDENTIFIER nls functionArgs nls functionBody
    ;

functionArgs
    : LPAREN nls functionArgsDeclaration nls RPAREN
    ;

functionArgsDeclaration
    :
    ;

functionBody
    : LBRACE nls functionBodyDeclaration nls RBRACE
    ;

functionBodyDeclaration
    : statements?
    ;

statements
    : statement (stmtSep statement)* nls
    ;

statement
    : functionCall
    ;

functionCall
    : IDENTIFIER nls LPAREN nls functionCallArgs nls RPAREN
    ;

functionCallArgs
    : (functionCallArg nls COMMA nls)* functionCallArg?
    ;

functionCallArg
    : primitive
    ;

primitive
    : NumberLiteral
    | StringLiteral
    ;

visibility
    : PUBLIC
    ;

stmtSep
    : NL | SEMICOLON
    ;

nls
    : NL*
    ;