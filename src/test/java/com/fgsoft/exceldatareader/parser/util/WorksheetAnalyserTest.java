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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WorksheetAnalyserTest {
    private final List<String> PrimaryOnlyValues = Arrays.asList("testName","bigDecimal", "booleanValue", "date",
            "doubleValue", "intValue", "localDateTime", "localDate", "localTime", "longValue", "stringValue", "sample");
    private final List<String> CompositeValues = Arrays.asList("testName","bigDecimal", "booleanValue", "date",
            "doubleValue", "intValue", "localDateTime", "localDate", "localTime", "longValue", "stringValue", "sample",
            "composite.bigDecimal", "composite.booleanValue", "composite.date", "composite.doubleValue",
            "composite.intValue", "composite.localDateTime", "composite.localDate", "composite.localTime",
            "composite.longValue", "composite.stringValue", "composite.sample", "listOfStrings",
            "listOfComposites.bigDecimal", "listOfComposites.booleanValue", "listOfComposites.date",
            "listOfComposites.doubleValue", "listOfComposites.intValue", "listOfComposites.localDateTime",
            "listOfComposites.localDate", "listOfComposites.localTime", "listOfComposites.longValue",
            "listOfComposites.stringValue", "listOfComposites.sample");

    @Test
    final void getHeadersMapForSimpleClass() throws IOException, URISyntaxException {
        // Given
        final Map<String, Integer> expected = buildExpectedHeadersMapForPrimaryOnly();
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstancePrimaryOnly");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final Map<String, Integer> actual = analyser.getHeadersMap();
            // Then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    final void getHeadersMapForCompositeClass() throws URISyntaxException, IOException {
        // Given
        final Map<String, Integer> expected = buildExpectedHeadersMapForComposite();
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final Map<String, Integer> actual = analyser.getHeadersMap();
            // Then
            assertThat(actual).hasSameSizeAs(expected);
            for (Map.Entry<String, Integer> entry : actual.entrySet()) {
                assertThat(entry.getValue()).isEqualTo(expected.get(entry.getKey()));
            }
        }
    }


    private Map<String, Integer> buildExpectedHeadersMapForPrimaryOnly() {
        return PrimaryOnlyValues.stream()
                .filter((a) -> !"testName".equals(a))
                .collect(Collectors.toMap(Function.identity(), PrimaryOnlyValues::indexOf));
    }

    private Map<String, Integer> buildExpectedHeadersMapForComposite() {
        return CompositeValues.stream()
                .filter((a) -> !"testName".equals(a))
                .collect(Collectors.toMap(Function.identity(), CompositeValues::indexOf));
    }
}
