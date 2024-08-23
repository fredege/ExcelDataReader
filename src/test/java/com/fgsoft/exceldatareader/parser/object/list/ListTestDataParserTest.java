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
import com.fgsoft.exceldatareader.util.samples.SampleCompositeClass;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ListTestDataParserTest {
    @Mock
    private WorksheetAnalyser worksheetAnalyser;
    @Mock
    private CellRangeAddress cellRange;
    @Mock
    private CellRangeAddress headerRange;

    @Test
    final void testParse() {
        // Given
        final ListTestDataParser<List<SampleCompositeClass>, SampleCompositeClass> parser =
                new ListTestDataParser<>(SampleCompositeClass.class, cellRange, headerRange);
        final Class<List<SampleCompositeClass>> clazz = parser.getType();
        // When // Then
        assertThrows(UnsupportedOperationException.class, () -> parser.parse(worksheetAnalyser, clazz));
    }
}
