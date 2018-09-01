
import org.nlogo.api.*;


//Class to manage the Complexity Extension for NetLogo
public class ComplexityComputerExtension extends DefaultClassManager
{

    @Override
    public void load(PrimitiveManager pm)
    {
        //usage: comp:calculate comp adj size
        pm.addPrimitive("calculate", new Calculate());
        
        //usage: comp:init outfile threshold ticks
        pm.addPrimitive("init",new InitComp());
    }

    @Override
    public ExtensionObject readExtensionObject(ExtensionManager reader,
            String typeName, String value) throws ExtensionException
    {
        throw new ExtensionException("Have not implemented readExtensionObject yet for " + typeName);
    }
}
