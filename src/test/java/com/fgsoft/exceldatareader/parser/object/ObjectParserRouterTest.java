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

import com.fgsoft.exceldatareader.parser.object.list.BeanWithOnlySingleCellValuesListParser;
import com.fgsoft.exceldatareader.parser.object.list.ListTestDataParser;
import com.fgsoft.exceldatareader.parser.object.list.SingleCellValuesListParser;
import com.fgsoft.exceldatareader.parser.object.object.ObjectTestDataParser;
import com.fgsoft.exceldatareader.util.samples.SampleCompositeClass;
import com.fgsoft.exceldatareader.util.samples.SampleNotFullySupported;
import com.fgsoft.exceldatareader.util.samples.SampleSingleCellFieldsOnly;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ObjectParserRouterTest {
    @Mock
    private CellRangeAddress cellRange;
    @Mock
    private CellRangeAddress headerRange;

    private final Class<?> type = SampleCompositeClass.class;

    @Test
    final void findParser() throws NoSuchFieldException {
        // Given
        final Field field = type.getDeclaredField("composite");
        // When
        final TestDataParser<?> parser = ObjectParserRouter.findParser(field, cellRange, headerRange);
        // Then
        assertThat(parser).isInstanceOf(ObjectTestDataParser.class);
        final ObjectTestDataParser<?> testParser = (ObjectTestDataParser<?>) parser;
        assertThat(testParser.getType()).isSameAs(SampleSingleCellFieldsOnly.class);
    }

    @Test
    final void findParserForListOfSingleCellValues() throws NoSuchFieldException {
        // Given
        final Field field = type.getDeclaredField("listOfStrings");
        // When
        final TestDataParser<?> parser = ObjectParserRouter.findParser(field, cellRange, headerRange);
        // Then
        assertThat(parser).isInstanceOf(SingleCellValuesListParser.class);
        final SingleCellValuesListParser<?, ?> testParser = (SingleCellValuesListParser<?, ?>) parser;
        assertThat(testParser).isNotNull();
        assertThat(testParser.getItemType()).isSameAs(String.class);
    }

    @Test
    final void findParserForBeanWithOnlySingleCellValuesList() throws NoSuchFieldException {
        // Given
        final Field field = type.getDeclaredField("listOfComposites");
        // When
        final TestDataParser<?> parser = ObjectParserRouter.findParser(field, cellRange, headerRange);
        // Then
        assertThat(parser).isInstanceOf(BeanWithOnlySingleCellValuesListParser.class);
        final BeanWithOnlySingleCellValuesListParser<?, ?> testParser = (BeanWithOnlySingleCellValuesListParser<?, ?>) parser;
        assertThat(testParser).isNotNull();
        assertThat(testParser.getItemType()).isSameAs(SampleSingleCellFieldsOnly.class);
    }


    @Test
    final void findParserForList() throws NoSuchFieldException {
        // Given
        final Class<?> type2 = SampleNotFullySupported.class;
        final Field field = type2.getDeclaredField("list");
        // When
        final TestDataParser<?> parser = ObjectParserRouter.findParser(field, cellRange, headerRange);
        // Then
        assertThat(parser).isInstanceOf(ListTestDataParser.class);
        final ListTestDataParser<?, ?> testParser = (ListTestDataParser<?, ?>) parser;
        assertThat(testParser).isNotNull();
        assertThat(testParser.getItemType()).isSameAs(SampleCompositeClass.class);
    }

}
