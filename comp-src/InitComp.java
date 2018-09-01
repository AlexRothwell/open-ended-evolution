import java.io.IOException;
import org.nlogo.api.*;

//Class to handle the extension function call "init-comp"
class InitComp extends DefaultReporter
{
    @Override
    public Syntax getSyntax()
    {
        return Syntax.reporterSyntax(new int[]
        {
            Syntax.StringType(), Syntax.BooleanType(), Syntax.NumberType()
        }, Syntax.WildcardType());
    }

    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException, LogoException
    {
        String outfile = args[0].getString();
        boolean threshold = args[1].getBooleanValue();
        int ticks = args[2].getIntValue();
        try
        {
            return new Comp(outfile,threshold,ticks);
        }
        catch (IOException e)
        {
            throw new ExtensionException("Error: " + e);
        }
    }
    
}
