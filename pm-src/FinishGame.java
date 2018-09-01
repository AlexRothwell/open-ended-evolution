
import org.nlogo.api.*;

//Class to handle the extension function call "finish-game"
class FinishGame extends DefaultCommand
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.commandSyntax(new int[]
        {
            Syntax.WildcardType(), Syntax.NumberType()
        });
    }
    
    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        Object o = args[0].get();
        if (o instanceof PopulationManager)
        {
            ((PopulationManager)o).finishGame(args[1].getIntValue());
        } else throw new ExtensionException(o + " is not a PopulationManager") ;
    }
    
}
