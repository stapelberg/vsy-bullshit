/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _JSONRequestTypes_h_ 
#define _JSONRequestTypes_h_

// Lol, RTTI
enum JSONRequestType {
	JSON_Get = 0,
	JSON_REGISTER_PLAYER = 1,
	JSON_CURRENT_GAMES = 2,
	JSON_CREATE_GAME = 3,
	JSON_JOIN_GAME = 4,
	JSON_LEAVE_GAME = 5,
	JSON_MAKE_MOVE = 6,
	JSON_CHECK_WINNER = 7
}; 

#endif