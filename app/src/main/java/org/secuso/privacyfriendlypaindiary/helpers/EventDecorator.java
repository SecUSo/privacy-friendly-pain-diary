package org.secuso.privacyfriendlypaindiary.helpers;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;
import java.util.Set;

/**
 * Adapted from <a href="https://github.com/prolificinteractive/material-calendarview/blob/master/docs/DECORATORS.md"/a>.
 *
 * @author Susanne Felsen
 * @version 20171205
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