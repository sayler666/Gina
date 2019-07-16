package com.sayler.gina.domain.stats.decorator

import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.stats.Statistic
import com.sayler.gina.domain.stats.StatisticPair

/**
 * Created by sayler on 03.10.2017.
 */
class WordsDecorator(stat: Statistic) : StatisticDecorator(stat) {
    override fun generate(days: List<IDay>): MutableList<StatisticPair> {
        val list = super.generate(days)

        var words = 0
        days.forEach { iDay: IDay ->
            words += with(iDay.content) {
                this.split(" ").count()
            }
        }

        list.add(StatisticPair("Words", words.toString()))
        return list
    }
}