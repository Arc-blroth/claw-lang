const Parser = require('./compiler/parser.js');

let parser = new Parser();
parser.parse("sprite MySprite { onGreenFlag() {say(\"hi\"); let x = 10; move(x); ifOnEdgeBounce(); } }");
parser.assembler.assemble();