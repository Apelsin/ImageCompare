import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;

import imagecompare.Commands;

public class Main
{
    private enum Command
    {
        Compare,
        Hash,
        Test,
    }
    public static void main(String[] args) throws Exception
    {
        ArgumentParser parser =
                ArgumentParsers.newArgumentParser("ic")
                .defaultHelp(true)
                .description("Calculate the amount of visual similarity "
                        + "between two or more images.");
        
        // Top level
        Subparsers subs = parser.addSubparsers();
        Subparser sub_compare = subs.addParser("compare")
                .setDefault("command", Command.Compare);
        Subparser sub_hash = subs.addParser("hash")
                .setDefault("command", Command.Hash);
        Subparser sub_test = subs.addParser("test")
                .setDefault("command", Command.Test);
        
        // Compare
        MutuallyExclusiveGroup mutex =
                sub_compare.addMutuallyExclusiveGroup("Reference")
                .required(true);
        mutex.addArgument("-R", "--reference-hash")
                .help("Supply the reference hash rather than calculating it.");
        mutex.addArgument("-r", "--reference")
                .help("Reference image file.");
        sub_compare.addArgument("image").nargs("+")
                .help("Image files to compare to reference.");
        
        // Hash
        sub_hash.addArgument("image").help("Image file to hash.");
        sub_hash.addArgument("degree").help("Sets the degree of the hash "
                + "(number of subdivisions, tree depth)").setDefault(4);
            
        Namespace arg_ns;
        try
        {
            arg_ns = parser.parseArgs(args);
            switch((Command)arg_ns.get("command"))
            {
                default:
                case Compare:
                    Commands.Compare(arg_ns);
                    break;
                case Hash:
                    Commands.Hash(arg_ns);
                    break;
                case Test:
                    Commands.Test(arg_ns);
                    break;
            }
        }
        catch (ArgumentParserException e)
        {
            parser.handleError(e);
            System.exit(1);
        }
    }
}
