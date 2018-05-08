/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.plugins.ide.idea
import org.gradle.integtests.fixtures.TestResources
import org.gradle.plugins.ide.AbstractIdeIntegrationTest
import org.junit.Rule
import org.junit.Test

class IdeaDependencySubstitutionIntegrationTest extends AbstractIdeIntegrationTest {
    @Rule
    public final TestResources testResources = new TestResources(testDirectoryProvider)

    @Test
    void "external dependency substituted with project dependency"() {
        runTask("idea", "include 'project1', 'project2'", """
allprojects {
    apply plugin: "java"
    apply plugin: "idea"
}

project(":project2") {
    dependencies {
        compile group: "junit", name: "junit", version: "4.7"
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute module("junit:junit:4.7") with project(":project1")
        }
    }
}
""")

        def dependencies = parseIml("project2/project2.iml").dependencies
        assert dependencies.libraries.size() == 0
        assert dependencies.modules.size() == 3
        dependencies.assertHasModule(['PROVIDED', 'RUNTIME','TEST'], 'project1')
    }

    @Test
    void "transitive external dependency substituted with project dependency"() {
        mavenRepo.module("org.gradle", "module1").dependsOnModules("module2").publish()
        mavenRepo.module("org.gradle", "module2").publish()

        runTask("idea", "include 'project1', 'project2'", """
allprojects {
    apply plugin: "java"
    apply plugin: "idea"
}

project(":project2") {
    repositories {
        maven { url "${mavenRepo.uri}" }
    }

    dependencies {
        compile "org.gradle:module1:1.0"
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute module("org.gradle:module2:1.0") with project(":project1")
        }
    }
}
""")

        def dependencies = parseIml("project2/project2.iml").dependencies
        assert dependencies.libraries.size() == 3
        dependencies.assertHasLibrary(['PROVIDED', 'RUNTIME','TEST'], "module1-1.0.jar")
        assert dependencies.modules.size() == 3
        dependencies.assertHasModule(['PROVIDED', 'RUNTIME','TEST'], 'project1')
    }

    @Test
    void "project dependency substituted with external dependency"() {
        runTask("idea", "include 'project1', 'project2'", """
allprojects {
    apply plugin: "java"
    apply plugin: "idea"
}

project(":project2") {
    ${mavenCentralRepository()}

    dependencies {
        compile project(":project1")
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute project(":project1") with module("junit:junit:4.7")
        }
    }
}
""")

        def dependencies = parseIml("project2/project2.iml").dependencies
        assert dependencies.libraries.size() == 3
        dependencies.assertHasLibrary(['PROVIDED', 'RUNTIME','TEST'], 'junit-4.7.jar')
        assert dependencies.modules.size() == 0
    }
}
