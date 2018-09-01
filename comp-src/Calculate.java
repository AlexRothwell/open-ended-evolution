import java.io.IOException;
import java.util.Iterator;
import org.nlogo.api.*;

//Class to handle the extension function call "calculate"
class Calculate extends DefaultCommand
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.commandSyntax(new int[]
        {
            Syntax.WildcardType(), Syntax.ListType(), Syntax.NumberType()
        });
    }

    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        Object o = args[0].get();
        if (o instanceof Comp)
        {
            Comp c = (Comp)o;
            Iterator<Object> rows = args[1].getList().iterator();
            Iterator<Object> items;
            int n = args[2].getIntValue();
            int threshold;
            if (c.getThreshold())
            {
                threshold = (int)((1/8d)*c.getTicks());
            }
            else
            {
                threshold = 0;
            }
            int[][] adj = new int[n][n];
            int row = 0;
            int col;
            int value;
            while (rows.hasNext())
            {
                LogoList curr = (LogoList)rows.next();
                items = curr.iterator();
                col = 0;
                while (items.hasNext())
                {
                    value = ((Double)items.next()).intValue();
                    if (value > threshold)
                    {
                        adj[row][col++] = value;
                    }
                    else
                    {
                        adj[row][col++] = 0;
                    }
                }
                row++;
            }
            try
            {
                c.compute(adj,n);
            }
            catch (IOException e)
            {
                throw new ExtensionException("Error in output: " + e);
            }
        } else throw new ExtensionException(o + " is not a Comp");
    }
}
