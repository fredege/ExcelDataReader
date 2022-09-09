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
package com.fgsoft.exceldatareader.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Descriptor of the header part of the Excel worksheet. Representing in Excel requires to allocate one or several rows
 * to provide heading information that will be used by the parser to populate the corresponding beans.
 * Headers can have the following lines:
 * <ul>
 *     <li>Title rows: these rows will be used to compute the key corresponding to a given column. First row
 *     corresponds to the upper entry of the value for single cell values or can be used to represent a group of values
 *     for multiple cells values. Header title rows must be grouped: any row between the fist and the last title rows
 *     contributes to the computation of the key that will used to select the relevant column. For all title rows but
 *     the last one, celles can be horizontally grouped in order to represent an inner object or a list of inner
 *     objects.</li>
 *     <li>Optional comment rows can be used to provide information that will not be used for parsing. These rows can
 *     be put before or after the title rows, but never between the first and the last title rows.</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public class HeaderDescriptor {
    private final int firstTitleRow; // Index of the fist title row
    private final int lastTitleRow; // Index of the last title row
    private final int lastHeaderRow; // Index of the last header row. Further rows are dedicated to contain data
}
