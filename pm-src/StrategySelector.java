import org.nlogo.api.*;
import java.util.*;

//The class containing the strategy tree and associated methods
public class StrategySelector
{
    private final static boolean LOOP_CHECKING = true;
    private final static boolean DEBUG = false;

    private Node root;
    private Node current;
    private double decay_rate;
    
    public StrategySelector(double decay_rate,Random rand)
    {
        root = new Node(rand,null);
        root.parent = root;
        this.decay_rate = decay_rate;
        if (DEBUG)
        {
            System.out.println("New tree with root " + root);
        }
    }
    
    public StrategySelector(StrategySelector toCopy)
    {
        this.root = new Node(toCopy.root);
        this.root.parent = this.root;
        this.decay_rate = toCopy.decay_rate;
        if (DEBUG)
        {
            System.out.println("Copied tree with root " + root);
        }
    }

    int[] find(Game game) throws ExtensionException
    {
        current = findMatch(game);
        return current.strategy;
    }
    
    int getHistory(Game game) throws ExtensionException
    {
        return findMatch(game).history;
    }
    
    int getCurrentHistory()
    {
        if (current != null)
        {
            return current.history;
        }
        else
        {
            return 0;
        }
    }
    
    int getHistory()
    {
        return root.history;
    }
    
    public void mutate(Random rand, int strategy_chance, int node_chance)
    {
        root.mutate(rand,strategy_chance);
        
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Node> next = new ArrayList<>();
        ArrayList<Node> full_list = new ArrayList<>();
        ArrayList<Node> to_swap = new ArrayList<>();
        Iterator<Node> it;
        Node n;
        nodes.add(root);
        while (!nodes.isEmpty())
        {
            it = nodes.iterator();
            while (it.hasNext())
            {
                n = it.next();
                next.addAll(n.children);
            }
            nodes.clear();
            nodes.addAll(next);
            full_list.addAll(next);
            next.clear();
        }
        
        it = full_list.iterator();
        while (it.hasNext())
        {
            n = it.next();

            //delete
            if (rand.nextInt(100) < node_chance)
            {
                n.delete();
            }
            else
            {
                //add
                if (rand.nextInt(100) < node_chance)
                {
                    Node node = new Node(rand,n);
                    if (DEBUG)
                    {
                        System.out.println("Adding " + node + " to " + n);
                    }
                    n.children.add(node);
                }
                
                //select to swap
                if (rand.nextInt(100) < node_chance/4)
                {
                    to_swap.add(n);
                }
            }
        }
        
        it = to_swap.iterator();
        Node from, to;
        while (it.hasNext())
        {
            from = it.next();
            if (it.hasNext())
            {
                to = it.next();
                swap(from,to);
            }
        }
    }
    
    public int size()
    {
        return root.count();
    }
    
    public int depth()
    {
        return root.depth();
    }
    
    public int breadth()
    {
        int max = 1;
        ArrayList<Node> curr = new ArrayList<Node>();
        ArrayList<Node> next = new ArrayList<Node>();
        Iterator<Node> it;
        curr.add(root);
        while (!curr.isEmpty())
        {
            it = curr.iterator();
            while (it.hasNext())
            {
                next.addAll(it.next().children);
            }
            max = Math.max(next.size(),max);
            curr.clear();
            curr.addAll(next);
            next.clear();
        }
        return max;
    }
    
    public void check() throws ExtensionException
    {
        root.check();
    }
    
    public void addPayoff(int amount)
    {
        current.addPayoff(amount);
        root.decay(decay_rate);
    }
    
    private Node findMatch(Game game) throws ExtensionException
    {
        if (root != null)
        {
            ArrayList<Node> current_level = new ArrayList<>();
            ArrayList<Node> next_level = new ArrayList<>();
            Node curr;
            Pattern pattern;
            next_level.add(root);
            while (!next_level.isEmpty())
            {
                current_level.clear();
                current_level.addAll(next_level);
                next_level.clear();
                for (Iterator<Node> it = current_level.iterator(); it.hasNext();)
                {
                    curr = it.next();
                    pattern = curr.pattern;
                    if(GamePatternMatcher.checkMatch(pattern,game))
                    {
                        if (curr.children.isEmpty())
                        {
                            return curr;
                        }
                        else
                        {
                            next_level.addAll(curr.children);
                        }
                    }
                }
                
            }
            return current_level.get(0).parent;
            
        }
        else
        {
            throw new ExtensionException("Missing strategy tree");
        }
    }
    
    private void swap(Node from, Node to)
    {
        if (DEBUG)
        {
            System.out.println("Swapping: " + from + " " + to);
            System.out.println("Root: " + root);
        }
        if (from != root && to != root)
        {
            if (DEBUG)
            {
                System.out.println("Checking ancestry");
            }
            //Check if one is the ancestor of the other
            Node curr = from;
            while (curr != root && curr != to)
            {
                curr = curr.parent;
            }
            if (curr == to)
            {
                if (DEBUG)
                {
                    System.out.println("Swapping with ancestor");
                }
                swapWithAncestor(to,from);
            }
            else
            {
                curr = to;
                while (curr != root && curr != from)
                {
                    curr = curr.parent;
                }
                if (curr == from)
                {
                    if (DEBUG)
                    {
                        System.out.println("Swapping with ancestor");
                    }
                    swapWithAncestor(from,to);
                }
                else
                {
                    if (DEBUG)
                    {
                        System.out.println("Swapping");
                    }
                    swapOther(from,to);
                }
            }
        }
    }
    
