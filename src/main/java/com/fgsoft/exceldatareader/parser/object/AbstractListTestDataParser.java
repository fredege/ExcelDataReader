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

import lombok.Getter;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

@Getter
abstract class AbstractListTestDataParser<T extends List<V>, V> extends AbstractTestDataParser<T> {
    private final Class<V> itemType;

    AbstractListTestDataParser(Class<V> itemType, CellRangeAddress cellRange, CellRangeAddress headerRange) {
        super(buildParserType(itemType), cellRange, headerRange);
        this.itemType = itemType;
    }

    @SuppressWarnings({"unchecked", "java:S1172", "java:S1854", "java:S2133"})
    private static <T extends List<V>, V> Class<T> buildParserType(Class<V> type) {
        final List<V> list = new ArrayList<>();
        return (Class<T>) list.getClass();
    }
}
