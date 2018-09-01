import org.nlogo.api.*;

//Class to handle the extension function call "get-game"
class GetGame extends DefaultReporter
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.reporterSyntax(new int[]
        {
            Syntax.WildcardType()
        }, Syntax.ListType());
    }

    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        Object o = args[0].get();
        if (o instanceof PopulationManager)
        {
            double[] game = ((PopulationManager)o).getGame();
            LogoListBuilder list = new LogoListBuilder();
            for (double num : game)
            {
                list.add(num);
            }
            return list.toLogoList();
        } else throw new ExtensionException(o + " is not a PopulationManager") ;
    }
    
}
