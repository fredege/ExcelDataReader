/* Copyright 2024 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.object.object;

import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import com.fgsoft.exceldatareader.reader.ExcelDataReader;
import com.fgsoft.exceldatareader.util.DataBuilder;
import com.fgsoft.exceldatareader.util.samples.SampleNonSingleCellFieldsOnly;
import com.fgsoft.exceldatareader.util.samples.SampleSingleCellFieldsOnly;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjectTestDataParserTest {
    private final DataBuilder dataBuilder = new DataBuilder();

    @Mock
    private ExcelDataReader reader;
    @Mock
    private WorksheetAnalyser worksheetAnalyser;
    @Mock
    private FormulaEvaluator formulaEvaluator;
    @Mock
    private CellRangeAddress cellRange;
    @Mock
    private CellRangeAddress headerRange;

    @BeforeEach
    final void setUp() {
        lenient().when(worksheetAnalyser.getFormulaEvaluator()).thenReturn(formulaEvaluator);
    }

    @Test
    void testParsePrimaryOnly() {
        // Given
        final ObjectTestDataParser<SampleSingleCellFieldsOnly> parser =
                new ObjectTestDataParser<>(SampleSingleCellFieldsOnly.class, cellRange, headerRange);
        final SampleSingleCellFieldsOnly expected = dataBuilder.buildSampleInstancePrimaryOnly();
        final Field[] allFields = SampleSingleCellFieldsOnly.class.getDeclaredFields();
        Arrays.stream(allFields).forEach(field -> {
            field.setAccessible(true);
            final Cell fieldCell = mock(Cell.class);
            try {
                if (field.get(expected) == null) {
                    when(fieldCell.getCellType()).thenReturn(CellType.BLANK);
                } else {
                    when(fieldCell.getCellType()).thenReturn(CellType.STRING);
                    when(fieldCell.getStringCellValue()).thenReturn(String.valueOf(field.get(expected)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            when(worksheetAnalyser.getCell(field.getName(), cellRange, headerRange)).thenReturn(fieldCell);
        });
        // When
        final SampleSingleCellFieldsOnly actual = parser.parse(reader, worksheetAnalyser, SampleSingleCellFieldsOnly.class, false);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testParsePrimaryOnlyWithIgnoredFields() {
        // Given
        final ObjectTestDataParser<SampleSingleCellFieldsOnly> parser =
                new ObjectTestDataParser<>(SampleSingleCellFieldsOnly.class, cellRange, headerRange);
        final SampleSingleCellFieldsOnly expected = dataBuilder.buildSampleInstancePrimaryOnly();
        expected.setBigDecimal(null);
        final SampleSingleCellFieldsOnly fromWorkbook = dataBuilder.buildSampleInstancePrimaryOnly();
        final Field[] allFields = SampleSingleCellFieldsOnly.class.getDeclaredFields();
        Arrays.stream(allFields).forEach(field -> {
            field.setAccessible(true);
            final Cell fieldCell = mock(Cell.class);
            try {
                if (field.get(fromWorkbook) == null) {
                    when(fieldCell.getCellType()).thenReturn(CellType.BLANK);
                } else {
                    lenient().when(fieldCell.getCellType()).thenReturn(CellType.STRING);
                    lenient().when(fieldCell.getStringCellValue()).thenReturn(String.valueOf(field.get(fromWorkbook)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            lenient().when(worksheetAnalyser.getCell(field.getName(), cellRange, headerRange)).thenReturn(fieldCell);
        });
        // When
        final SampleSingleCellFieldsOnly actual = parser.parse(reader, worksheetAnalyser, SampleSingleCellFieldsOnly.class, false, "bigDecimal");
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    final void testNonSingleCellFieldsOnly() {
        // Given
        final ObjectTestDataParser<SampleNonSingleCellFieldsOnly> parser =
                new ObjectTestDataParser<>(SampleNonSingleCellFieldsOnly.class, cellRange, headerRange);
        final SampleNonSingleCellFieldsOnly expected = dataBuilder.buidSampleNonSingleCellFieldsOnly();
        final SampleSingleCellFieldsOnly value = expected.getFieldOne();
        final Field[] allFields = SampleSingleCellFieldsOnly.class.getDeclaredFields();
        Arrays.stream(allFields).forEach(field -> {
            field.setAccessible(true);
            final Cell fieldCell = mock(Cell.class);
            try {
                if (field.get(value) == null) {
                    when(fieldCell.getCellType()).thenReturn(CellType.BLANK);
                } else {
                    when(fieldCell.getCellType()).thenReturn(CellType.STRING);
                    when(fieldCell.getStringCellValue()).thenReturn(String.valueOf(field.get(value)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            when(worksheetAnalyser.getCell(field.getName(), cellRange, headerRange)).thenReturn(fieldCell);
        });
        when(worksheetAnalyser.getCellRange("fieldOne", cellRange, headerRange)).thenReturn(cellRange);
        when(worksheetAnalyser.getHeaderRange("fieldOne", headerRange)).thenReturn(headerRange);
        when(worksheetAnalyser.getCellRange("fieldTwo", cellRange, headerRange)).thenReturn(cellRange);
        when(worksheetAnalyser.getHeaderRange("fieldTwo", headerRange)).thenReturn(headerRange);
        // When
        final SampleNonSingleCellFieldsOnly actual = parser.parse(reader, worksheetAnalyser, SampleNonSingleCellFieldsOnly.class, false);
        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    final void testNonSingleCellFieldsOnlyWithIgnoredFields() {
        // Given
        final ObjectTestDataParser<SampleNonSingleCellFieldsOnly> parser =
                new ObjectTestDataParser<>(SampleNonSingleCellFieldsOnly.class, cellRange, headerRange);
        final SampleNonSingleCellFieldsOnly expected = dataBuilder.buidSampleNonSingleCellFieldsOnly();
        expected.setFieldTwo(null);
        final SampleSingleCellFieldsOnly value = expected.getFieldOne();
        final Field[] allFields = SampleSingleCellFieldsOnly.class.getDeclaredFields();
        Arrays.stream(allFields).forEach(field -> {
            field.setAccessible(true);
            final Cell fieldCell = mock(Cell.class);
            try {
                if (field.get(value) == null) {
                    when(fieldCell.getCellType()).thenReturn(CellType.BLANK);
                } else {
                    when(fieldCell.getCellType()).thenReturn(CellType.STRING);
                    when(fieldCell.getStringCellValue()).thenReturn(String.valueOf(field.get(value)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            when(worksheetAnalyser.getCell(field.getName(), cellRange, headerRange)).thenReturn(fieldCell);
        });
        when(worksheetAnalyser.getCellRange("fieldOne", cellRange, headerRange)).thenReturn(cellRange);
        when(worksheetAnalyser.getHeaderRange("fieldOne", headerRange)).thenReturn(headerRange);
        // When
        final SampleNonSingleCellFieldsOnly actual = parser.parse(reader, worksheetAnalyser, SampleNonSingleCellFieldsOnly.class, false, "fieldTwo");
        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    final void testObjectWithReference() {
        // Given
        // When
        // Then
    }
}
