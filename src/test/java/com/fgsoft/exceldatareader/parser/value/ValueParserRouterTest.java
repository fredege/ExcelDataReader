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
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueParserRouterTest {
    @Test
    final void stringMustBeParsedByStringValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(String.class);
        // Then
        assertThat(parser).isInstanceOf(StringValueParser.class);
    }

    @Test
    final void doubleMustBeParsedByDoubleValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(Double.class);
        // Then
        assertThat(parser).isInstanceOf(DoubleValueParser.class);
    }

    @Test
    final void integerMustBeParsedByIntegerValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(Integer.class);
        // Then
        assertThat(parser).isInstanceOf(IntegerValueParser.class);
    }

    @Test
    final void longMustBeParsedByLongValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(Long.class);
        // Then
        assertThat(parser).isInstanceOf(LongValueParser.class);
    }

    @Test
    final void bigDecimalMustBeParsedByLongValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(BigDecimal.class);
        // Then
        assertThat(parser).isInstanceOf(BigDecimalValueParser.class);
    }

    @Test
    final void booleanMustBeParsedByBooleanValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(Integer.class);
        // Then
        assertThat(parser).isInstanceOf(IntegerValueParser.class);
    }

    @Test
    final void dateMustBeParsedByDateValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(Date.class);
        // Then
        assertThat(parser).isInstanceOf(DateValueParser.class);
    }

    @Test
    final void localDateMustBeParsedByDateValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(LocalDate.class);
        // Then
        assertThat(parser).isInstanceOf(DateValueParser.class);
    }

    @Test
    final void localTimeMustBeParsedByTimeValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(LocalTime.class);
        // Then
        assertThat(parser).isInstanceOf(TimeValueParser.class);
    }

    @Test
    final void localDateTimeMustBeParsedByDateTimeValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(LocalDateTime.class);
        // Then
        assertThat(parser).isInstanceOf(DateTimeValueParser.class);
    }

    @Test
    final void offsetDateTimeMustBeParsedByOffsetDateTimeValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(OffsetDateTime.class);
        // Then
        assertThat(parser).isInstanceOf(OffsetDateTimeValueParser.class);
    }

    @Test
    final void zonedDateTimeMustBeParsedByZonedDateTimeValueParser() {
        // Given
        // When
        final AbstractSingleCellValueParser<?> parser = ValueParserRouter.getParser(ZonedDateTime.class);
        // Then
        assertThat(parser).isInstanceOf(ZonedDateTimeValueParser.class);
    }

    @Test
    final void unknownClassWillRaiseException() {
        // Given
        final String message = String.format(ExcelReaderErrorCode.INVALID_TYPE.getMessage(),
                TestClass.class.getName());
        // When
        // Then
        Throwable exception = assertThrows(ExcelReaderException.class,
                () -> ValueParserRouter.getParser(TestClass.class));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    static class TestClass {
    }
}
