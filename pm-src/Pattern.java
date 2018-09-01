import java.util.*;
import org.nlogo.api.*;

//Class representing a pattern for recognising a game
public class Pattern
{
    private final int NORM_SD = 5;
    private final int NORM_MEAN = 4;

    ArrayList<Integer> pattern;
    
    public Pattern()
    {
        pattern = new ArrayList<>();
    }
    
    public Pattern(int[] pattern)
    {
        ArrayList<Integer> p = new ArrayList<Integer>();
        for (int val : pattern)
        {
            p.add(val);
        }
        this.pattern = p;
    }
    
    public Pattern(ArrayList<Integer> pattern)
    {
        this.pattern = pattern;
    }
    
    public Pattern(Random rand)
    {
        genPattern(rand);
    }
    
    //Generate new pattern from old one, making a more specific pattern
    public Pattern(Random rand, Pattern parent)
    {
        addToPatternFromParent(rand,parent.pattern);
    }
    
    public Pattern copy()
    {
        return new Pattern(new ArrayList<Integer>(this.pattern));
    }
    
    public boolean equals(Pattern other)
    {
        return other.pattern.toArray().equals(this.pattern.toArray());
    }
    
    public void mutate(Random rand, int chance)
    {
        //pattern
        //remove cells
        if (rand.nextInt(100) < chance && this.pattern.size() > 3)
        {
            removeFromPattern(rand);
        }
        
        //mutate
        if (this.pattern.size() > 0)
        {
            mutatePattern(rand,chance);
        }
        
        //add cells
        if (rand.nextInt(100) < chance)
        {
            addToPattern(rand);
        }
    }
    
    public void check() throws ExtensionException
    {
        for (int i = 0; i < pattern.size(); i++)
        {
            if (i%2 == 0)
            {
                if (pattern.get(i) < 0)
                {
                    throw new ExtensionException("Pattern: " + this.toString());
                }
            }
            else
            {
                if (pattern.get(i) > 0)
                {
                    throw new ExtensionException("Pattern: " + this.toString());
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        if (pattern.size() > 0)
        {
            StringBuilder str = new StringBuilder();
            for (int val : pattern)
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
    
    private void genPattern(Random rand)
    {
        //Max number to be used in pattern generation
        int max = Math.abs((int)(rand.nextGaussian()*NORM_SD + NORM_MEAN));
        int length = rand.nextInt(max + 1) + 2;
        //Require patterns to be not empty
        max += 2;
        length = Math.min(length + 2, max);
        
        pattern = new ArrayList<Integer>();
        
        int[] values = Utils.getValues(rand,0,max,length);
        if (length > 0)
        {
            start(values[0],(rand.nextInt(4) == 0 ? Utils.EQ : Utils.LT),values[1]);
            for (int i = 2; i < length; i++)
            {
                add(values[i],(rand.nextInt(4) == 0 ? Utils.EQ : Utils.LT),i+1);
            }
        }
    }
    
    private void add(int num, int op, int pos)
    {
        int full_pos = pos * 2;
        if (full_pos < pattern.size())
        {
            pattern.add(full_pos,num);
            pattern.add(full_pos + 1,op);
        }
        else
        {
            pattern.add(op);
            pattern.add(num);
        }
    }
    
    private void start(int num1, int op, int num2)
    {
        pattern.add(num1);
        pattern.add(op);
        pattern.add(num2);
    }
    
    private void remove(int pos)
    {
        int full_pos = pos * 2;
        if (full_pos == pattern.size() - 1 && pattern.size() > 1)
        {
            pattern.subList(full_pos - 1,pattern.size()).clear();
        }
        else if (pattern.size() == 1)
        {
            pattern.clear();
        }
        else
        {
            pattern.subList(full_pos, full_pos + 2).clear();
        }
    }
    
    private void removeFromPattern(Random rand)
    {
        //Create sorted list of elements to remove
        int[] nums = Utils.getValues(rand,0,pattern.size()/2 + 1,(int)Utils.getExponentialRandNum(rand,1.5));
        List<Integer> toRemove = new ArrayList<Integer>();
        for (int num : nums)
        {
            toRemove.add(num);
        }
        //Sort list in reverse order
        Collections.sort(toRemove,Collections.reverseOrder());
        for (int pos : toRemove)
        {
            remove(pos);
        }
    }
    
    private void mutatePattern(Random rand,int chance)
    {
        //Get list of currently unused values from 0 to max(pattern) + random exp num
        ArrayList<Integer> unused = getUnused(pattern,Collections.max(pattern) +
            (int)Utils.getExponentialRandNum(rand,1.5));
        int total_length = pattern.size()/2 + 1 + unused.size();
        int index, tmp;
        
        for (int i = 0; i < pattern.size(); i++)
        {
            if (i%2 == 0)
            {
                if (rand.nextInt(100) < chance)
                {
                    index = rand.nextInt(total_length);
                    if (index < unused.size())
                    {
                        tmp = unused.get(index);
                        unused.set(index,pattern.get(i));
                        pattern.set(i,tmp);
                    }
                    else
                    {
                        index -= unused.size();
                        index = index * 2;
                        tmp = pattern.get(index);
                        pattern.set(index,pattern.get(i));
                        pattern.set(i,tmp);
                    }
                }
            }
            else
            {
                if (rand.nextInt(100) < chance)
                {
                    pattern.set(i,(pattern.get(i) == Utils.LT ? Utils.EQ : Utils.LT));
                }
            }
        }
    }
    
    private void addToPattern(Random rand)
    {
        //Get unused cells
        int max = 0;
        if (pattern.size() > 0)
        {
            max = Collections.max(pattern);
        }
        
        int current_length = pattern.size()/2 + 1;
        if (pattern.size() == 0)
        {
            current_length = 0;
        }
        ArrayList<Integer> numbers;
        if (current_length == 0)
        {
            numbers = getUnused(pattern,Math.max(2,(int)Utils.getExponentialRandNum(rand,1.5)));
        }
        else
        {
            numbers = getUnused(pattern,max + (int)Utils.getExponentialRandNum(rand,1.5));
        }
        
        if (numbers.size() > 0)
        {
            //Shuffle
            Collections.shuffle(numbers,rand);
            
            //Select number to be inserted
            int num = rand.nextInt(numbers.size()); 
            
            //Select positions to be inserted at
            int[] pos = Utils.getValues(rand,0,current_length + 1,num);
            
            //Insert
            for (int i = 0; i < pos.length; i++)
            {
                if (current_length == 0)
                {
                    start(numbers.get(i),(rand.nextInt(4) == 0 ? Utils.EQ : Utils.LT),numbers.get(i+1));
                    i++;
                    current_length++;
                }
                else
                {
                    add(numbers.get(i),(rand.nextInt(4) == 0 ? Utils.EQ : Utils.LT),pos[i]);
                }
                current_length++;
            }
        }
    }
    
    private void addToPatternFromParent(Random rand,ArrayList<Integer> parent)
    {
        if (parent.isEmpty())
        {
            genPattern(rand);
        }
        else
        {
            this.pattern = new ArrayList<Integer>(parent);
            addToPattern(rand);
        }
    }
    
    private ArrayList<Integer> getUnused(ArrayList<Integer> pattern,int max)
    {
        //List of cells available
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < max; i++)
        {
            numbers.add(i);
        }
        
        //Remove used cells
        ArrayList<Integer> copy = new ArrayList<Integer>(pattern);
        copy.removeAll(Collections.singleton(Utils.EQ));
        copy.removeAll(Collections.singleton(Utils.LT));
        numbers.removeAll(copy);
        return numbers;
    }
}