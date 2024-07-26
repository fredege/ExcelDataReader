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

import java.lang.reflect.Field;

public class ObjectTestDataParser<T> extends AbstractTestDataParser<T> {
    @Override
    public T parse(WorksheetAnalyser worksheetAnalyser, Class<T> clazz) {
        final T instance = InstanceBuilder.buildInstance(clazz);
        final BeanAnalyzer beanAnalyzer = new BeanAnalyzer();
        beanAnalyzer.getSingleCellValues(clazz)
                .forEach(field -> setSingleCellValueOnField(worksheetAnalyser, field, field.getType(), instance));
        beanAnalyzer.getMultipleCellsValues(clazz)
                .forEach(field -> setValueOnField(worksheetAnalyser, field, field.getType(), instance));
        return instance;
    }

    private <V> void setSingleCellValueOnField(WorksheetAnalyser worksheetAnalyser, Field field, Class<V> type, T instance) {
        final SingleCellValueParser<V> parser = SingleCellValueParserRouter.getParser(type);
        final Cell cell = worksheetAnalyser.getCell(field.getName());
        final FormulaEvaluator formulaEvaluator = worksheetAnalyser.getFormulaEvaluator();
        final V value = parser.getValue(cell, formulaEvaluator);
        final BeanAnalyzer beanAnalyzer = new BeanAnalyzer();
        beanAnalyzer.setValueOnField(instance, field, value);
    }

    private <V> void setValueOnField(WorksheetAnalyser worksheetAnalyser, Field field, Class<V> type, T instance) {
        final TestDataParser<V> parser = findParser(type);
        final V value = parser.parse(worksheetAnalyser, type);
        final BeanAnalyzer beanAnalyzer = new BeanAnalyzer();
        beanAnalyzer.setValueOnField(instance, field, value);
    }

}
