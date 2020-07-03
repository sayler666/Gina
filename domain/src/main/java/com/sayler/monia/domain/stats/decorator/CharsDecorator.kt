package com.sayler.monia.domain.stats.decorator

import com.sayler.monia.domain.IDay
import com.sayler.monia.domain.stats.Statistic
import com.sayler.monia.domain.stats.StatisticPair

/**
 * Created by sayler on 03.10.2017.
 */
class CharsDecorator(stat: Statistic) : StatisticDecorator(stat) {
    override fun generate(days: List<IDay>): MutableList<StatisticPair> {
        val list = super.generate(days)

        var chars = 0
        days.forEach { iDay: IDay ->
            chars += with(iDay.content) {
                this.count()
            }
        }

        list.add(StatisticPair("Chars", chars.toString()))
        return list
    }
}