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

import com.fgsoft.exceldatareader.util.TestConstants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnumValueParserTest {
    @Mock
    private Cell cell;
    @Mock
    private FormulaEvaluator evaluator;

    @ParameterizedTest
    @ValueSource(strings = {"ONE", "TWO", "THREE"})
    final void testParseStringValueOK(String strValue) {
        // Given
        final TestConstants.Sample sample = TestConstants.Sample.valueOf(strValue);
        final EnumValueParser parser = new EnumValueParser(TestConstants.Sample.class);
        when(cell.getStringCellValue()).thenReturn(strValue);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        // When
        final TestConstants.Sample value = (TestConstants.Sample) parser.getValue(cell, evaluator);
        // Then
        assertThat(value).isEqualTo(sample);
    }

}
