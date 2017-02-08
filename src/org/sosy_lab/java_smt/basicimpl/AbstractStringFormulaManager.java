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

}
