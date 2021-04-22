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
    : visibility FUNCTION IDENTIFIER functionBody
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