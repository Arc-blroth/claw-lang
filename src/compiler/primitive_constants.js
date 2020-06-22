module.exports = {
	// Taken from https://github.com/LLK/scratch-vm/blob/develop/src/serialization/sb3.js#L60
	// Constants referring to 'primitive' blocks that are either shadows, variables, or lists
	MATH_NUM_PRIMITIVE: 4,      // math_number
    POSITIVE_NUM_PRIMITIVE: 5,  // math_positive_number
    WHOLE_NUM_PRIMITIVE: 6,     // math_whole_number
    INTEGER_NUM_PRIMITIVE: 7,   // math_integer
    ANGLE_NUM_PRIMITIVE: 8,     // math_angle
    COLOR_PICKER_PRIMITIVE: 9,  // colour_picker
    TEXT_PRIMITIVE: 10,         // text
    BROADCAST_PRIMITIVE: 11,    // event_broadcast_menu
    VAR_PRIMITIVE: 12,          // data_variable
    LIST_PRIMITIVE: 13          // data_listcontents
};