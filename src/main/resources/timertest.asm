
CODE 0	; 0
;program exit point
	halt 0

	proc	; main
	; line 6
	; line 7
	zero.pri
	push.pri
	;$par
	push.c 4
	sysreq.c 0	; print
	stack 8
	;$exp
	; line 8
	const.pri 90
	push.pri
	;$par
	push.c 4
	sysreq.c 0	; print
	stack 8
	;$exp
	; line 9
	const.pri 114
	push.pri
	;$par
	push.c 4
	sysreq.c 0	; print
	stack 8
	;$exp
	zero.pri
	retn


DATA 0	; 0
dump a 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 
dump 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 
dump 2d 2d 2d 0 20 20 54 68 69 73 20 69 73 20 61 20 
dump 62 6c 61 6e 6b 20 47 61 6d 65 4d 6f 64 65 53 63 
dump 72 69 70 74 0 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 
dump 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d 
dump 2d 2d 2d 2d 2d 2d 2d a 0 

CODE 0	; 7c
	proc	; OnGameModeInit
	; line 10
	; line 12
	const.pri 1
	push.pri
	;$par
	const.pri 3e8
	push.pri
	;$par
	const.pri 1a4
	push.pri
	;$par
	push.c c
	sysreq.c 1	; SetTimer
	stack 10
	;$exp
	; line 13
	const.pri 1d4
	push.pri
	;$par
	push.c 4
	sysreq.c 0	; print
	stack 8
	;$exp
	; line 14
	const.pri 210
	push.pri
	;$par
	push.c 4
	sysreq.c 2	; SetGameModeText
	stack 8
	;$exp
	; line 15
	zero.pri
	push.pri
	;$par
	zero.pri
	push.pri
	;$par
	zero.pri
	push.pri
	;$par
	zero.pri
	push.pri
	;$par
	zero.pri
	push.pri
	;$par
	zero.pri
	push.pri
	;$par
	const.pri 4386923d
	push.pri
	;$par
	const.pri 4175fe5d
	push.pri
	;$par
	const.pri 44a7e508
	push.pri
	;$par
	const.pri 44f4cc1b
	push.pri
	;$par
	zero.pri
	push.pri
	;$par
	push.c 2c
	sysreq.c 3	; AddPlayerClass
	stack 30
	;$exp
	; line 16
	const.pri 1
	retn


DATA 0	; 1a4
dump 4f 6e 65 53 65 63 54 69 6d 65 72 0 47 61 6d 65 
dump 4d 6f 64 65 49 6e 69 74 28 29 0 54 69 6d 65 72 
dump 20 54 65 73 74 0 

CODE 0	; 190
	proc	; OneSecTimer
	; line 19
	; line 1a
	;$lcl sText fffffc00
	stack fffffc00
	zero.pri
	addr.alt fffffc00
	fill 400
	; line 1b
	push.c 0
	sysreq.c 4	; GetTickCount
	stack 4
	heap 4
	stor.i
	move.pri
	push.pri
	;$par
	const.pri 23c
	push.pri
	;$par
	const.pri 100
	push.pri
	;$par
	addr.pri fffffc00
	push.pri
	;$par
	push.c 10
	sysreq.c 5	; format
	stack 14
	heap fffffffc
	;$exp
	; line 1c
	addr.pri fffffc00
	push.pri
	;$par
	push.c 4
	sysreq.c 0	; print
	stack 8
	;$exp
	; line 1d
	addr.pri fffffc00
	push.pri
	;$par
	const.pri ff0000
	push.pri
	;$par
	push.c 8
	sysreq.c 6	; SendClientMessageToAll
	stack c
	;$exp
	stack 400
	zero.pri
	retn


DATA 0	; 23c
dump 47 65 74 54 69 63 6b 43 6f 75 6e 74 20 3d 20 25 
dump 64 0 

STKSIZE 1000