    private void swapWithRoot(Node oldRoot, Node other)
    {
        this.root = other;
        other.parent.children.remove(other);
        other.parent = other;
        oldRoot.parent = other;
        other.children.add(oldRoot);
    }
    
    private void swapWithAncestor(Node ancestor, Node other)
    {
        other.parent.children.remove(other);
        ancestor.parent.children.remove(ancestor);
        other.parent = ancestor.parent;
        other.parent.children.add(other);
        ancestor.parent = other;
        other.children.add(ancestor);
        
    }
    
    private void swapOther(Node to, Node from)
    {
        Node tmp;
        from.parent.children.remove(from);
        to.parent.children.remove(to);
        tmp = from.parent;
        from.parent = to.parent;
        to.parent = tmp;
        to.parent.children.add(to);
        from.parent.children.add(from);
    }
    
    private class Node
    {
        Node parent;
        ArrayList<Node> children;
        int[] strategy;
        Pattern pattern;
        int history;
        
        public Node(Node parent, ArrayList<Node> children, int[] strategy, Pattern pattern)
        {
            this.parent = parent;
            this.children = children;
            this.strategy = strategy;
            this.pattern = pattern;
            this.history = 0;
            if (DEBUG)
            {
                System.out.println("New node " + this);
            }
        }
        
        public Node(Random rand, Node parent)
        {
            this.parent = parent;
            this.children = new ArrayList<Node>();
            this.strategy = new int[(int)Utils.getExponentialRandNum(rand,0.5)];
            for (int i = 0; i < this.strategy.length; i++)
            {
                this.strategy[i] = (int)Utils.getExponentialRandNum(rand,1);
            }
            if (parent != null)
            {
                this.pattern = new Pattern(rand,parent.pattern);
            }
            else
            {
                this.pattern = new Pattern();
            }
            int num = (int) Utils.getExponentialRandNum(rand,1.5);
            for (int i = 0; i < num; i++)
            {
                this.children.add(new Node(rand,this));
            }
            this.history = 0;
            if (DEBUG)
            {
                System.out.println("New node " + this + " " + parent);
            }
        }
        
        public Node(Node other)
        {
            this(other,null);
        }
        
        public Node(Node other, Node parent)
        {
            this.children = new ArrayList<>();
            this.strategy = new int[other.strategy.length];
            System.arraycopy(other.strategy,0,this.strategy,0,this.strategy.length);
            this.parent = parent;
            this.history = 0;
            this.pattern = other.pattern.copy();
            Iterator<Node> it = other.children.iterator();
            while (it.hasNext())
            {
                this.children.add(new Node(it.next(),this));
            }
            if (DEBUG)
            {
                System.out.println("New node " + this + " from " + other + " with parent " + parent);
            }
        }
        
        boolean equals(Node other)
        {
            return (other.parent == this.parent) && (other.children.size() == this.children.size())
                && (other.strategy.equals(this.strategy)) && (other.pattern.equals(this.pattern));
        }
        
        void delete()
        {
            //If not the root
            if (this.parent != this)
            {
                if (this.parent.children.remove(this))
                {
                    if (DEBUG)
                    {
                        System.out.println("Deleting " + this + " with parent " + this.parent + " and root " + root);
                    }
                    Iterator<Node> it = children.iterator();
                    Node node;
                    while (it.hasNext())
                    {
                        node = it.next();
                        node.parent = this.parent;
                        this.parent.children.add(node);
                    }
                }
                else if (DEBUG)
                {
                    System.out.println("Double deletion");
                }
            }
            else
            {
                //Ensure that there is at least one child if deleting the root
                if (children.size() > 0)
                {
                    if (DEBUG)
                    {
                        System.out.println("Deleting root, new " + children.get(0));
                    }
                    Iterator<Node> it = children.iterator();
                    //Make the first child the new root
                    StrategySelector.this.root = it.next();
                    root.parent = root;
                    Node node;
                    while (it.hasNext())
                    {
                        node = it.next();
                        root.children.add(node);
                        node.parent = root;
                    }
                }
            }
        }
        
        int count()
        {
            int count = 1;
            Iterator<Node> it = children.iterator();
            while (it.hasNext())
            {
                count += it.next().count();
            }
            return count;
        }
        
        int depth()
        {
            int max = 0;
            Iterator<Node> it = children.iterator();
            while (it.hasNext())
            {
                max = Math.max(it.next().depth(),max);
            }
            return max + 1;
        }
        
