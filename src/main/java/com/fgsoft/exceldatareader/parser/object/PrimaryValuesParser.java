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
package com.fgsoft.exceldatareader.parser.object;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

/**
 * This parser is used to get all the primary values of a given instance from a row in excel
 */
@RequiredArgsConstructor
public class PrimaryValuesParser {
    private final Sheet worksheet;

    public void parse(Object instance, List<String> headers, String baseHeader, Row row) {

    }
}
