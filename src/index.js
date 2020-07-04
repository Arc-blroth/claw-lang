const Parser = require('./compiler/parser.js');

let parser = new Parser();
parser.parse("sprite MySprite { onGreenFlag() {say(\"hi\"); move(10); ifOnEdgeBounce(); } }");
parser.assembler.assemble();