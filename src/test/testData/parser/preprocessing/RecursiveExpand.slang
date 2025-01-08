#define A 1
#define B A
#define C A + B

struct Foo { int a = C; }