
import org.nlogo.api.*;


//Class to manage the Population Manager Extension for NetLogo
public class PopulationManagerExtension extends DefaultClassManager
{

    @Override
    public void load(PrimitiveManager pm)
    {
        //usage: population-manager:init-pm game game-mut round_mut_range tree-mut node-mut decay_rate rand-seed
        pm.addPrimitive("init-pm", new InitPm());
        
        //usgae: population-manager:get-game pm
        pm.addPrimitive("get-game", new GetGame());
        
        //usage: population-manager:get-opponent pm games
        pm.addPrimitive("get-opponent", new GetOpponent());
        
        //usage: population-manager:init-game pm game
        pm.addPrimitive("init-game", new InitGame());
        
        //usage: population-manager:first-action pm
        pm.addPrimitive("get-first-action", new GetFirstAction());
        
        //usage: population-manager:get-action pm opp-action
        pm.addPrimitive("get-action", new GetAction());
        
        //usage: population-manager:finish-game pm
        pm.addPrimitive("finish-game", new FinishGame());
        
        //usage: population-manager:mutate pm
        pm.addPrimitive("mutate", new Mutate());
        
        //usage: population-manager:replicate pm
        pm.addPrimitive("replicate", new Replicate());
        
        //usage: population-manager:get-colour pm
        pm.addPrimitive("get-colour", new GetColour());
        
        //usage: population-manager:get-size pm
        pm.addPrimitive("get-size", new GetSize());
    }

    @Override
    public ExtensionObject readExtensionObject(ExtensionManager reader,
            String typeName, String value) throws ExtensionException
    {
        throw new ExtensionException("Have not implemented readExtensionObject yet for " + typeName);
    }
}
