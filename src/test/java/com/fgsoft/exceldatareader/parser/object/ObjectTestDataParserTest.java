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
package com.fgsoft.exceldatareader.parser.object;

import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import com.fgsoft.exceldatareader.util.DataBuilder;
import com.fgsoft.exceldatareader.util.SampleInstancePrimaryOnly;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjectTestDataParserTest {
    private final DataBuilder dataBuilder = new DataBuilder();

    @Mock
    private WorksheetAnalyser worksheetAnalyser;
    @Mock
    private FormulaEvaluator formulaEvaluator;
    @Mock
    private CellRange<Cell> cellRange;
    @Mock
    private CellRange<Cell> headerRange;

    @BeforeEach
    final void setUp() {
        when(worksheetAnalyser.getFormulaEvaluator()).thenReturn(formulaEvaluator);
    }

    @Test
    void testParsePrimaryOnly() {
        // Given
        final ObjectTestDataParser<SampleInstancePrimaryOnly> parser = new ObjectTestDataParser<>(cellRange, headerRange);
        final SampleInstancePrimaryOnly expected = dataBuilder.buildSampleInstancePrimaryOnly();
        final Field[] allFields = SampleInstancePrimaryOnly.class.getDeclaredFields();
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
        final SampleInstancePrimaryOnly actual = parser.parse(worksheetAnalyser, SampleInstancePrimaryOnly.class);
        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
