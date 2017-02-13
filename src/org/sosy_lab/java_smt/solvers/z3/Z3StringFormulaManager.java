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
package org.sosy_lab.java_smt.solvers.z3;

import com.microsoft.z3.Native;
import org.sosy_lab.java_smt.basicimpl.AbstractStringFormulaManager;

public class Z3StringFormulaManager extends AbstractStringFormulaManager<Long,Long,Long,Long> {
  private final long z3context;

  Z3StringFormulaManager(Z3FormulaCreator creator) {
    super(creator);
    this.z3context = creator.getEnv();
  }

  @Override
  protected Long internalMakeString(String pName) {
    return Native.mkStr(z3context, pName);
  }

  @Override
  protected Long makeVariableImpl(String pName) {
    long type = getFormulaCreator().getStringType();
    return getFormulaCreator().makeVariable(type, pName);
  }

  @Override
  protected Long concat(Long pString1, Long pString2) {
    return Native.mkStrConcat(z3context, pString1, pString2);
  }

  @Override
  protected Long substring(Long pString, Long pStart, Long pLength) {
    return Native.mkStrSubstr(z3context, pString, pStart, pLength);
  }

  @Override
  protected Long replace(Long pString, Long pFind, Long pReplace) {
    return Native.mkStrReplace(z3context, pString, pFind, pReplace);
  }

  @Override
  protected Long charAt(Long pArray, Long pIndex) {
    return Native.mkStrAt(z3context,pArray,pIndex);
  }

  @Override
  protected Long equal(Long pParam1, Long pParam2) {
    return Native.mkEq(z3context, pParam1, pParam2);
  }

  @Override
  protected Long contains(Long pString, Long pSearch) {
    return Native.mkStrContains(z3context, pString, pSearch);
  }

  @Override
  protected Long startsWith(Long pString, Long pPrefix) {
    return Native.mkStrPrefixof(z3context, pString, pPrefix);
  }

  @Override
  protected Long endsWith(Long pString, Long pSuffix) {
    return Native.mkStrSuffixof(z3context, pString, pSuffix);
  }

  @Override
  protected Long regexIn(Long pString, Long pRegex) {
    return Native.mkStrInRegex(z3context, pString, pRegex);
  }

  @Override
  protected Long length(Long pString) {
    return Native.mkStrLength(z3context, pString);
  }

  @Override
  protected Long indexOf(Long pString, Long pSearch, Long pInt) {
    return Native.mkStrIndexof(z3context, pString, pSearch, pInt);
  }

  @Override
  protected Long str2RegexImpl(String pString) {
    return Native.mkStrToRegex(z3context, pString);
  }

  @Override
  protected Long regexStar(Long pRegex) {
    return Native.mkRegexStar(z3context, pRegex);
  }

  @Override
  protected Long regexPlus(Long pRegex) {
    return Native.mkRegexPlus(z3context, pRegex);
  }

  @Override
  protected Long regexQuestion(Long pRegex) {
    /*
     * Apparently this doesn't exist, so I'm going to implement it myself
     */
    return Native.mkRegexUnion(z3context,
        pRegex,
        Native.mkStrToRegex(z3context, "")
    );
  }

  @Override
  protected Long regexConcat(Long pRegex1, Long pRegex2) {
    return Native.mkRegexConcat(z3context, pRegex1, pRegex2);
  }

  @Override
  protected Long regexUnion(Long pRegex1, Long pRegex2) {
    return Native.mkRegexUnion(z3context, pRegex1, pRegex2);
  }

  @Override
  protected Long regexRangeImpl(String pStart, String pEnd) {
    return Native.mkRegexRange(z3context, pStart, pEnd);
  }
}
