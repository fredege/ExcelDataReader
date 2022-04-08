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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntegerValueParserTest {
    private static final String SHEET_NAME = "Sheet name";

    @Mock
    private Cell cell;
    @Mock
    private Sheet sheet;
    @Mock
    private FormulaEvaluator evaluator;

    @Test
    final void testParseStringValueOK() {
        // Given
        final String strValue = "1";
        final IntegerValueParser parser = new IntegerValueParser();
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        // When
        final Integer value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNotNull().isEqualTo(Integer.valueOf(strValue));
    }

    @Test
    final void testParseStringValueKO() {
        // Given
        final String strValue = "Sample string";
        final IntegerValueParser parser = new IntegerValueParser();
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                strValue, Integer.class.getName(), 0, 0, SHEET_NAME);
        // When
        // Then
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testParseIntegerValueOK() {
        // Given
        final double dblValue = 1.2;
        final IntegerValueParser parser = new IntegerValueParser();
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        // When
        final Integer value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNotNull().isEqualTo(1);
    }

    @Test
    final void testParseBooleanValueKO() {
        // Given
        final boolean boolValue = true;
        final IntegerValueParser parser = new IntegerValueParser();
        when(cell.getBooleanCellValue()).thenReturn(boolValue);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                "true", Integer.class.getName(), 0, 0, SHEET_NAME);
        // When
        // Then
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final IntegerValueParser parser = new IntegerValueParser();
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final Integer value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final IntegerValueParser parser = new IntegerValueParser();
        // When
        final Integer value = parser.getValue(null, evaluator);
        // Then
        assertThat(value).isNull();
    }
}
