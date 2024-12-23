class Foo {
    enum A {
        val0 = 0,
        val1,
        val2,
    };

    enum class B: A {
        valX = A.val0,
        valY = x[0]
    };
};