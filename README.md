# Pawn for Java!

Pawn for Java is a hobby test project if it is possible to implement the Pawn AMX runtime in Java (https://www.compuphase.com/pawn/pawn.htm).

## Pawn Version

The target version of pawn is the same that **SA:MP** uses.

Not all instructions are implemented yet.
Current working example is below.

## How to use

```
Amx amx = new Amx();  
Script timertest = amx.loadFromFile("Desktop/timertest.amx");
timertest.executePublic("OnGameModeInit");
```

The main function of the given script is executed when the script is loaded.
## Developing plugins
`native print(text[]);`
```
Amx amx = new Amx();
amx.loadPlugin(new BuiltInRuntimePlugin());
```
```
public class BuiltInRuntimePlugin implements Plugin {  
  
  @Override  
  public void onPluginLoad(AmxContext amxContext) {  
	  amxContext.addNative("AddPlayerClass", new AddPlayerClass());  
	  amxContext.addNative("GetTickCount", new GetTickCount());  
	  amxContext.addNative("print", new Print());
	  amxContext.addNative("SetGameModeText", new SetGameModeText());
	  amxContext.addNative("SetTimer", new SetTimer());
	  amxContext.addNative("format", new Format());
	  amxContext.addNative("SendClientMessageToAll", new SendClientMessageToAll());  
  }  
}
```
```
@Log4j2  
public class Print implements NativeCallback {  
  
  @Override  
  public int call(ExecutionContext executionContext, List<Integer> callArguments) {  
	  log.info("{}", executionContext.readString(callArguments.get(0)));  
	  return 0;  
  }  
}
```

## Current working example
The file has to be compiled in 32-bit Pawn mode and use the command line:
`-C- -O0 -d0`

```
#include <a_samp>

forward OneSecTimer();

main()
{
	print("\n----------------------------------");
	print("  This is a blank GameModeScript");
	print("----------------------------------\n");
	
	//printf("GetVehicleComponentType %u",GetVehicleComponentType(1100));
	
}

public OnGameModeInit()
{
	// Set timer of 1 second.
	SetTimer("OneSecTimer", 1000, 1);
	print("GameModeInit()");
	SetGameModeText("Timer Test");
	AddPlayerClass(0, 1958.3783, 1343.1572, 15.3746, 269.1425, 0, 0, 0, 0, 0, 0);
	return 1;
}

public OneSecTimer() {
	new sText[256];
	format(sText,sizeof(sText),"GetTickCount = %d",GetTickCount());
	print(sText);
	SendClientMessageToAll(0xFF0000, sText);
}
```
