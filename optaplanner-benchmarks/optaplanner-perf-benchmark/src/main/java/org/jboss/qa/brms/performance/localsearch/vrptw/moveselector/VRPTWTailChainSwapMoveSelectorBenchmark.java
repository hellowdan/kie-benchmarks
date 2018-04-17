package org.jboss.qa.brms.performance.localsearch.vrptw.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;

import java.util.List;

public class VRPTWTailChainSwapMoveSelectorBenchmark extends AbstractVRPTWMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createTailChainSwapMoveSelectorList();
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
