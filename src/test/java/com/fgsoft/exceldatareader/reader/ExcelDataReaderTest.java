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

import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import com.fgsoft.exceldatareader.util.samples.SampleCompositeClass;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExcelDataReaderTest {
    private static final String SAMPLE_FILE_PATH = "/testData/SampleDataFile.xlsx";

    @Test
    @SuppressWarnings("unchecked")
    final void testCreationOK() throws IllegalAccessException {
        // Given
        final Field workookField = ReflectionUtils
                .findFields(ExcelDataReader.class, f -> f.getName().equals("workbook"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        workookField.setAccessible(true);
        final Field evaluatorField = ReflectionUtils
                .findFields(ExcelDataReader.class, f -> f.getName().equals("formulaEvaluator"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        evaluatorField.setAccessible(true);
        final Field sheetNamesField = ReflectionUtils
                .findFields(ExcelDataReader.class, f -> f.getName().equals("sheetNames"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        sheetNamesField.setAccessible(true);
        // When
        final ExcelDataReader reader = new ExcelDataReader(SAMPLE_FILE_PATH);
        // Then
        assertThat(reader).isNotNull();
        final Workbook workbook = (Workbook) workookField.get(reader);
        assertThat(workbook).isNotNull();
        final FormulaEvaluator evaluator = (FormulaEvaluator) evaluatorField.get(reader);
        assertThat(evaluator).isNotNull();
        final Map<Class<?>, String> sheetNames = (Map<Class<?>, String>) sheetNamesField.get(reader);
        assertThat(sheetNames).isNotNull().isEmpty();
    }

    @Test
    final void testCreationError() {
        // Given
        // When
        assertThrows(ExcelReaderException.class, () -> new ExcelDataReader("notExists.xls"));
        // Then
    }

    @Test
    @SuppressWarnings("unchecked")
    final void testRegister() throws IllegalAccessException {
        // Given
        final Field sheetNamesField = ReflectionUtils
                .findFields(ExcelDataReader.class, f -> f.getName().equals("sheetNames"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        sheetNamesField.setAccessible(true);
        final ExcelDataReader reader = new ExcelDataReader(SAMPLE_FILE_PATH);
        // When
        assertDoesNotThrow(() -> reader.register(SampleCompositeClass.class, "SampleInstanceComposite"));
        // Then
        final Map<Class<?>, String> sheetNames = (Map<Class<?>, String>) sheetNamesField.get(reader);
        assertThat(sheetNames).containsEntry(SampleCompositeClass.class, "SampleInstanceComposite");
    }

    @Test
    final void getTestData() {
        // Given
        final ExcelDataReader reader = new ExcelDataReader(SAMPLE_FILE_PATH);
        reader.register(SampleCompositeClass.class, "SampleInstanceComposite");
        // When
        final SampleCompositeClass actual = assertDoesNotThrow(() -> reader.getTestData(SampleCompositeClass.class, "TEST-01", true));
        // Then
        assertThat(actual).isNotNull();
    }

    @Test
    final void getAllTestData() {
        // Given
        final ExcelDataReader reader = new ExcelDataReader(SAMPLE_FILE_PATH);
        reader.register(SampleCompositeClass.class, "SampleInstanceComposite");
        // When
        final List<SampleCompositeClass> allData = reader.getAllTestData(SampleCompositeClass.class, true);
        // Then
        assertThat(allData).hasSize(2);
    }
}
