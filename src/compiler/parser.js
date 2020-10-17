const acorn = require("acorn");
const Assembler = require('./assembler.js');
const NativeBlocks = require('./native_blocks.js');

class Parser {
    
    constructor() {
        this.parser = acorn.Parser.extend(
          require("./acorn-clawscript.js")
        );
        this.assembler = new Assembler();
    }
    
    parse(input) {
        let program = this.parser.parse(input);
        program.body
            .filter(node => node.type === "ClassDeclaration")
            .forEach(node => {
                this.assembler.pushSprite(node.name);
                node.body.body
                    .filter(methodNode => methodNode.type === "MethodDefinition")
                    .forEach(methodNode => {
                        this.assembler.pushFunction(methodNode.key.name);
                        if(methodNode.value.body.type === "BlockStatement") {
                            methodNode.value.body.body
                                .forEach(statementNode => this._handleStatement.call(this, statementNode));
                        }
                        this.assembler.popFunction(methodNode.key.name);
                    });
                this.assembler.popSprite(node.name);
            });
    }
    
    _handleStatement(statementNode) {
        console.log(statementNode);
        if(statementNode.type === "ExpressionStatement") {
            let expression = statementNode.expression;
            if(expression.type === "CallExpression") {
                if(!!NativeBlocks.AsClawFunctions[expression.callee.name]) {
                    let opcode = NativeBlocks.AsClawFunctions[expression.callee.name].opcode;
                    let args = expression.arguments.map(arg => arg.value);
                    this.assembler.pushBlock(opcode, args);
                }
            }
        }
    }
    
}

module.exports = Parser;