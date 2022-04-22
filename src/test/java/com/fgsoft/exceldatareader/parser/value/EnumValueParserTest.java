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
import com.fgsoft.exceldatareader.util.Sample;
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

import static com.fgsoft.exceldatareader.util.TestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnumValueParserTest {
    private static final int ENUM_COL = 5;
    private static final int ROW_NUM = 0;
    private static final int COL_NUM = 0;

    @Mock
    private Cell cell;
    @Mock
    private Sheet sheet;
    @Mock
    private Row row;
    @Mock
    private FormulaEvaluator evaluator;

    @ParameterizedTest
    @ValueSource(strings = {"ONE", "TWO", "THREE"})
    final void testParseStringValueOK(String strValue) {
        // Given
        final Sample sample = Sample.valueOf(strValue);
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        // When
        final Sample value = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(sample);
    }

    @Test
    final void testParseStringValueKO() {
        // Given
        final String strValue = "UNKNOWN";
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(ROW_NUM);
        when(cell.getColumnIndex()).thenReturn(COL_NUM);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                strValue, Sample.class.getName(), ROW_NUM, COL_NUM, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testParseNullValueReturnedByFormula() {
        // Given
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        final CellValue cellValue = new CellValue(0.0);
        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(evaluator.evaluate(cell)).thenReturn(cellValue);
        // When
        final Sample actual = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isNull();
    }

    @Test
    final void testBlankValueOK() {
        // Given
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        when(cell.getCellType()).thenReturn(CellType.BLANK);
        // When
        final Sample value = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testNullValueOK() {
        // Given
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        cell = null;
        // When
        final Sample value = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isNull();
    }

    @Test
    final void testParseNumericValue(){
        // Given
        final double dblValue = 1.0;
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(dblValue);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(ROW_NUM);
        when(cell.getColumnIndex()).thenReturn(COL_NUM);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                dblValue, Sample.class.getName(), ROW_NUM, COL_NUM, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testParseBooleanValue(){
        // Given
        final boolean booleanValue = true;
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(cell.getBooleanCellValue()).thenReturn(booleanValue);
        when(sheet.getSheetName()).thenReturn(SHEET_NAME);
        when(cell.getSheet()).thenReturn(sheet);
        when(cell.getRowIndex()).thenReturn(ROW_NUM);
        when(cell.getColumnIndex()).thenReturn(COL_NUM);
        final String message = String.format(ExcelReaderErrorCode.INCORRECT_VALUE_FOR_TYPE.getMessage(),
                booleanValue, Sample.class.getName(), ROW_NUM, COL_NUM, SHEET_NAME);
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> parser.getValue(cell, evaluator));
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    final void testReadFromExcel() throws IOException {
        // Given
        final String excelFilePath = "testData/SampleDataFile.xlsx";
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(excelFilePath);
        assertThat(inputStream).isNotNull();
        final Workbook workbook = new XSSFWorkbook(inputStream);
        final Sheet dataSheet = workbook.getSheetAt(0);
        final Cell cell = dataSheet.getRow(DATA_ROW_NUM).getCell(ENUM_COL);
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        // When
        final Sample actual = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(Sample.ONE);
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
        final Cell cell = dataSheet.getRow(FORMULA_ROW_NUM).getCell(ENUM_COL);
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        // When
        final Sample actual = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isEqualTo(Sample.ONE);
    }

    @Test
    final void testReadFromExcelNullValue() throws IOException {
        // Given
        final String excelFilePath = "testData/SampleDataFile.xlsx";
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(excelFilePath);
        assertThat(inputStream).isNotNull();
        final Workbook workbook = new XSSFWorkbook(inputStream);
        final Sheet dataSheet = workbook.getSheetAt(0);
        final Cell cell = dataSheet.getRow(NULL_DATA_ROW_NUM).getCell(ENUM_COL);
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        // When
        final Sample actual = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isNull();
    }

    @Test
    final void testReadFromExcelFormulaNullValue() throws IOException {
        // Given
        final String excelFilePath = "testData/SampleDataFile.xlsx";
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(excelFilePath);
        assertThat(inputStream).isNotNull();
        final Workbook workbook = new XSSFWorkbook(inputStream);
        final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        final Sheet dataSheet = workbook.getSheetAt(0);
        final Cell cell = dataSheet.getRow(NULL_FORMULA_ROW_NUM).getCell(ENUM_COL);
        final EnumValueParser parser = new EnumValueParser(Sample.class);
        // When
        final Sample actual = (Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(actual).isNull();
    }

}
