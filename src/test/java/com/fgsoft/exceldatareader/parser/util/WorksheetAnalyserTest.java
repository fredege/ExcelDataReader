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

import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import com.fgsoft.exceldatareader.parser.HeaderDescriptor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import static com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode.HEADER_NOT_FOUND;
import static com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode.TEST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class WorksheetAnalyserTest {
    private final List<String> PrimaryOnlyValues = Arrays.asList("testName", "bigDecimal", "booleanValue", "date",
            "doubleValue", "intValue", "localDateTime", "localDate", "localTime", "longValue", "stringValue", "sample");
    private final List<String> CompositeValues = Arrays.asList("testName", "bigDecimal", "booleanValue", "date",
            "doubleValue", "intValue", "localDateTime", "localDate", "localTime", "longValue", "stringValue", "sample",
            "listOfStrings", "composite.bigDecimal", "composite.booleanValue", "composite.date", "composite.doubleValue",
            "composite.intValue", "composite.localDateTime", "composite.localDate", "composite.localTime",
            "composite.longValue", "composite.stringValue", "composite.sample",
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

    @ParameterizedTest
    @CsvSource({
            "TEST-01, B3:AI5",
            "TEST-02, B7:AI9",
    })
    final void findTestRange(final String testName, final String expectedRange) throws URISyntaxException, IOException {
        // Given
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final CellRangeAddress expected = CellRangeAddress.valueOf(expectedRange);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final CellRangeAddress actual = analyser.findTestDataRange(testName);
            // Then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "composite, N3:X3",
            "listOfStrings, M3:M4",
            "listOfComposites, Y3:AI5"
    })
    final void getCellRange(final String fieldName, final String expectedRange) throws URISyntaxException, IOException {
        // Given
        final CellRangeAddress headerRange = CellRangeAddress.valueOf("B1:AI2");
        final CellRangeAddress testDataRange = CellRangeAddress.valueOf("B3:AI6");
        final CellRangeAddress expected = CellRangeAddress.valueOf(expectedRange);
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final CellRangeAddress actual = analyser.getCellRange(fieldName, testDataRange, headerRange);
            // Then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    final void getCellRangeNotFound() throws URISyntaxException {
        // Given
        final CellRangeAddress headerRange = CellRangeAddress.valueOf("B1:AI2");
        final CellRangeAddress testDataRange = CellRangeAddress.valueOf("B3:AI5");
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final ExcelReaderException exception = assertThrows(ExcelReaderException.class,
                    ()-> analyser.getCellRange("NotFound", testDataRange, headerRange));
            // Then
            assertThat(exception.getMessage()).isEqualTo(String.format(HEADER_NOT_FOUND.getMessage(), "NotFound"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "composite, N2:X2",
            "listOfStrings, M1:M1",
            "listOfComposites, Y2:AI2"
    })
    final void getHeaderRange(final String fieldName, final String expectedRange) throws URISyntaxException, IOException {
        final CellRangeAddress headerRange = CellRangeAddress.valueOf("B1:AI2");
        final CellRangeAddress expected = CellRangeAddress.valueOf(expectedRange);
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final CellRangeAddress actual = analyser.getHeaderRange(fieldName, headerRange);
            // Then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    final void findTestRangeNotFound() throws URISyntaxException, IOException {
        // Given
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final ExcelReaderException exception = assertThrows(ExcelReaderException.class,
                    () -> analyser.findTestDataRange("NotFound"));
            // Then
            assertThat(exception.getMessage()).isEqualTo(String.format(TEST_NOT_FOUND.getMessage(), "NotFound"));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "bigDecimal, 2, 1, B3:AI5, B1:AI2",
            "booleanValue, 2, 2, B3:AI5, B1:AI2",
            "date, 2, 3, B3:AI5, B1:AI2",
            "doubleValue, 2, 4, B3:AI5, B1:AI2",
            "intValue, 2, 5, B3:AI5, B1:AI2",
            "localDateTime, 2, 6, B3:AI5, B1:AI2",
            "localDate, 2, 7, B3:AI5, B1:AI2",
            "localTime, 2, 8, B3:AI5, B1:AI2",
            "longValue, 2, 9, B3:AI5, B1:AI2",
            "stringValue, 2, 10, B3:AI5, B1:AI2",
            "sample, 2, 11, B3:AI5, B1:AI2",
            "bigDecimal, 2, 13, N3:X3, N2:X2",
            "booleanValue, 2, 14, N3:X3, N2:X2",
            "date, 2, 15, N3:X3, N2:X2",
            "doubleValue, 2, 16, N3:X3, N2:X2",
            "intValue, 2, 17, N3:X3, N2:X2",
            "localDateTime, 2, 18, N3:X3, N2:X2",
            "localDate, 2, 19, N3:X3, N2:X2",
            "localTime, 2, 20, N3:X3, N2:X2",
            "longValue, 2, 21, N3:X3, N2:X2",
            "stringValue, 2, 22, N3:X3, N2:X2",
            "sample, 2, 23, N3:X3, N2:X2",
    })
    final void getCell(final String name, final int rowNum, final int colNum,
                       final String cellRegion, final String headerRegion) throws URISyntaxException, IOException {
        // Given
        final CellRangeAddress cellRange = CellRangeAddress.valueOf(cellRegion);
        final CellRangeAddress headerRange = CellRangeAddress.valueOf(headerRegion);
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final Cell actual = analyser.getCell(name, cellRange, headerRange);
            // Then
            assertThat(actual).isNotNull();
            assertThat(actual.getRowIndex()).isEqualTo(rowNum);
            assertThat(actual.getColumnIndex()).isEqualTo(colNum);
        }
    }

    @Test
    final void getCellHeaderNotFound() throws URISyntaxException, IOException {
        // Given
        final CellRangeAddress cellRange = CellRangeAddress.valueOf("B3:AI5");
        final CellRangeAddress headerRange = CellRangeAddress.valueOf("B1:AI2");
        final HeaderDescriptor headerDescriptor = new HeaderDescriptor(0, 1, 1);
        final URL url = this.getClass().getResource("/testData/SampleDataFile.xlsx");
        assertThat(url).isNotNull();
        try (FileInputStream file = new FileInputStream(new File(url.toURI()))) {
            final XSSFWorkbook workbook = new XSSFWorkbook(file);
            final XSSFSheet sheet = workbook.getSheet("SampleInstanceComposite");
            final WorksheetAnalyser analyser = new WorksheetAnalyser(sheet, headerDescriptor);
            // When
            final ExcelReaderException exception = assertThrows(ExcelReaderException.class,
                    () -> analyser.getCell("NotFound", cellRange, headerRange));
            // Then
            assertThat(exception.getMessage()).isEqualTo(String.format(HEADER_NOT_FOUND.getMessage(), "NotFound"));
        }
    }

    private Map<String, Integer> buildExpectedHeadersMapForPrimaryOnly() {
        return PrimaryOnlyValues.stream()
                .filter(a -> !"testName".equals(a))
                .collect(Collectors.toMap(Function.identity(), PrimaryOnlyValues::indexOf));
    }

    private Map<String, Integer> buildExpectedHeadersMapForComposite() {
        return CompositeValues.stream()
                .filter(a -> !"testName".equals(a))
                .collect(Collectors.toMap(Function.identity(), CompositeValues::indexOf));
    }
}
