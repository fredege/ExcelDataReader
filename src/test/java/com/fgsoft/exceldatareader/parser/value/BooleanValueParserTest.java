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

import com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode;
import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import com.fgsoft.exceldatareader.exception.IncorrectValueForTypeException;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fgsoft.exceldatareader.util.TestConstants.SHEET_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BooleanValueParserTest {
    @Mock
    private Cell cell;
    @Mock
    private Sheet sheet;
    @Mock
    private FormulaEvaluator evaluator;

    @BeforeEach
    final void initialize() {
    }

    @ParameterizedTest
    @CsvSource({"YES, true", "NO, false",
            "Y, true", "N, false",
            "Yes, true", "No, false",
            "true, true", "false, false"
    })
    final void testParseStringValueOK(String strValue, Boolean expected) {
        // Given
        final BooleanValueParser parser = new BooleanValueParser();
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        // When
        final Boolean value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(expected);
    }

    @Test
    final void testParseStringValueKO() {
        // Given
        final String strValue = "Not good";
        final BooleanValueParser parser = new BooleanValueParser();
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                strValue, Boolean.class.getName(), 0, 0, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testDoubleValue() {
        // Given
        final double dblValue = 1.0;
        final BooleanValueParser parser = new BooleanValueParser();
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                dblValue, Boolean.class.getName(), 0, 0, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testBooleanValueOK() {
        // Given
        final boolean boolValue = true;
        final BooleanValueParser parser = new BooleanValueParser();
        when(cell.getBooleanCellValue()).thenReturn(boolValue);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        // When
        final Boolean value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(boolValue);
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final BooleanValueParser parser = new BooleanValueParser();
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final Boolean value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final BooleanValueParser parser = new BooleanValueParser();
        cell = null;
        // When
        final Boolean value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testFormulaReturningAString() {
        // Given
        final String strValue = "YES";
        final CellValue cellValue = new CellValue(strValue);
        final BooleanValueParser parser = new BooleanValueParser();
        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(evaluator.evaluate(cell)).thenReturn(cellValue);
        // When
        final Boolean value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isTrue();
    }

    @Test
    final void testFormulaReturningADouble() {
        // Given
        final double dblValue = 0.0;
        final CellValue cellValue = new CellValue(dblValue);
        final BooleanValueParser parser = new BooleanValueParser();
        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(evaluator.evaluate(cell)).thenReturn(cellValue);
        // When
        final Boolean value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }
}
