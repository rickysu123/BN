package bn.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;

public class part1 {
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		long startTime = System.currentTimeMillis();
		BayesianNetwork bn = null;
		RandomVariable query = null;
		RandomVariable temp = null;
		Assignment e = new Assignment();
		// command line arguments begin with filename, query variable, etc.
		for(int i = 0; i < args.length; i++){
			if(i == 0){
				bn = getBayesianNetwork(args[i]);
				continue;
			}
			if(i == 1){
				query = bn.getVariableByName(args[i]);
				continue;
			}
			if(i % 2 == 0){
				// RandomVariables
				temp = bn.getVariableByName(args[i]);
				continue;
			}
			if(i % 2 == 1){
				// Object value
				Object s = args[i];
				e.set(temp, s);
			}
		}
		
		// PART 1
		System.out.println("Exact distribution of Random Variable: " + query.getName() + " given evidence: " + e);
		Distribution probs = ENUMERATION_ASK(query,e,bn);
		probs.normalize();
		System.out.println(probs);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Runtime: " + totalTime + " milliseconds.");
	}
	
	// deals with files, returns the proper BayesianNetwork
	public static BayesianNetwork getBayesianNetwork(String filename) throws IOException{
		BayesianNetwork temp = null;
		if(filename.endsWith(".xml")){
			XMLBIFParser parser = new XMLBIFParser();
			temp = parser.createBayesianNetwork(filename);
		} else if(filename.endsWith(".bif")){
			BIFParser parser = new BIFParser(new FileInputStream(filename));
			temp = parser.parseNetwork();
		}
		return temp;
	}
	
	////////************************** PART 1 **************************////////

	public static Distribution ENUMERATION_ASK(RandomVariable X, Assignment e, BayesianNetwork bn){
		Distribution probs = new Distribution();	// probability holder
		// for each possible choice in domain (True/False)
		for(Object o: X.getDomain()){
			Assignment temp = e.copy();					// shallow copy of assignment for each new object
			e.put(X,o);									// put query with its object
			// get list of variables in BayesianNetwork
			List<RandomVariable> bn_vars = new ArrayList<RandomVariable>(bn.getVariableListTopologicallySorted());
			probs.put(o, ENUMERATE_ALL(bn, bn_vars, e));	// add probability value
			e = temp.copy();							// set original Assignment for next Object in Domain
		}
		return(probs);
	}
	
	
	public static double ENUMERATE_ALL(BayesianNetwork bn, List<RandomVariable> bn_vars, Assignment e){
		if(bn_vars.isEmpty()){
			return(1.0);
		}
		RandomVariable Y = bn_vars.remove(0); 						// get+remove first element
		if(e.containsKey(Y)){										// if Assignment contains current RV
			Assignment a = get_evidence_assignment(bn, Y, e);		// get assignment of query and parents
			double prob = bn.getProb(Y, a);							// get probability of current query variable
			double temp = prob * ENUMERATE_ALL(bn, bn_vars, e);		// recursive call for next RV in BN_vars
			return temp;
		}
		else{
			double sum = 0;											// keep track of sum of probabilities
			for(Object o : Y.getDomain()){							// for each object in domain (True/False)
				e.put(Y,o);											// add into Assignment
				double prob = bn.getProb(Y, e);						// get probability
				bn_vars = get_rest_bnvars(bn, Y);					// get additional RVs after current RV
				Assignment temp = e.copy();							// make copy for next iteration
				sum += prob * ENUMERATE_ALL(bn, bn_vars, e);		// add to sum
				e = temp.copy();									// set Assignment back to original
			}
			return sum;
		}
	}
	
	/* 
	 * from topologically sorted list of RV in BN, get the rest of the RVs after a certain RV
	 * for example, given list of RVs {B,E,J,M}, get_rest_bnvars(bn, E)
	 * >> {J,M}
	 */
	public static List<RandomVariable> get_rest_bnvars(BayesianNetwork bn, RandomVariable Y){
		List<RandomVariable> rest = new ArrayList<RandomVariable>();			// hold RVs
		List<RandomVariable> bn_vars = new ArrayList<RandomVariable>(bn.getVariableListTopologicallySorted());
		boolean found = false;									// check if current RV is found yet
		for(RandomVariable r : bn_vars){
			if(found){
				rest.add(r);
			}
			if(r == Y){
				found = true;
			}
		}
		return(rest);
	}
	
	// returns a List of parents of a RandomVariable from a BayesianNetwork
	public static List<RandomVariable> get_parents(RandomVariable Y, BayesianNetwork bn){
		List <RandomVariable> parents = new ArrayList<RandomVariable>();		// List to hold parents
		for(RandomVariable r : bn.getVariableList()){							// for each RV in BN
			Set <RandomVariable> children = bn.getChildren(r);					// get children
			if(children.contains(Y)){											// if children contains child RV
				parents.add(r);													// add to parents List
			}
		}
		return parents;
	}
	
	/*
	 * returns assignment of Query and Parents
	 */
	// used for get_prob() method in BayesianNetwork
	public static Assignment get_evidence_assignment(BayesianNetwork bn, RandomVariable y, HashMap<RandomVariable, Object> e){
		List<RandomVariable> parents = get_parents(y, bn);
		Assignment a = new Assignment();
		a.set(y, e.get(y));							// set assignment of original variable
		for(RandomVariable n : parents){			// get assignment of each parent of variable
			Object o = e.get(n);
			a.set(n, o);
		}
		return a;									// return Assignment
	}
	
	////////************************** PART 1 **************************////////
}
