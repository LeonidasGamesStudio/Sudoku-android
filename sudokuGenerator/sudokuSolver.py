# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import random

count = 0


def check_rows(grid, number, i, j):
    for k in range(0, 9):
        if k != j:
            if grid[i][k] == number:
                return True  ##True for clash
    return False


def check_columns(grid, number, i, j):
    for k in range(0, 9):
        if k != i:
            if grid[k][j] == number:
                return True  ##True for clash
    return False


def check_boxes(grid, number, i, j):
    # get box top left
    startX = int(i / 3) * 3
    startY = int(j / 3) * 3
    for k in range(startX, startX + 3):
        for l in range(startY, startY + 3):
            if k != i and l != j:
                if grid[k][l] == number:
                    return True
    return False


def check_if_safe(grid, number, i, j):
    if check_rows(grid, number, i, j) or check_columns(grid, number, i, j) or check_boxes(grid, number, i, j):
        return False
    return True


def find_next_coordinates(grid, i, j):
    if i == 0 and j == 0 and grid[i][j] == 0:
        return 0, 0
    while 1:
        if j == 8:
            if i == 8:
                return -1, 0
            else:
                i = i + 1
                j = 0
                if grid[i][j] == 0:
                    return i, j
        else:
            j = j + 1
            if grid[i][j] == 0:
                return i, j


def recursive_sudoku_solver(grid, number, i, j):
    if i == 9:
        return True
    grid[i][j] = number

    if check_if_safe(grid, number, i, j):
        x, y = find_next_coordinates(grid, i, j)
        if x == -1:
            return True
        possibleNumbers = [1, 2, 3, 4, 5, 6, 7, 8, 9]
        while len(possibleNumbers) > 0:
            randomNumber = random.choice(possibleNumbers)
            possibleNumbers.remove(randomNumber)
            if recursive_sudoku_solver(grid, randomNumber, x, y):
                return True

    grid[i][j] = 0
    return False

    # check for clashes, if none, go one deeper


# build sudoku generator first
def solve_sudoku(grid):
    x, y = find_next_coordinates(grid, 0, 0)
    possibleNumbers = [1, 2, 3, 4, 5, 6, 7, 8, 9]
    while len(possibleNumbers) > 0:
        randomNumber = random.choice(possibleNumbers)
        possibleNumbers.remove(randomNumber)
        if recursive_sudoku_solver(grid, randomNumber, x, y):
            break


def recursive_sudoku_counter(grid, number, i, j):
    global count
    if i == 9:
        count = count + 1
        return True
    grid[i][j] = number

    if check_if_safe(grid, number, i, j):
        x, y = find_next_coordinates(grid, i, j)
        if x == -1:
            count = count + 1
            return True
        for k in range(1, 10):
            recursive_sudoku_counter(grid, k, x, y)
    grid[i][j] = 0
    return False


def find_all_solutions(grid):
    count = 0
    x, y = find_next_coordinates(grid, 0, 0)
    for i in range(1, 10):
        recursive_sudoku_counter(grid, i, x, y)
    return count
