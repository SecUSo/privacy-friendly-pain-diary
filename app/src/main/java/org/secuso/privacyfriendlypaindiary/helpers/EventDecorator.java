/*
    This file is part of Privacy Friendly Pain Diary.

    Privacy Friendly Pain Diary is free software: you can redistribute it
    and/or modify it under the terms of the GNU General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.secuso.privacyfriendlypaindiary.helpers;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;
import java.util.Set;

/**
 * Decorates (i.e. highlights) all dates contained in a set of dates supplied to it.
 *
 * @author Susanne Felsen
 * @version 20171205
 * <p>
 * Adapted from <a href="https://github.com/prolificinteractive/material-calendarview/blob/master/docs/DECORATORS.md">here</a>.
 */
public class EventDecorator implements DayViewDecorator {

    private final int color;
    private Set<CalendarDay> dates;

    public EventDecorator(int color) {
        this.color = color;
        this.dates = new HashSet<>();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }

    public void setDates(Set<CalendarDay> dates) {
        this.dates = dates;
    }

}