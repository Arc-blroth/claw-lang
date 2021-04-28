lexer grammar ClawLexer;

// Keywords
FUNCTION:           'function';
INTRINSIC:          'intrinsic';
PUBLIC:             'public';
SPRITE:             'sprite';

// Seperators
LPAREN:             '(';
RPAREN:             ')';
LBRACE:             '{';
RBRACE:             '}';
BACKSLASH:          '\\';
SEMICOLON:          ';';
COLON:              ':';
QUOTE:              '"';
COMMA:              ',';
PERIOD:             '.';
SLASH:              '/';

NumberLiteral
    : '-'? Digit* (Digit '.'? | '.' Digit) Digit*
    ;

fragment Digit
    : [0-9]
    ;

StringLiteral
    : QUOTE StringLiteralChar* QUOTE
    ;

fragment StringLiteralChar
    : ~["\r\n]
    | StringLiteralCharEscapeSequence
    ;

fragment StringLiteralCharEscapeSequence
    : BACKSLASH ["\r\n]
    ;

// Misc
IDENTIFIER:         [a-zA-Z_]? [a-zA-Z$_]+;

// Whitespace
NL:                 '\r'? '\n';
WS:                 [ \t]+ -> skip;
COMMENT:            '//' .*? NL -> skip;
MULTILINE_COMMENT:  '/*' .*? '*/' -> skip;