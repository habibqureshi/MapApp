/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.language.cpp

import org.gradle.integtests.fixtures.ExperimentalFeaturesFixture
import org.gradle.nativeplatform.fixtures.app.CppApp
import org.gradle.nativeplatform.fixtures.app.CppAppWithLibrary
import org.gradle.test.fixtures.maven.MavenFileRepository

class CppExecutablePublishingIntegrationTest extends AbstractCppInstalledToolChainIntegrationTest implements CppTaskNames {
    def repo = new MavenFileRepository(file("repo"))
    def consumer = file("consumer").createDir()

    def setup() {
        when:
        ExperimentalFeaturesFixture.enable(consumer.file("settings.gradle"))
        consumer.file("build.gradle") << """
            repositories {
                maven { 
                    url '${repo.uri}' 
                }
            }
            configurations {
                install {
                    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage, 'native-runtime'))
                    attributes.attribute(Attribute.of('org.gradle.native.debuggable', Boolean), true)
                }
            }
            task install(type: Sync) {
                from configurations.install
                into 'install'
            }
"""
    }

    def "can publish the binaries of an application to a Maven repository"() {
        def app = new CppApp()

        given:
        buildFile << """
            apply plugin: 'cpp-executable'
            apply plugin: 'maven-publish'
            
            group = 'some.group'
            version = '1.2'
            executable {
                baseName = 'test'
            }
            publishing {
                repositories { maven { url '$repo.uri' } }
            }
"""
        app.writeToProject(testDirectory)

        when:
        run('publish')

        then:
        result.assertTasksExecuted(
            compileAndLinkTasks(debug),
            compileAndLinkTasks(release),
            ":generatePomFileForDebugPublication",
            ":generateMetadataFileForDebugPublication",
            ":publishDebugPublicationToMavenRepository",
            ":generatePomFileForMainPublication",
            ":generateMetadataFileForMainPublication",
            ":publishMainPublicationToMavenRepository",
            ":generatePomFileForReleasePublication",
            ":generateMetadataFileForReleasePublication",
            ":publishReleasePublicationToMavenRepository",
            ":publish")

        def main = repo.module('some.group', 'test', '1.2')
        main.assertPublished()
        main.assertArtifactsPublished("test-1.2.pom", "test-1.2.module")
        main.parsedPom.scopes.isEmpty()
        def mainMetadata = main.parsedModuleMetadata
        mainMetadata.variants.size() == 2
        mainMetadata.variant("debug-runtime").availableAt.coords == "some.group:test_debug:1.2"
        mainMetadata.variant("release-runtime").availableAt.coords == "some.group:test_release:1.2"

        def debug = repo.module('some.group', 'test_debug', '1.2')
        debug.assertPublished()
        debug.assertArtifactsPublished(executableName("test_debug-1.2"), "test_debug-1.2.pom", "test_debug-1.2.module")
        debug.artifactFile(type: executableExtension).assertIsCopyOf(executable("build/exe/main/debug/test").file)

        debug.parsedPom.scopes.isEmpty()

        def debugMetadata = debug.parsedModuleMetadata
        debugMetadata.variants.size() == 1
        def debugRuntime = debugMetadata.variant("debug-runtime")
        debugRuntime.dependencies.empty
        debugRuntime.files.size() == 1
        debugRuntime.files[0].name == executableName('test')
        debugRuntime.files[0].url == executableName("test_debug-1.2")

        def release = repo.module('some.group', 'test_release', '1.2')
        release.assertPublished()
        release.assertArtifactsPublished(executableName("test_release-1.2"), "test_release-1.2.pom", "test_release-1.2.module")
        release.artifactFile(type: executableExtension).assertIsCopyOf(executable("build/exe/main/release/test").file)

        release.parsedPom.scopes.isEmpty()

        def releaseMetadata = release.parsedModuleMetadata
        releaseMetadata.variants.size() == 1
        def releaseRuntime = releaseMetadata.variant("release-runtime")
        releaseRuntime.dependencies.empty
        releaseRuntime.files.size() == 1
        releaseRuntime.files[0].name == executableName('test')
        releaseRuntime.files[0].url == executableName("test_release-1.2")

        when:
        consumer.file("build.gradle") << """
            dependencies {
                install 'some.group:test:1.2'
            }
"""
        executer.inDirectory(consumer)
        run("install")

        then:
        def executable = executable("consumer/install/test")
        executable.file.setExecutable(true)
        executable.exec().out == app.expectedOutput
    }

