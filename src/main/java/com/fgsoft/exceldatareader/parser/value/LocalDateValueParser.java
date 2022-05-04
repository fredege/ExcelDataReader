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
package com.fgsoft.exceldatareader.parser.value;

import com.fgsoft.exceldatareader.exception.IncorrectValueForTypeException;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateValueParser extends AbstractSingleCellValueParser<LocalDate> {
    final LocalDateTimeValueParser mainParser = new LocalDateTimeValueParser();

    @Override
    protected LocalDate getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected LocalDate getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        return mainParser.getValueForCell(value, rowIndex, colIndex, worksheet).toLocalDate();
    }

    @Override
    protected LocalDate getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, value, LocalDate.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected LocalDate getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        final LocalDate localDate;
        try {
            localDate = LocalDate.parse(value);
        } catch (DateTimeParseException exc) {
            throw new IncorrectValueForTypeException(exc, value, LocalDateTime.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
        return localDate;
    }
}
