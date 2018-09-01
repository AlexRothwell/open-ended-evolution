
import java.util.Iterator;
import org.nlogo.api.*;

//Class to handle the extension function call "init-pm"
class InitPm extends DefaultReporter
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.reporterSyntax(new int[]
        {
            Syntax.ListType(),Syntax.NumberType(),Syntax.NumberType(),
            Syntax.NumberType(),Syntax.NumberType(),Syntax.NumberType(),
            Syntax.NumberType()
        }, Syntax.WildcardType());
    }

    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        LogoList input = args[0].getList();
        Iterator<Object> it = input.iterator();
        int index = 0;
        double[] game = new double[input.size()];
        while (it.hasNext())
        {
            game[index++] = (double)it.next();
        }
        int rt = (int)Math.sqrt(game.length);
        if (rt*rt != game.length - 1)
        {
            throw new ExtensionException("Misformed game: length is " + rt*rt + ", expected " + (game.length-1));
        }
        
        return new PopulationManager(game,args[1].getDoubleValue(),args[2].getIntValue(),args[3].getDoubleValue(),
                args[4].getDoubleValue( ),args[5].getDoubleValue(),args[6].getIntValue());
    }
    
}
