/* Copyright 2023 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.util;

import com.fgsoft.exceldatareader.parser.HeaderDescriptor;
import com.fgsoft.exceldatareader.util.SampleInstancePrimaryOnly;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorksheetAnalyserTest {
    private final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 0, 0);
    @Mock
    private Sheet worksheet;

    private WorksheetAnalyser worksheetAnalyser;
    private final List<String> rowZeroStringValues = Arrays.asList("testName", "bigDecimal", "booleanValue", "date",
            "doubleValue", "intValue", "localDateTime", "localDate", "localTime", "longValue", "stringValue", "sample");

    @BeforeEach
    final void initialize() {
        worksheetAnalyser = new WorksheetAnalyser(worksheet, headerDescriptor);
        final Row rowZero = mock(Row.class);
        final Iterator<Cell> rowZeroIterator = mockRowZero();
        when(rowZero.iterator()).thenReturn(rowZeroIterator);
        when(worksheet.getRow(0)).thenReturn(rowZero);
    }

    @Test
    final void getHeadersMap() {
        // Given
        final Map<String, Integer> expected = buildExpectedHeadersMap();
        // When
        final Map<String, Integer> actual = worksheetAnalyser.getHeadersMap(SampleInstancePrimaryOnly.class);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    private Map<String, Integer> buildExpectedHeadersMap() {
        return rowZeroStringValues.stream()
                .filter((a) -> !"testName".equals(a))
                .collect(Collectors.toMap(Function.identity(), rowZeroStringValues::indexOf));
    }

    private Iterator<Cell> mockRowZero() {
        @SuppressWarnings("unchecked")
        final Iterator<Cell> rowOneIterator = mock(Iterator.class);
        final List<Boolean> hasNextValues = Arrays.asList(true, true, true, true, true, true, true, true, true, true,
                true, true, false);
        doAnswer(AdditionalAnswers.returnsElementsOf(hasNextValues)).when(rowOneIterator).hasNext();
        final List<Cell> allCells = new ArrayList<>(rowZeroStringValues.size());
        for (int cnt = 0; cnt  < rowZeroStringValues.size(); cnt++) {
            final Cell cell = mock(Cell.class);
            when(cell.getStringCellValue()).thenReturn(rowZeroStringValues.get(cnt));
            when(cell.getColumnIndex()).thenReturn(cnt);
            allCells.add(cell);
        }
        doAnswer(AdditionalAnswers.returnsElementsOf(allCells)).when(rowOneIterator).next();
        return rowOneIterator;
    }
}
