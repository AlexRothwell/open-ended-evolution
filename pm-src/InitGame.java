
import java.util.Iterator;
import org.nlogo.api.*;

//Class to handle the extension function call "init-game"
class InitGame extends DefaultCommand
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.commandSyntax(new int[]
        {
            Syntax.WildcardType(), Syntax.ListType()
        });
    }

    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        Object o = args[0].get();
        if (o instanceof PopulationManager)
        {
            ((PopulationManager)o).initGame(Utils.logoToGame(args[1].getList()));
        } else throw new ExtensionException(o + " is not a PopulationManager") ;
    }

}
