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

/**
 * This is the base code for automatically generate a java object based on the field names and class.
 * In order to have a proper parsing, data has to be organized according to the following rules:
 * <ul>
 *     <li>All field values for a given object are organized horizontally</li>
 *     <li>For each object, the first column contains a name that will be uses as a reference</li>
 * </ul>
 * Horizontal representation is convenient for representing lists. In such a case, any element of the list is
 * represented on its own row.
 */
public abstract class AbstractObjectParser {
}
