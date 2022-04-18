#if defined _core_included
	#endinput
#endif
#define _core_included

#pragma tabsize 4

native strlen(const string[]);
native print(const string[]);

native format(output[], len, const format[], {Float,_}:...); /* returns 1 if the length of the result string exceeded "len" and got truncated */

native timestamp(); /* returns 32-bit unix timestamp */
native GetTime(&hour, &minute, &second);
native GetDate(&day, &month, &year);

native random(max); /* returns an integer in range between 0 to max-1*/