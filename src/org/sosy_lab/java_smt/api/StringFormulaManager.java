/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
 * This interface is taken from Figure 1 of Zheng et al.'s Z3str2: An Efficient Solver for Strings,
 * Regular Expressions, and Length Constraints
 *
 *
 */
public interface StringFormulaManager {

  /**
   * Declare a string constant
   *
   * @param pString the constant string
   * @return Formula that represents the string
   */
  StringFormula makeString(String pString);

  /**
   * Declare a new string variable
   *
   * @param pName the variable's name
   * @return A Formula that represents the string
   */
  StringFormula makeVariable(String pName);

  /**
   * Find if a string contains a substring
   *
   * @param pString the string to look in
   * @param pSearch the substring
   * @return a BooleanFormula representing the containment
   */
  BooleanFormula contains(StringFormula pString, StringFormula pSearch);

  /**
   * Determine if a string has prefix
   *
   * @param pString the string
   * @param pPrefix the prefix
   * @return boolean formula representing the truth value
   */
  BooleanFormula startsWith(StringFormula pString, StringFormula pPrefix);

  /**
   * Determine if a string has a suffix
   *
   * @param pString the string
   * @param pSuffix the suffix
   * @return a boolean formula representing the truth value
   */
  BooleanFormula endsWith(StringFormula pString, StringFormula pSuffix);

  /**
   * Determine if a string is matched by a regular expression
   *
   * @param pString the string
   * @param pRegex the regular expression
   * @return a boolean formula representing the truth value
   */
  BooleanFormula regexIn(StringFormula pString, RegexFormula pRegex);

  /**
   * get the length of the string
   *
   * @param pString
   * @return integer formula that is the length
   */
  IntegerFormula length(StringFormula pString);

  /**
   * Get the index of a substring in the string starting at an offset
   *
   * @param pString the string
   * @param pSearch the substring
   * @param pInt the offset
   * @return integer formula of the location
   */
  IntegerFormula indexOf(StringFormula pString, StringFormula pSearch, IntegerFormula pInt);

  /**
   * Combine two strings into one string
   *
   * @param pString1 The first string
   * @param pString2 The second string
   * @return Formula that represents the concatenated strings
   */
  StringFormula concat(StringFormula pString1, StringFormula pString2);

  /**
   * Get a substring from a string
   *
   * @param pString the input string
   * @param pStart the starting index of the substring
   * @param pLength the length of the substring
   * @return Formula that represents the substring
   */
  StringFormula substring(StringFormula pString, IntegerFormula pStart, IntegerFormula pLength);

  /**
   * Update a string by replacing occurrences
   *
   * @param pString the string to work on
   * @param pFind the string to find
   * @param pReplace the string to replace
   * @return A formula that represents the resulting string
   */
  StringFormula replace(StringFormula pString, StringFormula pFind, StringFormula pReplace);

  /**
   * Get the character stored at an index
   *
   * @param pString the string from which to read
   * @param pIndex the position from which to read
   * @return A formula that represents the result of the "read"
   */
  StringFormula charAt(StringFormula pString, NumeralFormula.IntegerFormula pIndex);

  /**
   * Create an exact match regex for the string formula
   *
   * @param pString match this exactly
   * @return the Regex Formula
   */
  RegexFormula str2Regex(StringFormula pString);

  /**
   * Kleene star
   *
   * @param pRegex regex formula
   * @return (R)*
   */
  RegexFormula regexStar(RegexFormula pRegex);

  /**
   * Kleene plus
   *
   * @param pRegex regex formula
   * @return (R)+
   */
  RegexFormula regexPlus(RegexFormula pRegex);

  /**
   * Optional operator
   *
   * @param pRegex regex formula
   * @return (R)?
   */
  RegexFormula regexQuestion(RegexFormula pRegex);

  /**
   * Regex concatenation
   *
   * @param pRegex1 first regex
   * @param pRegex2 second regex
   * @return R_1R_2
   */
  RegexFormula regexConcat(RegexFormula pRegex1, RegexFormula pRegex2);

  /**
   * Regex union
   *
   * @param pRegex1 first regex
   * @param pRegex2 second regex
   * @return R_1 | R_2
   */
  RegexFormula regexUnion(RegexFormula pRegex1, RegexFormula pRegex2);

  /**
   * Regex complement
   *
   * @param pRegex1 first regex
   * @return !R_1
   */
  RegexFormula regexComp(RegexFormula pRegex1);

  /**
   * Make a regular expression character class range
   *
   * @param pStart the starting character
   * @param pEnd the ending character
   * @return a regular expression formula representing the range
   */
  RegexFormula regexRange(StringFormula pStart, StringFormula pEnd);

  /**
   * Test if a string is a member of a regular language
   *
   * @param pString the string
   * @param pRegex the regular expression
   * @return A formula that represents whether the string is in the language
   */
  BooleanFormula strInRegex(StringFormula pString, RegexFormula pRegex);

  /**
   * Determine if two strings are equal
   *
   * @param pString1 the first string
   * @param pString2 the second string
   * @return A formula that represents the result of the equality check
   */
  BooleanFormula equal(StringFormula pString1, StringFormula pString2);

  /**
   * Make an empty regular expression
   *
   * @return A formula that matches nothing
   */
  RegexFormula regexEmpty();

  /**
   * Make a regular expression that matches everything
   *
   * @return A formula that matches everything
   */
  RegexFormula regexAll();
}
