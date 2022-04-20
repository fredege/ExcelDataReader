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
package com.fgsoft.exceldatareader.parser.value;

import com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode;
import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import com.fgsoft.exceldatareader.exception.IncorrectValueForTypeException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.fgsoft.exceldatareader.util.TestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalTimeValueParserTest {
    private static final int ROW_NUM = 0;
    private static final int COL_NUM = 0;

    @Mock
    private Cell cell;
    @Mock
    private CellStyle cellStyle;
    @Mock
    private Sheet sheet;
    @Mock
    private Row row;
    @Mock
    private FormulaEvaluator evaluator;

    @Test
    final void testParseStringOK() {
        // Given
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        final LocalTime time = LocalTime.now();
        final String strValue = time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        // When
        final LocalTime actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(time);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sample string", "17:05:65", "17:65:15", "32:05:15"})
    final void testParseStringKOAnyString(final String strValue) {
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(ROW_NUM);
        when(cell.getColumnIndex()).thenReturn(COL_NUM);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                strValue, LocalTime.class.getName(), ROW_NUM, COL_NUM, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testParseDoubleOK() {
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        final LocalDateTime localDateTime = LocalDateTime.now();
        final LocalTime time = localDateTime.toLocalTime();
        final double dblValue = 44693.0;
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(ROW_NUM);
        when(cell.getColumnIndex()).thenReturn(COL_NUM);
        when(sheet.getRow(ROW_NUM)).thenReturn(row);
        when(row.getCell(COL_NUM)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getCellStyle()).thenReturn(cellStyle);
        when(cellStyle.getDataFormat()).thenReturn((short) 15);
        when(cellStyle.getDataFormatString()).thenReturn("d-mmm-yy");
        when(cell.getLocalDateTimeCellValue()).thenReturn(localDateTime);
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        // When
        final LocalTime actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(time);
    }

    @Test
    final void testParseDoubleKO() {
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        final double dblValue = 44693.0;
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(ROW_NUM);
        when(cell.getColumnIndex()).thenReturn(COL_NUM);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(sheet.getRow(ROW_NUM)).thenReturn(row);
        when(row.getCell(COL_NUM)).thenReturn(cell);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getCellStyle()).thenReturn(cellStyle);
        when(cellStyle.getDataFormat()).thenReturn((short) 0);
        when(cellStyle.getDataFormatString()).thenReturn("General");
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                dblValue, LocalTime.class.getName(), ROW_NUM, COL_NUM, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testParseBooleanValueKO() {
        // Given
        final boolean boolValue = true;
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        when(cell.getBooleanCellValue()).thenReturn(boolValue);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                "true", LocalTime.class.getName(), 0, 0, SHEET_NAME);
        // When
        // Then
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final LocalTime value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        // When
        final LocalTime value = parser.getValue(null, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testReadFromExcel() throws IOException {
        // Given
        final String excelFilePath = "testData/SampleDataFile.xlsx";
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(excelFilePath);
        assertThat(inputStream).isNotNull();
        final Workbook workbook = new XSSFWorkbook(inputStream);
        final Sheet dataSheet = workbook.getSheetAt(0);
        final Cell cell = dataSheet.getRow(DATA_ROW_NUM).getCell(TIME_COL_NUM);
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        // When
        final LocalTime actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(LocalTime.of(HOURS, MINUTES, SECONDS));
    }

    @Test
    final void testReadFromExcelFormula() throws IOException {
        // Given
        final String excelFilePath = "testData/SampleDataFile.xlsx";
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(excelFilePath);
        assertThat(inputStream).isNotNull();
        final Workbook workbook = new XSSFWorkbook(inputStream);
        final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        final Sheet dataSheet = workbook.getSheetAt(0);
        final Cell cell = dataSheet.getRow(FORMULA_ROW_NUM).getCell(TIME_COL_NUM);
        final LocalTimeValueParser parser = new LocalTimeValueParser();
        // When
        final LocalTime actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(LocalTime.of(HOURS, MINUTES, SECONDS));
    }
}
