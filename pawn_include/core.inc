#if defined _core_included
	#endinput
#endif
#define _core_included

#pragma tabsize 4

native print(const string[]);

native format(output[], len, const format[], {Float,_}:...); /* returns 1 if the length of the result string exceeded "len" and got truncated */

native timestamp(); /* returns 32-bit unix timestamp */
native GetTime(&hour, &minute, &second);