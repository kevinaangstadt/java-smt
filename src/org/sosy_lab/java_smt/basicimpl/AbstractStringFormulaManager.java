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
package org.sosy_lab.java_smt.basicimpl;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.NumeralFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.RegexFormula;
import org.sosy_lab.java_smt.api.StringFormula;
import org.sosy_lab.java_smt.api.StringFormulaManager;




public abstract class AbstractStringFormulaManager<TFormulaInfo, TType, TEnv, TFuncDecl>
    extends AbstractBaseFormulaManager<TFormulaInfo, TType, TEnv, TFuncDecl>
    implements StringFormulaManager {

  public AbstractStringFormulaManager(
      FormulaCreator<TFormulaInfo, TType, TEnv, TFuncDecl> pFormulaCreator) {
    super(pFormulaCreator);
  }

  private StringFormula wrap(TFormulaInfo formulaInfo) {
    return getFormulaCreator().encapsulateString(formulaInfo);
  }

  private IntegerFormula wrapInt(TFormulaInfo formulaInfo) {
    return getFormulaCreator().encapsulateInteger(formulaInfo);
  }

  private RegexFormula wrapRegex(TFormulaInfo formulaInfo) {
    return getFormulaCreator().encapsulateRegex(formulaInfo);
  }

  @Override
  public StringFormula makeVariable(String pName) {
    checkVariableName(pName);
    return wrap(makeVariableImpl(pName));
  }

  protected abstract TFormulaInfo makeVariableImpl(String pName);

  @Override
  public StringFormula makeString(String pString) {
    return wrap(internalMakeString(pString));
  }

  protected abstract TFormulaInfo internalMakeString(String pName);

  @Override
  public StringFormula concat(StringFormula pString1, StringFormula pString2) {
    return wrap(concat(extractInfo(pString1), extractInfo(pString2)));
  }

  protected abstract TFormulaInfo concat(TFormulaInfo pString1, TFormulaInfo pString2);


  @Override
  public StringFormula substring(StringFormula pString, IntegerFormula pStart, IntegerFormula pLength) {
    return wrap(substring(extractInfo(pString), extractInfo(pStart), extractInfo(pLength)));
  }

  protected abstract TFormulaInfo substring(TFormulaInfo pString, TFormulaInfo pStart, TFormulaInfo pLength);

  @Override
  public StringFormula replace(StringFormula pString, StringFormula pFind, StringFormula pReplace) {
    return wrap(replace(extractInfo(pString), extractInfo(pFind), extractInfo(pReplace)));
  }

  protected abstract TFormulaInfo replace(TFormulaInfo pString, TFormulaInfo pFind, TFormulaInfo pReplace);

  @Override
  public StringFormula charAt(StringFormula pString, NumeralFormula.IntegerFormula pIndex) {
    return wrap(charAt(extractInfo(pString), extractInfo(pIndex)));
  }

  protected abstract TFormulaInfo charAt(TFormulaInfo pArray, TFormulaInfo pIndex);

  @Override
  public BooleanFormula equal(StringFormula pString1, StringFormula pString2) {
    TFormulaInfo param1 = extractInfo(pString1);
    TFormulaInfo param2 = extractInfo(pString2);

    return wrapBool(equal(param1, param2));
  }

  protected abstract TFormulaInfo equal(TFormulaInfo pParam1, TFormulaInfo pParam2);

  @Override
  public BooleanFormula contains(StringFormula pString, StringFormula pSearch) {
    return wrapBool(contains(extractInfo(pString), extractInfo(pSearch)));
  }

  protected abstract TFormulaInfo contains(TFormulaInfo pString, TFormulaInfo pSearch);

  @Override
  public BooleanFormula startsWith(StringFormula pString, StringFormula pPrefix) {
    return wrapBool(startsWith(extractInfo(pString), extractInfo(pPrefix)));
  }

  protected abstract TFormulaInfo startsWith(TFormulaInfo pString, TFormulaInfo pPrefix);

  @Override
  public BooleanFormula endsWith(StringFormula pString, StringFormula pSuffix) {
    return wrapBool(endsWith(extractInfo(pString), extractInfo(pSuffix)));
  }

  protected abstract TFormulaInfo endsWith(TFormulaInfo pString, TFormulaInfo pSuffix);

  @Override
  public BooleanFormula regexIn(StringFormula pString, RegexFormula pRegex) {
    return wrapBool(regexIn(extractInfo(pString), extractInfo(pRegex)));
  }

  protected abstract TFormulaInfo regexIn(TFormulaInfo pString, TFormulaInfo pRegex);

  @Override
  public IntegerFormula length(StringFormula pString) {
    return wrapInt(length(extractInfo(pString)));
  }

  protected abstract TFormulaInfo length(TFormulaInfo pString);

  @Override
  public IntegerFormula indexOf(StringFormula pString, StringFormula pSearch,
      IntegerFormula pInt) {
    return wrapInt(indexOf(extractInfo(pString), extractInfo(pSearch), extractInfo(pInt)));
  }

  protected abstract TFormulaInfo indexOf(TFormulaInfo pString, TFormulaInfo pSearch, TFormulaInfo pInt);

  @Override
  public RegexFormula str2Regex(String pString) {
    return wrapRegex(str2RegexImpl(pString));
  }

  protected abstract TFormulaInfo str2RegexImpl(String pString);

  @Override
  public RegexFormula regexStar(RegexFormula pRegex) {
    return wrapRegex(regexStar(extractInfo(pRegex)));
  }

  protected abstract TFormulaInfo regexStar(TFormulaInfo pRegex);

  @Override
  public RegexFormula regexPlus(RegexFormula pRegex) {
    return wrapRegex(regexPlus(extractInfo(pRegex)));
  }

  protected abstract TFormulaInfo regexPlus(TFormulaInfo pRegex);

  @Override
  public RegexFormula regexQuestion(RegexFormula pRegex) {
    return wrapRegex(regexQuestion(extractInfo(pRegex)));
  }

  protected abstract TFormulaInfo regexQuestion(TFormulaInfo pRegex);

  @Override
  public RegexFormula regexConcat(RegexFormula pRegex1, RegexFormula pRegex2) {
    return wrapRegex(regexConcat(extractInfo(pRegex1), extractInfo(pRegex2)));
  }

  protected abstract TFormulaInfo regexConcat(TFormulaInfo pRegex1, TFormulaInfo pRegex2);

  @Override
  public RegexFormula regexUnion(RegexFormula pRegex1, RegexFormula pRegex2) {
    return wrapRegex(regexUnion(extractInfo(pRegex1), extractInfo(pRegex2)));
  }

  protected abstract TFormulaInfo regexUnion(TFormulaInfo pRegex1, TFormulaInfo pRegex2);

  @Override
  public RegexFormula regexRange(String pStart, String pEnd) {
    return wrapRegex(regexRangeImpl(pStart, pEnd));
  }

  protected abstract TFormulaInfo regexRangeImpl(String pStart, String pEnd);

}
