/*
 *  JavaSMT is an API wrapper for a collection of SMT solvers.
 *  This file is part of JavaSMT.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
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
package org.sosy_lab.java_smt.test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.common.truth.TruthJUnit.assume;
import static org.sosy_lab.java_smt.test.ProverEnvironmentSubject.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.truth.Truth;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.sosy_lab.common.rationals.ExtendedRational;
import org.sosy_lab.common.rationals.Rational;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.FloatingPointFormula;
import org.sosy_lab.java_smt.api.FloatingPointRoundingMode;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.FormulaType.FloatingPointType;
import org.sosy_lab.java_smt.api.InterpolatingProverEnvironment;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.Model.ValueAssignment;
import org.sosy_lab.java_smt.api.NumeralFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

@RunWith(Parameterized.class)
public class FloatingPointFormulaManagerTest extends SolverBasedTest0 {

  private static final int NUM_RANDOM_TESTS = 100;

  @Parameters(name = "{0}")
  public static Object[] getAllSolvers() {
    return Solvers.values();
  }

  @Parameter(0)
  public Solvers solver;

  @Override
  protected Solvers solverToUse() {
    return solver;
  }

  private FloatingPointType singlePrecType;
  private FloatingPointType doublePrecType;
  private FloatingPointFormula nan;
  private FloatingPointFormula posInf;
  private FloatingPointFormula negInf;
  private FloatingPointFormula zero;
  private FloatingPointFormula one;

  @Before
  public void init() {
    requireFloats();

    singlePrecType = FormulaType.getSinglePrecisionFloatingPointType();
    doublePrecType = FormulaType.getDoublePrecisionFloatingPointType();
    nan = fpmgr.makeNaN(singlePrecType);
    posInf = fpmgr.makePlusInfinity(singlePrecType);
    negInf = fpmgr.makeMinusInfinity(singlePrecType);
    zero = fpmgr.makeNumber(0.0, singlePrecType);
    one = fpmgr.makeNumber(1.0, singlePrecType);
  }

  @Test
  public void floatingPointType() {
    FloatingPointType type = FormulaType.getFloatingPointType(23, 42);
    FloatingPointFormula var = fpmgr.makeVariable("x", type);
    FloatingPointType result = (FloatingPointType) mgr.getFormulaType(var);

    assertWithMessage("exponent size")
        .that(result.getExponentSize())
        .isEqualTo(type.getExponentSize());
    assertWithMessage("mantissa size")
        .that(result.getMantissaSize())
        .isEqualTo(type.getMantissaSize());
  }

  @Test
  public void negative() throws SolverException, InterruptedException {
    for (double d : new double[] {-1, -2, -0.0, Double.NEGATIVE_INFINITY}) {
      FloatingPointFormula formula = fpmgr.makeNumber(d, singlePrecType);
      assertThatFormula(fpmgr.isNegative(formula)).isTautological();
      assertThatFormula(fpmgr.isNegative(fpmgr.negate(formula))).isUnsatisfiable();
    }
    for (double d : new double[] {1, 2, 0.0, Double.POSITIVE_INFINITY}) {
      FloatingPointFormula formula = fpmgr.makeNumber(d, singlePrecType);
      assertThatFormula(fpmgr.isNegative(formula)).isUnsatisfiable();
      assertThatFormula(fpmgr.isNegative(fpmgr.negate(formula))).isTautological();
    }
  }

  @Test
  public void parser() throws SolverException, InterruptedException {
    for (String s : new String[] {"-1", "-Infinity", "-0", "-0.0", "-0.000"}) {
      FloatingPointFormula formula = fpmgr.makeNumber(s, singlePrecType);
      assertThatFormula(fpmgr.isNegative(formula)).isTautological();
      assertThatFormula(fpmgr.isNegative(fpmgr.negate(formula))).isUnsatisfiable();
    }
    for (String s : new String[] {"1", "Infinity", "0", "0.0", "0.000"}) {
      FloatingPointFormula formula = fpmgr.makeNumber(s, singlePrecType);
      assertThatFormula(fpmgr.isNegative(formula)).isUnsatisfiable();
      assertThatFormula(fpmgr.isNegative(fpmgr.negate(formula))).isTautological();
    }
    for (String s : new String[] {"+1", "+Infinity", "+0", "+0.0", "+0.000"}) {
      FloatingPointFormula formula = fpmgr.makeNumber(s, singlePrecType);
      assertThatFormula(fpmgr.isNegative(formula)).isUnsatisfiable();
      assertThatFormula(fpmgr.isNegative(fpmgr.negate(formula))).isTautological();
    }
    // NaN is not positive and not negative.
    for (String s : new String[] {"NaN", "-NaN", "+NaN"}) {
      FloatingPointFormula formula = fpmgr.makeNumber(s, singlePrecType);
      assertThatFormula(fpmgr.isNegative(formula)).isUnsatisfiable();
      assertThatFormula(fpmgr.isNegative(fpmgr.negate(formula))).isUnsatisfiable();
    }
  }

  @Test
  public void negativeZeroDivision() throws SolverException, InterruptedException {
    BooleanFormula formula =
        fpmgr.equalWithFPSemantics(
            fpmgr.divide(
                one, fpmgr.makeNumber(-0.0, singlePrecType), FloatingPointRoundingMode.TOWARD_ZERO),
            fpmgr.makeMinusInfinity(singlePrecType));
    assertThatFormula(formula).isSatisfiable();
    assertThatFormula(bmgr.not(formula)).isUnsatisfiable();
  }

  @Test
  public void nanEqualNanIsUnsat() throws SolverException, InterruptedException {
    assertThatFormula(fpmgr.equalWithFPSemantics(nan, nan)).isUnsatisfiable();
  }

  @Test
  public void nanAssignedNanIsTrue() throws SolverException, InterruptedException {
    assertThatFormula(fpmgr.assignment(nan, nan)).isTautological();
  }

  @Test
  public void infinityOrdering() throws SolverException, InterruptedException {
    BooleanFormula order1 = fpmgr.greaterThan(posInf, zero);
    BooleanFormula order2 = fpmgr.greaterThan(zero, negInf);
    BooleanFormula order3 = fpmgr.greaterThan(posInf, negInf);

    assertThatFormula(bmgr.and(order1, order2, order3)).isTautological();
  }

  @Test
  public void infinityVariableOrdering() throws SolverException, InterruptedException {
    FloatingPointFormula var = fpmgr.makeVariable("x", singlePrecType);
    BooleanFormula varIsNan = fpmgr.isNaN(var);

    BooleanFormula order1 = fpmgr.greaterOrEquals(posInf, var);
    BooleanFormula order2 = fpmgr.lessOrEquals(negInf, var);

    assertThatFormula(bmgr.or(varIsNan, bmgr.and(order1, order2))).isTautological();
  }

  @Test
  public void specialValueFunctions() throws SolverException, InterruptedException {
    assertThatFormula(fpmgr.isInfinity(posInf)).isTautological();
    assertThatFormula(fpmgr.isNormal(posInf)).isUnsatisfiable();
    assertThatFormula(fpmgr.isSubnormal(posInf)).isUnsatisfiable();

    assertThatFormula(fpmgr.isInfinity(negInf)).isTautological();
    assertThatFormula(fpmgr.isNormal(negInf)).isUnsatisfiable();
    assertThatFormula(fpmgr.isSubnormal(negInf)).isUnsatisfiable();

    assertThatFormula(fpmgr.isNaN(nan)).isTautological();
    assertThatFormula(fpmgr.isNormal(nan)).isUnsatisfiable();
    assertThatFormula(fpmgr.isSubnormal(nan)).isUnsatisfiable();

    assertThatFormula(fpmgr.isZero(zero)).isTautological();
    assertThatFormula(fpmgr.isSubnormal(zero)).isUnsatisfiable();
    assertThatFormula(fpmgr.isSubnormal(zero)).isUnsatisfiable();

    FloatingPointFormula negZero = fpmgr.makeNumber(-0.0, singlePrecType);
    assertThatFormula(fpmgr.isZero(negZero)).isTautological();
    assertThatFormula(fpmgr.isSubnormal(negZero)).isUnsatisfiable();
    assertThatFormula(fpmgr.isSubnormal(negZero)).isUnsatisfiable();

    FloatingPointFormula minPosNormalValue = fpmgr.makeNumber(Float.MIN_NORMAL, singlePrecType);
    assertThatFormula(fpmgr.isSubnormal(minPosNormalValue)).isUnsatisfiable();
    assertThatFormula(fpmgr.isNormal(minPosNormalValue)).isSatisfiable();
    assertThatFormula(fpmgr.isZero(minPosNormalValue)).isUnsatisfiable();
  }

  @Test
  public void specialDoubles() throws SolverException, InterruptedException {
    assertThatFormula(fpmgr.assignment(fpmgr.makeNumber(Double.NaN, singlePrecType), nan))
        .isTautological();
    assertThatFormula(
            fpmgr.assignment(fpmgr.makeNumber(Double.POSITIVE_INFINITY, singlePrecType), posInf))
        .isTautological();
    assertThatFormula(
            fpmgr.assignment(fpmgr.makeNumber(Double.NEGATIVE_INFINITY, singlePrecType), negInf))
        .isTautological();
  }

  private void checkEqualityOfNumberConstantsFor(double value, FloatingPointType type)
      throws SolverException, InterruptedException {
    FloatingPointFormula doubleNumber = fpmgr.makeNumber(value, type);
    FloatingPointFormula stringNumber = fpmgr.makeNumber(Double.toString(value), type);
    FloatingPointFormula bigDecimalNumber = fpmgr.makeNumber(BigDecimal.valueOf(value), type);
    FloatingPointFormula rationalNumber =
        fpmgr.makeNumber(Rational.ofBigDecimal(BigDecimal.valueOf(value)), type);

    BooleanFormula eq1 = fpmgr.equalWithFPSemantics(doubleNumber, stringNumber);
    BooleanFormula eq2 = fpmgr.equalWithFPSemantics(doubleNumber, bigDecimalNumber);

    BooleanFormula eq3 = fpmgr.equalWithFPSemantics(doubleNumber, rationalNumber);
    assertThatFormula(bmgr.and(eq1, eq2, eq3)).isTautological();
  }

  @Test
  public void numberConstants() throws SolverException, InterruptedException {
    checkEqualityOfNumberConstantsFor(1.0, singlePrecType);
    checkEqualityOfNumberConstantsFor(-5.8774717541114375E-39, singlePrecType);
    checkEqualityOfNumberConstantsFor(-5.8774717541114375E-39, doublePrecType);
    checkEqualityOfNumberConstantsFor(3.4028234663852886e+38, singlePrecType);
    checkEqualityOfNumberConstantsFor(3.4028234663852886e+38, doublePrecType);

    // check unequality for large types
    FloatingPointType nearDouble = FormulaType.getFloatingPointType(12, 52);
    FloatingPointFormula h1 =
        fpmgr.makeNumber(BigDecimal.TEN.pow(309).multiply(BigDecimal.valueOf(1.0001)), nearDouble);
    FloatingPointFormula h2 =
        fpmgr.makeNumber(BigDecimal.TEN.pow(309).multiply(BigDecimal.valueOf(1.0002)), nearDouble);
    assertThatFormula(fpmgr.equalWithFPSemantics(h1, h2)).isUnsatisfiable();

    // check equality for short types
    FloatingPointType smallType = FormulaType.getFloatingPointType(4, 4);
    FloatingPointFormula i1 =
        fpmgr.makeNumber(BigDecimal.TEN.pow(50).multiply(BigDecimal.valueOf(1.001)), smallType);
    FloatingPointFormula i2 =
        fpmgr.makeNumber(BigDecimal.TEN.pow(50).multiply(BigDecimal.valueOf(1.002)), smallType);
    assertThatFormula(fpmgr.equalWithFPSemantics(i1, i2)).isTautological();

    // check equality for short types
    FloatingPointType smallType2 = FormulaType.getFloatingPointType(4, 4);
    FloatingPointFormula j1 =
        fpmgr.makeNumber(BigDecimal.TEN.pow(500).multiply(BigDecimal.valueOf(1.001)), smallType2);
    FloatingPointFormula j2 =
        fpmgr.makeNumber(BigDecimal.TEN.pow(500).multiply(BigDecimal.valueOf(1.002)), smallType2);
    assertThatFormula(fpmgr.equalWithFPSemantics(j1, j2)).isTautological();

    // Z3 supports at least FloatingPointType(15, 112). Larger types seem to be rounded.
    if (!solver.equals(Solvers.Z3)) {
      // check unequality for very large types
      FloatingPointType largeType = FormulaType.getFloatingPointType(100, 100);
      FloatingPointFormula k1 =
          fpmgr.makeNumber(BigDecimal.TEN.pow(200).multiply(BigDecimal.valueOf(1.001)), largeType);
      FloatingPointFormula k2 =
          fpmgr.makeNumber(BigDecimal.TEN.pow(200).multiply(BigDecimal.valueOf(1.002)), largeType);
      assertThatFormula(fpmgr.equalWithFPSemantics(k1, k2)).isUnsatisfiable();
    }
  }

  @Test
  public void cast() throws SolverException, InterruptedException {
    FloatingPointFormula doublePrecNumber = fpmgr.makeNumber(1.5, doublePrecType);
    FloatingPointFormula singlePrecNumber = fpmgr.makeNumber(1.5, singlePrecType);

    FloatingPointFormula narrowedNumber = fpmgr.castTo(doublePrecNumber, singlePrecType);
    FloatingPointFormula widenedNumber = fpmgr.castTo(singlePrecNumber, doublePrecType);

    assertThatFormula(fpmgr.equalWithFPSemantics(narrowedNumber, singlePrecNumber))
        .isTautological();
    assertThatFormula(fpmgr.equalWithFPSemantics(widenedNumber, doublePrecNumber)).isTautological();

    FloatingPointFormula doublePrecSmallNumber =
        fpmgr.makeNumber(5.8774717541114375E-39, doublePrecType);
    FloatingPointFormula singlePrecSmallNumber =
        fpmgr.makeNumber(5.8774717541114375E-39, singlePrecType);
    FloatingPointFormula widenedSmallNumber = fpmgr.castTo(singlePrecSmallNumber, doublePrecType);
    assertThatFormula(fpmgr.equalWithFPSemantics(widenedSmallNumber, doublePrecSmallNumber))
        .isTautological();
  }

  @Test
  public void bvToFpOne() throws SolverException, InterruptedException {
    requireBitvectors();

    BitvectorFormula bvOne = bvmgr.makeBitvector(32, 1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(1.0, singlePrecType);

    FloatingPointFormula signedBvToFpOne = fpmgr.castFrom(bvOne, true, singlePrecType);
    FloatingPointFormula unsignedBvToFpOne = fpmgr.castFrom(bvOne, false, singlePrecType);

    assertThatFormula(fpmgr.equalWithFPSemantics(fpOne, signedBvToFpOne)).isTautological();
    assertThatFormula(fpmgr.equalWithFPSemantics(fpOne, unsignedBvToFpOne)).isTautological();
  }

  /** check whether rounded input is equal to result with rounding-mode. */
  private void round0(
      double value, double toZero, double pos, double neg, double tiesEven, double tiesAway)
      throws SolverException, InterruptedException {
    FloatingPointFormula f = fpmgr.makeNumber(value, singlePrecType);

    // check types
    assertThat(mgr.getFormulaType(fpmgr.round(f, FloatingPointRoundingMode.TOWARD_ZERO)))
        .isEqualTo(singlePrecType);
    assertThat(mgr.getFormulaType(fpmgr.round(f, FloatingPointRoundingMode.TOWARD_POSITIVE)))
        .isEqualTo(singlePrecType);
    assertThat(mgr.getFormulaType(fpmgr.round(f, FloatingPointRoundingMode.TOWARD_NEGATIVE)))
        .isEqualTo(singlePrecType);
    assertThat(mgr.getFormulaType(fpmgr.round(f, FloatingPointRoundingMode.NEAREST_TIES_TO_EVEN)))
        .isEqualTo(singlePrecType);
    if (solver != Solvers.MATHSAT5) { // Mathsat does not support NEAREST_TIES_AWAY
      assertThat(mgr.getFormulaType(fpmgr.round(f, FloatingPointRoundingMode.NEAREST_TIES_AWAY)))
          .isEqualTo(singlePrecType);
    }

    // check values
    assertEquals(
        fpmgr.makeNumber(toZero, singlePrecType),
        fpmgr.round(f, FloatingPointRoundingMode.TOWARD_ZERO));

    assertEquals(
        fpmgr.makeNumber(pos, singlePrecType),
        fpmgr.round(f, FloatingPointRoundingMode.TOWARD_POSITIVE));

    assertEquals(
        fpmgr.makeNumber(neg, singlePrecType),
        fpmgr.round(f, FloatingPointRoundingMode.TOWARD_NEGATIVE));

    assertEquals(
        fpmgr.makeNumber(tiesEven, singlePrecType),
        fpmgr.round(f, FloatingPointRoundingMode.NEAREST_TIES_TO_EVEN));

    if (solver != Solvers.MATHSAT5) { // Mathsat does not support NEAREST_TIES_AWAY
      assertEquals(
          fpmgr.makeNumber(tiesAway, singlePrecType),
          fpmgr.round(f, FloatingPointRoundingMode.NEAREST_TIES_AWAY));
    }
  }

  private void assertEquals(FloatingPointFormula f1, FloatingPointFormula f2)
      throws SolverException, InterruptedException {
    assertThatFormula(fpmgr.equalWithFPSemantics(f1, f2)).isTautological();
  }

  @Test
  public void round() throws SolverException, InterruptedException {

    // constants
    round0(0, 0, 0, 0, 0, 0);
    round0(1, 1, 1, 1, 1, 1);
    round0(-1, -1, -1, -1, -1, -1);

    // positive odd
    round0(1.1, 1, 2, 1, 1, 1);
    round0(1.5, 1, 2, 1, 2, 2);
    round0(1.9, 1, 2, 1, 2, 2);

    // positive even
    round0(10.1, 10, 11, 10, 10, 10);
    round0(10.5, 10, 11, 10, 10, 11);
    round0(10.9, 10, 11, 10, 11, 11);

    // negative odd
    round0(-1.1, -1, -1, -2, -1, -1);
    round0(-1.5, -1, -1, -2, -2, -2);
    round0(-1.9, -1, -1, -2, -2, -2);

    // negative even
    round0(-10.1, -10, -10, -11, -10, -10);
    round0(-10.5, -10, -10, -11, -10, -11);
    round0(-10.9, -10, -10, -11, -11, -11);
  }

  @Test
  public void bvToFpMinusOne() throws SolverException, InterruptedException {
    requireBitvectors();

    BitvectorFormula bvOne = bvmgr.makeBitvector(32, -1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(-1.0, singlePrecType);

    // A 32bit value "-1" when interpreted as unsigned is 2^31 - 1
    FloatingPointFormula fpMinInt = fpmgr.makeNumber(Math.pow(2, 32) - 1, singlePrecType);

    FloatingPointFormula unsignedBvToFpOne = fpmgr.castFrom(bvOne, false, singlePrecType);
    FloatingPointFormula signedBvToFpOne = fpmgr.castFrom(bvOne, true, singlePrecType);

    assertThatFormula(fpmgr.equalWithFPSemantics(fpOne, signedBvToFpOne)).isTautological();
    assertThatFormula(fpmgr.equalWithFPSemantics(fpMinInt, unsignedBvToFpOne)).isTautological();
  }

  @Test
  public void fpToBvOne() throws SolverException, InterruptedException {
    requireBitvectors();

    BitvectorFormula bvOne = bvmgr.makeBitvector(32, 1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(1.0, singlePrecType);

    BitvectorFormula fpToBvOne = fpmgr.castTo(fpOne, FormulaType.getBitvectorTypeWithSize(32));

    assertThatFormula(bvmgr.equal(bvOne, fpToBvOne)).isTautological();
  }

  @Test
  public void fpToBvMinusOne() throws SolverException, InterruptedException {
    requireBitvectors();

    BitvectorFormula bvOne = bvmgr.makeBitvector(32, -1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(-1.0, singlePrecType);

    BitvectorFormula fpToBvOne = fpmgr.castTo(fpOne, FormulaType.getBitvectorTypeWithSize(32));
    assertThatFormula(bvmgr.equal(bvOne, fpToBvOne)).isTautological();
  }

  @Test
  public void rationalToFpOne() throws SolverException, InterruptedException {
    requireRationals();

    NumeralFormula ratOne = rmgr.makeNumber(1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(1.0, singlePrecType);

    FloatingPointFormula ratToFpOne = fpmgr.castFrom(ratOne, true, singlePrecType);
    FloatingPointFormula unsignedRatToFpOne = fpmgr.castFrom(ratOne, false, singlePrecType);
    assertThat(unsignedRatToFpOne).isEqualTo(ratToFpOne);

    assertThatFormula(fpmgr.equalWithFPSemantics(fpOne, ratToFpOne)).isSatisfiable();
  }

  @Test
  public void rationalToFpMinusOne() throws SolverException, InterruptedException {
    requireBitvectors();

    NumeralFormula ratOne = rmgr.makeNumber(-1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(-1.0, singlePrecType);

    FloatingPointFormula ratToFpOne = fpmgr.castFrom(ratOne, true, singlePrecType);
    FloatingPointFormula unsignedRatToFpOne = fpmgr.castFrom(ratOne, false, singlePrecType);
    assertThat(unsignedRatToFpOne).isEqualTo(ratToFpOne);

    assertThatFormula(fpmgr.equalWithFPSemantics(fpOne, ratToFpOne)).isSatisfiable();
  }

  @Test
  public void fpToRationalOne() throws SolverException, InterruptedException {
    requireRationals();

    NumeralFormula ratOne = rmgr.makeNumber(1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(1.0, singlePrecType);

    NumeralFormula fpToRatOne = fpmgr.castTo(fpOne, FormulaType.RationalType);

    assertThatFormula(rmgr.equal(ratOne, fpToRatOne)).isSatisfiable();
  }

  @Test
  public void fpToRationalMinusOne() throws SolverException, InterruptedException {
    requireRationals();

    NumeralFormula ratOne = rmgr.makeNumber(-1);
    FloatingPointFormula fpOne = fpmgr.makeNumber(-1.0, singlePrecType);

    NumeralFormula fpToRatOne = fpmgr.castTo(fpOne, FormulaType.RationalType);

    assertThatFormula(rmgr.equal(ratOne, fpToRatOne)).isSatisfiable();
  }

  @Test
  public void fpTraversal() {
    assertThat(mgr.extractVariables(zero)).isEmpty();
    assertThat(mgr.extractVariablesAndUFs(zero)).isEmpty();

    assertThat(mgr.extractVariables(one)).isEmpty();
    assertThat(mgr.extractVariablesAndUFs(one)).isEmpty();

    assertThat(mgr.extractVariables(posInf)).isEmpty();
    assertThat(mgr.extractVariablesAndUFs(posInf)).isEmpty();

    assertThat(mgr.extractVariables(nan)).isEmpty();
    assertThat(mgr.extractVariablesAndUFs(nan)).isEmpty();

    FloatingPointFormula var = fpmgr.makeVariable("x", singlePrecType);
    assertThat(mgr.extractVariables(var)).containsExactly("x", var);
    assertThat(mgr.extractVariablesAndUFs(var)).containsExactly("x", var);
  }

  @Test
  public void fpTraversalWithRoundingMode() {
    FloatingPointFormula two = fpmgr.makeNumber(2.0, singlePrecType);
    FloatingPointFormula var = fpmgr.makeVariable("x", singlePrecType);
    FloatingPointFormula mult = fpmgr.multiply(two, var);
    assertThat(mgr.extractVariables(mult)).containsExactly("x", var);
    assertThat(mgr.extractVariablesAndUFs(mult)).containsExactly("x", var);
  }

  @Test
  public void fpIeeeConversionTypes() {
    assume()
        .withMessage("FP-BV conversion of Z3 misses sign bit")
        .that(solverToUse())
        .isNotEqualTo(Solvers.Z3);

    FloatingPointFormula var = fpmgr.makeVariable("var", singlePrecType);
    assertThat(mgr.getFormulaType(fpmgr.toIeeeBitvector(var)))
        .isEqualTo(FormulaType.getBitvectorTypeWithSize(32));
  }

  @Test
  public void fpIeeeConversion() throws SolverException, InterruptedException {
    assume()
        .withMessage("FP-BV conversion of Z3 misses sign bit")
        .that(solverToUse())
        .isNotEqualTo(Solvers.Z3);

    FloatingPointFormula var = fpmgr.makeVariable("var", singlePrecType);
    assertThatFormula(
            fpmgr.assignment(
                var, fpmgr.fromIeeeBitvector(fpmgr.toIeeeBitvector(var), singlePrecType)))
        .isTautological();
  }

  @Test
  public void ieeeFpConversion() throws SolverException, InterruptedException {
    assume()
        .withMessage("FP-BV conversion of Z3 misses sign bit")
        .that(solverToUse())
        .isNotEqualTo(Solvers.Z3);

    BitvectorFormula var = bvmgr.makeBitvector(32, 123456789);
    assertThatFormula(
            bvmgr.equal(var, fpmgr.toIeeeBitvector(fpmgr.fromIeeeBitvector(var, singlePrecType))))
        .isTautological();
  }

  @Test
  public void checkIeeeFpConversion32() throws SolverException, InterruptedException {
    for (float f : getListOfFloats()) {
      checkFP(
          singlePrecType,
          bvmgr.makeBitvector(32, Float.floatToRawIntBits(f)),
          fpmgr.makeNumber(f, singlePrecType));
    }
  }

  @Test
  public void checkIeeeFpConversion64() throws SolverException, InterruptedException {
    for (double d : getListOfDoubles()) {
      checkFP(
          doublePrecType,
          bvmgr.makeBitvector(64, Double.doubleToRawLongBits(d)),
          fpmgr.makeNumber(d, doublePrecType));
    }
  }

  private List<Float> getListOfFloats() {
    List<Float> flts =
        Lists.newArrayList(
            // Float.NaN, // NaN is no unique bitvector
            Float.MIN_NORMAL,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            Float.POSITIVE_INFINITY,
            Float.NEGATIVE_INFINITY,
            0.0f // , -0.0f // MathSat5 fails for NEGATIVE_ZERO
            );

    for (int i = 1; i < 20; i++) {
      for (int j = 1; j < 20; j++) {
        flts.add((float) (i * Math.pow(10, j)));
      }
    }

    Random rand = new Random(0);
    for (int i = 0; i < NUM_RANDOM_TESTS; i++) {
      float flt = Float.intBitsToFloat(rand.nextInt());
      if (!Float.isNaN(flt)) {
        flts.add(flt);
      }
    }

    return flts;
  }

  private List<Double> getListOfDoubles() {
    List<Double> dbls =
        Lists.newArrayList(
            // Double.NaN, // NaN is no unique bitvector
            Double.MIN_NORMAL,
            Double.MIN_VALUE,
            Double.MAX_VALUE,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            0.0 // , -0.0 // MathSat5 fails for NEGATIVE_ZERO
            );

    for (int i = 1; i < 20; i++) {
      for (int j = 1; j < 20; j++) {
        dbls.add(i * Math.pow(10, j));
      }
    }

    Random rand = new Random(0);
    for (int i = 0; i < NUM_RANDOM_TESTS; i++) {
      double d = Double.longBitsToDouble(rand.nextLong());
      if (!Double.isNaN(d)) {
        dbls.add(d);
      }
    }

    return dbls;
  }

  private void checkFP(FloatingPointType type, BitvectorFormula bv, FloatingPointFormula flt)
      throws SolverException, InterruptedException {
    BitvectorFormula var = bvmgr.makeVariable(type.getTotalSize(), "x");

    Truth.assertThat(mgr.getFormulaType(var))
        .isEqualTo(mgr.getFormulaType(fpmgr.toIeeeBitvector(flt)));
    Truth.assertThat(mgr.getFormulaType(flt))
        .isEqualTo(mgr.getFormulaType(fpmgr.fromIeeeBitvector(bv, type)));

    assertThatFormula(bmgr.and(bvmgr.equal(bv, var), bvmgr.equal(var, fpmgr.toIeeeBitvector(flt))))
        .isSatisfiable();
    assertThatFormula(bvmgr.equal(bv, fpmgr.toIeeeBitvector(flt))).isTautological();
    assertThatFormula(fpmgr.equalWithFPSemantics(flt, fpmgr.fromIeeeBitvector(bv, type)))
        .isTautological();
  }

  @Test
  public void fpModelValue() throws SolverException, InterruptedException {
    FloatingPointFormula zeroVar = fpmgr.makeVariable("zero", singlePrecType);
    BooleanFormula zeroEq = fpmgr.assignment(zeroVar, zero);

    FloatingPointFormula oneVar = fpmgr.makeVariable("one", singlePrecType);
    BooleanFormula oneEq = fpmgr.assignment(oneVar, one);

    FloatingPointFormula nanVar = fpmgr.makeVariable("nan", singlePrecType);
    BooleanFormula nanEq = fpmgr.assignment(nanVar, nan);

    FloatingPointFormula posInfVar = fpmgr.makeVariable("posInf", singlePrecType);
    BooleanFormula posInfEq = fpmgr.assignment(posInfVar, posInf);

    FloatingPointFormula negInfVar = fpmgr.makeVariable("negInf", singlePrecType);
    BooleanFormula negInfEq = fpmgr.assignment(negInfVar, negInf);

    try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
      prover.push(zeroEq);
      prover.push(oneEq);
      prover.push(nanEq);
      prover.push(posInfEq);
      prover.push(negInfEq);

      assertThat(prover).isSatisfiable();

      try (Model model = prover.getModel()) {

        Object zeroValue = model.evaluate(zeroVar);
        ValueAssignment zeroAssignment =
            new ValueAssignment(zeroVar, zero, zeroEq, "zero", zeroValue, ImmutableList.of());
        assertThat(zeroValue)
            .isAnyOf(ExtendedRational.ZERO, Rational.ZERO, BigDecimal.ZERO, 0.0, 0.0f);

        Object oneValue = model.evaluate(oneVar);
        ValueAssignment oneAssignment =
            new ValueAssignment(oneVar, one, oneEq, "one", oneValue, ImmutableList.of());
        assertThat(oneValue)
            .isAnyOf(new ExtendedRational(Rational.ONE), Rational.ONE, BigDecimal.ONE, 1.0, 1.0f);

        Object nanValue = model.evaluate(nanVar);
        ValueAssignment nanAssignment =
            new ValueAssignment(nanVar, nan, nanEq, "nan", nanValue, ImmutableList.of());
        assertThat(nanValue).isAnyOf(ExtendedRational.NaN, Double.NaN, Float.NaN);

        Object posInfValue = model.evaluate(posInfVar);
        ValueAssignment posInfAssignment =
            new ValueAssignment(
                posInfVar, posInf, posInfEq, "posInf", posInfValue, ImmutableList.of());
        assertThat(posInfValue)
            .isAnyOf(ExtendedRational.INFTY, Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

        Object negInfValue = model.evaluate(negInfVar);
        ValueAssignment negInfAssignment =
            new ValueAssignment(
                negInfVar, negInf, negInfEq, "negInf", negInfValue, ImmutableList.of());
        assertThat(negInfValue)
            .isAnyOf(ExtendedRational.NEG_INFTY, Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        assertThat(model)
            .containsExactly(
                zeroAssignment, oneAssignment, nanAssignment, posInfAssignment, negInfAssignment);
      }
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void fpInterpolation() throws SolverException, InterruptedException {
    assume()
        .withMessage("MathSAT5 does not support floating-point interpolation")
        .that(solver)
        .isNotEqualTo(Solvers.MATHSAT5);

    FloatingPointFormula var = fpmgr.makeVariable("x", singlePrecType);
    BooleanFormula f1 = fpmgr.equalWithFPSemantics(var, zero);
    BooleanFormula f2 = bmgr.not(fpmgr.isZero(var));
    try (InterpolatingProverEnvironment<Object> prover =
        (InterpolatingProverEnvironment<Object>) context.newProverEnvironmentWithInterpolation()) {
      Object itpGroup1 = prover.push(f1);
      prover.push(f2);

      assertThat(prover).isUnsatisfiable();

      BooleanFormula itp = prover.getInterpolant(ImmutableList.of(itpGroup1));
      assertThatFormula(f1).implies(itp);
      assertThatFormula(bmgr.and(itp, f2)).isUnsatisfiable();
    }
  }
}
