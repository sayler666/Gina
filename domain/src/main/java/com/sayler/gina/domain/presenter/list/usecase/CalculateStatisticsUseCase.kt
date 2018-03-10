package com.sayler.gina.domain.presenter.list.usecase

import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.stats.decorator.CharsDecorator
import com.sayler.gina.domain.stats.decorator.EntriresStatistic
import com.sayler.gina.domain.stats.decorator.SentencesDecorator
import com.sayler.gina.domain.stats.decorator.WordsDecorator

/**
 * Created by sayler on 2018-02-04.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class CalculateStatisticsUseCase {
    fun calculate(data: List<IDay>): String {
        var statistics = ""
        if (data.isNotEmpty()) {
            val statisticGenerator = CharsDecorator(WordsDecorator(SentencesDecorator(EntriresStatistic())))
            val statisticPairs = statisticGenerator.generate(data)
            val statisticData = Stream.of(statisticPairs).map { t -> "${t.label}: ${t.value}\n" }.collect(Collectors.joining())
            statistics = statisticData.trimEnd('\n')
        }
        return statistics
    }
}