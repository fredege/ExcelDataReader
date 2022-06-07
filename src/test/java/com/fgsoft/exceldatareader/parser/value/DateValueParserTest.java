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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.fgsoft.exceldatareader.util.TestConstants.SHEET_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DateValueParserTest {
    private static final int ROW_NUM = 0;
    private static final int COL_NUM = 0;
    private static final int YEAR = 2022;
    private static final int MONTH = 4;
    private static final int DAY = 16;
    private static final int HOURS = 11;
    private static final int MINUTES = 2;
    private static final int SECONDS = 25;
    private static final int NANOS = 320000000;
    private static final Date DATE = initializeDate();

    private static Date initializeDate() {
        final LocalDateTime localDateTime = LocalDateTime.of(YEAR, MONTH, DAY, HOURS, MINUTES, SECONDS, NANOS);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


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
        final DateValueParser parser = new DateValueParser();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        final Date date = new Date();
        final String strValue = sdf.format(date);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getSheet()).thenReturn(sheet);
        // When
        final Date actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(date);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sample string", "15/05/2022",
            "2022-05-15T17:05:65", "2022-05-15T17:65:15", "2022-05-15T32:05:15",
            "2022-05-32T17:05:15", "2022-13-15T17:05:15"})
    final void testParseStringKOAnyString(final String strValue) {
        final DateValueParser parser = new DateValueParser();
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(ROW_NUM);
        when(cell.getColumnIndex()).thenReturn(COL_NUM);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                strValue, Date.class.getName(), ROW_NUM, COL_NUM, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception).isInstanceOf(IncorrectValueForTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testParseDoubleOK() {
        final DateValueParser parser = new DateValueParser();
        final Date date = new Date();
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
        when(cell.getDateCellValue()).thenReturn(date);
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        // When
        final Date actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(date);
    }

    @Test
    final void testParseDoubleKO() {
        final DateValueParser parser = new DateValueParser();
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
                dblValue, Date.class.getName(), ROW_NUM, COL_NUM, SHEET_NAME);
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
        final DateValueParser parser = new DateValueParser();
        when(cell.getBooleanCellValue()).thenReturn(boolValue);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(0);
        when(cell.getColumnIndex()).thenReturn(0);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                "true", Date.class.getName(), 0, 0, SHEET_NAME);
        // When
        // Then
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final DateValueParser parser = new DateValueParser();
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final Date value = parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final DateValueParser parser = new DateValueParser();
        // When
        final Date value = parser.getValue(null, evaluator);
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
        final Cell cell = dataSheet.getRow(2).getCell(2);
        final DateValueParser parser = new DateValueParser();
        // When
        final Date actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(DATE);
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
        final Cell cell = dataSheet.getRow(3).getCell(2);
        final DateValueParser parser = new DateValueParser();
        // When
        final Date actual = parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(DATE);
    }
}
