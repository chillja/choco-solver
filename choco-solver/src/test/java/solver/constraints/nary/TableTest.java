/**
 *  Copyright (c) 1999-2011, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/**
 * @author Jean-Guillaume Fages
 * @since 10/04/14
 * Created by IntelliJ IDEA.
 */
package solver.constraints.nary;

import org.testng.Assert;
import org.testng.annotations.Test;
import solver.Solver;
import solver.constraints.ICF;
import solver.constraints.extension.TuplesFactory;
import solver.search.strategy.ISF;
import solver.variables.IntVar;
import solver.variables.VF;

public class TableTest {

    private static String[] algos = {"FC", "GAC2001", "GACSTR", "GAC2001+", "GAC3rm+", "GAC3rm"};

    private void allEquals(Solver solver, IntVar[] vars, int algo) {
        if (algo > -1) {
            solver.post(ICF.table(vars, TuplesFactory.allEquals(vars), algos[algo]));
        } else {
            for (int i = 1; i < vars.length; i++) {
                solver.post(ICF.arithm(vars[0], "=", vars[i]));
            }
        }
    }

    @Test(groups = "10s")
    public void testAllEquals() {
        int[][] params = {{5, 2, 9}, {5, -2, 3}, {10, 2, 4}, {5, 0, 20}};
        for (int p = 0; p < params.length; p++) {
            Solver solver = new Solver();
            IntVar[] vars = VF.enumeratedArray("v1", params[p][0], params[p][1], params[p][2], solver);
            allEquals(solver, vars, -1);
            long nbs = solver.findAllSolutions();
            long nbn = solver.getMeasures().getNodeCount();
//            System.out.printf("%s\n", solver.getMeasures().toOneLineString());
            for (int a = 0; a < algos.length; a++) {
                for (int s = 0; s < 10; s++) {
                    Solver tsolver = new Solver(algos[a]);
                    IntVar[] tvars = VF.enumeratedArray("v1", params[p][0], params[p][1], params[p][2], tsolver);
                    allEquals(tsolver, tvars, a);
                    tsolver.set(ISF.random_value(tvars));
                    Assert.assertEquals(tsolver.findAllSolutions(), nbs);
                    if (a > 1) Assert.assertEquals(tsolver.getMeasures().getNodeCount(), nbn);
//                    System.out.printf("%s\n", tsolver.getMeasures().toOneLineString());
                }
            }
        }
    }

    private void allDifferent(Solver solver, IntVar[] vars, int algo) {
        if (algo > -1) {
            solver.post(ICF.table(vars, TuplesFactory.allDifferent(vars), algos[algo]));
        } else {
            solver.post(ICF.alldifferent(vars, "AC"));
        }
    }

    @Test(groups = "10s")
    public void testAllDifferent() {
        int[][] params = {{5, 2, 9}, {5, -2, 3}, {7, 0, 7}};

        for (int p = 0; p < params.length; p++) {
            Solver solver = new Solver();
            IntVar[] vars = VF.enumeratedArray("v1", params[p][0], params[p][1], params[p][2], solver);
            allDifferent(solver, vars, -1);
            long nbs = solver.findAllSolutions();
            long nbn = solver.getMeasures().getNodeCount();
//            System.out.printf("%s\n", solver.getMeasures().toOneLineString());
            for (int a = 0; a < algos.length; a++) {
                for (int s = 0; s < 1; s++) {
                    Solver tsolver = new Solver(algos[a]);
                    IntVar[] tvars = VF.enumeratedArray("v1", params[p][0], params[p][1], params[p][2], tsolver);
                    allDifferent(tsolver, tvars, a);
                    tsolver.set(ISF.random_value(tvars));
                    Assert.assertEquals(tsolver.findAllSolutions(), nbs);
                    if (a > 1) Assert.assertEquals(tsolver.getMeasures().getNodeCount(), nbn);
//                    System.out.printf("%s\n", tsolver.getMeasures().toOneLineString());
                }
            }
        }
    }

}
