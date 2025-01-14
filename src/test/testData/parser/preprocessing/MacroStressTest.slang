#define DECLARE_STRUCT(name, declarations...) struct name {     \
    declarations                                                \
};

#define DECLARE_VARIABLE(type, name, value) \
type name = value;

#define BASE_VARIABLES          \
DECLARE_VARIABLE(int, a, 0)     \
DECLARE_VARIABLE(float, b, 0.5f)

DECLARE_STRUCT(Foo, BASE_VARIABLES)
DECLARE_STRUCT(Bar, DECLARE_VARIABLE(uint, x, 0))