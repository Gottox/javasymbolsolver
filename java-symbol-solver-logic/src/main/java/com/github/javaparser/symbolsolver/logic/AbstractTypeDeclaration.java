/*
 * Copyright 2016 Federico Tomassetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.javaparser.symbolsolver.logic;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.ReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Common ancestor for most types.
 *
 * @author Federico Tomassetti
 */
public abstract class AbstractTypeDeclaration implements ReferenceTypeDeclaration {

    private IdentityHashMap<DataKey<?>, Object> data = null;

    /**
     * Gets data for this component using the given key.
     *
     * @param <M> The type of the data.
     * @param key The key for the data
     * @return The data or null of no data was found for the given key
     * @see DataKey
     */
    @SuppressWarnings("unchecked")
    public <M> M getData(final DataKey<M> key) {
        if (data == null) {
            return null;
        }
        return (M) data.get(key);
    }

    /**
     * Sets data for this component using the given key.
     * For information on creating DataKey, see {@link DataKey}.
     *
     * @param <M> The type of data
     * @param key The singleton key for the data
     * @param object The data object
     * @see DataKey
     */
    public <M> void setData(DataKey<M> key, M object) {
        if (data == null) {
            data = new IdentityHashMap<>();
        }
        data.put(key, object);
    }

    @Override
    public final Set<MethodUsage> getAllMethods() {
        Set<MethodUsage> methods = new HashSet<>();

        Set<String> methodsSignatures = new HashSet<>();

        for (MethodDeclaration methodDeclaration : getDeclaredMethods()) {
            methods.add(new MethodUsage(methodDeclaration));
            methodsSignatures.add(methodDeclaration.getSignature());
        }

        for (ReferenceType ancestor : getAllAncestors()) {
            for (MethodUsage mu : ancestor.getDeclaredMethods()) {
                String signature = mu.getDeclaration().getSignature();
                if (!methodsSignatures.contains(signature)) {
                    methodsSignatures.add(signature);
                    methods.add(mu);
                }
            }
        }

        return methods;
    }

    @Override
    public final boolean isFunctionalInterface() {
        return FunctionalInterfaceLogic.getFunctionalMethod(this).isPresent();
    }
}
