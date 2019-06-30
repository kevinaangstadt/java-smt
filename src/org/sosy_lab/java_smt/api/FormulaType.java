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
package org.sosy_lab.java_smt.api;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Immutable;
import java.util.List;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.RationalFormula;

/**
 * Type of a formula.
 *
 * @param <T> Formula class corresponding to the given formula type.
 */
@SuppressWarnings("checkstyle:constantname")
@Immutable
public abstract class FormulaType<T extends Formula> {

  private FormulaType() {}

  public boolean isArrayType() {
    return false;
  }

  public boolean isBitvectorType() {
    return false;
  }

  public boolean isBooleanType() {
    return false;
  }

  public boolean isFloatingPointType() {
    return false;
  }

  public boolean isFloatingPointRoundingModeType() {
    return false;
  }

  public boolean isNumeralType() {
    return false;
  }

  public boolean isRationalType() {
    return false;
  }

  public boolean isIntegerType() {
    return false;
  }

  public boolean isStringType() {
    return false;
  }

  public boolean isRegexType() {
    return false;
  }

  @Override
  public abstract String toString();

  @Immutable
  public abstract static class NumeralType<T extends NumeralFormula> extends FormulaType<T> {

    @Override
    public final boolean isNumeralType() {
      return true;
    }
  }

  public static final FormulaType<RationalFormula> RationalType =
      new NumeralType<RationalFormula>() {

        @Override
        public boolean isRationalType() {
          return true;
        }

        @Override
        public String toString() {
          return "Rational";
        }
      };

  public static final FormulaType<IntegerFormula> IntegerType =
      new NumeralType<IntegerFormula>() {

        @Override
        public boolean isIntegerType() {
          return true;
        }

        @Override
        public String toString() {
          return "Integer";
        }
      };

  public static final FormulaType<BooleanFormula> BooleanType =
      new FormulaType<BooleanFormula>() {

        @Override
        public boolean isBooleanType() {
          return true;
        }

        @Override
        public String toString() {
          return "Boolean";
        }
      };

  public static BitvectorType getBitvectorTypeWithSize(int size) {
    return new BitvectorType(size);
  }

  @Immutable
  public static final class BitvectorType extends FormulaType<BitvectorFormula> {
    private final int size;

    private BitvectorType(int size) {
      this.size = size;
    }

    @Override
    public boolean isBitvectorType() {
      return true;
    }

    public int getSize() {
      return size;
    }

    @Override
    public String toString() {
      return "Bitvector<" + getSize() + ">";
    }

    @Override
    public boolean equals(Object pObj) {
      if (pObj == this) {
        return true;
      }
      if (!(pObj instanceof BitvectorType)) {
        return false;
      }
      BitvectorType other = (BitvectorType) pObj;
      return size == other.size;
    }

    @Override
    public int hashCode() {
      return size;
    }
  }

  public static FloatingPointType getFloatingPointType(int exponentSize, int mantissaSize) {
    return new FloatingPointType(exponentSize, mantissaSize);
  }

  public static FloatingPointType getSinglePrecisionFloatingPointType() {
    return FloatingPointType.SINGLE_PRECISION_FP_TYPE;
  }

  public static FloatingPointType getDoublePrecisionFloatingPointType() {
    return FloatingPointType.DOUBLE_PRECISION_FP_TYPE;
  }

  @Immutable
  public static final class FloatingPointType extends FormulaType<FloatingPointFormula> {

    private static final FloatingPointType SINGLE_PRECISION_FP_TYPE = new FloatingPointType(8, 23);
    private static final FloatingPointType DOUBLE_PRECISION_FP_TYPE = new FloatingPointType(11, 52);

    private final int exponentSize;
    private final int mantissaSize;

    private FloatingPointType(int pExponentSize, int pMantissaSize) {
      exponentSize = pExponentSize;
      mantissaSize = pMantissaSize;
    }

    @Override
    public boolean isFloatingPointType() {
      return true;
    }

    public int getExponentSize() {
      return exponentSize;
    }

    public int getMantissaSize() {
      return mantissaSize;
    }

    /** Return the total size of a value of this type in bits. */
    public int getTotalSize() {
      return exponentSize + mantissaSize + 1;
    }

