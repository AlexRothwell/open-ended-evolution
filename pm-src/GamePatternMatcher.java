import java.util.*;

public class GamePatternMatcher
{
    /* Method checks to see if the given matrix matches the pattern given
    *  pattern  -   string comprised of integers, < and = denoting the pattern to be checked
    *  matrix   -   integer array with length nxn where n is the number of actions in the game, a 1D mapping
    *  returns whether the matrix matches the pattern
    */
    public static boolean checkMatch(Pattern pattern, Game game)
    {
        double[] matrix = game.getRewards();
        int i = 0;
        int j = 0;
        double first,second;
        boolean less_than = false;
        boolean start = true;
        ArrayList<Integer> values = pattern.pattern;
        if (values.size() == 0)
        {
            return true;
        }
        do
        {
            if (start)
            {
                //Find first cell in game
                while(!inGame(values.get(i),matrix) && i < values.size() - 2)
                {
                    i += 2;
                }
                
                if (i < values.size() - 1)
                {
                    less_than = values.get(i+1) == Utils.LT;
                }
                start = false;
            }
            else
            {
                i = j;
            }
            j = i + 2;
            //Find second cell in game
            while (!inGame(j,matrix) && j < values.size())
            {
                //Determine if the relation is <
                //(if a chain contains any <, then the entire relation is <)
                less_than |= values.get(j-1) == Utils.LT;
                j += 2;
            }
            //If there isn't two values to compare, then ____
            if (i > values.size() - 3 || j > values.size())
            {
                return true;
            }
            if (!inGame(values.get(i),matrix) || !inGame(values.get(j),matrix))
            {
                return true;
            }
            first = getMatrixValue(values.get(i),matrix);
            second = getMatrixValue(values.get(j),matrix);
            if(!less_than)
            {
                if(first != second)
                {
                    return false;
                }
            }
            else
            {
                if (!(first < second))
                {
                    return false;
                }
            }
        } while(j < values.size());
        return true;
    }

    private static int getMatrixValue(int index, double[] matrix)
    {
        if (index == 0)
        {
            return 0;
        }
        else
        {
            return (int)matrix[index - 1];
        }
    }
    
    private static boolean inGame(int num, double[] matrix)
    {
        return (num == 0) || (num <= matrix.length);
    }

    
    public static void main(String[] args)
    {
        //Test data
        int EQ = Utils.EQ;
        int LT = Utils.LT;
        final double[] PD_MATRIX = {2,3,0,1};
        final double[] BIG_MATRIX = {3,0,1,0,5,0,2,0,1};
        final Game PD = new Game(10,PD_MATRIX);
        final Game BIG = new Game(10,BIG_MATRIX);
        final Pattern TEST_1_PATTERN = new Pattern(new int[]{0,EQ,3,LT,4,LT,1,LT,2});
        final Pattern TEST_2_PATTERN = new Pattern(new int[]{4,LT,1});
        final Pattern TEST_3_PATTERN = new Pattern(new int[]{0,EQ,3,LT,2});
        final Pattern TEST_4_PATTERN = new Pattern(new int[]{0,EQ,2,EQ,4,EQ,6,EQ,8,LT,3,EQ,9,LT,7,LT,1,LT,5});
        
        
        System.out.println("Test 1: " + (checkMatch(TEST_1_PATTERN,PD) ? "PASS" : "FAIL"));
        System.out.println("Test 2: " + (checkMatch(TEST_2_PATTERN,PD) ? "PASS" : "FAIL"));
        System.out.println("Test 3: " + (checkMatch(TEST_3_PATTERN,PD) ? "PASS" : "FAIL"));
        System.out.println("Test 4: " + (checkMatch(TEST_4_PATTERN,BIG) ? "PASS" : "FAIL"));
        System.out.println("Test 5: " + (!checkMatch(TEST_4_PATTERN,PD) ? "PASS" : "FAIL"));
    }
}