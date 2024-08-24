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
package com.fgsoft.exceldatareader.parser.object.list;

import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import com.fgsoft.exceldatareader.parser.value.SingleCellValueParser;
import com.fgsoft.exceldatareader.parser.value.SingleCellValueParserRouter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public class SingleCellValuesListParser<T extends List<V>, V> extends AbstractListTestDataParser<T, V> {
    public SingleCellValuesListParser(Class<V> itemType, CellRangeAddress cellRange, CellRangeAddress headerRange) {
        super(itemType, cellRange, headerRange);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse(WorksheetAnalyser worksheetAnalyser, Class<T> clazz, String... ignore) {
        final List<V> retList = new ArrayList<>();
        final int columnNumber = getHeaderRange().getFirstColumn();
        final SingleCellValueParser<V> singleCellValueParser = SingleCellValueParserRouter.getParser(getItemType());
        final FormulaEvaluator formulaEvaluator = worksheetAnalyser.getFormulaEvaluator();
        for (int rowNum = getCellRange().getFirstRow(); rowNum <= getCellRange().getLastRow(); rowNum++) {
            final  Row row = worksheetAnalyser.getWorksheet().getRow(rowNum);
            final Cell cell = row.getCell(columnNumber);
            final V value = singleCellValueParser.getValue(cell, formulaEvaluator);
            retList.add(value);
        }
        return (T) retList;
    }
}
