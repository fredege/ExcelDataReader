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

import com.fgsoft.exceldatareader.parser.object.ObjectParserRouter;
import com.fgsoft.exceldatareader.parser.object.object.ObjectTestDataParser;
import com.fgsoft.exceldatareader.parser.util.BeanAnalyzer;
import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ListTestDataParser<T extends List<V>, V> extends AbstractListTestDataParser<T, V> {
    public ListTestDataParser(Class<V> type, CellRangeAddress fieldRange, CellRangeAddress headerRange) {
        super(type, fieldRange, headerRange);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse(WorksheetAnalyser worksheetAnalyser, Class<T> clazz, boolean skipMissingHeader, String... ignore) {
        final List<V> retList = new ArrayList<>();
        final Class<V> itemType = getItemType();
        int rowNum = getCellRange().getFirstRow();
        while (rowNum <= getCellRange().getLastRow()) {
            final CellRangeAddress itemCellRange = computeItemCellRange(worksheetAnalyser, rowNum, itemType);
            final ObjectTestDataParser<V> itemParser =
                    (ObjectTestDataParser<V>) ObjectParserRouter.findParser(itemType, itemCellRange, getHeaderRange());
            final V value = itemParser.parse(worksheetAnalyser, itemType, skipMissingHeader, ignore);
            retList.add(value);
            rowNum = itemCellRange.getLastRow() + 1;
        }
        return (T) retList;
    }

    private CellRangeAddress computeItemCellRange(WorksheetAnalyser worksheetAnalyzer, int rowNum, Class<V> itemType) {
        final Field nonNullField = BeanAnalyzer.getNonNullField(itemType);
        final int colNum = worksheetAnalyzer.getHeaderColumnNumber(nonNullField.getName(), getHeaderRange());
        int rowMax = rowNum + 1;
        while (rowMax <= getCellRange().getLastRow()) {
            if (worksheetAnalyzer.isCellEmpty(rowMax, colNum)) {
                rowMax++;
            } else {
                break;
            }
        }
        return new CellRangeAddress(rowNum, rowMax-1,
                getCellRange().getFirstColumn(), getCellRange().getLastColumn());
    }

 }
