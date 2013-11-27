/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.nativebinaries.internal;

import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.typeconversion.NotationParser;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.nativebinaries.BuildType;
import org.gradle.nativebinaries.FlavorContainer;
import org.gradle.nativebinaries.NativeBinary;
import org.gradle.nativebinaries.Platform;
import org.gradle.util.GUtil;

import java.util.HashSet;
import java.util.Set;

public class DefaultNativeComponent implements NativeComponentInternal {
    private final NotationParser<Object, Set<LanguageSourceSet>> sourcesNotationParser = SourceSetNotationParser.parser();
    private final String name;
    private final DomainObjectSet<LanguageSourceSet> sourceSets;
    private final DefaultDomainObjectSet<NativeBinary> binaries;
    private final DefaultFlavorContainer flavors;
    private final Set<String> targetPlatforms = new HashSet<String>();
    private final Set<String> buildTypes = new HashSet<String>();
    private String baseName;

    public DefaultNativeComponent(String name, Instantiator instantiator) {
        this.name = name;
        this.sourceSets = new DefaultDomainObjectSet<LanguageSourceSet>(LanguageSourceSet.class);
        binaries = new DefaultDomainObjectSet<NativeBinary>(NativeBinary.class);
        flavors = instantiator.newInstance(DefaultFlavorContainer.class, instantiator);
    }

    public String getName() {
        return name;
    }

    public DomainObjectSet<LanguageSourceSet> getSource() {
        return sourceSets;
    }

    public void source(Object sources) {
        sourceSets.addAll(sourcesNotationParser.parseNotation(sources));
    }

    public DomainObjectSet<NativeBinary> getBinaries() {
        return binaries;
    }

    public String getBaseName() {
        return GUtil.elvis(baseName, name);
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public FlavorContainer getFlavors() {
        return flavors;
    }

    public void flavors(Action<? super FlavorContainer> config) {
        config.execute(flavors);
    }

    public void targetPlatforms(Object platformSelector) {
        // TODO:DAZ Allow Platform instance
        // TODO:DAZ Allow platform selector
        assert platformSelector instanceof String;
        targetPlatforms.add((String) platformSelector);
    }

    public void buildTypes(Object buildTypeSelector) {
        // TODO:DAZ Allow BuildType instance
        assert buildTypeSelector instanceof String;
        buildTypes.add((String) buildTypeSelector);
    }

    public boolean buildForPlatform(Platform platform) {
        // TODO:DAZ Only target the current by default
        return targetPlatforms.isEmpty() || targetPlatforms.contains(platform.getName());
    }

    public boolean buildForBuildType(BuildType buildType) {
        return buildTypes.isEmpty() || buildTypes.contains(buildType.getName());
    }
}