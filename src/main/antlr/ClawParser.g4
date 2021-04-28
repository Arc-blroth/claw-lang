parser grammar ClawParser;

options {
    tokenVocab = ClawLexer;
}

compilationUnit
    : nls (topLevelDeclaration nls)* EOF
    ;

topLevelDeclaration
    : spriteDeclaration
    | functionDeclaration
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
    : (modifier nls)* nls FUNCTION nls IDENTIFIER nls functionArgs nls functionBody
    ;

functionArgs
    : LPAREN nls functionArgsDeclaration nls RPAREN
    ;

functionArgsDeclaration
    : (functionArgDeclaration nls COMMA nls)* functionArgDeclaration? nls COMMA?
    ;

functionArgDeclaration
    : IDENTIFIER nls COLON nls IDENTIFIER
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
    : (functionCallArg nls COMMA nls)* functionCallArg? COMMA?
    ;

functionCallArg
    : primitive
    ;

primitive
    : NumberLiteral
    | StringLiteral
    ;

modifier
    : visibility
    | INTRINSIC
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