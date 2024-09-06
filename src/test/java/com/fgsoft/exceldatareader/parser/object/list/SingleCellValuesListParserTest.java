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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SingleCellValuesListParserTest {
    @Mock
    private WorksheetAnalyser worksheetAnalyser;
    @Mock
    private FormulaEvaluator formulaEvaluator;
    @Mock
    private CellRangeAddress cellRange;
    @Mock
    private CellRangeAddress headerRange;
    @Mock
    private Sheet sheet;

    @Test
    @SuppressWarnings("unchecked")
    final void testParse() {
        // Given
        final int firstRowNum = 2;
        final int firstColumnNum = 12;
        final SingleCellValuesListParser<List<String>, String> parser =
                new SingleCellValuesListParser<>(String.class, cellRange, headerRange);
        final List<String> expected = List.of("A", "B");
        when(worksheetAnalyser.getWorksheet()).thenReturn(sheet);
        when(headerRange.getFirstColumn()).thenReturn(firstColumnNum);
        when(worksheetAnalyser.getFormulaEvaluator()).thenReturn(formulaEvaluator);
        when(cellRange.getFirstRow()).thenReturn(firstRowNum);
        when(cellRange.getLastRow()).thenReturn(firstRowNum + expected.size() -1);
        for (int cnt = 0; cnt < expected.size(); cnt++) {
            final Row row = mock(Row.class);
            final Cell cell = mock(Cell.class);
            when(sheet.getRow(cnt + firstRowNum)).thenReturn(row);
            when(row.getCell(firstColumnNum)).thenReturn(cell);
            when(cell.getStringCellValue()).thenReturn(expected.get(cnt));
            when(cell.getCellType()).thenReturn(CellType.STRING);
        }
        // When
        final List<String> actual = parser.parse(worksheetAnalyser, (Class<List<String>>) expected.getClass(), false);
        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
