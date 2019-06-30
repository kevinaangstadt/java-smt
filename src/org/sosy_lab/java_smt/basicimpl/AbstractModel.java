/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.math.BigInteger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.common.rationals.Rational;
import org.sosy_lab.java_smt.api.ArrayFormula;
import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.RationalFormula;
import org.sosy_lab.java_smt.api.StringFormula;

public abstract class AbstractModel<TFormulaInfo, TType, TEnv> implements Model {

  protected final FormulaCreator<TFormulaInfo, TType, TEnv, ?> creator;

  protected AbstractModel(FormulaCreator<TFormulaInfo, TType, TEnv, ?> creator) {
    this.creator = creator;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T extends Formula> T eval(T f) {
    TFormulaInfo evaluation = evalImpl(creator.extractInfo(f));
    return evaluation == null ? null : (T) creator.encapsulateWithTypeOf(evaluation);
  }

  @Nullable
  @Override
  public BigInteger evaluate(IntegerFormula f) {
    return (BigInteger) evaluateImpl(creator.extractInfo(f));
  }

  @Nullable
  @Override
  public Rational evaluate(RationalFormula f) {
    return (Rational) evaluateImpl(creator.extractInfo(f));
  }

  @Nullable
  @Override
  public Boolean evaluate(BooleanFormula f) {
    return (Boolean) evaluateImpl(creator.extractInfo(f));
  }

  @Nullable
  @Override
  public BigInteger evaluate(BitvectorFormula f) {
    return (BigInteger) evaluateImpl(creator.extractInfo(f));
  }

  @Nullable
  @Override
  public String evaluate(StringFormula f) {
    return (String) evaluateImpl(creator.extractInfo(f));
  }

  @Nullable
  @Override
  public final Object evaluate(Formula f) {
    Preconditions.checkArgument(
        !(f instanceof ArrayFormula),
        "cannot compute a simple constant evaluation for an array-formula");
    return evaluateImpl(creator.extractInfo(f));
  }

  /**
   * Simplify the given formula and replace all symbols with their model values. If a symbol is not
   * set in the model and evaluation aborts, return <code>null</code>.
   */
  @Nullable
  protected abstract TFormulaInfo evalImpl(TFormulaInfo formula);

  /**
   * Simplify the given formula and replace all symbols with their model values. If a symbol is not
   * set in the model and evaluation aborts, return <code>null</code>. Afterwards convert the
   * formula into a Java object as far as possible, i.e., try to match a primitive or simple type.
   */
  @Nullable
  protected Object evaluateImpl(TFormulaInfo f) {
    TFormulaInfo evaluatedF = evalImpl(f);
    return evaluatedF == null ? null : creator.convertValue(f, evaluatedF);
  }

  @Override
  public String toString() {
    return Joiner.on('\n').join(iterator());
  }

  public abstract static class CachingAbstractModel<TFormulaInfo, TType, TEnv>
      extends AbstractModel<TFormulaInfo, TType, TEnv> {

    private @Nullable ImmutableList<ValueAssignment> modelAssignments = null;

    protected CachingAbstractModel(FormulaCreator<TFormulaInfo, TType, TEnv, ?> pCreator) {
      super(pCreator);
    }

    @Override
    public ImmutableList<ValueAssignment> asList() {
      if (modelAssignments == null) {
        modelAssignments = toList();
      }
      return modelAssignments;
    }

    /** Build a list of all available assignments from the model. */
    protected abstract ImmutableList<ValueAssignment> toList();
  }
}
