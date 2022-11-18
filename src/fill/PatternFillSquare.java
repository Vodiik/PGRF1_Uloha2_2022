package fill;

public class PatternFillSquare implements PatternFill {
    int[][] pattern = new int[][]{ // vzor čtverců
            {0x000000, 0xFFFFFF},
            {0xFFFFFF, 0x000000}
    };

    @Override
    public int getColor(int x, int y) {
        int i = x % pattern.length;
        int j = y % pattern[i].length;
        return pattern[i][j];
    }
}
