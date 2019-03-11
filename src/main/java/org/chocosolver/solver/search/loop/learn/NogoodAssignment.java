/**
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2019, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.search.loop.learn;

public class NogoodAssignment {
	@Override
	public String toString() {
		return "(" + varName + ", " + value + ")";
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public NogoodAssignment(String varName, int value) {
		this.varName = varName;
		this.value = value;
	}
	
	private String varName;
	private int value;
}
