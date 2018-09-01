
import java.util.Arrays;

//Class determining how a game will be played. Builds, contains and uses the FSAs
public class GameStrategy {
    
    final boolean DEBUG_GAME_RETURN = false;
    final boolean LOOP_GENERATE = true;
    
    //Matrix describing the FSA with each value denoting what state to move to next
    //This is achieved by taking the current state's number as the row and the opponent's action as the column
    int[][] fsa;
    //This array denotes what action is made when the FSA is in the corresponding state
    int[] state_actions;
    //The current state number of the FSA
    int current_state;
    
    public GameStrategy(int[] input, int actions)
    {
        int states;
        if (LOOP_GENERATE)
        {
            if (input.length > 0)
            {
                states = 0;
                for (int i = 0; i < input.length; i++)
                {
                    states = Math.max(states,input[i]);
                }
                states++;
                fsa = new int[states][actions];
                state_actions = new int[states];
                for (int i = 0; i < states; i++)
                {
                    state_actions[i] = input[(i * (actions + 1)) % input.length] % actions;
                    for (int j = 1; j < actions + 1; j++)
                    {
                        fsa[i][j-1] = input[(i*(actions + 1) + j) % input.length];
                    }
                }
            }
            else
            {
                states = 1;
                fsa = new int[states][actions];
                state_actions = new int[states];
                state_actions[0] = 0;
                for (int i = 0; i < actions; i++)
                {
                    fsa[0][i] = 0;
                }
            }
        }
        else
        {
            states = input.length / (actions + 1);
            if (states > 0)
            {
                fsa = new int[states][actions];
                state_actions = new int[states];
                for (int i = 0; i < states; i++)
                {
                    state_actions[i] = input[i * (actions + 1)] % actions;
                    for (int j = 1; j < actions + 1; j++)
                    {
                        fsa[i][j-1] = input[i * (actions + 1) + j] % states;
                    }
                }
            }
            else
            {
                states = 1;
                fsa = new int[states][actions];
                state_actions = new int[states];
                state_actions[0] = 0;
                for (int i = 0; i < actions; i++)
                {
                    fsa[0][i] = 0;
                }
            }
        }
        current_state = 0;
    }
    
    public int getAction()
    {
        if(DEBUG_GAME_RETURN){System.out.println("Played: " + state_actions[current_state]);}
        return state_actions[current_state];
    }
    
    public int getAction(int opp_action)
    {
        current_state = fsa[current_state][opp_action];
        if(DEBUG_GAME_RETURN){System.out.println("Played: " + state_actions[current_state]);}
        return state_actions[current_state];
    }

    
    private static boolean testGames(int[] input, int actions, int[] opp_actions, int[] expected)
    {
        boolean result = true;
        GameStrategy gs = new GameStrategy(input, actions);
        result &= (gs.getAction()==expected[0]);
        for (int i = 1; i < expected.length; i++)
        {
            result = result && (gs.getAction(opp_actions[i-1]) == expected[i]);
        }
        
        return result;
    }
    
    @Override
    public String toString()
    {
        if (state_actions.length > 0)
        {
            StringBuilder str = new StringBuilder();
            str.append(state_actions.length);
            str.append(" ");
            for (int val : state_actions)
            {
                str.append(val);
                str.append(",");
            }
            return str.substring(0,str.length() - 1);
        }
        else
        {
            return "";
        }
    }
    
    public static void main(String[] args)
    {
        boolean result;
        final int[] INPUT_1 = {1,0,3,2,0,2,1,1,2};
        final int[] INPUT_2 = {1,0,0,1};
        final int[][] EXPECTED_1_2 = {{0,3},{0,2},{1,2},{0,3}};
        final int[][] EXPECTED_1_3 = {{0,3,2},{2,1,1},{1,0,3},{0,2,1}};
        final int[][] EXPECTED_1_4 = {{0,3,2,0},{1,1,2,1},{3,2,0,2},{1,2,1,0}};
        final int[][] EXPECTED_2_2 = {{0,0},{1,0}};
        final int[] EXPECTED_1_2_ACTIONS = {1,0,1,1};
        final int[] EXPECTED_1_3_ACTIONS = {1,0,2,2};
        final int[] EXPECTED_1_4_ACTIONS = {1,2,0,1};
        final int[] EXPECTED_2_2_ACTIONS = {1,1};
        
        System.out.println("Testing construction:");
        GameStrategy gs1 = new GameStrategy(INPUT_1,2);
        result = Arrays.deepEquals(gs1.fsa,EXPECTED_1_2) && Arrays.equals(gs1.state_actions, EXPECTED_1_2_ACTIONS);
        System.out.println("TEST 1: " + (result ? "PASS" : "FAIL"));
        GameStrategy gs2 = new GameStrategy(INPUT_1,3);
        result = Arrays.deepEquals(gs2.fsa,EXPECTED_1_3) && Arrays.equals(gs2.state_actions, EXPECTED_1_3_ACTIONS);
        System.out.println("TEST 2: " + (result ? "PASS" : "FAIL"));
        GameStrategy gs3 = new GameStrategy(INPUT_1,4);
        result = Arrays.deepEquals(gs3.fsa,EXPECTED_1_4) && Arrays.equals(gs3.state_actions, EXPECTED_1_4_ACTIONS);
        System.out.println("TEST 3: " + (result ? "PASS" : "FAIL"));
        GameStrategy gs4 = new GameStrategy(INPUT_2,2);
        result = Arrays.deepEquals(gs4.fsa,EXPECTED_2_2) && Arrays.equals(gs4.state_actions, EXPECTED_2_2_ACTIONS);
        System.out.println("TEST 4: " + (result ? "PASS" : "FAIL"));
        GameStrategy gs5 = new GameStrategy(INPUT_1,2);
        result = Arrays.deepEquals(gs5.fsa,EXPECTED_2_2) && Arrays.equals(gs5.state_actions, EXPECTED_2_2_ACTIONS);
        System.out.println("TEST 5: " + (!result ? "PASS" : "FAIL"));
        
        final int[] OPP_ACTIONS_1_1 = {1,0,1};
        final int[] OPP_ACTIONS_1_2 = {2,0,1,0,2,1};
        
        final int[] EXPECTED_ACTIONS_1_1 = {1,1,1,1};
        final int[] EXPECTED_ACTIONS_1_2 = {1,2,0,0,2,2,2};
        
        System.out.println("Testing gameplay:");
        System.out.println("TEST 1: " + (testGames(INPUT_1,2,OPP_ACTIONS_1_1,EXPECTED_ACTIONS_1_1) ? "PASS" : "FAIL"));
        System.out.println("TEST 2: " + (testGames(INPUT_1,3,OPP_ACTIONS_1_2,EXPECTED_ACTIONS_1_2) ? "PASS" : "FAIL"));
        
        
    }
}
