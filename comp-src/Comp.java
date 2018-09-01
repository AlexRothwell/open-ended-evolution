import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.nlogo.api.*;

//Class to calculate and output the complexity of the system
public class Comp implements org.nlogo.api.ExtensionObject
{
    String outfile;
    boolean threshold;
    int ticks;
    int curr_tick;

	//Create a new Comp object, setting up the output file in the process
    public Comp(String outfile, boolean threshold, int ticks) throws IOException
    {
        this.outfile = outfile + ".csv";
        this.threshold = threshold;
        this.ticks = ticks;
        curr_tick = 0;
        String header = "tick,n,e,dens,dm,cc,shannon\n";
        Files.write(Paths.get(this.outfile),header.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    @Override
    public String dump(boolean bln, boolean bln1, boolean bln2)
    {
        return "";
    }

    @Override
    public String getExtensionName()
    {
        return "comp";
    }

    @Override
    public String getNLTypeName()
    {
        return "Comp";
    }

    @Override
    public boolean recursivelyEqual(Object o)
    {
        return (dump(false, false, false).equals(((Comp) o).dump(false, false, false)));
    }
    
    //Compute the complexity of the system using the adjacency matrix adj and output
    public void compute(int[][] adj, int n) throws IOException
    {
        int edges = 0;
        for (int[] row : adj)
        {
            for (int item : row)
            {
                if (item != 0)
                {
                    edges++;
                }
            }
        }
        StringBuilder str = new StringBuilder();
        str.append(curr_tick);
        str.append(",");
        str.append(n);
        str.append(",");
        str.append(edges);
        str.append(",");
        str.append(((double)edges)/(n*n));
        str.append(",");
        str.append((double)edges/n);
        str.append(",");
        str.append(calcCC(adj,n));
        str.append(",");
        str.append(calcFactorial(n,edges));
        str.append("\n");

        
        Files.write(Paths.get(outfile),str.toString().getBytes(),
            new OpenOption[]{StandardOpenOption.WRITE,StandardOpenOption.APPEND});
        
        curr_tick += ticks;
    }
    
	//Function to calculate the clustering coefficient
    private double calcCC(int[][] adj, int n)
    {
        double result = 0;
        int n_i;
        int s_i;
        for (int i = 0; i < n; i++)
        {
            n_i = 0;
            s_i = 0;
            for (int j = 0; j < n; j++)
            {
                if (adj[i][j] != 0)
                {
                    n_i++;
                    if (isS(i,j,adj,n))
                    {
                        s_i++;
                    }
                }
            }
            if (n_i != 0)
            {
                result += s_i/n_i;
            }
        }
        return result/n;
    }
    
	//Helper function to determine if i is a neighbour of a neighbour of j
    private boolean isS(int i, int j, int[][] adj, int n)
    {
        for (int i_ = 0; i_ < n; i_++)
        {
            if (adj[i][i_] != 0 && isNeighbour(i_,j,adj))
            {
                return true;
            }
        }
        return false;
    }
    
	//Helper function to determine if i is a neighbour of j
    private boolean isNeighbour(int i, int j, int[][] adj)
    {
        return adj[i][j] != 0;
    }
    
	//Function used to calculate the Shannon complexity's factorials using Stirling's approximation
    private double calcFactorial(int n, int e)
    {
        if (n != 0 && e > 1)
        {
            double total = n*n*Math.log(1+(1+e)/(n*n-e));
            total += e*Math.log(n*n/e-1);
            total += Math.log(n*n + 1);
            total += 0.5*Math.log((n*n+1)/(2*Math.PI*e*(n*n-e)));
            total -= 1;
            return total/Math.log(2);
        }
        else
        {
            return 0;
        }
    }
    
    public boolean getThreshold()
    {
        return threshold;
    }
    
    public int getTicks()
    {
        return ticks;
    }
}
