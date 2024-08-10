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
package com.fgsoft.exceldatareader.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DataBuilder {
    public SampleInstancePrimaryOnly buildSampleInstancePrimaryOnly() {
        final SampleInstancePrimaryOnly retVal = new SampleInstancePrimaryOnly();
        retVal.setBigDecimal(BigDecimal.valueOf(123.56));
        retVal.setStringValue("Some sample string");
        retVal.setDoubleValue(123.56);
        retVal.setBooleanValue(true);
        retVal.setLocalDateTime(LocalDateTime.now());
        retVal.setLocalDate(LocalDate.now());
        retVal.setIntValue(123);
        retVal.setLongValue(123L);
        retVal.setLocalTime(LocalTime.NOON);
        return retVal;
    }
}
