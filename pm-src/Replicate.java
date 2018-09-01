
import org.nlogo.api.*;

//Class to handle the extension function call "replicate"
class Replicate extends DefaultReporter
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
            return ((PopulationManager)o).replicate();
        } else throw new ExtensionException(o + " is not a PopulationManager") ;
    }

}
