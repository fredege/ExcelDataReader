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

import com.fgsoft.exceldatareader.parser.util.BeanAnalyzer;
import com.fgsoft.exceldatareader.parser.util.InstanceBuilder;
import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import com.fgsoft.exceldatareader.parser.value.SingleCellValueParser;
import com.fgsoft.exceldatareader.parser.value.SingleCellValueParserRouter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;

import static com.fgsoft.exceldatareader.parser.object.ObjectParserRouter.findParser;

public class ObjectTestDataParser<T> extends AbstractTestDataParser<T> {

    public ObjectTestDataParser(Class<T> type, CellRangeAddress fieldRange, CellRangeAddress headerRange) {
        super(type, fieldRange, headerRange);
    }

    @Override
    public T parse(WorksheetAnalyser worksheetAnalyser, Class<T> clazz) {
        final T instance = InstanceBuilder.buildInstance(getType());
        BeanAnalyzer.getSingleCellValues(getType())
                .forEach(field -> setSingleCellValueOnField(worksheetAnalyser, field, instance));
        BeanAnalyzer.getMultipleCellsValues(getType())
                .forEach(field -> setValueOnField(worksheetAnalyser, field, instance));
        return instance;
    }

    @SuppressWarnings("unchecked")
    private <V> void setSingleCellValueOnField(WorksheetAnalyser worksheetAnalyser, Field field, T instance) {
        final SingleCellValueParser<V> parser = (SingleCellValueParser<V>) SingleCellValueParserRouter.getParser(field.getType());
        final Cell cell = worksheetAnalyser.getCell(field.getName(), getCellRange(), getHeaderRange());
        final FormulaEvaluator formulaEvaluator = worksheetAnalyser.getFormulaEvaluator();
        final V value = parser.getValue(cell, formulaEvaluator);
        BeanAnalyzer.setValueOnField(instance, field, value);
    }

    @SuppressWarnings("unchecked")
    private <V> void setValueOnField(WorksheetAnalyser worksheetAnalyser, Field field, T instance) {
        final CellRangeAddress fieldCellRange = worksheetAnalyser.getCellRange(field.getName(), getCellRange(), getHeaderRange());
        final CellRangeAddress headerCellRange = worksheetAnalyser.getHeaderRange(field.getName(), getHeaderRange());
        final TestDataParser<V> parser = findParser(field, fieldCellRange, headerCellRange);
        final V value = parser.parse(worksheetAnalyser, (Class<V>) field.getType());
        BeanAnalyzer.setValueOnField(instance, field, value);
    }

}
