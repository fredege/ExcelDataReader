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

import com.fgsoft.exceldatareader.parser.util.WorksheetAnalyser;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

public class ListTestDataParser<T> extends AbstractTestDataParser<List<T>> {
    public ListTestDataParser(CellRangeAddress fieldRange, CellRangeAddress headerRange) {
        super(fieldRange, headerRange);
    }

    @Override
    public List<T> parse(WorksheetAnalyser worksheetAnalyser, Class<List<T>> clazz) {
        return List.of();
    }
}
