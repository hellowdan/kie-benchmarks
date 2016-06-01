/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.session;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.RulesWithJoins;
import org.drools.benchmarks.domain.A;
import org.drools.benchmarks.domain.B;
import org.drools.benchmarks.domain.C;
import org.drools.benchmarks.domain.D;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * Inserts facts and fires at each insertion causing the activation of all rules.
 */
public class InsertFireLoopBenchmark extends AbstractBenchmark {

    @Param({"12", "48", "192", "768"})
    private int rulesNr;

    @Param({"10", "100", "1000"})
    private int factsNr;

    @Param({"true", "false"})
    private boolean multithread;

    @Param({"true", "false"})
    private boolean async;

    @Param({"true", "false"})
    private boolean cep;

    @Param({"1", "2", "3"})
    private int joinsNr;

    @Param({"true", "false"})
    private boolean batchFire;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new RulesWithJoins(joinsNr, cep, true);
        createKieBaseFromDrl( drlProvider.getDrl(rulesNr),
                              multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO,
                              cep ? EventProcessingOption.STREAM : EventProcessingOption.CLOUD );
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
    }

    @Benchmark
    public void test() {
        StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) kieSession;
        A a = new A( rulesNr + 1 );
        if (async) {
            session.insertAsync( a );
        } else {
            session.insert( a );
        }
        for ( int i = 0; i < factsNr; i++ ) {
            if (async) {
                session.insertAsync( new B( rulesNr + i + 3 ) );
                if (joinsNr > 1) {
                    session.insertAsync( new C( rulesNr + factsNr + i + 3 ) );
                }
                if (joinsNr > 2) {
                    session.insertAsync( new D( rulesNr + factsNr*2 + i + 3 ) );
                }
            } else {
                session.insert( new B( rulesNr + i + 3 ) );
                if (joinsNr > 1) {
                    session.insert( new C( rulesNr + factsNr + i + 3 ) );
                }
                if (joinsNr > 2) {
                    session.insert( new D( rulesNr + factsNr*2 + i + 3 ) );
                }
            }
            if (!batchFire) {
                kieSession.fireAllRules();
            }
        }
        if (batchFire) {
            kieSession.fireAllRules();
        }
    }
}
