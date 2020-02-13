""" https://www.python-kurs.eu/python3_tests.php """


def fib(n):
    a, b = 0, 1
    for i in range(n):
        a, b = b, a + b
    return a


def fib_list(n):
    fib = [0, 1]
    for i in range(1, n):
        fib += [fib[-1] + fib[-2]]
    return fib


if __name__ == "__main__":
    if fib(0) == 0 and fib(10) == 55 and fib(50) == 12586269025:
        print("Test for Fibonacci function successful")
    else:
        print("Test for Fibonacci function not successful")
