/* Copyright 2022 Frederic GEDIN
 *
 *       Licensed under the Apache License,Version2.0(the"License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing,software
 *       distributed under the License is distributed on an"AS IS"BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */
package com.fgsoft.exceldatareader.parser.util;

import com.fgsoft.exceldatareader.util.SampleCompositeClass;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Test suite for the bean analyzer.
 */
class BeanAnalyzerTest {
    final BeanAnalyzer beanAnalyzer = new BeanAnalyzer();

    @Test
    final void testIsByteOK() {
        // Given
        final Byte objectOne = 100;
        final byte objectTwo = 100;
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }

    @Test
    final void testIsShortOK() {
        // Given
        final Short objectOne = 100;
        final short objectTwo = 100;
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }

    @Test
    final void testIsIntegerOK() {
        // Given
        final Integer objectOne = 100;
        final int objectTwo = 100;
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }

    @Test
    final void testIsLongOK() {
        // Given
        final Long objectOne = 100L;
        final long objectTwo = 100L;
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }

    @Test
    final void testIsFloatOK() {
        // Given
        final Float objectOne = 100.0f;
        final float objectTwo = 100.0f;
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }
    @Test
    final void testIsDoubleOK() {
        // Given
        final Double objectOne = 100.0;
        final double objectTwo = 100.0;
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }

    @Test
    final void testIsBooleanOK() {
        // Given
        final Boolean objectOne = true;
        final boolean objectTwo = true;
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }

    @Test
    final void testIsCharacterOK() {
        // Given
        final Character objectOne = 'A';
        final char objectTwo = 'A';
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(objectOne)).isTrue();
        assertThat(beanAnalyzer.hasSingleCellValue(objectTwo)).isTrue();
    }

    @Test
    final void testIsStringOK() {
        // Given
        final String string = "A string";
        // When Then
        assertThat(beanAnalyzer.hasSingleCellValue(string)).isTrue();
    }

    @Test
    final void getSingleCellValues() {
        // Given
        final List<Field> expected = new ArrayList<>();
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "bigDecimal", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "booleanValue", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "date", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "doubleValue", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "intValue", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "localDateTime", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "localDate", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "localTime", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "longValue", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "stringValue", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "sample", true));
        // When
        final List<Field> actual = beanAnalyzer.getSingleCellValues(SampleCompositeClass.class);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    final void getMultipleCellsValues() {
        // Given
        final List<Field> expected = new ArrayList<>();
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "composite", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "listOfStrings", true));
        expected.add(FieldUtils.getDeclaredField(SampleCompositeClass.class, "listOfComposites", true));
        // When
        final List<Field> actual = beanAnalyzer.getMultipleCellsValues(SampleCompositeClass.class);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    final void setValueOnField() {
        // Given
        final SampleCompositeClass instance = new SampleCompositeClass();
        final BigDecimal value = new BigDecimal("100.00");
        final Field field = FieldUtils.getDeclaredField(SampleCompositeClass.class, "bigDecimal", true);
        // When
        beanAnalyzer.setValueOnField(instance, field, value);
        // Then
        assertThat(instance.getBigDecimal()).isEqualTo(value);
    }
}
