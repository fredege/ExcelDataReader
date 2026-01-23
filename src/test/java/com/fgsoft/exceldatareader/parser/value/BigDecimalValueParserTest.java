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
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BigDecimalValueParserTest {
    private static final String SHEET_NAME = "Sheet name";

    @Mock
    private Cell cell;
    @Mock
    private Sheet sheet;
    @Mock
    private FormulaEvaluator evaluator;
    @Mock
    private CellStyle cellStyle;
    @Mock
    private Row row;

    @Test
    final void testParseStringValueOK() {
        // Given
        final String strValue = "1.0";
        final BigDecimalValueParser parser = new BigDecimalValueParser();
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        // When
        final BigDecimal value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNotNull().isEqualTo(BigDecimal.valueOf(1.0));
    }

    @Test
    final void testParseStringValueKO() {
        // Given
        final String strValue = "Sample string";
        final BigDecimalValueParser parser = new BigDecimalValueParser();
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                strValue, BigDecimal.class.getName(), 0, 0, SHEET_NAME);
        // When
        // Then
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "##0.00 €",
            "##0.00 $",
            "##0.00 £",
            "#,##0.00 \"€\""
    })    final void testParseBigDecimalValueOK(String format) {
        // Given
        final double dblValue = 1.0;
        final BigDecimal expected = BigDecimal.valueOf(dblValue).setScale(2, RoundingMode.HALF_UP);
        final BigDecimalValueParser parser = new BigDecimalValueParser();
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getSheet()).thenReturn(sheet);
        when(sheet.getRow(0)).thenReturn(row);
        when(row.getCell(0)).thenReturn(cell);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        when(cell.getCellStyle()).thenReturn(cellStyle);
        when(cellStyle.getDataFormatString()).thenReturn(format);
        // When
        final BigDecimal value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNotNull().isEqualTo(expected);
    }

    @Test
    final void testParseBooleanValueKO() {
        // Given
        final boolean boolValue = true;
        final BigDecimalValueParser parser = new BigDecimalValueParser();
        when(cell.getBooleanCellValue()).thenReturn(boolValue);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                "true", BigDecimal.class.getName(), 0, 0, SHEET_NAME);
        // When
        // Then
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final BigDecimalValueParser parser = new BigDecimalValueParser();
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final BigDecimal value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final BigDecimalValueParser parser = new BigDecimalValueParser();
        // When
        final BigDecimal value = parser.getValue(null, evaluator);
        // Then
        assertThat(value).isNull();
    }
}
