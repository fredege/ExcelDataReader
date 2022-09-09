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
package com.fgsoft.exceldatareader.parser.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
public class BeanAnalyzer {
    private static final List<Class<?>> SINGLE_CELL_TYPES = Arrays.asList(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            Character.class,
            String.class,
            Date.class,
            LocalTime.class,
            LocalDate.class,
            LocalDateTime.class,
            BigDecimal.class,
            Enumeration.class,
            Currency.class
    );

    /**
     * Checks is an object can be read from a single Excel cell. The following types are considered as single cell value:
     * <ul>
     *     <li>Java primitive types</li>
     *     <li>String</li>
     *     <li>Enum</li>
     *     <li>Java time objects, i.e. LocalDate, LocalTime, LocalDateTime</li>
     * </ul>
     * @param object object to be checked
     * @return true when can be parsed from a single cell
     */
    public boolean hasSingleCellValue(@NonNull final Object object) {
        return isSingleCellType(object.getClass());
    }

    private boolean isSingleCellType(final Class<?> type) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Checking type '%s' as single cell represented", type));
        }
        return  type == null || type.isPrimitive() || type.isEnum() || SINGLE_CELL_TYPES.contains(type);
    }
}
