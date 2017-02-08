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
    return Native.mkString(z3context, pName);
  }

  @Override
  protected Long makeVariableImpl(String pName) {
    long type = getFormulaCreator().getStringType();
    return getFormulaCreator().makeVariable(type, pName);
  }

  @Override
  protected Long concat(Long pString1, Long pString2) {
    return Native.mkConcat(z3context, pString1, pString2);
  }

  @Override
  protected Long substring(Long pString, Long pStart, Long pLength) {
    return Native.mkSeqExtract(z3context, pString, pStart, pLength);
  }

  @Override
  protected Long replace(Long pString, Long pFind, Long pReplace) {
    return Native.mkSeqReplace(z3context, pString, pFind, pReplace);
  }

  @Override
  protected Long charAt(Long pArray, Long pIndex) {
    return Native.mkSeqAt(z3context,pArray,pIndex);
  }

  @Override
  protected Long equal(Long pParam1, Long pParam2) {
    return Native.mkEq(z3context, pParam1, pParam2);
  }
}
