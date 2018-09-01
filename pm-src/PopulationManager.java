
import java.util.*;
import org.nlogo.api.*;

//Main class for the Population Manager. All calls to the object go through here
public class PopulationManager implements org.nlogo.api.ExtensionObject
{
    private static final boolean EVOL_TEST = false;
    private static final boolean CHOICE_OUT = false;

    private final StrategySelector strategies;
    private final Game game;
    private GameStrategy current_game_strategy;
    private double game_mut_chance;
    private int round_mut_range;
    private double strat_mut_chance;
    private double node_mut_chance;
    private double decay_rate;
    private long rand_seed;
    private Random rand;
    
    public PopulationManager(double[] game, double game_mut, int round_mut, double strat_mut,
                                double node_mut, double decay_rate, int rand_seed)
    {
        this.game = new Game(game);
        this.game_mut_chance = game_mut;
        this.round_mut_range = round_mut;
        this.strat_mut_chance = strat_mut;
        this.node_mut_chance = node_mut;
        this.rand_seed = rand_seed;
        this.decay_rate = decay_rate;
        rand = new Random(rand_seed);
        strategies = new StrategySelector(decay_rate,rand);
    }
    
    public PopulationManager(PopulationManager toCopy)
    {
        this.strategies = new StrategySelector(toCopy.strategies);
        this.game = new Game(toCopy.game);
        this.game_mut_chance = toCopy.game_mut_chance;
        this.round_mut_range = toCopy.round_mut_range;
        this.strat_mut_chance = toCopy.strat_mut_chance;
        this.node_mut_chance = toCopy.node_mut_chance;
        this.decay_rate = toCopy.decay_rate;
        this.rand_seed = toCopy.rand.nextLong();
        this.rand = new Random(this.rand_seed);
    }
    
    @Override
    public String dump(boolean bln, boolean bln1, boolean bln2)
    {
        String result = Integer.toString(getSize());
        result += rand_seed;
        return result;
    }

    @Override
    public String getExtensionName()
    {
        return "population-manager";
    }

    @Override
    public String getNLTypeName()
    {
        return "PopulationManager";
    }

    @Override
    public boolean recursivelyEqual(Object o)
    {
        return (dump(false, false, false).equals(((PopulationManager) o).dump(false, false, false)));
    }
    
    public double[] getGame()
    {
        return game.getGame();
    }
    
    public double getOpponent(ArrayList<Game> games) throws ExtensionException
    {
        int[] histories = new int[games.size()];
        int total = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < games.size(); i++)
        {
            histories[i] = strategies.getHistory(games.get(i));
            total += histories[i];
            min = Math.min(min, histories[i]);
        }
        //Using an offset here to move all histories to [1,max+offset]
        //This ensures that the worst history, while still having a chance of getting chosen,
        // has a smaller chance than the more successful ones.
        //Also ensures that negatives are chosen less often than positive
        //And deals with the case of all histories being 0
        int offset = 1 - min;
        for (int i = 0; i < histories.length; i++)
        {
            histories[i] += offset;
        }
        if (CHOICE_OUT)
        {
            for (int h : histories)
            {
                System.out.print(h + " ");
            }
        }
        total += offset * histories.length;
        int chosen = rand.nextInt(total);
        int i = 0;
        int sum = 0;
        while (sum < chosen)
        {
            sum += histories[i];
            i++;
        }
        if (CHOICE_OUT)
        {
            System.out.println("Picked " + histories[Math.max(i-1,0)]);
        }
        return Math.max(--i,0);
    }

    public void initGame(Game game) throws ExtensionException
    {
        if (EVOL_TEST)
        {
            int[] strat = strategies.find(game);
            //Print strategy
            StringBuilder str = new StringBuilder();
            String delim = "";
            for (int val : strat)
            {
                str.append(delim);
                str.append(val);
                delim = ", ";
            }
            System.out.println(str.toString());
            current_game_strategy = new GameStrategy(strat,game.getSize());
        }
        else
        {
            current_game_strategy = new GameStrategy(strategies.find(game), game.getSize());
        }
    }
    
    public int getFirstAction()
    {
        return current_game_strategy.getAction();
    }

    public int getAction(int opp_action)
    {
        return current_game_strategy.getAction(opp_action);
    }

    public void finishGame(int payoff)
    {
        strategies.addPayoff(payoff);
        current_game_strategy = null;
    }
    
    public void mutate()
    {
        if (!EVOL_TEST)
        {
            game.mutate(rand,(int)(game_mut_chance*100),round_mut_range);
        }
        strategies.mutate(rand,(int)(strat_mut_chance*100),(int)(node_mut_chance*100));
    }
    
    public void check() throws ExtensionException
    {
        strategies.check();
        game.check();
    }
    
    public PopulationManager replicate()
    {
        PopulationManager pm = new PopulationManager(this);
        pm.mutate();
        return pm;
    }
    
    public int getSize()
    {
        //Give agents 2 free child nodes
        return Math.min(strategies.size() - 3,0);
    }
    
    public int getColour()
    {
    /*
        int R = strategies.depth();
        int G = getSize();
        int B = game.getRounds();
        System.out.println(R);
        return 256*256*R + 256*G + B;*/
        return strategies.getHistory();
    }

}
