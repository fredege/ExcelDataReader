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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueParserRouterTest {
    @ParameterizedTest
    @CsvSource({"java.lang.String, com.fgsoft.exceldatareader.parser.value.StringValueParser",
            "java.lang.Double, com.fgsoft.exceldatareader.parser.value.DoubleValueParser",
            "java.lang.Integer, com.fgsoft.exceldatareader.parser.value.IntegerValueParser",
            "java.lang.Long, com.fgsoft.exceldatareader.parser.value.LongValueParser",
            "java.math.BigDecimal, com.fgsoft.exceldatareader.parser.value.BigDecimalValueParser",
            "java.lang.Boolean, com.fgsoft.exceldatareader.parser.value.BooleanValueParser",
            "java.util.Date, com.fgsoft.exceldatareader.parser.value.DateValueParser",
            "java.time.LocalDate, com.fgsoft.exceldatareader.parser.value.LocalDateValueParser",
            "java.time.LocalTime, com.fgsoft.exceldatareader.parser.value.LocalTimeValueParser",
            "java.time.LocalDateTime, com.fgsoft.exceldatareader.parser.value.LocalDateTimeValueParser",
            "com.fgsoft.exceldatareader.util.Sample, com.fgsoft.exceldatareader.parser.value.EnumValueParser"
    })
    final void testSelectParser(String valueClassName, String parserClassName) throws ClassNotFoundException {
        // Given
        final Class<?> valueClass = Class.forName(valueClassName);
        final Class<?> parserClass = Class.forName(parserClassName);
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(valueClass);
        // Then
        assertThat(parser).isInstanceOf(parserClass);
    }

    @Test
    final void unknownClassWillRaiseException() {
        // Given
        final String message = String.format(ExcelReaderErrorCode.INVALID_TYPE.getMessage(),
                TestClass.class.getName());
        // When
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> ValueParserRouter.getParser(TestClass.class));
        // Then
        assertThat(exception).isInstanceOf(InvalidTypeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    static class TestClass {
    }
}
