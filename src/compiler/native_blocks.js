const primitives = require("./primitive_constants.js");

class NativeBlock {
	
	constructor(opcode, inputPrimitiveNames, inputPrimitiveTypes, isTopLevel) {
		this.opcode = opcode;
		this.inputPrimitiveNames = inputPrimitiveNames;
		this.inputPrimitiveTypes = inputPrimitiveTypes;
		this.isTopLevel = isTopLevel || false;
	}
	
}

NativeBlocks = {};

function registerBlock(opcode, inputPrimitiveNames, inputPrimitiveTypes, isTopLevel) {
	if(inputPrimitiveNames.length != inputPrimitiveTypes.length) throw "input primitive length mismatch";
	NativeBlocks[opcode] = new NativeBlock(opcode, inputPrimitiveNames, inputPrimitiveTypes, isTopLevel === undefined ? false : isTopLevel);
}

registerBlock("motion_movesteps", ["STEPS"], [primitives.MATH_NUM_PRIMITIVE]);
registerBlock("event_whenflagclicked", [], [], true);

module.exports = NativeBlocks;