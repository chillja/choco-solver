/**
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2019, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.search.loop.learn;

import java.util.Arrays;
import java.util.List;

import org.chocosolver.solver.DefaultSettings;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Settings;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.trace.IMessage;
import org.chocosolver.solver.variables.IntVar;
import org.testng.annotations.Test;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 18/06/13
 */
public class CBJTest {
	private class MagicSquare extends Model {

		private final IntVar[][] square_vars;
		private final IntVar square_sum;
		private int order;
		
		public MagicSquare(int order, boolean setSum, boolean frenicleForm) {
			this(order, setSum, frenicleForm, new DefaultSettings());
		}

		public MagicSquare(int order, boolean setSum, boolean frenicleForm, Settings settings) {
			super("Magic Square " + order + ","
				+ "magic constant " + (setSum ? "set" : "not set") + ", "
				+ (frenicleForm ? "normed." : "not normed. "), settings);
			
			this.order = order;

			// The original square variable setup
			square_vars = intVarMatrix(order, order, 1, ((int) Math.pow(order, 2)));

			// Service view for the square as a contiguous Array
			IntVar[] square_flattened = Arrays.stream(square_vars).flatMap(x -> Arrays.stream(x))
					.toArray(size -> new IntVar[size]);

			// Sum variable
			square_sum = intVar("square_sum", 0, /* upper bound of sum = (n^2) * n */ ((int) Math.pow(order, 2) * order));

			allDifferent(square_flattened).post();

			// sum constraint for all rows
			for (IntVar[] row : square_vars) {
				sum(row, "=", square_sum).post();
			}

			// sum constraints for all columns
			for (int column = 0; column < square_vars[0].length; column++) {
				IntVar[] column_view = new IntVar[order];
				for (int row = 0; row < square_vars.length; row++) {
					column_view[row] = square_vars[row][column];
				}
				sum(column_view, "=", square_sum).post();
			}

			// sum constraints for the main diagonals
			IntVar[] diag1 = new IntVar[order];
			IntVar[] diag2 = new IntVar[order];
			for (int i = 0; i < order; i++) {
				diag1[i] = square_vars[i][i];
				diag2[i] = square_vars[i][(order - 1) - i];
			}
			sum(diag1, "=", square_sum).post();
			sum(diag2, "=", square_sum).post();

			// optionally set fix the sum to the magic constant
			if (setSum) {
				int magic_constant = (int) (order * (((int) Math.pow(order, 2)) + 1) * 0.5);
				arithm(square_sum, "=", magic_constant).post();
			}

			if (frenicleForm) {// ensure Frénicle norm
				// the element in the upper left corner is the smallest corner element
				arithm(square_vars[0][0], "<=", square_vars[0][order - 1]).post();
				arithm(square_vars[0][0], "<=", square_vars[order - 1][0]).post();
				arithm(square_vars[0][0], "<=", square_vars[order - 1][order - 1]).post();
				// the element right next to it is smaller than the one below
				arithm(square_vars[0][1], "<=", square_vars[1][0]).post();
			}
		}
	}

	
    @Test
    public void testCBJNogoods() {
		/* SETUP */
		Model problem = new MagicSquare(3, true /* setSum*/ , false /* normed */);
		
		Solver solver = problem.getSolver();
		solver.limitTime(15000);
		
		solver.setSearch(Search.domOverWDegSearch(problem.retrieveIntVars(true)));

		class myMessage implements IMessage {

			private Solver solver;

			myMessage(Solver solver) { this.solver = solver; }
			
			@Override
			public String print() {
				String s = "";
				for (IntVar var : solver.getModel().retrieveIntVars(true)) {
					s += var + " ";
				}
				return s;
			}
			
		};
		
		solver.showDecisions(new myMessage(solver));
		
		solver.setCBJLearning(true /* nogoodsOn */, true /* setUserFeedbackOn */);
		
		/* Solution Step */
		
		List<Solution> sols = solver.findAllSolutions();
		
		/* Debug output */
		
		solver.printStatistics();
		for (Solution sol : sols) {
			System.out.println(sol);
		}
		
		if (solver.getLearner() instanceof LearnCBJ) {
			System.out.println("Found Nogoods:");
			for (List<NogoodAssignment> nogood : ((LearnCBJ) solver.getLearner()).getFoundNogoods()) {
				for (NogoodAssignment a : nogood) {
					System.out.print(a + " ");
				}
				System.out.println();
			}
		}
		
    }
    
    public void main(String[] args) {
    	testCBJNogoods();
    }

}
