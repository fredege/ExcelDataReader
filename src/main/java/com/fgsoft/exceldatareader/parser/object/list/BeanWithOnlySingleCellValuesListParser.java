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
import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public class BeanWithOnlySingleCellValuesListParser<T extends List<V>, V> extends AbstractListTestDataParser<T, V> {
    public BeanWithOnlySingleCellValuesListParser(Class<V> itemType, CellRangeAddress cellRange, CellRangeAddress headerRange) {
        super(itemType, cellRange, headerRange);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse(WorksheetAnalyser worksheetAnalyser, Class<T> clazz, boolean skipMissingHeader, String... ignore) {
        final List<V> retList = new ArrayList<>();
        final Class<V> itemType = getItemType();
        for (int rowNum = getCellRange().getFirstRow(); rowNum <= getCellRange().getLastRow(); rowNum++) {
            final CellRangeAddress itemCellRange = new CellRangeAddress(rowNum, rowNum,
                    getCellRange().getFirstColumn(), getCellRange().getLastColumn());
            final ObjectTestDataParser<V> itemParser =
                    (ObjectTestDataParser<V>) ObjectParserRouter.findParser(itemType, itemCellRange, getHeaderRange());
            final V value = itemParser.parse(worksheetAnalyser, itemType, false);
            retList.add(value);
        }
        return (T) retList;
    }
}
