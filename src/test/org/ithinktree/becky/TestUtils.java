/**
 * TestUtils.java
 *
 * BECKY
 */
package test.org.ithinktree.becky;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import dr.evolution.io.NewickImporter;
import dr.evolution.io.NexusImporter;
import dr.evolution.tree.BranchRates;
import dr.evolution.tree.MutableTree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.math.MathUtils;

/**
 * @author Arman D. Bilge
 *
 */
public class TestUtils {

	public static final Tree DEFAULT_TREE;
	public static final NodeRef A, B, C, D, E, DE, CDE, BCDE;
	
	static {
		DEFAULT_TREE = treeFromNewick("(A:1.875,(B:1.75,(C:1.5,(D:1,E:1):0.5):0.25):0.125);", true);
		final Set<String> taxa = new HashSet<String>(4);
		taxa.add("A");
		A = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		taxa.clear();
		taxa.add("B");
		B = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		taxa.clear();
		taxa.add("C");
		C = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		taxa.clear();
		taxa.add("D");
		D = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		taxa.clear();
		taxa.add("E");
		E = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		taxa.add("D");
		DE = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		taxa.add("C");
		CDE = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		taxa.add("B");
		BCDE = Tree.Utils.getCommonAncestorNode(DEFAULT_TREE, taxa);
		
	}
	
	public static final Tree treeFromNewick(final String newick, final boolean testUltrametricity) {
		final Tree tree;
		try {
			tree = new NewickImporter(newick).importNextTree();
		} catch (Exception e) {
			Assert.fail("Fatal problem with internally defined tree: " + e.toString());
			return null;
		}
		Assert.assertTrue("FATAL: Tree is not binary.", Tree.Utils.isBinary(tree));
		if (testUltrametricity)
			Assert.assertTrue("FATAL: Tree is not ultrametric.", Tree.Utils.isUltrametric(tree));
		return tree;
	}

    public static final Tree treeFromNexus(final String nexus, final boolean testUltrametricity) {
        final Tree tree;
        try {
            tree = new NexusImporter(new StringReader(nexus)).importNextTree();
        } catch (Exception e) {
            Assert.fail("Fatal problem with internally defined tree: " + e.toString());
            return null;
        }
        Assert.assertTrue("FATAL: Tree is not binary.", Tree.Utils.isBinary(tree));
        if (testUltrametricity)
            Assert.assertTrue("FATAL: Tree is not ultrametric.", Tree.Utils.isUltrametric(tree));
        return tree;
    }

    public static final NodeRef retrieveNodeByAttribute(final Tree tree, final String attribute, final Object value) {
        
        for (int i = 0; i < tree.getNodeCount(); ++i) {
            NodeRef n = tree.getNode(i);
            if (tree.getNodeAttribute(n, attribute).equals(value)) return n;
        }
        
        return null;
        
    }
    
    public static final class SimpleBranchRates implements BranchRates {
        private final double rate;
        public SimpleBranchRates(final double rate) {
            this.rate = rate;
        }
        public double getBranchRate(Tree t, NodeRef n) {
            return rate;
        }
        
    }
	
    public static final void reduceDecimalPrecision(final MutableTree t) {
        for (int i = 0; i < t.getNodeCount(); ++i) {
            final NodeRef n = t.getNode(i);
            t.setNodeHeight(n, MathUtils.round(t.getNodeHeight(n), 8));
        }
    }
}
