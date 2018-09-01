
import java.util.*;
import org.nlogo.api.*;

//Class to hold utility methods used in multiple locations
public class Utils
{
    public static final int LT = -1;
    public static final int EQ = -2;

    public static double getExponentialRandNum(Random rand, double scale)
    {
        return -Math.log(1-rand.nextDouble())/scale;
    }
    
        
    public static int getDelta(Random rand,int chance)
    {
        int num = rand.nextInt(200) - 100;
        if (0 < num && num <= chance)
        {
            return 1;
        }
        else if ( -chance <= num && num < 0)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
    
    //Method to get num_values randomly from range [min,max)
    public static int[] getValues(Random rand, int min, int max, int num_values)
    {
        int count = max-min;
        int[] numbers = new int[count];
        num_values = Math.min(count,num_values);
        for (int i = 0; i < count; i++)
        {
            numbers[i] = i + min;
        }
        int index, temp;
        for (int i = numbers.length - 1; i > 0; i--)
        {
            index = rand.nextInt(i + 1);
            temp = numbers[index];
            numbers[index] = numbers[i];
            numbers[i] = temp;
        }
        int[] result = new int[num_values];
        System.arraycopy(numbers,0,result,0,num_values);
        return result;
    }
    
	//Takes a LogoList and converts it to a Game
    public static Game logoToGame(LogoList logo) throws ExtensionException
    {
        Iterator<Object> it = logo.iterator();
        double[] game_array = new double[logo.size()-1];
        int rounds = ((Double)it.next()).intValue();
        int index = 0;
        while (it.hasNext())
        {
            game_array[index++] = (double)it.next();
        }
        int rt = (int)Math.sqrt(game_array.length);
        if (rt*rt != game_array.length || rounds < 1)
        {
            throw new ExtensionException("Misformed game: length is " + rt*rt + ", expected " + game_array.length + " number of rounds: " + rounds);
        }
        return new Game(rounds,game_array);
    }
}