        void addPayoff(int amount)
        {
            history += amount;
            if (parent != this)
            {
                parent.addPayoff(amount);
            }
        }
        
        void decay(double factor)
        {
            this.history = (int)(this.history * factor);
            for (Node child :children)
            {
                child.decay(factor);
            }
        }
        
        void mutate(Random rand, int chance)
        {
            //Mutate pattern
            if (rand.nextInt(100) < chance && this != root)
            {
                pattern.mutate(rand,chance);
            }
            //Mutate strategy
            int max = 0;
            for (int i = 0; i < strategy.length; i++)
            {
                if (rand.nextInt(100) < chance)
                {
                    strategy[i] += rand.nextInt(2 * chance) - chance;
                }
                if (strategy[i] > max)
                {
                    max = strategy[i];
                }
            }
            for (int i = 0; i < strategy.length; i++)
            {
                if (strategy[i] < 0)
                {
                    if (max > 0)
                    {
                        strategy[i] = Math.abs(strategy[i]) % max;
                    }
                    else
                    {
                        strategy[i] = 0;
                    }
                }
            }
            
            //Mutate strategy length
            int num = rand.nextInt(100);
            if (num < chance)
            {
                //Change strategy size
                int delta = (int)rand.nextGaussian();
                if (delta > 0)
                {
                    if (strategy.length > 0)
                    {
                        int[] places = Utils.getValues(rand,0,strategy.length + delta - 1,delta);
                        ArrayList<Integer> sorted = new ArrayList<>();
                        for (int place : places)
                        {
                            sorted.add(place);
                        }
                        Collections.sort(sorted);
                        int[] new_strat = new int[strategy.length + delta];
                        int old_index = 0;
                        int new_index = 0;
                        for (int i = 0; i < new_strat.length; i++)
                        {
                            if (new_index < sorted.size() && sorted.get(new_index) == i)
                            {
                                new_strat[i] = (int)Utils.getExponentialRandNum(rand,1);
                                new_index++;
                            }
                            else
                            {
                                new_strat[i] = strategy[old_index];
                                old_index++;
                            }
                        }
                        strategy = new_strat;
                    }
                    else
                    {
                        strategy = new int[delta];
                        for (int i = 0; i < delta; i++)
                        {
                            strategy[i] = (int)Utils.getExponentialRandNum(rand,1);
                        }
                    }
                }
                else if (delta < 0)
                {
                    if (strategy.length + delta > 0)
                    {
                        int[] places = Utils.getValues(rand,0,strategy.length - 1,Math.abs(delta));
                        ArrayList<Integer> sorted = new ArrayList<>();
                        for (int place : places)
                        {
                            sorted.add(place);
                        }
                        Collections.sort(sorted);
                        int[] new_strat = new int[strategy.length + delta];
                        int rem_index = 0;
                        int new_index = 0;
                        for (int i = 0; i < strategy.length; i++)
                        {
                            if (rem_index < sorted.size() && sorted.get(rem_index) == i)
                            {
                                rem_index++;
                            }
                            else
                            {
                                new_strat[new_index] = strategy[i];
                                new_index++;
                            }
                        }
                        strategy = new_strat;
                    }
                    else
                    {
                        strategy = new int[0];
                    }
                }
            }
            //Mutate children
            Iterator<Node> it = children.iterator();
            while (it.hasNext())
            {
                it.next().mutate(rand,chance);
            }
        }
        
        void check() throws ExtensionException
        {
            Node curr = this;
            ArrayList<Node> tree = new ArrayList<>();
            ArrayList<Node> level = new ArrayList<>();
            ArrayList<Node> next = new ArrayList<>();
            Iterator<Node> it;
            Node parent;
            
            level.add(curr);
            while (!level.isEmpty())
            {
                it = level.iterator();
                while (it.hasNext())
                {
                    curr = it.next();
                    curr.pattern.check();
                    parent = curr.parent;
                    while (parent != root)
                    {
                        if (curr == parent)
                        {
                           // printTree(root);
                            throw new ExtensionException("Loop in strategy tree " + parent + " " + curr + " " + StrategySelector.this.root);
                        }
                        parent = parent.parent;
                    }
                    if (tree.contains(curr))
                    {
                        //printTree(root);
                        throw new ExtensionException("Loop found on " + curr);
                    }
                    else
                    {
                        tree.add(curr);
                    }
                    next.addAll(curr.children);
                }
                level.clear();
                level.addAll(next);
                next.clear();
            }
        }
    }
    
    private void printTree(Node node)
    {
        if (node == root)
        {
            System.out.print("Root: ");
        }
        else
        {
            System.out.print("Node: ");
        }
        System.out.print(node.history + "\nChildren: ");
        for (Node child : node.children)
        {
            System.out.print(child + " ");
        }
        System.out.println();
        for (Node child : node.children)
        {
            printTree(child);
        }
    }
    
    private void printTree()
    {
        System.out.println("Root: " + root + "\nChildren: ");
        for (Node child : root.children)
        {
            System.out.print(child + " " );
        }
        System.out.println();
    }
}
