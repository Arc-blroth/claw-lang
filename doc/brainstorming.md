# Primitives

bool
num (any number)
int (whole numbers)
color (color8; 24 bit)
char (unicode)
string

"color.green syntax that does color >> 8 & 0xFF would be nice." -access transfarmer

# Other Types
list

# Stuctures
enum
class // c++ style mangling time
sprite

## Enums

Enums, short for enumeration, store a limited set of types:

    enum Pets {
        CAT,
        DOG,
        DRAGON
    }

## Sprites

Sprites are the main structure in Claw. They translate directly
to Scratch's sprites. All sprites must be declared in the global
scope and each must have a unique name.

    sprite MySprite {
        function onGreenFlag() {
            say("Hello World!", 1)
        }
    }

## Classes

Classes can have variables and functions. Note that both class
names and variables may be mangled by the compiler.

# Variables

Variables can have one of 3 scopes: global, class, or function.

Variables are declared with the syntax 

    var name: type[ = value]
    
If value is not specified, a default 0 or empty value will be allocated.

Global variables can specify an extra modifier `cloud`:

    cloud var highscore: int
    
This value will be updated atomically with other running instances of this program, if the program is connected to the internet.

# Functions

Functions are declared with the syntax

    [public|private] function name([args]) -> [return] {
    
    }