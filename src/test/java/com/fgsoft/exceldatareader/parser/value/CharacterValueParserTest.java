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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fgsoft.exceldatareader.util.TestConstants.SHEET_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CharacterValueParserTest {
    @Mock
    private Cell cell;
    @Mock
    private Sheet sheet;
    @Mock
    private FormulaEvaluator evaluator;

    @Test
    final void testParseStringValueKO() {
        // Given
        final String strValue = "Sample string value";
        final CharacterValueParser parser = new CharacterValueParser();
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                strValue, Character.class.getName(), 0, 0, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testParseStringValueOK() {
        // Given
        final String strValue = "S";
        final CharacterValueParser parser = new CharacterValueParser();
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        // When
        final Character actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(strValue.charAt(0));
    }

    @Test
    final void testDoubleValueKO() {
        // Given
        final double dblValue = 1.0;
        final CharacterValueParser parser = new CharacterValueParser();
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                dblValue, Character.class.getName(), 0, 0, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testBooleanValueKO() {
        // Given
        final boolean boolValue = true;
        final CharacterValueParser parser = new CharacterValueParser();
        when(cell.getBooleanCellValue()).thenReturn(boolValue);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                boolValue, Character.class.getName(), 0, 0, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final CharacterValueParser parser = new CharacterValueParser();
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final Character value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final CharacterValueParser parser = new CharacterValueParser();
        cell = null;
        // When
        final Character value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testFormula() {
        // Given
        final String strValue = "S";
        final CellValue cellValue = new CellValue(strValue);
        final CharacterValueParser parser = new CharacterValueParser();
        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(evaluator.evaluate(cell)).thenReturn(cellValue);
        // When
        final Character value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(strValue.charAt(0));
    }
}
