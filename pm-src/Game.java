import java.util.Random;
import org.nlogo.api.*;

//Class representing a population's game
public class Game
{
    private int rounds;
    private double[] rewards;
    private int size;
    
    public Game(int rounds, double[] rewards)
    {
        this.rounds = rounds;
        this.rewards = rewards;
        this.size = (int)Math.sqrt(this.rewards.length);
    }
    
    public Game(double[] game)
    {
        this.rounds = (int)game[0];
        this.rewards = new double[game.length - 1];
        System.arraycopy(game,1,this.rewards,0,this.rewards.length);
        this.size = (int)Math.sqrt(this.rewards.length);
    }
    
    public Game(Game other)
    {
        this.rounds = other.rounds;
        this.rewards = other.rewards;
        this.size = other.size;
    }
    
    public int getRounds()
    {
        return rounds;
    }
    
    public double[] getRewards()
    {
        return rewards;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public double[] getGame()
    {
        double[] result = new double[rewards.length + 1];
        result[0] = rounds;
        System.arraycopy(rewards,0,result,1,rewards.length);
        return result;
    }
    
    public void mutate(Random rand, int chance, int range)
    {
        //Mutate rounds
        rounds += rand.nextInt(2*range + 1) - range;
        if (rounds < 1)
        {
            rounds = 1;
        }
        
        //Mutate rewards
        for (int i = 0; i < size; i++)
        {
            rewards[i] += Utils.getDelta(rand,chance);
        }
        
        //Use chance as percentage chance of increasing or decreasing the game size
        int delta = Utils.getDelta(rand,chance);
        if (delta == 1)
        {
            //Increase game size
            size++;
            double[] new_rewards = new double[size*size];
            
            //Generate new rewards
            int max = findMax(rewards);
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    if (i == size - 1 || j == size - 1)
                    {
                        new_rewards[i*size+j] = rand.nextInt(2*max + 1) - max;
                    }
                    else
                    {
                        new_rewards[i*size+j] = rewards[i*(size-1)+j];
                    }
                }
            }
            rewards = new_rewards;
        }
        else if (delta == -1)
        {
            //Decrease game size
            if (size > 2)
            {
                //Remove randomly selected matching column and row
                int selected = rand.nextInt(size);
                int row_offset = 0;
                int col_offset = 0;
                size--;
                double[] old_rewards = rewards;
                rewards = new double[size*size];
                for (int row = 0; row < size; row++)
                {
                    if (row == selected)
                    {
                        row_offset = 1;
                    }
                    col_offset = 0;
                    for (int column = 0; column < size; column++)
                    {
                        if (column == selected)
                        {
                            col_offset = 1;
                        }
                        rewards[row*size+column] = old_rewards[(row+row_offset)*size + column + col_offset];
                    }
                }
            }
        }
        
        
    }
    
    public void check() throws ExtensionException
    {
        if (rounds < 1 || size < 2 || rewards.length != size*size)
        {
            StringBuilder str = new StringBuilder();
            for (double val : rewards)
            {
                str.append(val);
                str.append(",");
            }
            throw new ExtensionException("Game: " + str.substring(0,str.length() - 1) + "\nRounds: " + rounds +
                "\nSize: " + size);
        }
    }
    
    private int findMax(double[] matrix)
    {
        double best = 0;
        double curr;
        for (int i = 0; i < matrix.length; i++)
        {
            curr = Math.abs(matrix[i]);
            best = (curr > best ? curr : best);
        }
        return (int)best;
    }
}