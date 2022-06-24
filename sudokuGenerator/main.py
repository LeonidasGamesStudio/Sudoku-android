# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
from copy import deepcopy
from random import randint

from sudokuSolver import solve_sudoku, find_all_solutions


def remove_random_number(grid):
    while 1:
        i = randint(0, 8)
        j = randint(0, 8)
        if grid[i][j] != 0:
            grid[i][j] = 0
            return i, j


sudokuGrid = [[9, 0, 7, 5, 0, 0, 6, 0, 3],
              [4, 3, 0, 0, 0, 6, 0, 0, 5],
              [6, 0, 8, 1, 0, 9, 0, 2, 7],
              [2, 0, 6, 4, 5, 0, 0, 0, 0],
              [0, 0, 1, 0, 6, 0, 3, 4, 0],
              [7, 0, 0, 0, 0, 8, 0, 5, 0],
              [8, 0, 0, 7, 0, 0, 1, 3, 0],
              [0, 7, 4, 0, 2, 0, 5, 9, 0],
              [1, 0, 9, 3, 0, 5, 0, 0, 0]]

emptyGrid = [[0 for col in range(9)] for row in range(9)]
solve_sudoku(emptyGrid)

find_all_solutions(deepcopy(emptyGrid))
newGrid = deepcopy(sudokuGrid)

while True:
    x, y = remove_random_number(newGrid)
    solutions = find_all_solutions(deepcopy(newGrid))
    if solutions != 1:
        print(solutions)
        break
    else:
        sudokuGrid[x][y] = 0

for row in sudokuGrid:
    print(row)