/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.execution.taskgraph

import org.gradle.api.internal.TaskInternal
import spock.lang.Specification

class TaskInfoFactoryTest extends Specification {
    def graph = new TaskInfoFactory()
    def a = task('a')
    def b = task('b')
    def c = task('c')
    def d = task('d')
    def e = task('e')

    private TaskInternal task(String name) {
        Mock(TaskInternal) {
            getName() >> name
            compareTo(_) >> { args -> name.compareTo(args[0].name)}
        }
    }

    void 'can create a node for a task'() {
        when:
        def node = graph.createNode(a)

        then:
        !node.inKnownState
        node.dependencyPredecessors.empty
        node.mustSuccessors.empty
        node.dependencySuccessors.empty
        node.shouldSuccessors.empty
        node.finalizers.empty
    }

    void 'caches node for a given task'() {
        when:
        def node = graph.createNode(a)

        then:
        graph.createNode(a).is(node)
    }

    void 'can add multiple nodes'() {
        when:
        graph.createNode(a)
        graph.createNode(b)

        then:
        graph.tasks == [a, b] as Set
    }

    void 'clear'() {
        when:
        graph.createNode(a)
        graph.createNode(b)
        graph.createNode(c)
        graph.clear()

        then:
        !graph.tasks
    }
}
