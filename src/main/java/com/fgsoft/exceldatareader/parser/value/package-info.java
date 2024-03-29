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

/**
 * This package contains the definition of the parsers expected to return values that can be read from
 * a unique Excel cell. Typically, this covers (non-exclusive):
 * <ul>
 *     <li>Java primitive data types</li>
 *     <li>String</li>
 *     <li>enums</li>
 *     <li>date and time</li>
 * </ul>
 * From this package, it is also possible to add custom types that can be represented by a single cell
 * such as currency, country, etc.
 */
package com.fgsoft.exceldatareader.parser.value;

