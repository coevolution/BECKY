package org.ithinktree.becky.tools;

import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.ithinktree.becky.SimpleCophylogenyModel;

import dr.app.tools.NexusExporter;
import dr.app.util.Arguments;
import dr.app.util.Arguments.ArgumentException;
import dr.evolution.io.Importer.ImportException;
import dr.evolution.io.NexusImporter;
import dr.evolution.io.TreeImporter;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.inference.model.Parameter;

public class SCMSampler {

	public static void main(String[] args) throws IOException, ImportException {
		
		Locale.setDefault(Locale.US);
		Arguments arguments = new Arguments(new Arguments.Option[]{
				new Arguments.IntegerOption("h", "host tree file"),
				new Arguments.IntegerOption("t", "number of symbiont taxa"),
				new Arguments.RealArrayOption("r", 3, "coevolutionary rates"),
				new Arguments.RealOption("c", "relaxed clock stdev"),
				new Arguments.LongOption("seed", "random number generator seed"),
				new Arguments.IntegerOption("s", "sample size")
		});
		
		try {
			arguments.parseArguments(args);
		} catch (ArgumentException e) {
			e.printStackTrace(System.err);
			arguments.printUsage("scmsampler", "");
			System.exit(1);
		}

		final FileReader hostFileReader = new FileReader(arguments.getStringOption("h"));
		final TreeImporter hostTreeImporter = new NexusImporter(hostFileReader);
		final Tree hostTree = hostTreeImporter.importNextTree();
		hostFileReader.close();
		
		final CoevolutionSimulator sim = new CoevolutionSimulator();
		
		final SimpleCophylogenyModel model = new SimpleCophylogenyModel(new Parameter.Default(arguments.getRealArrayOption("r")[0]), new Parameter.Default(arguments.getRealArrayOption("r")[1]), new Parameter.Default(arguments.getRealArrayOption("r")[2]), Units.Type.YEARS);
		
		final NexusExporter exporter = new NexusExporter(System.out);
		Map<String,Integer> header = null;
		
		for (int i = 0; i < arguments.getIntegerOption("s"); ++i) {
			
			if (header == null) {
				header = exporter.writeNexusHeader(hostTree);
				System.out.println("\t\t;");
			}
			
			Tree tree;
			do {
				tree = sim.simulateCoevolution(hostTree, 1.0, model, false);
			} while (tree.getExternalNodeCount() != arguments.getIntegerOption("t"));
			exporter.exportTree(tree);
		}
		
		System.out.println("End;");
	}

}