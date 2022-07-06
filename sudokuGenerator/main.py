# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
from copy import deepcopy
from random import randint

from sudokuSolver import solve_sudoku, find_all_solutions

sudokuSize = 9


def find_solvable_puzzle(grid, threshold, size):
    count = size * size
    while count > threshold:
        nextStepGrid = deepcopy(grid)
        x, y, number, symNumber = remove_random_number(nextStepGrid)
        if find_all_solutions(deepcopy(nextStepGrid)) != 1:
            nextStepGrid[x][y] = number
            nextStepGrid[8 - x][8 - y] = symNumber
        else:
            grid[x][y] = 0
            grid[8 - x][8 - y] = 0
            count = count - 2


def remove_rotational_symmetry(grid, i, j):
    x = 8 - i
    y = 8 - j
    num = grid[x][y]
    grid[x][y] = 0
    return num


def remove_random_number(grid):
    while 1:
        i = randint(0, 8)
        j = randint(0, 8)
        if grid[i][j] != 0:
            symNum = remove_rotational_symmetry(grid, i, j)
            num = grid[i][j]
            grid[i][j] = 0
            return i, j, num, symNum


def count_numbers(grid):
    count = 0
    for i in range(0, sudokuSize):
        for j in range(0, sudokuSize):
            if grid[i][j] != 0:
                count = count + 1
    return count


sudokuGrid = [[9, 0, 7, 5, 0, 0, 6, 0, 3],
              [4, 3, 0, 0, 0, 6, 0, 0, 5],
              [6, 0, 8, 1, 0, 9, 0, 2, 7],
              [2, 0, 6, 4, 5, 0, 0, 0, 0],
              [0, 0, 1, 0, 6, 0, 3, 4, 0],
              [7, 0, 0, 0, 0, 8, 0, 5, 0],
              [8, 0, 0, 7, 0, 0, 1, 3, 0],
              [0, 7, 4, 0, 2, 0, 5, 9, 0],
              [1, 0, 9, 3, 0, 5, 0, 0, 0]]

testGrid = [[9, 0, 7, 5, 0, 0, 6, 0, 3],
            [4, 3, 0, 0, 0, 6, 0, 0, 5],
            [6, 0, 8, 1, 0, 9, 0, 2, 7],
            [2, 0, 6, 4, 5, 0, 0, 0, 0],
            [0, 0, 1, 0, 0, 0, 3, 4, 0],
            [7, 0, 0, 0, 0, 8, 0, 5, 0],
            [8, 0, 0, 7, 0, 0, 1, 3, 0],
            [0, 7, 4, 0, 2, 0, 5, 9, 0],
            [1, 0, 9, 3, 0, 5, 0, 0, 0]]

emptyGrid = [[0 for col in range(sudokuSize)] for row in range(sudokuSize)]
solve_sudoku(emptyGrid)
for row in emptyGrid:
    print(row)

find_solvable_puzzle(emptyGrid, 28, sudokuSize)

print(count_numbers(emptyGrid))
for row in emptyGrid:
    print(row)
