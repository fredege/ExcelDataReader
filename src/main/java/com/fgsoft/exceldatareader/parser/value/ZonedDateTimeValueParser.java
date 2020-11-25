/* Copyright 2020 Frederic GEDIN
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
package com.fgsoft.exceldatareader.parser.value;

import org.apache.poi.ss.usermodel.Sheet;

import java.time.ZonedDateTime;

public class ZonedDateTimeValueParser extends AbstractSingleCellValueParser<ZonedDateTime> {
    @Override
    protected ZonedDateTime getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected ZonedDateTime getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected ZonedDateTime getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected ZonedDateTime getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }
}
