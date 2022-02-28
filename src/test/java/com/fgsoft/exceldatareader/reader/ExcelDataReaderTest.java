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
package com.fgsoft.exceldatareader.reader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelDataReaderTest {
    private static final String SAMPLE_FILE_PATH = "/testData/SampleDataFile.xlsx";

    @Test
    final void testCreationOK() {
        // Given
        // When
        final ExcelDataReader reader = new ExcelDataReader(SAMPLE_FILE_PATH);
        // Then
        assertThat(reader).isNotNull();
    }
}
