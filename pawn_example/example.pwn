#include <pawn4j>

main()
{
	print("This is an example script!");

	new text[128];
	new hour, minute, second;
	new day, month, year;
	new textLen;

	GetTime(hour, minute, second);
	format(text, sizeof(text), "Time: %d:%d:%d", hour, minute, second);
	print(text);

	GetDate(day, month, year);
	format(text, sizeof(text), "Date: %d:%d:%d", day, month, year);
	print(text);

	textLen = strlen(text);
	format(text, sizeof(text), "Prev text len: %d", textLen);
	print(text);

	format(text, sizeof(text), "random: %d, timestamp: %d", random(1337), timestamp());
	print(text);

	new abc = 3;
	abc++;
	abc+=5;
	abc-=3;
	abc--;

	format(text, sizeof(text), "ABC: %d", abc);
	print(text);
}

forward OnGameModeInit();
public OnGameModeInit() {
    print("OnGameModeInit called!");
    return 1;
}
