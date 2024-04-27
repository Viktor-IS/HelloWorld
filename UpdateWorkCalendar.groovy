/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package scripts

import com.google.common.collect.Lists
import com.haulmont.cuba.core.EntityManager
import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.Transaction
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.workflow.core.entity.WorkCalendarEntity

import java.text.SimpleDateFormat

AppBeans.get(Persistence.class).createTransaction().execute(new Transaction.Runnable() {
    @Override
    void run(EntityManager em) {
        def exceptionDays = [
                "2024-01-01",
                "2024-01-02",
                "2024-01-03",
                "2024-01-04",
                "2024-01-05",
                "2024-01-08",
                ["2024-02-22", "09:00", "13:00"],
                ["2024-02-22", "14:00", "17:00"],
                "2024-02-23",
                ["2024-03-07", "09:00", "13:00"],
                ["2024-03-07", "14:00", "17:00"],
                "2024-03-08",
                ["2024-04-27", "09:00", "13:00"],
                ["2024-04-27", "14:00", "18:00"],
                "2024-04-29",
                "2024-04-30",
                "2024-05-01",
                ["2024-05-08", "09:00", "13:00"],
                ["2024-05-08", "14:00", "17:00"],
                "2024-05-09",
                "2024-05-10",
                ["2024-06-11", "09:00", "13:00"],
                ["2024-06-11", "14:00", "17:00"],
                "2024-06-12",
                ["2024-11-02", "09:00", "13:00"],
                ["2024-11-02", "14:00", "17:00"],
                "2024-11-04",
                ["2024-12-28", "09:00", "13:00"],
                ["2024-12-28", "14:00", "18:00"],
                "2024-12-30",
                "2024-12-31"

        ]

        def metadata = AppBeans.get(Metadata.class)
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm")

        List<WorkCalendarEntity> exceptionDaysToCreate = Lists.newArrayList()
        try {
            for (Object exceptionDayInfo : exceptionDays) {
                Date day = null
                Date start = null
                Date end = null
                if (exceptionDayInfo instanceof String) {
                    day = dayFormat.parse((String) exceptionDayInfo)
                } else if (exceptionDayInfo instanceof List) {
                    List<String> info = (List<String>) exceptionDayInfo
                    day = dayFormat.parse(info.get(0))
                    start = timeFormat.parse(info.get(1))
                    end = timeFormat.parse(info.get(2))
                }

                WorkCalendarEntity workCalendarEntity = metadata.create(WorkCalendarEntity.class)
                workCalendarEntity.setDay(day)
                workCalendarEntity.setStart(start)
                workCalendarEntity.setEnd(end)
                exceptionDaysToCreate.add(workCalendarEntity)
            }
        } catch (Exception e) {
            throw new RuntimeException("Work calendar exception days update error", e)
        }

        Date yearStart = dayFormat.parse('2024-01-01')
        Date yearEnd = dayFormat.parse('2024-12-31')
        em.createQuery('delete from wf$Calendar c where c.day >= :start and c.day <= :end')
                .setParameter('start', yearStart)
                .setParameter('end', yearEnd)
                .executeUpdate()

        for (WorkCalendarEntity exceptionDay : exceptionDaysToCreate)
            em.persist(exceptionDay)
    }
})

return "Work calendar updated successfully"