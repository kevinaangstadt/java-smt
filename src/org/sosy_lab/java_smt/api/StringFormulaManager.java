/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2017  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.sosy_lab.java_smt.api;

import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

/**
 * This interface is taken from Figure 1 of Zheng et al.'s
 * Z3str2: An Efficient Solver for Strings, Regular Expressions,
 * and Length Constraints
 *
 *
 */
public interface StringFormulaManager {

  /**
   * Declare a string constant
   * @param pString the constant string
   * @return Formula that represents the string
   */
  StringFormula makeString(String pString);

  /**
   * Declare a new string variable
   * @param pName the variable's name
   * @return A Formula that represents the string
   */
  StringFormula makeVariable(String pName);

  /**
   * Combine two strings into one string
   * @param pString1 The first string
   * @param pString2 The second string
   * @return Formula that represents the concatted strings
   */
  StringFormula concat(StringFormula pString1, StringFormula pString2);

  /**
   * Get a substring from a string
   * @param pString the input string
   * @param pStart the starting index of the substring
   * @param pLength the length of the stubstring
   * @return Formula that represents the substring
   */
  StringFormula substring(StringFormula pString, IntegerFormula pStart, IntegerFormula pLength);

  /**
   * Update a string by replacing occurrences
   * @param pString the string to work on
   * @param pFind the string to find
   * @param pReplace the string to replace
   * @return A formula that represents the resulting string
   */
  StringFormula replace(StringFormula pString, StringFormula pFind, StringFormula pReplace);

  /**
   * Get the character stored at an index
   * @param pString the string from which to read
   * @param pIndex the position from which to read
   * @return A formula that represents the result of the "read"
   */
  StringFormula charAt(StringFormula pString, NumeralFormula.IntegerFormula pIndex);

  /**
   * Determine if two strings are equal
   * @param pString1
   * @param pString2
   * @return A formula that represents the result of the equality check
   */
  BooleanFormula equal(StringFormula pString1, StringFormula pString2);
}
