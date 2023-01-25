/* Copyright 2023 Frederic GEDIN
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
package com.fgsoft.exceldatareader.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SampleCompositeClass {
    private BigDecimal bigDecimal;
    private boolean booleanValue;
    private Date date;
    private double doubleValue;
    private int intValue;
    private LocalDateTime localDateTime;
    private LocalDate localDate;
    private LocalTime localTime;
    private long longValue;
    private String stringValue;
    private Sample sample;
    private SampleInstancePrimaryOnly composite;
    private List<String> listOfStrings;
    private List<SampleInstancePrimaryOnly> listOfComposites;
}
