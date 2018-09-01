import org.nlogo.api.*;
import java.util.*;

//Class to handle the extension function call "get-opponent"
class GetOpponent extends DefaultReporter
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.reporterSyntax(new int[]
        {
            Syntax.WildcardType(),Syntax.ListType()
        }, Syntax.NumberType());
    }

    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        Object o = args[0].get();
        if (o instanceof PopulationManager)
        {
            ArrayList<Game> games = new ArrayList<>();
            LogoList list = args[1].getList();
            Iterator<Object> it = list.iterator();
            while (it.hasNext())
            {
                games.add(Utils.logoToGame((LogoList)it.next()));
            }
            return ((PopulationManager)o).getOpponent(games);
        } else throw new ExtensionException(o + " is not a PopulationManager") ;
    }
    
}
