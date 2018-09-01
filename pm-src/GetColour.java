
import org.nlogo.api.*;

//Class to handle the extension function call "get-colour"
class GetColour extends DefaultReporter
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.reporterSyntax(new int[]
        {
            Syntax.WildcardType()
        }, Syntax.NumberType());
    }

    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        Object o = args[0].get();
        if (o instanceof PopulationManager)
        {
            return (double)((PopulationManager)o).getColour();
        } else throw new ExtensionException(o + " is not a PopulationManager") ;
    }
    
}
