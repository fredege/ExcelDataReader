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
package com.fgsoft.exceldatareader.parser.object.collection;

import com.fgsoft.exceldatareader.exception.UnsupportedTypeException;
import com.fgsoft.exceldatareader.parser.object.AbstractTestDataParser;
import lombok.Getter;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

@Getter
abstract class AbstractCollectionTestDataParser<U extends Collection<V>, V> extends AbstractTestDataParser<U> {
    private final Class<V> itemType;

    AbstractCollectionTestDataParser(Class<U> collectionType, Class<V> itemType, CellRangeAddress cellRange, CellRangeAddress headerRange) {
        super(buildParserType(collectionType), cellRange, headerRange);
        this.itemType = itemType;
    }

    @SuppressWarnings({"unchecked", "java:S1172", "java:S1854", "java:S2133"})
    private static <T extends Collection<V>, V> Class<T> buildParserType(Class<T> collectionType) {
        return (Class<T>) buildInstance(collectionType).getClass();
    }

    protected static <V> Collection<V> buildInstance(Class<V> collectionType) {
        final Collection<V> retCollection;
        if (List.class.isAssignableFrom(collectionType)) {
            retCollection = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionType)) {
            retCollection = new HashSet<>();
        } else {
            throw new UnsupportedTypeException(collectionType);
        }
        return retCollection;
    }
}
