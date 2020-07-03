package com.sayler.monia.domain.stats.decorator

import com.sayler.monia.domain.IDay
import com.sayler.monia.domain.stats.Statistic
import com.sayler.monia.domain.stats.StatisticPair

/**
 * Created by sayler on 03.10.2017.
 */
class SentencesDecorator(stat: Statistic) : StatisticDecorator(stat) {
    override fun generate(days: List<IDay>): MutableList<StatisticPair> {
        val list = super.generate(days)

        var words = 0
        days.forEach { iDay: IDay ->
            words += with(iDay.content) {
                this.split(".").count()
            }
        }

        list.add(StatisticPair("Sentences", words.toString()))
        return list
    }
}