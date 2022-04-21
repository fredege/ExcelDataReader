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

import java.util.Arrays;

/**
 * Parser for Enum values.
 *
 * TODO find why I cannot use Enum<?> as class parameter
 */
public class EnumValueParser extends AbstractSingleCellValueParser<Object> {
    private final Class<? extends Enum<?>> enumType;

    public EnumValueParser(Class<? extends Enum<?>> type) {
        super();
        this.enumType = type;
    }

    @Override
    protected Enum<?> getValueForEmptyCell(int rowIndex, int colIndex, Sheet worksheet) {
        return null;
    }

    @Override
    protected Enum<?> getValueForCell(double value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new  IncorrectValueForTypeException(null, value, enumType.getName(),
                rowIndex, colIndex, worksheet.getSheetName()) ;
    }

    @Override
    protected Enum<?> getValueForCell(boolean value, int rowIndex, int colIndex, Sheet worksheet) {
        throw new  IncorrectValueForTypeException(null, value, enumType.getName(),
                rowIndex, colIndex, worksheet.getSheetName()) ;
    }

    @Override
    protected  Enum<?> getValueForCell(String value, int rowIndex, int colIndex, Sheet worksheet) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> e.name().equals(value)).findFirst()
                .orElseThrow(() -> new  IncorrectValueForTypeException(null, value, enumType.getName(),
                rowIndex, colIndex, worksheet.getSheetName()));
    }
}
