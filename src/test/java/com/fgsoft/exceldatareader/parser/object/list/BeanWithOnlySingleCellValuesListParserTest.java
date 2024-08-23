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
package com.fgsoft.exceldatareader.parser.object.list;

import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import com.fgsoft.exceldatareader.util.DataBuilder;
import com.fgsoft.exceldatareader.util.samples.SampleSingleCellFieldsOnly;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeanWithOnlySingleCellValuesListParserTest {
    private final DataBuilder dataBuilder = new DataBuilder();

    @Mock
    private WorksheetAnalyser worksheetAnalyser;
    @Mock
    private FormulaEvaluator formulaEvaluator;
    @Mock
    private CellRangeAddress cellRange;
    @Mock
    private CellRangeAddress headerRange;

    @Test
    @SuppressWarnings("unchecked")
    final void testParse() {
        // Given
        final Field[] allFields = SampleSingleCellFieldsOnly.class.getDeclaredFields();
        final int firstRowNum = 2;
        final int firstColumnNum = 12;
        final int lastColumnNum = firstColumnNum + allFields.length;
        final BeanWithOnlySingleCellValuesListParser<List<SampleSingleCellFieldsOnly>, SampleSingleCellFieldsOnly> parser =
                new BeanWithOnlySingleCellValuesListParser<>(SampleSingleCellFieldsOnly.class, cellRange, headerRange);
        final SampleSingleCellFieldsOnly itemOne = dataBuilder.buildSampleInstancePrimaryOnly();
        final SampleSingleCellFieldsOnly itemTwo = dataBuilder.buildSampleInstancePrimaryOnly();
        final List<SampleSingleCellFieldsOnly> expected = List.of(itemOne, itemTwo);
        when(worksheetAnalyser.getFormulaEvaluator()).thenReturn(formulaEvaluator);
        when(cellRange.getFirstRow()).thenReturn(firstRowNum);
        when(cellRange.getLastRow()).thenReturn(firstRowNum + expected.size() - 1);
        when(cellRange.getFirstColumn()).thenReturn(firstColumnNum);
        when(cellRange.getLastColumn()).thenReturn(lastColumnNum);
        for (int cnt = 0; cnt < expected.size(); cnt++) {
            final int rowNum = firstRowNum + cnt;
            final int index = cnt;
            Arrays.stream(allFields).forEach(field -> {
                field.setAccessible(true);
                final Cell fieldCell = mock(Cell.class);
                try {
                    if (field.get(expected.get(index)) == null) {
                        when(fieldCell.getCellType()).thenReturn(CellType.BLANK);
                    } else {
                        when(fieldCell.getCellType()).thenReturn(CellType.STRING);
                        when(fieldCell.getStringCellValue()).thenReturn(String.valueOf(field.get(expected.get(index))));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                final CellRangeAddress itemRange = new CellRangeAddress(rowNum, rowNum, firstColumnNum, lastColumnNum);
                when(worksheetAnalyser.getCell(field.getName(), itemRange, headerRange)).thenReturn(fieldCell);
            });
        }
        // When
        final List<SampleSingleCellFieldsOnly> actual = parser.parse(worksheetAnalyser,
                (Class<List<SampleSingleCellFieldsOnly>>) expected.getClass());
        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