    def "can publish an executable and library to a Maven repository"() {
        def app = new CppAppWithLibrary()

        given:
        settingsFile << "include 'greeter', 'app'"
        buildFile << """
            subprojects {
                apply plugin: 'maven-publish'
                
                group = 'some.group'
                version = '1.2'
                publishing {
                    repositories { maven { url '${repo.uri}' } }
                }
            }
            project(':app') { 
                apply plugin: 'cpp-executable'
                dependencies {
                    implementation project(':greeter')
                }
            }
            project(':greeter') { 
                apply plugin: 'cpp-library'
            }
"""
        app.greeter.writeToProject(file('greeter'))
        app.main.writeToProject(file('app'))

        when:
        run('publish')

        then:
        def appModule = repo.module('some.group', 'app', '1.2')
        appModule.assertPublished()

        def appDebugModule = repo.module('some.group', 'app_debug', '1.2')
        appDebugModule.assertPublished()
        appDebugModule.parsedPom.scopes.size() == 1
        appDebugModule.parsedPom.scopes.runtime.assertDependsOn("some.group:greeter:1.2")

        def appDebugMetadata = appDebugModule.parsedModuleMetadata
        def appDebugRuntime = appDebugMetadata.variant("debug-runtime")
        appDebugRuntime.dependencies.size() == 1
        appDebugRuntime.dependencies[0].coords == 'some.group:greeter:1.2'

        def appReleaseModule = repo.module('some.group', 'app_release', '1.2')
        appReleaseModule.assertPublished()
        appReleaseModule.parsedPom.scopes.size() == 1
        appReleaseModule.parsedPom.scopes.runtime.assertDependsOn("some.group:greeter:1.2")

        def appReleaseMetadata = appReleaseModule.parsedModuleMetadata
        def appReleaseRuntime = appReleaseMetadata.variant("release-runtime")
        appReleaseRuntime.dependencies.size() == 1
        appReleaseRuntime.dependencies[0].coords == 'some.group:greeter:1.2'

        def greeterModule = repo.module('some.group', 'greeter', '1.2')
        greeterModule.assertPublished()

        def greeterDebugModule = repo.module('some.group', 'greeter_debug', '1.2')
        greeterDebugModule.assertPublished()

        def greeterReleaseModule = repo.module('some.group', 'greeter_release', '1.2')
        greeterReleaseModule.assertPublished()

        when:
        def consumer = file("consumer").createDir()
        consumer.file("settings.gradle") << ''
        consumer.file("build.gradle") << """
            dependencies {
                install 'some.group:app:1.2'
            }
"""
        executer.inDirectory(consumer)
        run("install")

        then:
        def executable = executable("consumer/install/app")
        executable.file.setExecutable(true)
        executable.exec().out == app.expectedOutput
    }

    def "uses the basename to calculate the coords"() {
        def app = new CppAppWithLibrary()

        given:
        settingsFile << "include 'greeter', 'app'"
        buildFile << """
            subprojects {
                apply plugin: 'maven-publish'
                
                group = 'some.group'
                version = '1.2'
                publishing {
                    repositories { maven { url '${repo.uri}' } }
                }
            }
            project(':app') { 
                apply plugin: 'cpp-executable'
                executable.baseName = 'testApp'
                dependencies {
                    implementation project(':greeter')
                }
            }
            project(':greeter') { 
                apply plugin: 'cpp-library'
                library.baseName = 'appGreeter'
            }
"""
        app.greeter.writeToProject(file('greeter'))
        app.main.writeToProject(file('app'))

        when:
        run('publish')

        then:
        def appModule = repo.module('some.group', 'testApp', '1.2')
        appModule.assertPublished()

        def appDebugModule = repo.module('some.group', 'testApp_debug', '1.2')
        appDebugModule.assertPublished()

        def appDebugMetadata = appDebugModule.parsedModuleMetadata
        def appDebugRuntime = appDebugMetadata.variant("debug-runtime")
        appDebugRuntime.dependencies.size() == 1
        appDebugRuntime.dependencies[0].coords == 'some.group:appGreeter:1.2'

        def appReleaseModule = repo.module('some.group', 'testApp_release', '1.2')
        appReleaseModule.assertPublished()

        def appReleaseMetadata = appReleaseModule.parsedModuleMetadata
        def appReleaseRuntime = appReleaseMetadata.variant("release-runtime")
        appReleaseRuntime.dependencies.size() == 1
        appReleaseRuntime.dependencies[0].coords == 'some.group:appGreeter:1.2'

        def greeterModule = repo.module('some.group', 'appGreeter', '1.2')
        greeterModule.assertPublished()

        def greeterDebugModule = repo.module('some.group', 'appGreeter_debug', '1.2')
        greeterDebugModule.assertPublished()

        def greeterReleaseModule = repo.module('some.group', 'appGreeter_release', '1.2')
        greeterReleaseModule.assertPublished()

        when:
        def consumer = file("consumer").createDir()
        consumer.file("settings.gradle") << ''
        consumer.file("build.gradle") << """
            dependencies {
                install 'some.group:testApp:1.2'
            }
"""
        executer.inDirectory(consumer)
        run("install")

        then:
        def executable = executable("consumer/install/testApp")
        executable.file.setExecutable(true)
        executable.exec().out == app.expectedOutput
    }

}
