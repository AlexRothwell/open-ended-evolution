
import org.nlogo.api.*;

//Class to handle the extension function call "mutate"
class Mutate extends DefaultCommand
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.commandSyntax(new int[]
        {
            Syntax.WildcardType()
        });
    }

    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        Object o = args[0].get();
        if (o instanceof PopulationManager)
        {
            ((PopulationManager)o).mutate();
            ((PopulationManager)o).check();
            
        } else throw new ExtensionException(o + " is not a PopulationManager") ;
    }

}
