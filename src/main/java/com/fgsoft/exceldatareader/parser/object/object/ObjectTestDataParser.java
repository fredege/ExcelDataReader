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
package com.fgsoft.exceldatareader.parser.object.object;

import com.fgsoft.exceldatareader.exception.HeaderNotFoundException;
import com.fgsoft.exceldatareader.parser.object.AbstractTestDataParser;
import com.fgsoft.exceldatareader.parser.object.TestDataParser;
import com.fgsoft.exceldatareader.parser.util.BeanAnalyzer;
import com.fgsoft.exceldatareader.parser.util.InstanceBuilder;
import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import com.fgsoft.exceldatareader.parser.value.SingleCellValueParser;
import com.fgsoft.exceldatareader.parser.value.SingleCellValueParserRouter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.List;

import static com.fgsoft.exceldatareader.parser.object.ObjectParserRouter.findParser;

public class ObjectTestDataParser<T> extends AbstractTestDataParser<T> {

    public ObjectTestDataParser(Class<T> type, CellRangeAddress fieldRange, CellRangeAddress headerRange) {
        super(type, fieldRange, headerRange);
    }

    @Override
    public T parse(WorksheetAnalyser worksheetAnalyser, Class<T> clazz, boolean skipMissingHeader, String... ignore) {
        final T instance = InstanceBuilder.buildInstance(getType());
        final List<String> ignoredFieldName = List.of(ignore);
        BeanAnalyzer.getSingleCellValues(getType()).stream()
                .filter(field -> !ignoredFieldName.contains(field.getName()))
                .forEach(field -> setSingleCellValueOnField(worksheetAnalyser, field, instance, skipMissingHeader));
        BeanAnalyzer.getMultipleCellsValues(getType()).stream()
                .filter(field -> !ignoredFieldName.contains(field.getName()))
                .forEach(field -> setValueOnField(worksheetAnalyser, field, instance, skipMissingHeader, ignore));
        return instance;
    }

    @SuppressWarnings("unchecked")
    private <V> void setSingleCellValueOnField(WorksheetAnalyser worksheetAnalyser, Field field, T instance, boolean skipMissingHeader) {
        try {
            final SingleCellValueParser<V> parser = (SingleCellValueParser<V>) SingleCellValueParserRouter.getParser(field.getType());
            final Cell cell = worksheetAnalyser.getCell(field.getName(), getCellRange(), getHeaderRange());
            final FormulaEvaluator formulaEvaluator = worksheetAnalyser.getFormulaEvaluator();
            final V value = parser.getValue(cell, formulaEvaluator);
            BeanAnalyzer.setValueOnField(instance, field, value);
        } catch (HeaderNotFoundException exc) {
            if (!skipMissingHeader) throw exc;
        }
    }

    @SuppressWarnings("unchecked")
    private <V> void setValueOnField(WorksheetAnalyser worksheetAnalyser, Field field, T instance, boolean skipMissingHeader, String... ignore) {
        try {
            final CellRangeAddress fieldCellRange = worksheetAnalyser.getCellRange(field.getName(), getCellRange(), getHeaderRange());
            final CellRangeAddress headerCellRange = worksheetAnalyser.getHeaderRange(field.getName(), getHeaderRange());
            final TestDataParser<V> parser = findParser(field, fieldCellRange, headerCellRange);
            final V value = parser.parse(worksheetAnalyser, (Class<V>) field.getType(), skipMissingHeader, ignore);
            BeanAnalyzer.setValueOnField(instance, field, value);
        } catch (HeaderNotFoundException exc) {
            if (!skipMissingHeader) throw exc;
        }
    }

}
