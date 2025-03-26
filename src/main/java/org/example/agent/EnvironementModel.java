package org.example.agent;

public class EnvironementModel {
    private int[][] grid;
    private int width;
    private int height;

    public EnvironementModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[width][height];
    }

    public void setDirty(int x, int y) {
        if (isValidPosition(x, y)) {
            grid[x][y] = 1;
        }
    }

    public void cleanCell(int x, int y) {
        if (isValidPosition(x, y)) {
            grid[x][y] = 0;
        }
    }

    public boolean isDirty(int x, int y) {
        return isValidPosition(x, y) && grid[x][y] == 1;
    }
    public int countDirtyCells() {
        int count = 0;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) count++; // 1 = saleté
            }
        }
        return count;
    }
    public boolean isClean() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] == 1) return false;
            }
        }
        return true;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int[][] getGrid() {
        return grid;
    }
}