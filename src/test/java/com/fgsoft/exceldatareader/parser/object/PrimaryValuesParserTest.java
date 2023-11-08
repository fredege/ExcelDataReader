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
package com.fgsoft.exceldatareader.parser.object;

import com.fgsoft.exceldatareader.util.SampleInstancePrimaryOnly;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PrimaryValuesParserTest {
    private static final String BASE_HEADER = "base";
    private static final List<String> HEADERS = Arrays.asList(
            BASE_HEADER + ".bigDecimal", BASE_HEADER + ".booleanValue", BASE_HEADER +  ".date",
            BASE_HEADER + ".doubleValue", BASE_HEADER + ".sample", BASE_HEADER + ".intValue",
            BASE_HEADER + ".localDateTime", BASE_HEADER + ".localDate", BASE_HEADER + ".localTime",
            BASE_HEADER + ".longValue", BASE_HEADER + ".stringValue"
            );
    @Mock
    private Sheet worksheet;
    @Mock
    private Row row;

    private final PrimaryValuesParser parser = new PrimaryValuesParser(worksheet);

    @Test
    final void parseInstanceWithOnlyPrimaryFields() {
        // Given
        final SampleInstancePrimaryOnly instance = new SampleInstancePrimaryOnly();
        final SampleInstancePrimaryOnly expected = buildExpectedSampleInstance();
        // When
        parser.parse(instance, HEADERS, BASE_HEADER, row);
        // Then
        assertThat(instance).isEqualTo(expected);
    }

    private SampleInstancePrimaryOnly buildExpectedSampleInstance() {
        return SampleInstancePrimaryOnly.builder()
                .build();
    }

}