    @Override
    public int hashCode() {
      return (31 + exponentSize) * 31 + mantissaSize;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof FloatingPointType)) {
        return false;
      }
      FloatingPointType other = (FloatingPointType) obj;
      return this.exponentSize == other.exponentSize && this.mantissaSize == other.mantissaSize;
    }

    @Override
    public String toString() {
      return "FloatingPoint<exp=" + exponentSize + ",mant=" + mantissaSize + ">";
    }
  }

  public static final FormulaType<FloatingPointRoundingModeFormula> FloatingPointRoundingModeType =
      new FloatingPointRoundingModeType();

  private static class FloatingPointRoundingModeType
      extends FormulaType<FloatingPointRoundingModeFormula> {

    @Override
    public boolean isFloatingPointRoundingModeType() {
      return true;
    }

    @Override
    public String toString() {
      return "FloatingPointRoundingMode";
    }
  }

  public static <TD extends Formula, TR extends Formula> ArrayFormulaType<TD, TR> getArrayType(
      FormulaType<TD> pDomainSort, FormulaType<TR> pRangeSort) {
    return new ArrayFormulaType<>(pDomainSort, pRangeSort);
  }

  public static final class ArrayFormulaType<TI extends Formula, TE extends Formula>
      extends FormulaType<ArrayFormula<TI, TE>> {

    private final FormulaType<TE> elementType;
    private final FormulaType<TI> indexType;

    public ArrayFormulaType(FormulaType<TI> pIndexType, FormulaType<TE> pElementType) {
      this.indexType = Preconditions.checkNotNull(pIndexType);
      this.elementType = Preconditions.checkNotNull(pElementType);
    }

    public FormulaType<TE> getElementType() {
      return elementType;
    }

    public FormulaType<TI> getIndexType() {
      return indexType;
    }

    @Override
    public boolean isArrayType() {
      return true;
    }

    @Override
    public String toString() {
      return "Array";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + elementType.hashCode();
      result = prime * result + indexType.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (!(obj instanceof ArrayFormulaType)) {
        return false;
      }

      ArrayFormulaType<?, ?> other = (ArrayFormulaType<?, ?>) obj;

      return elementType.equals(other.elementType) && indexType.equals(other.indexType);
    }
  }

  public static StringType getStringType() {
    return new StringType();
  }

  public static final class StringType extends FormulaType<StringFormula> {

    @Override
    public boolean isStringType() {
      return true;
    }

    @Override
    public String toString() {
      return "String";
    }

    @Override
    public boolean equals(Object pObj) {
      if (pObj == this) {
        return true;
      }
      return pObj instanceof StringType;
    }

    @Override
    public int hashCode() {
      return 47;
    }

  }

  public static RegexType getRegexType() {
    return new RegexType();
  }

  public static final class RegexType extends FormulaType<RegexFormula> {

    @Override
    public boolean isRegexType() {
      return true;
    }

    @Override
    public String toString() {
      return "Regex";
    }

    @Override
    public boolean equals(Object pObj) {
      if (pObj == this) {
        return true;
      }
      return pObj instanceof RegexType;
    }

    @Override
    public int hashCode() {
      return 29;
    }
  }

  /**
   * Parse a string and return the corresponding type. This method is the counterpart of {@link
   * #toString()}.
   */
  public static FormulaType<?> fromString(String t) {
    if (BooleanType.toString().equals(t)) {
      return BooleanType;
    } else if (IntegerType.toString().equals(t)) {
      return IntegerType;
    } else if (RationalType.toString().equals(t)) {
      return RationalType;
    } else if (FloatingPointRoundingModeType.toString().equals(t)) {
      return FloatingPointRoundingModeType;
    } else if (t.startsWith("FloatingPoint<")) {
      // FloatingPoint<exp=11,mant=52>
      List<String> exman = Splitter.on(',').limit(2).splitToList(t.substring(14, t.length() - 1));
      return FormulaType.getFloatingPointType(
          Integer.parseInt(exman.get(0).substring(4)), Integer.parseInt(exman.get(1).substring(5)));
    } else if (t.startsWith("Bitvector<")) {
      // Bitvector<32>
      return FormulaType.getBitvectorTypeWithSize(
          Integer.parseInt(t.substring(10, t.length() - 1)));
    } else if (t.equals("String")) {
      return FormulaType.getStringType();
    } else if (t.equals("Regex")) {
      return FormulaType.getRegexType();
    } else {
      throw new AssertionError("unknown type:" + t);
    }
  }
}
