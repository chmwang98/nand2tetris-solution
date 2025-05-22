// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.


@SCREEN
D=A
@addr
M=D

(READ_INPUT)
    @KBD
    D=M
    @BLACK
    D;JNE

// WHITE
    @pixel
    MD=0
    @FILL_LOOP
    0;JMP

(BLACK)
    @pixel
    MD=-1

(FILL_LOOP)
    @addr
    A=M
    M=D     // set content in addr to pixel

    @addr
    M=M+1   // addr++

    @24576  // end of screen = screen + 8192
    D=A
    @addr
    D=D-M
    @READ_INPUT
    D;JGT   // if addr < 24576, continue 
    @SCREEN
    D=A
    @addr   // reset addr
    M=D
    @READ_INPUT
    0;JMP