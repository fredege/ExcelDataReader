/* Copyright 2020 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.value;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StringValueParserTest {
    @Mock
    private Cell cell;
    @Mock
    private FormulaEvaluator evaluator;

    @Test
    final void testParseStringValueOK() {
        // Given
        final String strValue = "Sample string value";
        final StringValueParser parser = new StringValueParser();
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        // When
        final String value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(strValue);
    }

    @Test
    final void testDoubleValueOK() {
        // Given
        final double dblValue = 1.0;
        final StringValueParser parser = new StringValueParser();
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        // When
        final String value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(String.valueOf(dblValue));
    }

    @Test
    final void testBooleanValueOK() {
        // Given
        final boolean boolValue = true;
        final StringValueParser parser = new StringValueParser();
        when(cell.getBooleanCellValue()).thenReturn(boolValue);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        // When
        final String value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(String.valueOf(boolValue));
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final StringValueParser parser = new StringValueParser();
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final String value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final StringValueParser parser = new StringValueParser();
        cell = null;
        // When
        final String value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testFormula() {
        // Given
        final String strValue = "Sample string value";
        final CellValue cellValue = new CellValue(strValue);
        final StringValueParser parser = new StringValueParser();
        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(evaluator.evaluate(cell)).thenReturn(cellValue);
        // When
        final String value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(strValue);
    }
}
