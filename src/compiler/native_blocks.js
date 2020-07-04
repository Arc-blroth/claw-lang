const primitives = require("./primitive_constants.js");

class NativeBlock {
	
	constructor(opcode, clawFunction, inputPrimitiveNames, inputPrimitiveTypes, isTopLevel) {
		this.opcode = opcode;
        this.clawFunction = clawFunction;
		this.inputPrimitiveNames = inputPrimitiveNames;
		this.inputPrimitiveTypes = inputPrimitiveTypes;
		this.isTopLevel = isTopLevel || false;
	}
	
}

NativeBlocks = {};
NativeBlocks.AsClawFunctions = {};

function registerBlock(opcode, clawFunction, inputPrimitiveNames, inputPrimitiveTypes, isTopLevel) {
	if(inputPrimitiveNames.length != inputPrimitiveTypes.length) throw "input primitive length mismatch";
	let block = new NativeBlock(opcode, clawFunction, inputPrimitiveNames, inputPrimitiveTypes, isTopLevel === undefined ? false : isTopLevel);
    NativeBlocks[opcode] = block;
    if(!!clawFunction) {
        NativeBlocks.AsClawFunctions[clawFunction] = block;
    }
}

registerBlock("motion_movesteps", "move", ["STEPS"], [primitives.MATH_NUM_PRIMITIVE]);
registerBlock("motion_turnright", "turnRight", ["DEGREES"], [primitives.ANGLE_NUM_PRIMITIVE]);
registerBlock("motion_turnleft", "turnLeft", ["DEGREES"], [primitives.ANGLE_NUM_PRIMITIVE]);
registerBlock("motion_pointindirection", "pointInDirection", ["DIRECTION"], [primitives.ANGLE_NUM_PRIMITIVE]);
registerBlock("motion_gotoxy", "goTo", ["X", "Y"], [primitives.MATH_NUM_PRIMITIVE, primitives.MATH_NUM_PRIMITIVE]);
registerBlock("motion_glidesecstoxy", "glideTo", ["SECS", "X", "Y"], [primitives.MATH_NUM_PRIMITIVE, primitives.MATH_NUM_PRIMITIVE, primitives.MATH_NUM_PRIMITIVE]);
registerBlock("motion_changexby", "changeX", ["DX"], [primitives.MATH_NUM_PRIMITIVE]);
registerBlock("motion_setx", "setX", ["X"], [primitives.MATH_NUM_PRIMITIVE]);
registerBlock("motion_changeyby", "changeY", ["DY"], [primitives.MATH_NUM_PRIMITIVE]);
registerBlock("motion_sety", "setY", ["Y"], [primitives.MATH_NUM_PRIMITIVE]);
registerBlock("motion_ifonedgebounce", "ifOnEdgeBounce", [], []);

registerBlock("looks_say", "say", ["MESSAGE"], [primitives.TEXT_PRIMITIVE]);

registerBlock("event_whenflagclicked", null, [], [], true);

module.exports = NativeBlocks;