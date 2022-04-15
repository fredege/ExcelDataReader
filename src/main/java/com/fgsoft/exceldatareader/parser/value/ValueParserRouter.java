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

import com.fgsoft.exceldatareader.exception.ExcelReaderErrorCode;
import com.fgsoft.exceldatareader.exception.ExcelReaderException;
import com.fgsoft.exceldatareader.exception.InvalidTypeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class in charge of routing to the relevant value parser given the class of the value to get from
 * parsing.
 */
public final class ValueParserRouter {
    private static final Map<Class<?>, Class<? extends AbstractSingleCellValueParser<?>>> PARSERS =
            new HashMap<>();

    static {
        PARSERS.put(Double.class, DoubleValueParser.class);
        PARSERS.put(Integer.class, IntegerValueParser.class);
        PARSERS.put(Long.class, LongValueParser.class);
        PARSERS.put(BigDecimal.class, BigDecimalValueParser.class);
        PARSERS.put(Boolean.class, BooleanValueParser.class);
        PARSERS.put(String.class, StringValueParser.class);
        PARSERS.put(Date.class, DateValueParser.class);
        PARSERS.put(LocalDate.class, LocalDateValueParser.class);
        PARSERS.put(LocalTime.class, LocalTimeValueParser.class);
        PARSERS.put(LocalDateTime.class, LocalDateTimeValueParser.class);
        PARSERS.put(ZonedDateTime.class, ZonedDateTimeValueParser.class);
        PARSERS.put(OffsetDateTime.class, OffsetDateTimeValueParser.class);
    }

    /**
     * Prevent instanciation.
     */
    private ValueParserRouter() {
    }


    /**
     * Return the appropriate parser for the given class
     *
     * @param type type of value to get from parsing
     * @return fond parser
     */
    @SuppressWarnings("unchecked")
    public static AbstractSingleCellValueParser<Object> getParser(Class<?> type) {
        final AbstractSingleCellValueParser<Object> parser;
        if (!PARSERS.containsKey(type)) {
            throw new InvalidTypeException(type.getName());
        } else {
            final Class<? extends AbstractSingleCellValueParser<?>> parserClass = PARSERS.get(type);
            try {
                final Constructor<AbstractSingleCellValueParser<?>> constructor =
                        (Constructor<AbstractSingleCellValueParser<?>>)
                                parserClass.getDeclaredConstructor();
                parser = (AbstractSingleCellValueParser<Object>) constructor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exc) {
                throw new ExcelReaderException(exc, ExcelReaderErrorCode.UNKNOWN);
            }
        }
        return parser;
    }
}
