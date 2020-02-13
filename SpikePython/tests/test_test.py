import unittest
from src.spike_test import fib


class TestTest(unittest.TestCase):

    def setUp(self):
        self.fib_elements = ((0, 0), (1, 1), (2, 1), (3, 2), (4, 3), (5, 5), (30, 832040))
        print("setUp executed!")

    def testCalculation(self):
        for (i, val) in self.fib_elements:
            self.assertEqual(fib(i), val)

    def tearDown(self):
        # Delete only for demonstration purposes
        self.fib_elements = None
        print("tearDown executed!")


if __name__ == "__main__":
    unittest.main()
