parser grammar ClawParser;

options {
    tokenVocab = ClawLexer;
}

compilationUnit
    : spriteDeclaration EOF
    ;

spriteDeclaration
    : SPRITE IDENTIFIER spriteBody
    ;

spriteBody
    : LBRACE spriteBodyDeclaration RBRACE
    ;

spriteBodyDeclaration
    : functionDeclaration*
    ;

functionDeclaration
    : visibility? FUNCTION IDENTIFIER functionArgs functionBody
    ;

functionArgs
    : LPAREN functionArgsDeclaration RPAREN
    ;

functionArgsDeclaration
    :
    ;

functionBody
    : LBRACE functionBodyDeclaration RBRACE
    ;

functionBodyDeclaration
    :
    ;

visibility
    : PUBLIC
    ;