const acorn = require("acorn")
const walk = require("acorn-walk")

const spriteTokenType = new acorn.TokenType("sprite", {keyword: "sprite", startsExpr: true});

function clawscript(Parser) {
	const isIdentifierStart = acorn.isIdentifierStart;
  
	return class extends Parser {
		readToken(code) {
			let context = this.curContext();
			if (isIdentifierStart(code) && code === 115) {
				let spriteToken = this.input.slice(this.pos, this.pos + 6);
				if(spriteToken === "sprite") {
					this.pos += 6;
					return this.finishToken(spriteTokenType, spriteToken);
				}
			}
			super.readToken(code);
		}
		
		parseStatement(context, topLevel, exports) {
			let node = this.startNode();
			if(this.type == spriteTokenType) {
				if (context) this.unexpected();
				return this.parseClass(node, true);
			}
			if(this.type.label == "class") {
				let where = this.claw_getLinePosString(this.input, this.start);
				console.warn(`Please use 'sprite' instead of 'class' (${where})`);
			}
			return super.parseStatement(context, topLevel, exports);
		}
		
		claw_getLinePosString(input, pos) {
			let beforePos = input.slice(0, pos);
			let line = (beforePos.match(/\n/g)||[]).length + 1;
			let lineStart = beforePos.lastIndexOf("\n") + 1;
			return `${line}:${pos - lineStart}`;
		}
	}
}

module.exports = clawscript;