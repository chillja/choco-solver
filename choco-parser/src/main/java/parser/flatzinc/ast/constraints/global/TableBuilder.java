/*
 * Copyright (c) 1999-2012, Ecole des Mines de Nantes
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Ecole des Mines de Nantes nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package parser.flatzinc.ast.constraints.global;

import parser.flatzinc.ast.Datas;
import parser.flatzinc.ast.constraints.IBuilder;
import parser.flatzinc.ast.expression.EAnnotation;
import parser.flatzinc.ast.expression.Expression;
import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.ICF;
import solver.constraints.IntConstraintFactory;
import solver.constraints.extension.Tuples;
import solver.variables.IntVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 26/07/12
 */
public class TableBuilder implements IBuilder {
    @Override
    public Constraint[] build(Solver solver, String name, List<Expression> exps, List<EAnnotation> annotations, Datas datas) {
        // array[int] of var int: x, array[int, int] of int: t
        IntVar[] x = exps.get(0).toIntVarArray(solver);
        int[] f_t = exps.get(1).toIntArray();
        int d2 = x.length;
        int d1 = f_t.length / d2;
        List<int[]> t = new ArrayList<>();
        for (int i = 0; i < d1; i++) {
            t.add(Arrays.copyOfRange(f_t, i * d2, (i + 1) * d2));
        }
        Tuples tuples = new Tuples(true);
        for (int[] couple : t) {
            tuples.add(couple);
        }
        if (x.length == 2) {
            return new Constraint[]{ICF.table(x[0], x[1], tuples, "AC3bit+rm")};
        } else {
            return new Constraint[]{IntConstraintFactory.table(x, tuples, "GAC3rm+")};
        }
    }
}
