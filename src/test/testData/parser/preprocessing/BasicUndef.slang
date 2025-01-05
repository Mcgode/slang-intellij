#define A 1

int foo() { return A; }

#undef A
#undef B // It is legal to undef a non-defined macro

float bar() { return A; }