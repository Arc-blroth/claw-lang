const NativeBlocks = require('./native_blocks.js');
const primitives = require("./primitive_constants.js");

class BlockFactory {
	
	constructor() {
		this.idCount = 0;
	}
	
	/**
	 * Builds a block with the given opcode and inputs
	 * @param {string} opcode block opcode
	 * @param {array} inputs clawscript input array
	 * @return {object} block
	 */
	buildBlock(opcode, inputs) {
		inputs = inputs === undefined ? [] : inputs;
		let nativeBlock = NativeBlocks[opcode];
		if(nativeBlock === undefined) throw "Unknown block '" + opcode + "'";
		if(nativeBlock.inputPrimitiveTypes.length < inputs.length) {
			throw "This block requires " + nativeBlock.inputPrimitiveTypes.length + " arguments, but only recieved " + inputs.length + ".";
		}
		let currentId = this.idCount++;
		let block = {
			id: currentId.toString(),
			opcode: opcode,
			parent: null,
			next: null,
			topLevel: nativeBlock.isTopLevel
		};
		if(inputs.length > 0) {
			let scratch_inputs = [];
			for(let i = 0; i < nativeBlock.inputPrimitiveTypes.length; i++) {
				switch(nativeBlock.inputPrimitiveTypes[i]) {
					case primitives.MATH_NUM_PRIMITIVE: {
						scratch_inputs.push(Number(inputs[i]));
						break;
					}
					case primitives.POSITIVE_NUM_PRIMITIVE: {
						scratch_inputs.push(Math.abs(Number(inputs[i])));
						break;
					}
					case primitives.WHOLE_NUM_PRIMITIVE: {
						scratch_inputs.push(parseInt(inputs[i]));
						break;
					}
					case primitives.INTEGER_NUM_PRIMITIVE:
					case primitives.ANGLE_NUM_PRIMITIVE: {
						scratch_inputs.push(parseFloat(inputs[i]));
						break;
					}
					case primitives.TEXT_PRIMITIVE: {
						scratch_inputs.push(i.toString());
						break;
					}
					default: {
						scratch_inputs.push(null);
						break;
					}
				}
			}
			block.inputs = {};
			for(let i = 0; i < nativeBlock.inputPrimitiveTypes.length; i++) {
				let primitive_block = [nativeBlock.inputPrimitiveTypes[i], scratch_inputs[i]];
				block.inputs[nativeBlock.inputPrimitiveNames[i]] = {
					name: nativeBlock.inputPrimitiveNames[i],
					block: primitive_block,
					shadow: primitive_block
				};
			}
		}
		return block;
	}
	
	link(parent, child) {
		parent.next = child.id;
		child.parent = parent.id;
	}
	
}

module.exports = BlockFactory;