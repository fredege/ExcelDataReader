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
package com.fgsoft.exceldatareader.parser.value;

import com.fgsoft.exceldatareader.exception.IncorrectValueForTypeException;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Currency;

public class CurrencyValueParser extends AbstractSingleCellValueParser<Currency> {
    @Override
    protected Currency getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected Currency getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, String.valueOf(value), Currency.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
    }

    @Override
    protected Currency getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new IncorrectValueForTypeException(null, value, Currency.class.getName(),
                rowIndex, colIndex, worksheet.getSheetName());
  }

    @Override
    protected Currency getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        try {
            return Currency.getInstance(value);
        } catch (IllegalArgumentException exc) {
            throw new IncorrectValueForTypeException(exc, value, Currency.class.getName(),
                    rowIndex, colIndex, worksheet.getSheetName());
        }
    }
}
