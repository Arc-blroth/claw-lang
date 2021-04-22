lexer grammar ClawLexer;

// Keywords
FUNCTION:           'function';
PUBLIC:             'public';
SPRITE:             'sprite';

// Seperators
LBRACE:             '{';
RBRACE:             '}';
LPAREN:             '(';
RPAREN:             ')';

// Misc
IDENTIFIER:         [a-zA-Z_]? [a-zA-Z$_]+;

// Whitespace
NEWLINE:            '\r'? '\n' -> skip;
WS:                 [ \t]+ -> skip;
COMMENT:            '//'.*? NEWLINE;
